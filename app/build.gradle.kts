plugins {
    alias(libs.plugins.android.application)

    // Apply Firebase plugins using version catalog aliases
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.leslie.cjpokeroddscalculator"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.leslie.cjpokeroddscalculator"
        minSdk = 34
        targetSdk = 37
        versionCode = 37
        versionName = "2.21"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += "-O3 -std=c++11 -Wall -Wpedantic"
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            // Disables runtime crash collection for debug builds
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false

            // Optional: Speeds up debug build time by skipping Crashlytics tasks
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }

            // Disables performance collection in debug builds
            configure<com.google.firebase.perf.plugin.FirebasePerfExtension> {
                setInstrumentationEnabled(false)
            }
        }

        getByName("release") {
            // Enables runtime crash collection for release builds
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            // Enables R8 code shrinking, obfuscation, and resource shrinking
            optimization {
                enable = true
            }

            // Includes default Android optimization rules and your custom proguard rules
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Ensures Crashlytics uploads the R8 mapping.txt file during release builds
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.guava)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.rxjava3)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}