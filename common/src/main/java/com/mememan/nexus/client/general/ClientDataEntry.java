package com.mememan.nexus.client.general;

import com.google.common.collect.ImmutableList;
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
     * Creates a template CDE instance.
     * <br></br>
     * Templates are not stored in {@link #getMappedCdes()}. They're particularly useful for re-using across multiple
     * {@linkplain ClientDataEntry ClientDataEntries}.
     *
     * @return A new template CDE instance.
     */
    public static ClientDataEntry createTemplate() {
        return new ClientDataEntry(true);
    }

    /**
     * Constructs a builder chain in which certain datagen/hardcoded properties can be assigned and re-built with in this
     * ClientDataEntry instance. Also sets this CDE instance's {@link #builder} to the newly-constructed
     * {@link CDEBuilder} instance.
     * <br></br>
     * <b>NOTE: THIS WILL OVERRIDE {@link #builder} ENTIRELY EVEN IF IT'S NOT {@code null} (e.g. you're inheriting from a
     * template, see {@link #of(ClientDataEntry)}).</b>
     *
     * @return A new {@link CDEBuilder} instance from the {@link #builder} field.
     *
     * @see #cachedBuilder()
     */
    public CDEBuilder builder() {
        return this.builder = new CDEBuilder(this);
    }

    /**
     * Gets the cached {@link CDEBuilder} instance from the {@link #builder} if it exists. May be {@code null}. Useful
     * for overriding specific properties after having copied another CDE instance/already set a CDEBuilder.
     *
     * @return The cached {@link CDEBuilder} instance, or {@code null} if the {@link #builder} is {@code null}.
     */
    @Nullable
    public CDEBuilder cachedBuilder() {
        return builder;
    }



    /**
     * Whether this CDE instance is a template. Templates are not stored in {@link #getMappedCdes()}.
     *
     * @return Whether this CDE instance is a template.
     *
     * @see #of(ClientDataEntry)
     * @see #createTemplate()
     */
    public boolean isTemplate() {
        return isTemplate;
    }

    /**
     * Gets an immutable view (via {@link ImmutableList}) of {@link #MAPPED_CDES}.
     *
     * @return An immutable view (via {@link ImmutableList}) of {@link #MAPPED_CDES}.
     */
    public static ImmutableList<ClientDataEntry> getMappedCdes() {
        return ImmutableList.copyOf(MAPPED_CDES);
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
            if (!ownerEntry.isTemplate()) MAPPED_CDES.add(ownerEntry);
            return ownerEntry;
        }
    }
}
