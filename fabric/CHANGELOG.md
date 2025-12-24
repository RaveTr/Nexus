# Nexus API Changelog (Fabric)

# v1.0.5 (1.20.1)

## Bug Fixes

- (Not really a bug) Corrected naming of parameters in ItemPropertyWrapperTemplates.
- (Also not really a bug) Corrected javadocs in FabricModData with proper time complexity analysis (was an inaccurate mess and mainly just insanely-off guesstimates cuz I got lazy after finally optimizing tf out of it).
- Fixed FabricModData#filterDuplicates logic pruning paths that shouldn't be pruned (e.g., when a path wasn't a subdirectory of another path, so "block_texture.png.mcmeta" would wrongly filter out "block_texture.png" due to the basic substring check that was done).
- Updated PropertyWrapperContainer#MAPPED_PROPERTY_WRAPPERS to additionally compare wrapper/supplier values in its equality check.
- Updated PropertyWrapper#getMappedPropertyWrappers() HashStrategy implementation to match PropertyWrapperContainer#MAPPED_PROPERTY_WRAPPERS.
- Updated PropertyWrappersContainer#getWrapperFor(Supplier) to query a copy of the original map if the base lookup returns an empty Optional (see javadoc for further details).

## New Features

- Added new template methods (+ overloads) for registering items and chaining them with custom property wrappers in ItemPropertyWrapperTemplates.
