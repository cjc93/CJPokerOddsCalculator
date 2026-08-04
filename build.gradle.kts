// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Firebase Plugins
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}