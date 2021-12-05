package com.zwendo.knot8.annotation

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class NotZero

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target( AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
annotation class PosOrZero

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
annotation class NotEmpty

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD)
annotation class BasicContainer(val suffix: String = "")