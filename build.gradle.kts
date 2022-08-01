import org.jetbrains.changelog.date

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("org.jetbrains.changelog") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "de.uniquegame"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    implementation("cloud.commandframework", "cloud-paper", "1.7.0")
    implementation("cloud.commandframework", "cloud-annotations", "1.7.0")
    implementation("cloud.commandframework", "cloud-minecraft-extras", "1.7.0")
    implementation("me.lucko:commodore:2.0") {
        isTransitive = false
    }
}

tasks {

    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }

    runServer {
        minecraftVersion("1.19")
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("${rootProject.name}.${archiveExtension.getOrElse("jar")}")
    }
}

bukkit {
    main = "${rootProject.group}.containersort.ContainerSortPlugin"
    apiVersion = "1.18"
    name = "ContainerSort"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    author = "UniqueGame"
}

changelog {
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}