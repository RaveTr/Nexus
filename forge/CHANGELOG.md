# Nexus API Changelog (Forge)

# v1.0.6 (1.20.1)

## Bug Fixes

- Fixed mining level check in DiggerItemMixin: Made it consistent with Vanilla logic by doing a >= check based on the specified minimum mining level in a BlockPropertyWrapper, instead of a hard-capped > check.