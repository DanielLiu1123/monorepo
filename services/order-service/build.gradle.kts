plugins {
    id("org.springframework.boot")
}

val mapstructVersion: String = providers.gradleProperty("mapstructVersion").get()

dependencies {
    implementation("io.github.danielliu1123:grpc-boot-starter")
    implementation(project(":packages:proto-gen-java:monorepo:order"))
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation(project(":packages:lib-java:mapstruct-spi-protobuf"))

    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:+")
    annotationProcessor(project(":packages:lib-java:mapstruct-spi-protobuf"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.google.testing.compile:compile-testing:+")
    testImplementation("org.mapstruct:mapstruct-processor:$mapstructVersion")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveBaseName.set("app")
    archiveVersion.set("")
}
