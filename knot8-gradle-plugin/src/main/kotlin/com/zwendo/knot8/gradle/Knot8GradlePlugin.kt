package com.zwendo.knot8.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption


class Knot8GradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create("knot8", Knot8Configuration::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.findByType(Knot8Configuration::class.java) ?: Knot8Configuration()

        val isEnabled = SubpluginOption("enabled", extension.enabled.toString())

        return project.provider {
            listOf(isEnabled)
        }
    }

    override fun getCompilerPluginId(): String = "knot8"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact("com.zwendo", "knot8-plugin", "0.1.0")

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.target.project.plugins.hasPlugin("com.zwendo.knot8")
    }
}

open class Knot8Configuration {
    var enabled: Boolean = true
}
