package com.zwendo.knot8.plugin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class Knot8ClassBuilder internal constructor(
    private val delegateBuilder: ClassBuilder,
    val configuration: CompilerConfiguration
) :
    DelegatingClassBuilder() {

    internal enum class MethodType {
        METHOD,
        CONSTRUCTOR
    }

    override fun getDelegate(): ClassBuilder = delegateBuilder

    override fun newMethod(
        origin: JvmDeclarationOrigin,
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)
        val type: MethodType = when (name) {
            "<init>" -> MethodType.CONSTRUCTOR
            else -> MethodType.METHOD
        }
        return Knot8MethodVisitor(original, type)
    }

}
