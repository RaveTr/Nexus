package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RegistryEvent extends BaseEvent {
    protected final Registry<?> registry;

    public RegistryEvent(Registry<?> registry) {
        super(ModSide.COMMON);

        this.registry = registry;
    }

    public Registry<?> getRegistry() {
        return registry;
    }

    public static class MissingRegistryEntriesEvent<T> extends RegistryEvent {
        protected final List<MissingRegistryEntriesEvent.WrappedEntry<T>> missingEntries;

        public MissingRegistryEntriesEvent(Registry<T> registry, List<MissingRegistryEntriesEvent.WrappedEntry<T>> missingEntries) {
            super(registry);

            this.missingEntries = missingEntries;
        }

        public List<MissingRegistryEntriesEvent.WrappedEntry<T>> getMissingEntries() {
            return missingEntries;
        }

        public static class WrappedEntry<T> {
            protected final ResourceKey<? extends Registry<T>> pertainingRegistryKey;
            protected final int lastKnownId; // Captured missing entry ID
            protected final ResourceLocation oldKey;
            protected MissingEntryConsumer mappingAction = MappingAction.BLOCK;

            public WrappedEntry(ResourceKey<? extends Registry<T>> pertainingRegistryKey, int lastKnownId, ResourceLocation oldKey) {
                this.pertainingRegistryKey = pertainingRegistryKey;
                this.lastKnownId = lastKnownId;
                this.oldKey = oldKey;
            }

            public ResourceKey<? extends Registry<T>> getRegistryKey() {
                return pertainingRegistryKey;
            }

            public int getLastKnownId() {
                return lastKnownId;
            }

            public ResourceLocation getOldKey() {
                return oldKey;
            }

            public MissingEntryConsumer getMappingAction() {
                return mappingAction;
            }

            public void attemptRemap(MissingEntryConsumer mappingAction, ResourceLocation newKey) {
                (this.mappingAction = mappingAction).accept(
                        BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) getRegistryKey()),
                        lastKnownId,
                        oldKey,
                        newKey
                );
            }

            public void attemptRemap(ResourceLocation newKey) {
                attemptRemap(MappingAction.APPELLATE, newKey);
            }

            @Override
            public String toString() {
                return String.format("WrappedEntry{pertainingRegistryKey=%s, lastKnownId=%d, oldKey=%s, mappingAction=%s}", pertainingRegistryKey, lastKnownId, oldKey, mappingAction);
            }
        }

        @FunctionalInterface
        public interface MissingEntryConsumer {
            void accept(Registry<?> pertainingRegistry, int regId, ResourceLocation oldKey, ResourceLocation newKey);
        }

        public enum MappingAction implements MissingEntryConsumer {
            APPELLATE((pertainingRegistry, lastKnownId, oldKey, newKey) -> {
                pertainingRegistry.getOptional(newKey).ifPresentOrElse(newlyMappedObj -> {
                    ResourceKey<? extends Registry<?>> regKey = pertainingRegistry.key();

                    NexusServices.REGISTRAR.getRegistryHookManager().appellate(oldKey, newKey, (ResourceKey) regKey);
                    NexusConstants.LOGGER.info("Appellated {} to {} (for registry: {})", newKey, oldKey, regKey);
                }, () -> {
                    throw new IllegalStateException(String.format("Missing registry entry for %s with id %d, old reference key: %s", pertainingRegistry.key(), lastKnownId, oldKey));
                });
            }),
            IGNORE((pertainingRegistry, lastKnownId, oldKey, newKey) -> {

            }),
            BLOCK((pertainingRegistry, lastKnownId, oldKey, newKey) -> {
                NexusConstants.LOGGER.info("Missing registry entry for {} with id {}, old reference key: {}. Blocking missing entry's ID from being re-used until an alias is identified or the associated mapping is present again.", pertainingRegistry.key(), lastKnownId, oldKey);

                NexusServices.REGISTRAR.getRegistryHookManager().blockId((ResourceKey) pertainingRegistry.key(), lastKnownId);
            }),
/*            SUBSTITUTE((pertainingRegistry, lastKnownId, oldKey, newKey) -> {
                NexusConstants.LOGGER.info("Attempting to substitute registry entry {} (with id {}, in registry {})", oldKey, lastKnownId, pertainingRegistry.key());
                // NO-OP: Functionality handled in NexusRegistryDataManager#updateRegistryData(LevelStorageSource.LevelDirectory)
            }),*/
            WARN((pertainingRegistry, lastKnownId, oldKey, newKey) -> {
                NexusConstants.LOGGER.warn("Missing registry entry for {} with id {}, old reference key: {}", pertainingRegistry.key(), lastKnownId, oldKey);
            }),
            FAIL((pertainingRegistry, lastKnownId, oldKey, newKey) -> {
                throw new IllegalStateException(String.format("Missing registry entry for %s with id %d, old reference key: %s, mod specified mappingAction to be MappingAction#FAIL. Preventing world from loading...", pertainingRegistry.key(), lastKnownId, oldKey));
            });

            private final MissingEntryConsumer action;

            MappingAction(MissingEntryConsumer action) {
                this.action = action;
            }

            @Override
            public void accept(Registry<?> pertainingRegistry, int regId, ResourceLocation oldKey, ResourceLocation newKey) {
                action.accept(pertainingRegistry, regId, oldKey, newKey);
            }
        }
    }
}
