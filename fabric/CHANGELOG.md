# Nexus API Changelog (Fabric)

# v1.1.7 (1.20.1)

## Internal Changes

- Fixed `EntityTypePropertyWrapper#getLootTableDir` not appending a "/" at the end, which broke entity loot tables.

## New Features

- Added `TagPropertyWrapper#getAdditionalStoredTaggedObjects` along with its builder methods and overloads and such, which allows for tagged objects not necessarily constrained to the exact generic type of the pertaining TPW. Primarily intended for use with dynamic registries (see javadocs for further info).
