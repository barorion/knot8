package com.zwendo.knot8.annotation

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class NotZero

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class PosOrZero

fun foo(@NotZero mySuperParameter: Int) {
    println("Ok $mySuperParameter")
}

fun main() {
    foo(0)
}
