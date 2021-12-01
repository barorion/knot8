package com.zwendo.knot8.plugin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Type

internal class Knot8ClassBuilder(
    private val delegateBuilder: ClassBuilder,
    val configuration: CompilerConfiguration
) : DelegatingClassBuilder() {

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
        val parameters = parameters(origin.descriptor.toString(), desc)
        return Knot8MethodVisitor(original, access, name, desc, signature ?: "", exceptions ?: arrayOf(), parameters)
    }

    private fun parameters(paramNameDesc: String, paramTypeDesc: String): List<FunctionParameter> {
        val types = Type.getArgumentTypes(paramTypeDesc)
        val names = paramNameDesc.slice(IntRange(paramNameDesc.indexOf('(') + 1, paramNameDesc.lastIndexOf(')')))
            .split(", ")
            .map { it.replaceAfter(':', "").dropLast(1) }

        val parameters = mutableListOf<FunctionParameter>()
        for (i in types.indices) {
            parameters.add(FunctionParameter(names[i], types[i]))
        }
        return parameters
    }
}
