package com.zwendo.knot8.annotation

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class NotZero

@Deprecated("", ReplaceWith("println(\"oui\")"))
fun foo(@NotZero ouep: Any) {
    println("oui")
}