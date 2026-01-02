import com.diffplug.gradle.spotless.SpotlessExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.ErrorProneOptions

plugins {
    id("org.springframework.boot") version "4.0.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.1.0" apply false
    id("com.google.protobuf") version "0.9.6" apply false
    id("net.ltgt.errorprone") version "4.3.0" apply false
}

val grpcStarterVersion: String by project
val grpcVersion: String by project
val protobufVersion: String by project
val errorProneCoreVersion: String by project
val nullAwayVersion: String by project
val springBootVersion: String by project

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")

    configure<DependencyManagementExtension> {
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

    configure<SpotlessExtension> {
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

//            custom("Refuse wildcard imports") {
//                if (Regex("\nimport .+\\*;").containsMatchIn(it)) {
//                    throw IllegalStateException("Do not use wildcard imports, 'spotlessApply' cannot resolve this issue, please fix it manually.")
//                }
//                it
//            }
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
        (this.options as ExtensionAware).extensions.configure<ErrorProneOptions>("errorprone") {
            excludedPaths.set("(.*/(generated|proto-gen-java)/.*)|(.*/(mapper|entity)/[^/]+\\.java)")
            check("NullAway", CheckSeverity.ERROR)
            option("NullAway:AnnotatedPackages", "monorepo")
            option("NullAway:HandleTestAssertionLibraries", "true")
        }
    }
}
