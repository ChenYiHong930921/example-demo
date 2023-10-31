plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.chenyihong.exampledemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.chenyihong.exampledemo"
        minSdk = 23
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("origin") {
            keyAlias = "expampledemo"
            keyPassword = "123456"
            storeFile = file("ExampleDemo")
            storePassword = "123456"
        }

        create("exampleFlavor") {
            keyAlias = "exampledemoflavor"
            keyPassword = "123456"
            storeFile = file("ExampleDemoFlavor.jks")
            storePassword = "123456"
        }

        create("twa") {
            keyAlias = "twaexample"
            keyPassword = "123456"
            storeFile = file("TWAExample")
            storePassword = "123456"
        }

        create("admob") {
            keyAlias = "expampledemo"
            keyPassword = "123456"
            storeFile = file("ExampleDemo")
            storePassword = "123456"
        }
    }

    flavorDimensions.add("example_value")

    productFlavors {
        create("origin") {
            manifestPlaceholders.put("channel_value", "origin channel")
            buildConfigField("String", "example_value", "\"origin server address\"")
            resValue("string", "example_value", "origin tips")

            signingConfig = signingConfigs.getByName("origin")
        }

        create("exampleFlavor") {
            applicationId = "com.chenyihong.exampledflavordemo"
            versionCode = 2
            versionName = "1.0.2-flavor"

            manifestPlaceholders.put("channel_value", "flavor channel")
            buildConfigField("String", "example_value", "\"flavor server address\"")
            resValue("string", "example_value", "flavor tips")

            signingConfig = signingConfigs.getByName("exampleFlavor")
        }

        create("admob") {
            versionName = "1.0-admob"

            manifestPlaceholders.put("channel_value", "admob channel")
            buildConfigField("String", "example_value", "\"admob server address\"")
            resValue("string", "example_value", "admob tips")

            signingConfig = signingConfigs.getByName("admob")
        }

        create("twa") {
            versionName = "1.0-twa"

            manifestPlaceholders.put("channel_value", "twa channel")
            buildConfigField("String", "example_value", "\"twa server address\"")
            resValue("string", "example_value", "twa tips")

            signingConfig = signingConfigs.getByName("twa")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = null
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = false
            signingConfig = null
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

configurations.configureEach {
    resolutionStrategy.force("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.appcompat:appcompat-resources:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")

    // gson
    implementation("com.google.code.gson:gson:2.10.1")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // glide
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // CameraX
    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:1.2.3")

    //Google Login
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    //Meta login
    implementation("com.facebook.android:facebook-login:16.2.0")
    //Mate share
    implementation("com.facebook.android:facebook-share:16.2.0")

    //Biometric
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Preference
    implementation("androidx.preference:preference-ktx:1.2.1")

    // GAID - Google
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("androidx.ads:ads-identifier:1.0.0-alpha05")
    implementation("com.google.guava:guava:28.0-android")

    // Admob
    implementation("com.google.android.gms:play-services-ads:22.4.0")

    // Firebase cloud message
    implementation("com.google.firebase:firebase-messaging:23.2.1")

    // Google Custom Tab
    implementation("androidx.browser:browser:1.6.0")

    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Trusted Web Activity Helper
    "twaImplementation"("com.google.androidbrowserhelper:androidbrowserhelper:2.5.0")

    // dom4j
    implementation("org.dom4j:dom4j:2.1.4")

    // Media3
    implementation("androidx.media3:media3-ui:1.1.1")
    implementation("androidx.media3:media3-session:1.1.1")
    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.1.1")

    // map
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.libraries.maps:maps:3.1.0-beta")
}