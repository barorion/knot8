package com.zwendo.knot8.plugin.util

/**
 * Tests if an [this] which is representing binary flags has particular flags
 *
 * @param flags the different flags, separated by a [or] or a [plus]
 * @return true if [this] has all the flags; else otherwise
 */
private fun Int.hasFlags(flags: Int): Boolean = this and flags == flags

/**
 * Tests if an [this] which is representing binary flags has particular flags
 *
 * @param flags the different flags, separated by a comma
 * @return true if [this] has all the flags; else otherwise
 */
internal fun Int.hasFlags(vararg flags: Int): Boolean = hasFlags(flags.sum())

internal fun Class<out Any>.doesInheritsFrom(clazz: Class<out Any>): Boolean {
    return (this == clazz) || (superclass?.doesInheritsFrom(clazz) ?: false)
}

internal fun Class<out Any>.findFirstSuperInterface(interfaces: Collection<Class<out Any>>): Class<out Any>? {
    return if (interfaces.contains(this)) {
        this
    } else {
        this.interfaces.find { it.findFirstSuperInterface(interfaces) != null }
    }
}
