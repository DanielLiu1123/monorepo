dependencies {
    api "com.google.protobuf:protobuf-java"
    api "io.grpc:grpc-protobuf"
    api "io.grpc:grpc-stub"
}

tasks.configureEach { task ->
    def lintTasks = ["spotless", "spotbugs"]
    if (lintTasks.any { task.name.contains(it) }) {
        enabled = false
    }
}
