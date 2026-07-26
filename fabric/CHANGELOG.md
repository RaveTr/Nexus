# Nexus API Changelog (Fabric)

# v1.1.5 (1.20.1)

## Bug Fixes

- Fixed faulty implementation of `ModelUtil#standardBow` and `ModelUtil#standardCrossbow` (which didn't properly prefix "item/" to the base model definition).
- Fixed generic types for static template registration method overloads in `BlockEntityTypePropertyWrapperTemplates` and `EntityTypePropertyWrapperTemplates`.
- Fixed `ItemModelDefinition#constructJson` incorrectly replacing the `predicates` property entirely for each new individual predicate added.
- Made `RecipeUtil#materialBlockFrom` consider the `_ingot` suffix when looking for corresponding block material.

## New Features

- Added client-side event hooks through the newly-added `ClientLifeCycleEventBlueprint`; more specifically, hooks for resource pack reloads.
