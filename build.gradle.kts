import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.file.Files
import java.nio.file.Paths
import java.net.URL

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `java-library`
}

group = "net.lortservers"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

object DependencyVersions {
    const val BYTEBUDDY = "1.12.16"
}

dependencies {
    compileOnly(urlFile("https://archive.org/download/BukkitMCBeta173/Bukkit-MCBeta173.zip/craftbukkit-0.0.1-SNAPSHOT.1000.jar"))
    api(group = "net.bytebuddy", name = "byte-buddy", version = DependencyVersions.BYTEBUDDY)
    api(group = "net.bytebuddy", name = "byte-buddy-agent", version = DependencyVersions.BYTEBUDDY)
}

tasks.withType<ShadowJar> {
    relocate("net.bytebuddy", "net.lortservers.protocolobserver.bytebuddy")
}

fun urlFile(url: String): ConfigurableFileCollection {
    val path = Paths.get(buildDir.absolutePath, "ext", url.substringAfterLast('/'))
    val file = path.toFile()
    file.parentFile.mkdirs()
    if (!file.exists()) {
        Files.copy(URL(url).openStream(), path)
    }

    return files(file.absolutePath)
}