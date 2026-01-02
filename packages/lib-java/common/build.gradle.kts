val springdocBridgeVersion: String = providers.gradleProperty("springdocBridgeVersion").get()

dependencies {
  api("org.springframework.boot:spring-boot-starter")
  api("org.springframework.boot:spring-boot-starter-jackson")
  api("com.google.protobuf:protobuf-java")
  api("com.google.protobuf:protobuf-java-util")
  api("io.github.danielliu1123:jackson-module-protobuf:$springdocBridgeVersion") {
    exclude(group = "com.google.protobuf", module = "protobuf-java-util")
  }

  compileOnly("org.springframework.boot:spring-boot-starter-opentelemetry")

  compileOnly("org.springframework.boot:spring-boot-starter-web")
  compileOnly("org.springframework.boot:spring-boot-starter-jdbc")
  compileOnly("org.springframework.boot:spring-boot-starter-restclient")
  compileOnly("io.github.danielliu1123:grpc-boot-starter")
}
