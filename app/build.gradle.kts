import org.gradle.kotlin.dsl.implementation
import java.util.Properties

val signingProperties = Properties().apply {
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(::load)
    }
}

fun signingCredential(environmentName: String, propertyName: String): String? =
    providers.environmentVariable(environmentName).orNull
        ?.takeIf(String::isNotBlank)
        ?: signingProperties.getProperty(propertyName)?.takeIf(String::isNotBlank)

val releaseStoreFile = signingCredential("FREDDY_UPLOAD_STORE_FILE", "uploadStoreFile")
val releaseStorePassword = signingCredential("FREDDY_UPLOAD_STORE_PASSWORD", "uploadStorePassword")
val releaseKeyAlias = signingCredential("FREDDY_UPLOAD_KEY_ALIAS", "uploadKeyAlias")
val releaseKeyPassword = signingCredential("FREDDY_UPLOAD_KEY_PASSWORD", "uploadKeyPassword")
val releaseSigningConfigured = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
).all { it != null }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "eu.indiewalkabout.fridgemanager"
    compileSdk = 36


    defaultConfig {
        applicationId = "eu.indiewalkabout.fridgemanager"
        minSdk = 26
        targetSdk = 36
        versionCode = 12
        versionName = "2.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = rootProject.file(requireNotNull(releaseStoreFile))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

if (!releaseSigningConfigured) {
    tasks.configureEach {
        if (name == "preReleaseBuild") {
            doFirst {
                throw GradleException(
                    "Release signing is not configured. Set FREDDY_UPLOAD_* environment " +
                        "variables or create an ignored keystore.properties file.",
                )
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.review)
    implementation(libs.app.update.ktx)
    implementation(libs.androidx.core.splashscreen)

    // compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.foundation)
    implementation(libs.foundation.layout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Accompanist lib for compose integration
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.permissions)

    // Core library desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // gson
    implementation(libs.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)

    // Ad mob
    implementation(libs.playservices.ads)
    implementation(libs.user.messaging.platform)

    // Unity
    // implementation(libs.unity.ads)

    // Preference
    implementation(libs.androidx.preference.ktx)

    // Kotpref SharePreferences lib: https://github.com/chibatching/Kotpref
    implementation(libs.kotpref)
    implementation(libs.initializer)
    implementation(libs.enum.support)
    implementation(libs.gson.support)
    implementation(libs.livedata.support)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.preference.screen.dsl)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
