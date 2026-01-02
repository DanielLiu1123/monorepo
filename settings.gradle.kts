pluginManagement {
    val springBootVersion: String = providers.gradleProperty("springBootVersion").get()
    val dependencyManagementPluginVersion: String = providers.gradleProperty("dependencyManagementPluginVersion").get()
    val spotlessPluginVersion: String = providers.gradleProperty("spotlessPluginVersion").get()
    val protobufPluginVersion: String = providers.gradleProperty("protobufPluginVersion").get()
    val errorPronePluginVersion: String = providers.gradleProperty("errorPronePluginVersion").get()

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementPluginVersion
        id("com.diffplug.spotless") version spotlessPluginVersion
        id("com.google.protobuf") version protobufPluginVersion
        id("net.ltgt.errorprone") version errorPronePluginVersion
    }
}

rootProject.name = "monorepo"

include(":packages:lib-java:common")
include(":packages:lib-java:mapstruct-spi-protobuf")
include(":packages:lib-java:mybatis")
include(":packages:lib-java:record-builder")

include(":packages:proto-gen-java:foo")
include(":packages:proto-gen-java:foo:bar")
include(":packages:proto-gen-java:monorepo")
include(":packages:proto-gen-java:monorepo:product")
include(":packages:proto-gen-java:monorepo:common")
include(":packages:proto-gen-java:monorepo:todo")
include(":packages:proto-gen-java:monorepo:user")
include(":packages:proto-gen-java:monorepo:order")

include(":services:product-service")
include(":services:order-service")
include(":services:search-service")
include(":services:todo-service")
