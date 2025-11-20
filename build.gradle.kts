import org.gradle.jvm.tasks.Jar

plugins {
    java
    application
}

group = "ru.university.malina2234"
version = "1.0.0"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// запуск тестов
tasks.test {
    useJUnitPlatform() // Указываем, что нужно использовать платформу JUnit 5
}

application {
    mainClass.set("ru.university.malina2234.app.Main")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}
// Принудительно устанавливаем UTF-8 для запуска приложения и тестов
tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8")
}

tasks.withType<Test> {
    jvmArgs("-Dfile.encoding=UTF-8")
}
// Говорим компилятору Java использовать кодировку UTF-8 для всех исходных файлов
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}