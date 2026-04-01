# Nexus API Changelog (Forge)

# v1.1.1 (1.20.1)

## New Features

- Added composition support for Property Wrapper Builders via `PropertyWrapperBuilder#compose`, which allows for the chaining of builder methods that take the parent object as input for further context where needed (see the javadoc for more info).
- Added `"_lump"` to `RegistryUtil#MATERIAL_SUFFIXES`.