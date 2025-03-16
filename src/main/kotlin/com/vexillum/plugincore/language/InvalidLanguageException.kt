package com.vexillum.plugincore.language

import com.fasterxml.jackson.databind.exc.MismatchedInputException

open class InvalidLanguageException(
    private val causeException: Exception?
) : Exception() {

    override val message: String?
        get() = causeException?.message
}

class MissingLanguageKeyException(
    mappingException: MismatchedInputException
) : InvalidLanguageException(null) {
    private var fileName: String? = null

    private val fields = mappingException.path.map {
        (it.from as Class<*>).simpleName to it.fieldName
    }

    fun throwWithFileName(fileName: String) {
        this.fileName = fileName
        throw this
    }

    override val message: String
        get() {
            var previous = "\n"
            for (i in fields.lastIndex downTo 0) {
                val (simpleName, fieldName) = fields[i]
                previous = StringBuilder().run {
                    if (i == 0) {
                        appendLine()
                        if (fileName != null) {
                            appendLine("// $fileName")
                        }
                        appendLine("{")
                    }
                    if (i != fields.lastIndex) {
                        appendIdent("\"$fieldName\": {")
                        appendIdent(previous)
                    } else {
                        appendIdent("\"$fieldName\": <--- Missing field in json, language class $simpleName is expecting this field")
                    }
                    append("}")
                    toString()
                }
            }
            return previous
        }

    private fun StringBuilder.appendIdent(line: String) =
        appendLine(line.prependIndent(INDENT))

    companion object {
        private const val INDENT = "  "
    }
}
