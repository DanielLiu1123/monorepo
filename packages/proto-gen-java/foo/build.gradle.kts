dependencies {
    api(project(":packages:proto-gen-java:foo:bar"))
}

allprojects {
    tasks.configureEach {
        val lintTasks = listOf("spotless", "spotbugs")
        if (lintTasks.any { name.contains(it) }) {
            enabled = false
        }
    }
}
