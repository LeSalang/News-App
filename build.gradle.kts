import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects.onEach {
    it.afterEvaluate {
        with(project.plugins) {
            if (hasPlugin(libs.plugins.jetbrains.kotlin.android.get().pluginId) || hasPlugin(libs.plugins.jetbrains.kotlin.jvm.get().pluginId)) {
                apply(libs.plugins.detekt.get().pluginId)
                extensions.configure<DetektExtension> {
                    config.setFrom(rootProject.files("default-detekt-config.yml"))
                }
                dependencies.add(
                    "detektPlugins",
                    libs.detekt.formatting.get()
                        .toString()
                )
            }
        }
    }
}
