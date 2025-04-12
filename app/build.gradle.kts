plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.movierecommender"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.movierecommender"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AndroidX Core and AppCompat
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

// ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")

// Material Components
    implementation("com.google.android.material:material:1.11.0")

// Room components
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
// Optional - Kotlin Extensions and Coroutines support
    implementation("androidx.room:room-ktx:2.6.1")

// ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

// Gson
    implementation("com.google.code.gson:gson:2.10.1")

// Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

// CardView
    implementation("androidx.cardview:cardview:1.0.0")

// CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

// Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

}