# Nexus API Changelog (Fabric)

# v1.1.6 (1.20.1)

## Internal Changes

- Changed `DatapackEntriesSyncPacket` to `DatapackEntriesSyncChunkPacket`. As the packet class's new name implies, datapacks are now synced in chunks of around 512 KiB through NAPI, with a single entry being allowed a maximum size of ~1.75 MiB on its own (the default packet size limit is roughly 2 MiB).
- Updated Javadocs in `ResourceReloadListenerConfig` accordingly.

## New Features

- Added `EntityEventBlueprint` and `PlayerEventBlueprint`. Only `PlayerEventBlueprint` has actual events at the moment (said events being `PLAYER_LOGIN`, `PLAYER_LOGOUT`, and `PLAYER_DISCONNECT`).
