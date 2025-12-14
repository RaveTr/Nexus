# Nexus API Changelog (Fabric)

# v1.0.4 (1.20.1)

## Bug Fixes

- Fixed incorrect configuration of #dropsLike for signs and hanging signs in RegistryUtil#registerStandardWoodFamily.
- Fixed atlas configuration for signs and hanging signs.
- Fixed BlockPropertyTemplates#WOODEN_PLANKS permitting pickaxes as a mining tool via wrongfully-tagging inheritors with "minecraft:blocks/mineable/pickaxe".
- Fixed wall sign and wall hanging sign particle textures not showing up due to missing blockstate configuration in BlockPropertyWrapperTemplates#WOODEN_WALL_SIGN and BlockPropertyWrapperTemplates#WOODEN_WALL_HANGING_SIGN.
- Made ModelUtil#sign(Supplier) look plank textures up rather than log textures by default for consistency.