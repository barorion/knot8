package com.zwendo.knot8.plugin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type

internal class Knot8ClassBuilder(
    private val delegateBuilder: ClassBuilder,
    val configuration: CompilerConfiguration
) : DelegatingClassBuilder() {
    private lateinit var currentClassName: String

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
        val parameters = parameters(origin.descriptor.toString(), desc, MethodKind.getKind(name, access))
        val data = Knot8MethodVisitorData(currentClassName, original, access, name, desc, signature, exceptions)
        return Knot8MethodVisitor(data, parameters)
    }

    override fun defineClass(
        origin: PsiElement?,
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String,
        interfaces: Array<out String>
    ) {
        currentClassName = name
        super.defineClass(origin, version, access, name, signature, superName, interfaces)
    }

    /**
     * Creates the list of [FunctionParameter] for the current method.
     *
     * @param detailedDesc the detailed descriptor of the method, containing param names
     * @param bytecodeDesc the bytecode version of the method descriptor
     * @param methodKind the current method kind
     * @return the list of [FunctionParameter] for the current method
     */
    private fun parameters(
        detailedDesc: String,
        bytecodeDesc: String,
        methodKind: MethodKind
    ): List<FunctionParameter> {
        val types = typesList(detailedDesc, methodKind)
        val names = namesList(methodKind)

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

    /**
     * Creates the list of parameters type for the current method. The purpose of this method is to
     * add the type of this is the current method is an instance method or a constructor.
     *
     * @param desc the detailed descriptor of the current method
     * @param methodKind the current method kind
     * @return the list of the parameters types (empty or containing this)
     */
    private fun typesList(desc: String, methodKind: MethodKind): MutableList<Type> = when (methodKind) {
        MethodKind.STATIC -> mutableListOf()
        MethodKind.CONSTRUCTOR -> { // this type is in return type
            val start = "returnType:"
            val fqName = desc.substring(desc.indexOf(start) + start.length, desc.lastIndexOf("[") - 1)
            mutableListOf(Type.getType(fqName.fqNameToDescriptor()))
        }
        MethodKind.INSTANCE -> { // this type is the first parameter type
            val start = "\$this:"
            val fqName = desc.substring(desc.indexOf(start) + start.length, desc.indexOf(","))
            mutableListOf(Type.getType(fqName.fqNameToDescriptor()))
        }
    }

    /**
     * Creates the list of parameters names for the current method. The purpose of this method is to
     * add the type of this is the current method is a constructor (not for instance method, because it
     * will be added automatically due to the presence of this in the parameters).
     *
     * @param methodKind the current method kind
     * @return the list of the parameters names (empty or containing this)
     */
    private fun namesList(methodKind: MethodKind): MutableList<String> = when (methodKind) {
        MethodKind.CONSTRUCTOR -> mutableListOf("\$this")
        // empty list, even if instance method because this is already in the detailed descriptor and will be added
        MethodKind.INSTANCE,
        MethodKind.STATIC -> mutableListOf()
    }
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

/**
 * Represents the 3 method kinds with different variable stacks and parameter related to 'this'.
 */
internal enum class MethodKind {
    /**
     * Stack -> no this
     * Parameters -> no this
     */
    STATIC,
    /**
     * Stack -> this
     * Parameters -> no this
     */
    CONSTRUCTOR,
    /**
     * Stack -> this
     * Parameters -> this
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
        fun getKind(name: String, access: Int) = if (name == "<init>") {
            CONSTRUCTOR
        } else if (access.hasFlags(Opcodes.ACC_STATIC)) {
            STATIC
        } else {
            INSTANCE
        }
    }
}
