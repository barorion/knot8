package com.zwendo.knot8.plugin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
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
        val parameters = parameters(origin.descriptor.toString(), desc, access.hasFlags(Opcodes.ACC_STATIC))
        return Knot8MethodVisitor(original, access, name, desc, signature ?: "", exceptions ?: arrayOf(), parameters)
    }

    private fun parameters(paramNameDesc: String, paramTypeDesc: String, isStatic: Boolean): List<FunctionParameter> {
        val types = Type.getArgumentTypes(paramTypeDesc)
        val names = paramNameDesc.slice(IntRange(paramNameDesc.indexOf('(') + 1, paramNameDesc.lastIndexOf(')')))
            .split(", ")
            .map { it.replaceAfter(':', "").dropLast(1) }

        val parameters = mutableListOf<FunctionParameter>()
        val offset = if (isStatic) 0 else 1
        for (i in types.indices) {
            val stackIndex = i + offset
            parameters.add(FunctionParameter(names[stackIndex], types[i], stackIndex))
        }
        return parameters
    }

}

internal data class FunctionParameter(val name: String, val type: Type, val index: Int)
