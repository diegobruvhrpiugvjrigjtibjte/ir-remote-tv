plugins { id("com.android.application") }

android {
    namespace = "app.telecomandoir"
    compileSdk = 34
    defaultConfig {
        applicationId = "app.telecomandoir"
        minSdk = 19          // ConsumerIrManager esiste dalla 4.4
        targetSdk = 21       // Android 5.0.1: ultima versione ufficiale del Galaxy S4
        versionCode = 3
        versionName = "2.0"
    }
    buildTypes { release { isMinifyEnabled = true; isShrinkResources = true; signingConfig = signingConfigs.getByName("debug") } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_1_8; targetCompatibility = JavaVersion.VERSION_1_8 }
    lint { abortOnError = false; checkReleaseBuilds = false }
}

dependencies { testImplementation("junit:junit:4.13.2"); testImplementation("org.json:json:20231013") }
