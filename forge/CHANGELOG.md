# Nexus API Changelog (Forge)

# v1.1.4 (1.20.1)

## Bug Fixes

- Fixed a few typos in Javadocs around the codebase, as well as minor log message inconsistencies.
- Fixed an issue in `NexusRegistryDataManager#populateRegistryEntriesFromMemory` where duplicate entries were not properly handled by the collector used to collect all registry elements in-stream to a `HashBiMap`.

## Internal Changes

- Changed datapack sync to use the newly-added `ServerLifeCycleEventBlueprint#DATAPACK_INDIVIDUAL_SYNC` event hooks (see "New Features" below). Internal datapack sync is now handled in `NexusServerLifeCycleManager`.
- Moved datapack sync to `PlayerListMixin` inside `common` to accommodate for the above change.
- Updated dependencies to latest (Forge version is now 47.4.20).

## New Features

- Added datapack event hooks to `ServerLifeCycleEventBlueprint` for datapack reload start/end and sync.
- Added `BOW`, `CROSSBOW`, and `CROSSBOW_FIREWORK` to `ItemPropertyWrapperTemplates`.
- Added `NexusAttributes`, containing common attribute hooks with cross-loader-dependency-compatibility in mind (see Javadocs for more info).
