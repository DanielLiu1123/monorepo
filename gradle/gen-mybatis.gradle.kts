val mybatisGeneratorVersion: String by project

val generator by configurations.creating

dependencies {
    generator("org.mybatis.generator:mybatis-generator-core:$mybatisGeneratorVersion")
    generator("org.postgresql:postgresql")
    generator(project(":packages:lib-java:mybatis"))
}

tasks.register<JavaExec>("genMyBatis") {
    group = "codegen"
    description = "Generates MyBatis artifacts"

    classpath = generator
    mainClass.set("org.mybatis.generator.api.ShellRunner")
    args("-configfile", "${projectDir}/generatorConfig.xml", "-overwrite", "-verbose")
}
