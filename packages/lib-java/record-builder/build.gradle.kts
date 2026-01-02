val javapoetVersion: String by project
val compileTestingVersion: String by project

dependencies {
  implementation("com.palantir.javapoet:javapoet:$javapoetVersion")

  testAnnotationProcessor(project(":packages:lib-java:record-builder"))
  testImplementation("org.jspecify:jspecify")
  testImplementation("com.google.testing.compile:compile-testing:$compileTestingVersion")
}
