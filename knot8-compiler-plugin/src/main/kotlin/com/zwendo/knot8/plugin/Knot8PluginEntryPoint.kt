package com.zwendo.knot8.plugin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin

internal val KEY_ENABLED: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey<Boolean>("enabled")

/**
 * Class that process command line when plugin is used
 */
internal class Knot8CommandLineProcessor : CommandLineProcessor {
    override val pluginId = "knot8"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption("enabled", "<true|false>", "whether plugin is enabled"),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) = when (option.optionName) {
        "enabled" -> configuration.put(KEY_ENABLED, value.toBooleanStrict())
        else -> throw CliOptionProcessingException("Unexpected option: ${option.optionName}")
    }
}

/**
 * Class that registers knot8 custom class generation interceptor
 */
internal class Knot8ComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) return // return if not enabled

        ClassBuilderInterceptorExtension.registerExtension(
            project,
            Knot8ClassGenerationInterceptor(configuration)
        )
    }
}

/**
 * Class that intercepts class generation and modifies the class builder factory to add kn8t logic
 */
internal class Knot8ClassGenerationInterceptor(val configuration: CompilerConfiguration) :
    ClassBuilderInterceptorExtension {

    override fun interceptClassBuilderFactory(
        interceptedFactory: ClassBuilderFactory,
        bindingContext: BindingContext,
        diagnostics: DiagnosticSink
    ): ClassBuilderFactory {
        return object : ClassBuilderFactory by interceptedFactory {
            override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder {
                return Knot8ClassBuilder(interceptedFactory.newClassBuilder(origin), configuration)
            }
        }
    }
}