import org.jetbrains.changelog.date

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "de.delta203"
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

    build {
        dependsOn(jar)
    }

    jar {
        archiveFileName.set("${rootProject.name}.${archiveExtension.getOrElse("jar")}")
    }
}

bukkit {
    main = "${rootProject.group}.containersort.ContainerSortPlugin"
    apiVersion = "1.18"
    name = "ChestSort"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    author = "Delta203"
    authors = listOf("UniqueGame")
    permissions {
        register("containersort.*") {
            children = listOf("container.sort.allow")
            childrenMap = mapOf("container.sort.allow" to true)
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }

        register("container.sort.allow") {
            description = "Allows you to sort containers"
            default = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
        }
    }
}

changelog {
    path.set("${project.projectDir}/CHANGELOG.md")
    header.set(provider { "[${version.get()}] - ${date()}" })
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}