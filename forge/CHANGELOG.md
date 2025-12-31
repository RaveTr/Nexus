# Nexus API Changelog (Forge)

# v1.0.8 (1.20.1)

## Bug Fixes
- Fixed StandardTagProvider including missing tags by default when validateAllEntries is set to false for its corresponding ProviderType.
- Added new methods for resolving texture locations to RegistryUtil and replaced all usages (getTextureLocationOrDefaultWithSuffix -> getTextureLocationWithSuffixOrDefault) appropriately for proper texture resolution.
