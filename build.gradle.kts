plugins {
    id("org.springframework.boot") version "4.0.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.1.0" apply false
    id("com.google.protobuf") version "0.9.6" apply false
    id("net.ltgt.errorprone") version "4.3.0" apply false
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
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")

    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("io.github.danielliu1123:grpc-starter-dependencies:$grpcStarterVersion")
            mavenBom("io.grpc:grpc-bom:$grpcVersion")
            mavenBom("com.google.protobuf:protobuf-bom:$protobufVersion")
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
        options.encoding = "UTF-8"
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        val compileOnly by configurations
        val annotationProcessor by configurations
        val testCompileOnly by configurations
        val testAnnotationProcessor by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")

        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        testImplementation("org.assertj:assertj-core")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs("-XX:+EnableDynamicAgentLoading")
    }

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        encoding("UTF-8")
        java {
            toggleOffOn()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            palantirJavaFormat()
            forbidWildcardImports()

            targetExclude(
                "build/generated/**",
                "**/entity/*.java",
                "**/mapper/*Mapper.java",
                "**/mapper/*DynamicSqlSupport.java"
            )

            custom("Refuse wildcard imports") { input ->
                if (Regex("\nimport .+\\*;").containsMatchIn(input)) {
                    throw GradleException("Do not use wildcard imports, 'spotlessApply' cannot resolve this issue, please fix it manually.")
                }
                input
            }
        }

        kotlinGradle {
            toggleOffOn()
            trimTrailingWhitespace()
            endWithNewline()
            ktfmt()
        }
    }

    apply(plugin = "net.ltgt.errorprone")
    dependencies {
        val errorprone by configurations
        errorprone("com.google.errorprone:error_prone_core:$errorProneCoreVersion")
        errorprone("com.uber.nullaway:nullaway:$nullAwayVersion")
    }

    tasks.withType<JavaCompile>().configureEach {
        (this.options as ExtensionAware).extensions.configure<net.ltgt.gradle.errorprone.ErrorProneOptions>("errorprone") {
            excludedPaths.set("(.*/(generated|proto-gen-java)/.*)|(.*/(mapper|entity)/[^/]+\\.java)")
            check("NullAway", net.ltgt.gradle.errorprone.CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "monorepo")
            option("NullAway:HandleTestAssertionLibraries", "true")
        }
    }
}
