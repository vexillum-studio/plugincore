package com.vexillum.plugincore.extensions

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption.COPY_ATTRIBUTES
import java.nio.file.attribute.BasicFileAttributes

fun Path.copyFolder(destination: Path) {
    Files.walkFileTree(
        this,
        object : SimpleFileVisitor<Path>() {

            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetDir = destination.resolve(relativize(dir))
                Files.copy(dir, targetDir, COPY_ATTRIBUTES)
                return FileVisitResult.CONTINUE
            }

            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val targetFile = destination.resolve(relativize(file))
                Files.copy(file, targetFile, COPY_ATTRIBUTES)
                return FileVisitResult.CONTINUE
            }
        }
    )
}
