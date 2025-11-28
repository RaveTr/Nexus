# Nexus API Changelog (Forge)

# v1.0.1 (1.20.1)
## Bug Fixes
- Fixed logical error in sign texture lookups: Allow for sign textures to be located under "entity/sign", in addition to "entity/signs".
- Properly included particle texture in sign block model file.
- Fixed BlockEntityTypePropertyWrapper generating locale entries when they're not even needed.
- Fixed WOODEN_PLANKS BPW Template failing to look up the "logs" tag of a given wooden plank using its assumed property wrapper whenever the tag had any directories prepended to its path (e.g. "modid:apple_logs" would work fine, but "nexus:path/apple_logs" wouldn't).