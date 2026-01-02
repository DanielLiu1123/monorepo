dependencies {
    api("com.google.protobuf:protobuf-java")
    api("io.grpc:grpc-protobuf")
    api("io.grpc:grpc-stub")
    api(project(":packages:proto-gen-java:monorepo:common"))
}
