// This only lists the first modules > you don't need to list them all now
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "datawave"
include("api", "domain", "infra", "integration-tests")