dependencies {
    api(project(":packages:proto-gen-java:monorepo:order"))
    api(project(":packages:proto-gen-java:monorepo:todo"))
    api(project(":packages:proto-gen-java:monorepo:product"))
    api(project(":packages:proto-gen-java:monorepo:common"))
    api(project(":packages:proto-gen-java:monorepo:user"))
}

allprojects {
    tasks.configureEach {
        val lintTasks = listOf("spotless", "spotbugs")
        if (lintTasks.any { name.contains(it) }) {
            enabled = false
        }
    }
}
