val generator = configurations.create("generator")

val mybatisGeneratorVersion: String = providers.gradleProperty("mybatisGeneratorVersion").get()
val javaparserVersion: String = providers.gradleProperty("javaparserVersion").get()

dependencies {
    generator("org.mybatis.generator:mybatis-generator-core:$mybatisGeneratorVersion")
    generator("org.postgresql:postgresql")
    generator(project(":packages:lib-java:mybatis"))
    generator("com.github.javaparser:javaparser-core:$javaparserVersion")
}

tasks.register<JavaExec>("genMyBatis") {
    group = "codegen"
    description = "Generates MyBatis Mapper and Model classes"

    classpath = generator
    mainClass.set("org.mybatis.generator.api.ShellRunner")
    args("-configfile", "${projectDir}/generatorConfig.xml", "-overwrite", "-javaMergeEnabled", "-verbose")
}
