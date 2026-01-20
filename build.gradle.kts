import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    id("com.diffplug.spotless") apply false
    id("com.google.protobuf") apply false
    id("net.ltgt.errorprone") apply false
}

val grpcStarterVersion: String = providers.gradleProperty("grpcStarterVersion").get()
val grpcVersion: String = providers.gradleProperty("grpcVersion").get()
val protobufVersion: String = providers.gradleProperty("protobufVersion").get()
val errorProneCoreVersion: String = providers.gradleProperty("errorProneCoreVersion").get()
val nullAwayVersion: String = providers.gradleProperty("nullAwayVersion").get()
val springBootVersion: String = providers.gradleProperty("springBootVersion").get()

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "io.spring.dependency-management")
    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("io.github.danielliu1123:grpc-starter-dependencies:$grpcStarterVersion")
            mavenBom("io.grpc:grpc-bom:$grpcVersion")
            mavenBom("com.google.protobuf:protobuf-bom:$protobufVersion")
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        }
    }

    dependencies {
        "compileOnly"("org.projectlombok:lombok")
        "annotationProcessor"("org.projectlombok:lombok")
        "testCompileOnly"("org.projectlombok:lombok")
        "testAnnotationProcessor"("org.projectlombok:lombok")

        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        "testImplementation"("org.assertj:assertj-core")
    }

    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            toggleOffOn()
            palantirJavaFormat()
            importOrder()
            removeUnusedImports()
            formatAnnotations()
            forbidWildcardImports()
            trimTrailingWhitespace()
            endWithNewline()

            targetExclude(
                "build/generated/**",
                "**/entity/*.java",
                "**/mapper/*Mapper.java",
                "**/mapper/*DynamicSqlSupport.java"
            )
        }
    }

    apply(plugin = "net.ltgt.errorprone")
    dependencies {
        val errorprone by configurations
        errorprone("com.google.errorprone:error_prone_core:$errorProneCoreVersion")
        errorprone("com.uber.nullaway:nullaway:$nullAwayVersion")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.errorprone {
            isEnabled = project.findProperty("errorprone.enabled")?.toString()?.toBoolean() != false
            // https://github.com/tbroyer/gradle-errorprone-plugin?tab=readme-ov-file#properties
            excludedPaths = "(.*/(generated|proto-gen-java)/.*)|(.*/(mapper|entity)/[^/]+\\.java)"
            // https://github.com/uber/NullAway/wiki/Configuration
            check("NullAway", net.ltgt.gradle.errorprone.CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "monorepo")
            option("NullAway:HandleTestAssertionLibraries", "true")
        }
    }
}
