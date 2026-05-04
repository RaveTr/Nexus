# Nexus API Changelog (Fabric)

# v1.1.3 (1.20.1)

## Bug Fixes

- Made `FabricRegistrar#getDynamicRegistries` impl consider dimension registries via stream concatenation of `DynamicRegistries#getDynamicRegistries` and `RegistryDataLoader#DIMENSION_REGISTRIES`.
- Fixed misc. logical errors in recipe templates provided in `RecipeUtil`.
- (Not really a bug fix) Bumped log level for `StandardBlockStateProvider` elements down to `debug` from `info` for consistency with other providers.
- Implemented `getName` for `StandardSoundDefinitionProvider`.
- Patched `DefaultableMultiLayerPlantBlock` to allow for custom level `IntegerProperty` spec without breaking due to superconstructor calls to `Block#createBaseDefinitiom` running before the pertaining property gets initialized via a pre-constructor initializer hack.
- Deleted leftover test code in `NexusRegistryDataManager`.