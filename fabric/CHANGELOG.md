# Nexus API Changelog (Fabric)

# v1.0.2 (1.20.1)

## Bug Fixes

- Fixed logical error for sign models where sign particle texture lookup was performed in the "textures/item" instead of "textures/block" directory.
- Fixed logical error related to hanging sign texture lookups always being prefixed with "entity/signs/hanging/" instead of "entity/sign/".
- Fixed NPE being thrown due to inadequate null safety for both BlockEntityClientData and EntityClientData during client setup, specifically for ModelLayerLocation and LayerDefinition registration.
- Fixed NPE being thrown due to inadequate null safety for both signs and hanging signs.