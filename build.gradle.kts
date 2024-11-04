// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.dagger.hilt) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    kotlin("jvm") version "1.9.10"
    id("com.google.devtools.ksp") apply false
}