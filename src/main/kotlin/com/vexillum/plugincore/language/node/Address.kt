package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonValue

interface Address {

    val hash: Int

    val parent: Address?

    val lastKey: String

    fun child(part: String): Address =
        if (part.isEmpty()) {
            this
        } else {
            val parts = part.split('.')
            if (parts.size == 1) {
                AddressNode(this, parts.first())
            } else {
                parts.fold(this) { acc, innerPart ->
                    acc.child(innerPart)
                }
            }
        }

    fun isRoot() =
        this == Root

    @JsonValue
    fun serialize(): Any =
        toString()

    object Root : Address {
        override val hash: Int = 1
        override val parent: Address? = null
        override val lastKey: String get() = error("Root address has no lastKey")
        override fun toString(): String = "<root>"
    }

    private class AddressNode(
        override val parent: Address,
        override val lastKey: String
    ) : Address {

        override val hash: Int = parent.hash * HASH_FACTOR + lastKey.hashCode()

        override fun toString(): String =
            if (parent.isRoot()) lastKey else "$parent.$lastKey"

        override fun hashCode(): Int =
            hash

        override fun equals(other: Any?): Boolean =
            (other as? Address)?.hash == this.hash

        companion object {
            private const val HASH_FACTOR = 31
        }
    }

    companion object {
        fun of(
            key: String,
            parent: Address = Root,
        ): Address =
            parent.child(key)
    }
}
