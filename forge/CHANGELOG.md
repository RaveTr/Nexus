# Nexus API Changelog (Forge)

# v1.0.4 (1.20.1)

## Bug Fixes

- Fixed incorrect configuration of #dropsLike for signs and hanging signs in RegistryUtil#registerStandardWoodFamily.
- Fixed atlas configuration for signs and hanging signs.
- Fixed BlockPropertyTemplates#WOODEN_PLANKS permitting pickaxes as a mining tool via wrongfully-tagging inheritors with "minecraft:blocks/mineable/pickaxe".