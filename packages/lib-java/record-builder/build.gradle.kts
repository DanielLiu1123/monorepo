val javapoetVersion: String = providers.gradleProperty("javapoetVersion").get()
val compileTestingVersion: String = providers.gradleProperty("compileTestingVersion").get()

dependencies {
  implementation("com.palantir.javapoet:javapoet:$javapoetVersion")

  testAnnotationProcessor(project(":packages:lib-java:record-builder"))
  testImplementation("org.jspecify:jspecify")
  testImplementation("com.google.testing.compile:compile-testing:$compileTestingVersion")
}
