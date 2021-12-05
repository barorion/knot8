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
        val parameters = parameters(origin.descriptor.toString(), desc, isThisInStack(access, name), name == "<init>")
        return Knot8MethodVisitor(original, access, name, desc, signature ?: "", exceptions ?: arrayOf(), parameters)
    }

    /**
     * Creates the list of [FunctionParameter] for the current method.
     *
     * @param detailedDesc the detailed descriptor of the method, containing param names
     * @param bytecodeDesc the bytecode version of the method descriptor
     * @param isThisInStack true if 'this' is in the variables stack of the method; else otherwise
     * @param isConstructor true if the current method is a constructor; false otherwise
     * @return the list of [FunctionParameter] for the current method
     */
    private fun parameters(
        detailedDesc: String,
        bytecodeDesc: String,
        isThisInStack: Boolean,
        isConstructor: Boolean,
    ): List<FunctionParameter> {
        val types = typesList(detailedDesc, isThisInStack, isConstructor)
        val names = namesList(isConstructor)

        // add all types and names to lists
        types += Type.getArgumentTypes(bytecodeDesc).toMutableList()
        names += detailedDesc.slice(IntRange(detailedDesc.indexOf('(') + 1, detailedDesc.lastIndexOf(')')))
            .split(", ")
            .map { it.replaceAfter(':', "").dropLast(1) }
            .filter { it.isNotEmpty() }

        // create FunctionParameters
        val parameters = mutableListOf<FunctionParameter>()
        for (i in types.indices) {
            parameters.add(FunctionParameter(names[i], types[i], i))
        }
        return parameters
    }

    private fun typesList(desc: String, isThisInStack: Boolean, isConstructor: Boolean): MutableList<Type> {
        val types = mutableListOf<Type>()
        val typeName = if (isConstructor) { // this type is in the return type
            val start = "returnType:"
            desc.substring(desc.indexOf(start) + start.length, desc.lastIndexOf("[") - 1)
        } else { // this type is the first parameter
            val start = "\$this:"
            desc.substring(desc.indexOf(start) + start.length, desc.indexOf(","))
        }
        types += Type.getType("L${typeName.replace(".", "/")};")
        return types
    }

    private fun namesList(isConstructor: Boolean): MutableList<String> = if (isConstructor) {
        // empty list, even if instance method because this is already in the detailed descriptor and will be added
        mutableListOf()
    } else {
        mutableListOf("\$this")
    }

    private fun isThisInStack(access: Int, name: String): Boolean = !access.hasFlags(Opcodes.ACC_STATIC)
}

/**
 * Represents a function parameter, an instance of this class contains all information about a parameter in a function.
 *
 * @constructor creates a [FunctionParameter] instance
 * @param name the name of the parameter
 * @param type the type of the parameter
 * @param index the index of the parameter in the local variables stack
 */
internal data class FunctionParameter(val name: String, val type: Type, val index: Int)
