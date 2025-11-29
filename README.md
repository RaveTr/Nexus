# Nexus API [W.I.P]

Bulk of common platform-agnostic utilities aimed at reducing boilerplate/compatibility issues,
thereby streamlining mod development on MultiLoader.

# Getting Started

Add the following to your `build.gradle` file:

```groovy
repositories { // Specific repo block depends on the platform you're building your mod on
    maven {
        name = "Meme Man Maven"
        url = "https://dl.cloudsmith.io/public/meme-man-mods/nexus/maven/"
    }
}

dependencies { // Choose based on the platform you're developing for
    // Forge (ForgeGradle)
    implementation fg.deobf("com.mememan:nexus-forge-${minecraft_version}:${nexus_version}")
    
    // Fabric (Loom)
    modImplementation("com.mememan:nexus-fabric-${minecraft_version}:${nexus_version}")
    
    // NeoForge (LegacyForge/ModDevGradle)
    modImplementation("com.mememan:nexus-forge-${minecraft_version}:${nexus_version}")
    
    // MultiLoader (VanillaGradle/LegacyForge - Common)
    implementation("com.mememan:nexus-common-${minecraft_version}:${nexus_version}")
    
    // Architectury (ArchLoom - Common)
    modImplementation("com.mememan:nexus-common-${minecraft_version}:${nexus_version}")
}
```

Nexus API also has a [Github Wiki](https://github.com/RaveTr/Nexus/wiki).

# Primary Feature Set

- Platform-agnostic registrar (supports all Vanilla-supported registry types, see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic loader-specific operations (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic networking (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic datagen (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Natively-provided utilities (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Property Wrapper system (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)

... and a whole lot more.

# Cheat Sheet
