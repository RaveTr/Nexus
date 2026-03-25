# Nexus API Changelog (Forge)

# v1.1.0 (1.20.1)

## Bug Fixes

- Made BlockPropertyWrapperTemplates#registerBlockWithItem actually register an associated BlockItem.
- Fixed all ModelUtil methods that use multiple textures and models (such as ModelUtil#doublePlant) attempting to look for invalid textures/models.
- Minor cleanup in event blueprint classes.
- Fixed odd crash that appeared as of v1.0.9, where `MinecraftMixin` would fail to locate the obfuscation mapping for shadowed field `timer`. For whatever reason, the jar passed into `publishMods` and uploaded didn't properly remap mixins internally despite the refmap being present and properly populated.
- (Not really a bug fix) bumped log level for `PropertyWrapper#getMappedPropertyWrappers` (+ the container field variant) down to `trace` from `debug`.
- Fixed defaultable vegetation not working properly in general.

## New Features

- Added `PostInit` annotation, which allows for classes to be statically-initialized after mod-loading is complete. In Forge's case, this happens when `FMLLoadCompleteEvent` fires.
- Added `StatTypePropertyWrapper` and `SoundEventPropertyWrapper` + associated builder and utility classes.
- Added new event blueprints: `RegistryEventBlueprint` and `LevelDataEventBlueprint`. `RegistryEventBlueprint` can be used to listen to `MissingRegistryEntriesEventBlueprint` (`MISSING_REGISTRY_ENTRIES`), whereas `LevelDataEventBlueprint` contains several hooks pertaining to different stages of level data loading and saving.
- Added new DefaultableFlowerPotBlock, DefaultableMultiLayerPlantBlock, and DefaultableMultilayerFlowerBlock instance classes + associated helpers and templates in ModelUtil and BlockPropertyWrapperTemplates.
- Tweaked base method signature for `ModData#discoverAnnotatedClasses` and added associated overloads in `PlatformManager`. The base method now accepts a Predicate to allow for class filtering before initialization. This filtering is applied after target classes are sorted, but before their `beforeInitClassConsumer` runs.
- Added optional `initSide` parameter to `RegistrarEntry`, which allows for specifying the physical side on which annotated classes should be initialized (uses `ModSide` to allow for default spec, `ModSide#COMMON`).
- Added missing javadocs for all def `PropertyWrapper`.
- Added new `RegistryHookManager` (singleton instance can be accessed through `NexusServices#REGISTRAR` | `Registrar#getRegistryHookManager`). This can be used to perform intrusive operations relatively safely, and adds some additional helper utilities, including methods to add/remove appellations (more on that below).
- Added `NexusRegistryDataManager`, which handles all registry op queries made through Nexus API and caches relevant data accordingly.
- Added ability to specify appellations (aliases) for registry entries. Appellations can be used as fallbacks for when registry entries aren't present for one reason or another. Circular appellations are supported (i.e. appellating a registry entry, then appellating that appellation with said entry's ID).
- Added `onEvent` overload in `ConcreteEventBlueprint` that allows for the consumption of the target event class for convenience. Passes `EventResult#success` in by default.
- Changed stored data in `StoneBlockGroup` and `WoodenBlockGroup` to use maps for lookups by registry entry ID in order to prevent accidental early resolution leading to exceptions being thrown.

## Known Issues

- Cyclical appellations can cause world loading to softlock, or functionally not work. This is an easy patch into Forge's alias system, just wasn't done for this update cuz time constraints. In the meantime, if you encounter such an edge case, modify your level.dat using an nbt editor to remove either or both cyclical alias reference(s), and do the same with RegistryDataLock.dat.
- ID Blocking (saving/loading blocked IDs, specifically) isn't fully implemented, so it just does nothing (for now).