package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.util.hasFlags
import org.jetbrains.org.objectweb.asm.Opcodes


/**
 * Represents the 3 method kinds with different variable stacks and parameter related to 'this'.
 */
internal enum class MethodKind {
    /**
     * Stack -> no 'this'
     * Parameters -> no 'this'
     */
    STATIC,

    /**
     * Stack -> 'this'
     * Parameters -> no 'this'
     */
    CONSTRUCTOR,

    /**
     * Stack -> 'this'
     * Parameters -> 'this'
     */
    INSTANCE;

    companion object {
        /**
         * Compute the corresponding kind for a given function.
         *
         * @param name the name of the method
         * @param access the access flags of the method
         * @return the method kind of the method
         */
        fun getKind(name: String, access: Int): MethodKind = when {
            name == "<init>" -> CONSTRUCTOR
            access.hasFlags(Opcodes.ACC_STATIC) -> STATIC
            else -> INSTANCE
        }
    }
}