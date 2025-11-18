plugins {
    kotlin("jvm") version "1.9.23"
}

dependencies {
    // dependências do módulo infra
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}