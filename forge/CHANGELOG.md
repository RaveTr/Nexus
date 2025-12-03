# Nexus API Changelog (Forge)

# v1.0.2 (1.20.1)

## Bug Fixes

- Fixed logical error for sign models where sign particle texture lookup was performed in the "textures/item" instead of "textures/block" directory.
- Fixed logical error related to client data entry texture lookups always being prefixed with prefixes that may or may not be relevant to the texture(s) being looked up.
- Fixed NPE being thrown due to inadequate null safety for both BlockEntityClientData and EntityClientData during client setup, specifically for ModelLayerLocation and LayerDefinition registration.
- Fixed functionality of registerAndReflect on Forge.