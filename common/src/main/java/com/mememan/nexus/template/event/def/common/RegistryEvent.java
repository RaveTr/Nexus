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
    protected final ResourceKey<? extends Registry<?>> registryKey;
    protected final Registry<?> registry;

    public RegistryEvent(ResourceKey<? extends Registry<?>> registryKey, Registry<?> registry) {
        super(ModSide.COMMON);

        this.registryKey = registryKey;
        this.registry = registry;
    }

    public ResourceKey<? extends Registry<?>> getRegistryKey() {
        return registryKey;
    }

    public Registry<?> getRegistry() {
        return registry;
    }

    public static class MissingRegistryEntriesEvent extends RegistryEvent {
        protected final List<MissingRegistryEntriesEvent.WrappedEntry<?>> missingEntries;

        public MissingRegistryEntriesEvent(ResourceKey<? extends Registry<?>> registryKey, Registry<?> registry, List<MissingRegistryEntriesEvent.WrappedEntry<?>> missingEntries) {
            super(registryKey, registry);

            this.missingEntries = missingEntries;
        }

        public List<MissingRegistryEntriesEvent.WrappedEntry<?>> getMissingEntries() {
            return missingEntries;
        }

        public static class WrappedEntry<T> {
            protected final ResourceKey<? extends Registry<T>> pertainingRegistryKey;
            protected final int regId; // Captured missing entry ID
            protected final ResourceLocation oldKey;
            protected MissingEntryConsumer mappingAction = MappingAction.WARN;

            public WrappedEntry(ResourceKey<? extends Registry<T>> pertainingRegistryKey, int regId, ResourceLocation oldKey) {
                this.pertainingRegistryKey = pertainingRegistryKey;
                this.regId = regId;
                this.oldKey = oldKey;
            }

            public ResourceKey<? extends Registry<T>> getRegistryKey() {
                return pertainingRegistryKey;
            }

            public int getId() {
                return regId;
            }

            public ResourceLocation getOldKey() {
                return oldKey;
            }

            public void attemptRemap(ResourceLocation newKey) {
                mappingAction.accept(
                        BuiltInRegistries.REGISTRY.getOrThrow((ResourceKey) getRegistryKey()),
                        regId,
                        oldKey,
                        newKey
                );
            }
        }

        @FunctionalInterface
        public interface MissingEntryConsumer {
            void accept(Registry<?> pertainingRegistry, int regId, ResourceLocation oldKey, ResourceLocation newKey);
        }

        public enum MappingAction implements MissingEntryConsumer {
            REPLACE((pertainingRegistry, regId, oldKey, newKey) -> {
                pertainingRegistry.getOptional(newKey).ifPresentOrElse(newlyMappedObj -> {
                    NexusServices.REGISTRAR.appellate(oldKey, newKey, (ResourceKey) pertainingRegistry.key());
                    NexusConstants.LOGGER.info("Appellated {} to {}", oldKey, newKey);
                }, () -> {
                    throw new IllegalStateException(String.format("Missing registry entry for %s with id %d, old reference key: %s", pertainingRegistry.key(), regId, oldKey));
                });
            }),
            IGNORE((pertainingRegistry, regId, oldKey, newKey) -> {

            }),
            WARN((pertainingRegistry, regId, oldKey, newKey) -> {
                NexusConstants.LOGGER.warn("Missing registry entry for {} with id {}, old reference key: {}", pertainingRegistry.key(), regId, oldKey);
            }),
            FAIL((pertainingRegistry, regId, oldKey, newKey) -> {
                throw new IllegalStateException(String.format("Missing registry entry for %s with id %d, old reference key: %s, mod specified mappingAction to be MappingAction#FAIL. Preventing world from loading...", pertainingRegistry.key(), regId, oldKey));
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
