plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version("8.3.0")
}

group = "org.lushplugins"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
    maven("https://repo.lushplugins.org/snapshots") // LushLib
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")

    // Soft Dependencies
    compileOnly("me.clip:placeholderapi:2.11.2")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.24")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    registerFeature("optional") {
        usingSourceSet(sourceSets["main"])
    }

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.lushplugins.lushlib", "org.lushplugins.simplypronouns.libraries.lushlib")

        minimize()

        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        filesMatching("plugin.yml") {
            expand(project.properties)
        }

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}
