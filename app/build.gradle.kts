import org.gradle.internal.impldep.org.jsoup.safety.Safelist.basic

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)  // Apply the Safe Args plugin for type-safe navigation arguments


}

android {
    namespace = "com.example.jawwna"
    compileSdk = 34
    ndkVersion ="23.0.7599858"
    defaultConfig {
        applicationId = "com.example.jawwna"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Add this line to access the token in BuildConfig
//        buildConfigField("String", "GOOGLE_API_KEY", "\"${project.properties["API_KEY"]}\"")

        buildConfigField("String", "OPEN_WEATHER_API_KEY", "\"${project.properties["OPEN_WEATHER_API_KEY"]}\"")
        buildConfigField("String", "OPEN_WEATHER_API_KEY_PRO", "\"${project.properties["OPEN_WEATHER_API_KEY_PRO"]}\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${project.properties["GOOGLE_MAPS_API_KEY"]}\"")
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${project.properties["MAPBOX_ACCESS_TOKEN"]}\"")

        manifestPlaceholders["google_maps_api_key"] = project.properties["GOOGLE_MAPS_API_KEY"] as Any
        manifestPlaceholders["mapbox_access_token"] = project.properties["MAPBOX_ACCESS_TOKEN"]as Any
        manifestPlaceholders["open_weather_map_api_key"] = project.properties["OPEN_WEATHER_API_KEY"]as Any
        manifestPlaceholders["open_weather_map_api_key_pro"] = project.properties["OPEN_WEATHER_API_KEY_PRO"]as Any

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        android.buildFeatures.buildConfig=true

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }



}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Gson

    implementation("com.google.code.gson:gson:2.11.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    //lottie
    implementation("com.airbnb.android:lottie:6.0.0")

    //navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.0")

    //google map
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    //material
    implementation ("com.google.android.material:material:1.12.0")
    implementation("com.google.android.material:material:1.10.0-alpha03")


    // mapbox
    //implementation("com.mapbox.maps:android:11.6.1")





}

