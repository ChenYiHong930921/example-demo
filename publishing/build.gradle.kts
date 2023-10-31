plugins {
    id ("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20230511-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")
}