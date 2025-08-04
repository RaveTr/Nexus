package com.mememan.nexus.client.general;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper {@code class} for storing side-safe client-only data. Attachable to different Property Wrapper implementations.
 */
public class ClientDataEntry {
    private static final ObjectArrayList<ClientDataEntry> MAPPED_CDES = new ObjectArrayList<>();
    private final boolean isTemplate;
    @Nullable
    private CDEBuilder builder;

    private ClientDataEntry(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    private ClientDataEntry() {
        this(true);
    }

    /**
     * Builder {@code class} used to build and store client data (tooltips, renderers, models, etc.).
     */
    public static class CDEBuilder {
        @NotNull
        private final ClientDataEntry ownerEntry;

        private CDEBuilder(@NotNull ClientDataEntry ownerEntry) {
            this.ownerEntry = ownerEntry;
        }

        public ClientDataEntry build() {
            return ownerEntry;
        }
    }
}
