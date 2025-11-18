plugins {
    kotlin("jvm") version "1.9.23"
}

dependencies {
    testImplementation(project(":api"))
    testImplementation(project(":domain"))
    testImplementation(project(":infra"))
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}