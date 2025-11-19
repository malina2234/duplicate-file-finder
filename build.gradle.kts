import org.gradle.jvm.tasks.Jar

plugins {
    java
    application
}

group = "ru.university.malina2234" // Убедитесь, что здесь ваша группа
version = "1.0.0"

repositories {
    mavenCentral()
}

// =======================================================
// ЭТОТ БЛОК КРИТИЧЕСКИ ВАЖЕН ДЛЯ ТЕСТОВ
// =======================================================
dependencies {
    // Эта строка подключает JUnit 5 (аннотации @Test, Assertions и т.д.)
    // 'testImplementation' означает, что эта библиотека нужна ТОЛЬКО для тестов.
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// =======================================================
// ЭТОТ БЛОК ГОВОРИТ GRADLE, КАК ЗАПУСКАТЬ ТЕСТЫ
// =======================================================
tasks.test {
    useJUnitPlatform() // Указываем, что нужно использовать платформу JUnit 5
}

application {
    mainClass.set("ru.university.malina2234.app.Main") // Убедитесь, что здесь ваш main класс
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