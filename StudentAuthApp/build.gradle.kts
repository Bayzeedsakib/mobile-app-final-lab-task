// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Google services plugin for Firebase (classpath so it can be applied in app module)
        classpath("com.google.gms:google-services:4.4.4")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}