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
    
    // Fabric (Loom)
    
    // NeoForge (ModDevGradle)
    
    // MultiLoader (VanillaGradle)
    
    // Architectury (ArchLoom)
}
```

Nexus API also has a [Github Wiki](https://github.com/RaveTr/Nexus/wiki).

# Primary Feature Set

- Platform-agnostic registrar (supports all Vanilla-supported registry types, see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic loader-specific operations (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic networking (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Platform-agnostic datagen (see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)
- Natively-provided utilities for object registration (supports most Vanilla registries, see the [wiki](https://github.com/RaveTr/Nexus/wiki) for more info)

... and a whole lot more.

# Cheat Sheet

# F.A.Q

## Q. What is Nexus API?

A. Nexus API is a platform-agnostic library aimed at streamlining mod development on MultiLoader. It works on all platforms 
and supplies its own benefits depending on where you're using it, but it guarantees top-value for development 
on Multiloader.

## Q. Why use Nexus API? Why not opt for an already-existing platform like Architectury?

A. Nexus API provides its own feature set to save developers time writing otherwise annoying boilerplate code that may 
have compatibility issues across different loaders. Unlike Architectury, Nexus API doesn't have a platform ecosystem 
that you'd prefer working with in order to maximize efficiency - it's more of a one-stop shop for devs that want to get 
things done quickly and efficiently while maintaining compatibility and quality.

## Q. How does Nexus API impact performance, given its seemingly-obtrusive feature set?

A. The bulk of Nexus API's feature set is focused on startup-related tasks and AOT (Ahead-of-Time) data generation (with
the exception of networking and ``PropertyWrapper`` implementations). 

After testing startup performance impact, I've 
found that in lightweight modpacks across all loaders, the startup impact was a negligible amount of ms (variable based 
on the mods in the pack), whilst heavier modpacks (390+ mods) tended to float around < 10 seconds of added startup time, 
which is negligible due to the fact that the game would already take minutes at a time to launch anyway. Optimization 
mods obviously remedy this in proportion. It's essentially context-dependent.

``PropertyWrapper`` implementations are optimized to the maximum and don't affect runtime performance in any meaningful 
way. For instance, even though Forge relies on computing different ``ToolActions`` every time the player right-clicks, 
Nexus API pre-emptively caches all registered actions on startup using ``FastUtil`` collections, as well as static class
initialization caching (+ a handful of other optimizations) in order to mimic Fabric's registry API and effectively 
reduce dynamic computation times to around O(1) (For the nerdy devs, a ``get()`` call is performed on a FastUtil 
``HashMap`` implementation, specifically ``Object2ObjectOpenHashMap``, which is O(1) time complexity). In short, it's 
fast enough to where you don't need to worry about it.
