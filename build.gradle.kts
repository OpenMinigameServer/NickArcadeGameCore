plugins {
    id("io.github.openminigameserver.arcadiumgradle") version "1.0-SNAPSHOT"
}

repositories {
    maven(url = "https://repo.rapture.pw/repository/maven-snapshots/")
    maven(url = "https://repo.spongepowered.org/maven")
    maven(url = "https://repo.glaremasters.me/repository/concuncan/")
}

nickarcade {
    name = "GameCore"
    depends("Display", "Party")
}

spigot {
    depends("SlimeWorldManager")
}

dependencies {
    api("com.grinderwolf:slimeworldmanager-plugin:2.5.4-SNAPSHOT")
}