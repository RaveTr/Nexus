# Nexus API Changelog (Fabric)

# v1.0.9 (1.20.1)

## Bug Fixes

- Fixed RecipeUtil materialPieceFrom not factoring all potential material suffixes in.

## New Features

- Added new BlockPropertyWrapper templates to BlockPropertyWrapperTemplates: 'BRICKS' (+ tool variants), 'CHISELED_STONE' (+ tool variants), 'CHISELED_STONE_COBBLED' (+ tool variants), 'PILLAR' (+ tool variants).
- Added utility methods to RecipeUtil for brick, chiseled block, chiseled block cobbled, and pillar recipes (+ overloads).
- Added several new utility shortcut methods to RegistryUtil for stone block families.
