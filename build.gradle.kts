plugins {
    id("io.github.openminigameserver.arcadiumgradle") version "1.0-SNAPSHOT"
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