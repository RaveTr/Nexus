# Nexus API Changelog (Forge)

# v1.0.5 (1.20.1)

## Bug Fixes

- (Not really a bug) Corrected naming of parameters in ItemPropertyWrapperTemplates.
- Updated PropertyWrapperContainer#MAPPED_PROPERTY_WRAPPERS to properly compare wrapper/supplier values in its equality check.
- Updated PropertyWrapper#getMappedPropertyWrappers() HashStrategy implementation to match PropertyWrapperContainer#MAPPED_PROPERTY_WRAPPERS.

## New Features

- Added new template methods (+ overloads) for registering items and chaining them with custom property wrappers in ItemPropertyWrapperTemplates.