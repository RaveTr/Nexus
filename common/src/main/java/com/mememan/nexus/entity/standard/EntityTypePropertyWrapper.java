package com.mememan.nexus.entity.standard;

import com.google.common.collect.ImmutableSortedMap;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A wrapper class used to store information referenced in datagen to simplify creating data entries and other backend
 * data (such as attributes) for entity types. Also used to store additional data such as renderers, models, etc.
 *
 * @param <E> Any {@link Entity} subclass.
 */
public class EntityTypePropertyWrapper<E extends Entity> {
    private static final Object2ObjectLinkedOpenHashMap<Supplier<? extends EntityType<?>>, EntityTypePropertyWrapper<?>> MAPPED_ETPWS = new Object2ObjectLinkedOpenHashMap<>();
    @Nullable
    private final ResourceLocation entityTypeRegName;
    private final Supplier<EntityType<E>> parentEntityType;
    private final boolean isTemplate;
    @Nullable
    private ETPWBuilder<E> builder;

    private EntityTypePropertyWrapper(@Nullable ResourceLocation entityTypeRegName, Supplier<EntityType<E>> parentEntityType) {
        this.entityTypeRegName = entityTypeRegName;
        this.parentEntityType = parentEntityType;
        this.isTemplate = false;
    }

    private EntityTypePropertyWrapper(Supplier<EntityType<E>> parentEntityType) {
        this(null, parentEntityType);
    }

    private EntityTypePropertyWrapper() {
        this.entityTypeRegName = null;
        this.parentEntityType = null;
        this.isTemplate = true; // Otherwise can't set with constructor overloading (Laziness:tm:)
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance. This is usually where you'll begin chaining
     * {@link #builder()} method calls if needed. Use this variant if you want to directly create an ETPW during a
     * registration call rather than after it/without storing it.
     *
     * @param entityTypeRegName The registry name of the {@code parentEntityType}. Used when access to the
     *                          {@code parentItem} returns a pig/{@code null} delegate (I.E. It's too early to access
     *                          the parent entity type).
     * @param parentEntityType The parent {@link Supplier<EntityType<E>>} stored in the newly-initialized ETPW instance.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance.
     *
     * @see #create(Supplier)
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> create(ResourceLocation entityTypeRegName, Supplier<EntityType<E>> parentEntityType) {
        return new EntityTypePropertyWrapper<>(entityTypeRegName, parentEntityType);
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance. This is usually where you'll begin chaining
     * {@link #builder()} method calls if needed. Use this variant if you want to create an ETPW instance with a stored
     * registration call, such that its parent {@link Supplier<EntityType<?>>} is not an air delegate/{@code null}.
     *
     * @param parentEntityType The parent {@link Supplier<EntityType<?>>} stored in the newly-initialized ETPW instance.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance.
     *
     * @see #create(ResourceLocation, Supplier)
     * @see #of(EntityTypePropertyWrapper, Supplier)
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> create(Supplier<EntityType<E>> parentEntityType) {
        return new EntityTypePropertyWrapper<>(parentEntityType);
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance. This is usually where you'll begin chaining
     * {@link #builder()} method calls if needed. Use this variant if you want to create an ETPW instance with a stored
     * registration call, such that its parent {@link Supplier<EntityType<?>>} is not an air delegate/{@code null}.
     *
     * @param parentEntityType The parent {@link Supplier<EntityType<?>>} stored in the newly-initialized ETPW instance.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance.
     *
     * @see #create(ResourceLocation, Supplier)
     * @see #of(EntityTypePropertyWrapper, Supplier)
     */
    public static EntityTypePropertyWrapper<?> createGeneric(Supplier<? extends EntityType<?>> parentEntityType) {
        return new EntityTypePropertyWrapper(parentEntityType);
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance as a template. Template ETPWs are not stored in
     * {@link #MAPPED_ETPWS} and do not store a parent {@link EntityType}. They're particularly useful for re-using
     * across multiple {@linkplain EntityType EntityTypes}.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance, set as a template.
     *
     * @see #of(EntityTypePropertyWrapper, Supplier)
     * @see #isTemplate()
     * @see #ofTemplate(EntityTypePropertyWrapper)
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> createTemplate() {
        return new EntityTypePropertyWrapper<>();
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance as a template, inheriting data from the provided ETPW
     * template. Template ETPWs are not stored in {@link #MAPPED_ETPWS} and do not store a parent {@link EntityType}.
     * They're particularly useful for re-using across multiple {@linkplain EntityType EntityTypes}.
     *
     * @param parentTemplateWrapper The parent {@link EntityTypePropertyWrapper} template from which {{@link #builder()}}
     *                              data should be copied.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance, set as a template, inheriting from the provided ETPW
     * template. If the provided ETPW template is {@code null}, returns {@link #createTemplate()}.
     *
     * @see #createTemplate()
     * @see #isTemplate()
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> ofTemplate(EntityTypePropertyWrapper<E> parentTemplateWrapper) {
        if (parentTemplateWrapper != null) {
            EntityTypePropertyWrapper<E> newTemplateWrapper = new EntityTypePropertyWrapper<>();

            return copyProperties(parentTemplateWrapper, newTemplateWrapper);
        } else return createTemplate();
    }

    /**
     * Creates a new {@link EntityTypePropertyWrapper} instance based on the provided {@link EntityTypePropertyWrapper}.
     * If the provided ETPW instance is {@code null}, returns {@link #create(Supplier)}. You'd typically use this if you
     * have an ETPW template you want multiple registered {@linkplain EntityType EntityTypes} to inherit from.
     *
     * @param parentWrapper The parent {@link EntityTypePropertyWrapper} instance from which {@link #builder()} should
     *                      be copied.
     * @param newEntityType The new registry entry to use for the newly constructed ETPW instance.
     *
     * @return A new {@link EntityTypePropertyWrapper} instance with copied properties based on the provided ETPW, or
     * an entirely new/clean instance if the provided ETPW is {@code null}.
     *
     * @see #create(Supplier)
     * @see #create(ResourceLocation, Supplier)
     * @see #createTemplate()
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> of(EntityTypePropertyWrapper<E> parentWrapper, Supplier<EntityType<E>> newEntityType) {
        if (parentWrapper != null) {
            EntityTypePropertyWrapper<E> newWrapper = new EntityTypePropertyWrapper<>(newEntityType);

            return copyProperties(parentWrapper, newWrapper);
        } else return create(newEntityType);
    }

    /**
     * Shortcut utility method centered around copying builder properties over from one ETPW instance to another.
     *
     * @param from The ETPW instance to copy properties from.
     * @param to The ETPW instance to copy properties to.
     *
     * @return The provided ETPW instance with copied properties.
     *
     * @param <E> Any {@link Entity} subclass.
     */
    public static <E extends Entity> EntityTypePropertyWrapper<E> copyProperties(EntityTypePropertyWrapper<E> from, EntityTypePropertyWrapper<E> to) {
        return to.builder()
                .withCustomName(from.builder.manuallyLocalizedItemName)
                .withCustomSeparatorWords(List.copyOf(from.builder.definedSeparatorWords))
                .withLocalization(from.builder.entityTypeTranslationFunc)
                .withLootTable(from.builder.entityLootTableBuilder)
                .withAttributes(from.builder.attribBuilder)
                .withSetTags(List.copyOf(from.builder.parentTags))
                .literalTranslation(from.builder.literalTranslation)
                .bypassDefaultTranslation(from.builder.bypassDefaultTranslation)
                .excludeFromNativeDatagen(from.builder.excludeFromNativeDatagen)
                .requiresSetDatagenEntries(Map.copyOf(from.builder.mappedProviderRequisites))
                .build(); // Direct setting of the builder would copy the entire object itself, which would in-turn overwrite it if any calls are made to the copied ETPW afterward
    }

    /**
     * Constructs a builder chain in which certain datagen properties can be assigned and re-built with in this
     * EntityTypePropertyWrapper instance. Also sets this ETPW instance's {@link #builder} to the newly-constructed
     * {@link ETPWBuilder<E>} instance.
     *
     * @return A new {@link ETPWBuilder<E>} instance from the {@link #builder} field.
     */
    public ETPWBuilder<E> builder() {
        return this.builder = new ETPWBuilder<>(this, parentEntityType);
    }

    /**
     * Gets the cached {@link ETPWBuilder<E>} instance from the {@link #builder} if it exists. May be {@code null}.
     * Useful for overriding specific properties after having copied another ETPW instance/already set an ETPWBuilder.
     *
     * @return The cached {@link ETPWBuilder<E>} instance, or {@code null} if the {@link #builder} is {@code null}.
     *
     * @see #of(EntityTypePropertyWrapper, Supplier)
     * @see #ofTemplate(EntityTypePropertyWrapper)
     */
    @Nullable
    public ETPWBuilder<E> cachedBuilder() {
        return builder;
    }

    /**
     * Gets the parent {@link Supplier<EntityType<E>>} of this ETPW instance.
     *
     * @return The parent {@link Supplier<EntityType<E>>} stored in this ETPW instance.
     */
    public Supplier<EntityType<E>> getParentEntityType() {
        return parentEntityType;
    }

    /**
     * Gets the manually localized entity type name from the {@link #builder()} if the builder exists.
     *
     * @return The manually localized entity type name, or an empty {@code String} if the {@link #builder()} is {@code null}.
     */
    public String getManuallyLocalizedEntityTypeName() {
        return builder == null ? "" : builder.manuallyLocalizedItemName;
    }

    /**
     * Gets whether this ETPW instance bypasses default translation corrections.
     *
     * @return Whether this ETPW instance bypasses default translation corrections.
     */
    public boolean hasLiteralTranslation() {
        return builder != null && builder.literalTranslation;
    }

    /**
     * Gets whether this ETPW instance bypasses default translation altogether.
     *
     * @return Whether this ETPW instance bypasses default translation altogether.
     */
    public boolean bypassDefaultTranslation() {
        return builder != null && builder.bypassDefaultTranslation;
    }

    /**
     * Gets the defined separator words from the {@link #builder()} if the builder exists.
     *
     * @return The defined separator words, or an empty {@link ObjectArrayList} if the {@link #builder()} is {@code null}.
     */
    public List<String> getDefinedSeparatorWords() {
        return builder == null ? ObjectArrayList.of() : builder.definedSeparatorWords;
    }

    /**
     * Gets the localization {@code Function<String, String>} from the {@link #builder()} if the builder exists, and it
     * is defined within said builder. May be {@code null}.
     *
     * @return The {@code Function<String, String>}, or {@code null} if the {@link #builder()} is {@code null} || it
     * isn't defined within said builder.
     */
    @Nullable
    public Function<String, String> getEntityTypeTranslationFunc() {
        return builder == null ? null : builder.entityTypeTranslationFunc;
    }

    /**
     * Gets the {@code Function<Supplier<EntityType<E>>, LootTable.Builder>} from the {@link #builder()} if the builder
     * exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The {@code Function<Supplier<EntityType<E>>, LootTable.Builder>}, or {@code null} if the {@link #builder()}
     * is {@code null} || it isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<EntityType<E>>, LootTable.Builder> getEntityLootTableMappingFunction() {
        return builder == null ? null : builder.entityLootTableBuilder;
    }

    /**
     * Gets the defined parent {@linkplain Supplier<TagKey<EntityType<E>>> Tags} from the {@link #builder()} if the
     * builder exists.
     *
     * @return The defined parent {@linkplain Supplier<TagKey<EntityType<E>>> Tags}, or an empty {@link ObjectArrayList}
     * if the {@link #builder()} is {@code null}.
     */
    public List<Supplier<TagKey<EntityType<E>>>> getParentTags() {
        return builder == null ? ObjectArrayList.of() : builder.parentTags;
    }

    /**
     * Gets the {@code Supplier<AttributeSupplier.Builder>} from the {@link #builder()} if the builder exists, and it is
     * defined within said builder.
     * May be {@code null}.
     *
     * @return The {@code Supplier<AttributeSupplier.Builder>}, or {@code null} if the {@link #builder()} is {@code null}
     * || it isn't defined within said builder.
     */
    @Nullable
    public Supplier<AttributeSupplier.Builder> getAttributeBuilder() {
        return builder == null ? null : builder.attribBuilder;
    }

    /**
     * Whether data present in {@link #builder()} (if not {@code null}) should automatically be handled/generated by
     * Nexus API.
     *
     * @return {@code true} if {@link #builder} isn't {@code null} and {@link ETPWBuilder#excludeFromNativeDatagen} is set
     * to {@code true}, {@code false} otherwise.
     */
    public boolean excludeFromNativeDatagen() {
        return builder != null && builder.excludeFromNativeDatagen;
    }

    /**
     * Gets a {@link Map} (usually {@link Object2BooleanOpenHashMap}) specifying the {@linkplain ProviderType ProviderTypes}
     * for which this BPW instance requires data to present for generation. May be empty if {@link #builder} is {@code null}
     * or the underlying {@link Map} is also empty.
     *
     * @return The {@link Map} representing different {@linkplain ProviderType ProviderTypes} and their requirements for
     * datagen. May be empty.
     */
    public Map<ProviderType, Boolean> getProviderTypeRequisites() {
        return builder == null ? new Object2BooleanOpenHashMap<>() : builder.mappedProviderRequisites;
    }

    /**
     * Whether this ETPW instance is a template. Templates are not stored in {@link #getMappedEtpws()} and have no parent
     * {@link EntityType}.
     *
     * @return Whether this ETPW instance is a template.
     *
     * @see #of(EntityTypePropertyWrapper, Supplier)
     * @see #createTemplate()
     */
    public boolean isTemplate() {
        return isTemplate;
    }

    /**
     * Gets an immutable view (via {@link ImmutableSortedMap}) of {@link #MAPPED_ETPWS}.
     *
     * @return An immutable view (via {@link ImmutableSortedMap}) of {@link #MAPPED_ETPWS}.
     */
    public static ImmutableSortedMap<Supplier<? extends EntityType<?>>, EntityTypePropertyWrapper<?>> getMappedEtpws() {
        return ImmutableSortedMap.copyOf(MAPPED_ETPWS);
    }

    /**
     * A builder class used to construct certain entity type-related data for datagen and other backend data, such as
     * attributes.
     */
    public static class ETPWBuilder<E extends Entity> {
        private final EntityTypePropertyWrapper<E> ownerWrapper;
        private final Supplier<EntityType<E>> entityTypeParent;
        private String manuallyLocalizedItemName = "";
        private List<String> definedSeparatorWords = ObjectArrayList.of();
        @Nullable
        private Function<Supplier<EntityType<E>>, LootTable.Builder> entityLootTableBuilder;
        private final List<Supplier<TagKey<EntityType<E>>>> parentTags = ObjectArrayList.of();
        @Nullable
        private Function<String, String> entityTypeTranslationFunc;
        private boolean literalTranslation = false;
        private boolean bypassDefaultTranslation = false;
        private Supplier<AttributeSupplier.Builder> attribBuilder;
        private boolean excludeFromNativeDatagen = false;
        private final Map<ProviderType, Boolean> mappedProviderRequisites = new Object2BooleanOpenHashMap<>();

        private ETPWBuilder(EntityTypePropertyWrapper<E> ownerWrapper, Supplier<EntityType<E>> entityTypeParent) {
            this.ownerWrapper = ownerWrapper;
            this.entityTypeParent = entityTypeParent;
        }

        /**
         * Assigns a custom translation key for datagen. By default, a basic regex algorithm is used to automatically localize
         * the entity type name into something more legible (I.E. The names you see in-game). This property is simply an override
         * mechanic which aims to give the end-developer more control over the resulting name instead of being forced to rely on
         * the aforementioned algorithm.
         * <br></br>
         * The algorithm in question, in a nutshell, works as follows (the code block below is purely demonstrative of the
         * localization process and has nothing to do with how the algorithm is actually written):
         * <pre>
         *     {@code
         *      public class AlgorithmExampleDescriptor {
         *
         *          public static void main(String[] args) {
         *              // Input
         *              String unlocalizedName = "entity.chaosawakens.robo_pounder"; // The registry name/initial un-localized name
         *
         *              // Steps
         *              AlgorithmLanguageProvider.validateNullity(unlocalizedName); // Checks whether the provided 'unlocalizedName' is empty/all whitespaces/you get the point
         *              AlgorithmLanguageProvider.validateRegex(unlocalizedName); // Checks whether the provided 'unlocalizedName' has the signature registry name separator character "."
         *              AlgorithmLanguageProvider.formatCaps(unlocalizedName); // Output: "Entity.Mymodid.My_Entity" <-- Capitalizes the first letter of each word based on regex-checks for special separators ("." and "_") (First character all the way to the left is always capitalized (duh), not that it matters)
         *              AlgorithmLanguageProvider.formatSeparators(unlocalizedName); // Output: "Entity.Mymodid.My_Entity" <-- Any defined "separator" Strings are lowercased, see #withCustomSeparatorWords(List). In this case, there aren't any, so this step does nothing
         *              AlgorithmLanguageProvider.formatSpecialSeparators(unlocalizedName); // Output: "My Entity" <-- All characters preceding the last "." are substringed/removed, and then any "_" characters are replaced with whitespaces
         *
         *              // End result
         *              System.out.println(unlocalizedName); // Output: "Robo Pounder"
         *          }
         *      }
         *     }
         * </pre>
         *
         * @param manuallyLocalizedItemName The name override used to localize the parent {@linkplain EntityType EntityType's}
         *                                  registry name.
         *
         * @return {@code this} (builder method).
         *
         * @see #withCustomSeparatorWords(List)
         * @see #literalTranslation(boolean)
         * @see #bypassDefaultTranslation(boolean)
         * @see #withLocalization(Function)
         */
        public ETPWBuilder<E> withCustomName(String manuallyLocalizedItemName) {
            this.manuallyLocalizedItemName = manuallyLocalizedItemName;
            return this;
        }

        /**
         * Assigns a {@link List} of custom separator words which are lowercased during the algorithm's de-localization
         * process. This is ignored if {@link #manuallyLocalizedItemName} is defined, {@link #literalTranslation} is
         * {@code true}, or if {@link #entityTypeTranslationFunc} is non-null. The default entries for this are
         * {"Of", "And"}. This {@link List} is appended to the default separator definitions rather than replacing them.
         *
         * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
         *                              running.
         *
         * @return {@code this} (builder method).
         *
         * @see #withCustomName(String)
         * @see #withLocalization(Function)
         * @see #literalTranslation(boolean)
         * @see #bypassDefaultTranslation(boolean)
         */
        public ETPWBuilder<E> withCustomSeparatorWords(List<String> definedSeparatorWords) {
            this.definedSeparatorWords = definedSeparatorWords;
            return this;
        }

        /**
         * Marks this builder as using literal translations, meaning that corrections (like the one seen in the example
         * provided by {@link #withCustomName(String)}) are not applied. Useless on entity types (for now).
         *
         * @param literalTranslation Whether to use literal translations.
         *
         * @return {@code this} (builder method).
         *
         * @see #withCustomName(String)
         * @see #withLocalization(Function)
         * @see #literalTranslation()
         * @see #bypassDefaultTranslation(boolean)
         */
        public ETPWBuilder<E> literalTranslation(boolean literalTranslation) {
            this.literalTranslation = literalTranslation;
            return this;
        }

        /**
         * Whether this ETPWBuilder instance should skip the translation process altogether.
         * <br></br>
         * Note that data won't be generated for this instance (NPEs may be thrown too, based on the validation policy
         * for your mod) unless {@link #literalTranslation(boolean)} is marked as {@code true} or {@link #withCustomName(String)}
         * is set to a non-{@code null} value.
         *
         * @return {@code this} (builder method).
         *
         * @see #literalTranslation(boolean)
         * @see #withCustomName(String)
         * @see #bypassDefaultTranslation()
         */
        public ETPWBuilder<E> bypassDefaultTranslation(boolean bypassDefaultTranslation) {
            this.bypassDefaultTranslation = bypassDefaultTranslation;
            return this;
        }

        /**
         * Overloaded variant of {@link #bypassDefaultTranslation(boolean)}, marking this builder to be skipped by the
         * default localization algorithm Nexus API employs. See the base variant for more info.
         *
         * @return {@link #bypassDefaultTranslation(boolean)}
         *
         * @see #literalTranslation(boolean)
         * @see #withCustomName(String)
         * @see #bypassDefaultTranslation(boolean)
         */
        public ETPWBuilder<E> bypassDefaultTranslation() {
            return bypassDefaultTranslation(true);
        }

        /**
         * A custom {@link Function} to apply miscellaneous modifications to the resulting localized entity type name.
         * This is influenced by {@link #withCustomName(String)} and {@link #literalTranslation(boolean)}, where
         * applicable.
         *
         * @param entityTypeTranslationFunc The {@link Function} responsible for directly modifying the resulting
         *                                  localized entity type name.
         *
         * @return {@code this} (builder method).
         *
         * @see #withCustomName(String)
         * @see #literalTranslation(boolean)
         */
        public ETPWBuilder<E> withLocalization(Function<String, String> entityTypeTranslationFunc) {
            this.entityTypeTranslationFunc = entityTypeTranslationFunc;
            return this;
        }

        /**
         * Overloaded variant of {@link #literalTranslation(boolean)} which marks this builder as using literal
         * translations.
         *
         * @return {@code this} (builder method).
         *
         * @see #literalTranslation(boolean)
         */
        public ETPWBuilder<E> literalTranslation() {
            return literalTranslation(true);
        }

        /**
         * Assigns a given {@link LootTable.Builder} to this builder via the input function. Can be {@code null}.
         *
         * @param entityLootTableBuilder The mapping {@code Function<Supplier<EntityType<E>>, LootTable.Builder>}
         *                               used to build this ETPWBuilder's parent entity's loot table in datagen.
         *
         * @return {@code this} (builder method).
         *
         * @see LootUtil
         */
        public ETPWBuilder<E> withLootTable(Function<Supplier<EntityType<E>>, LootTable.Builder> entityLootTableBuilder) {
            this.entityLootTableBuilder = entityLootTableBuilder;
            return this;
        }

        /**
         * Tags this ETPWBuilder's parent {@link EntityType<E>} with the provided {@link TagKey<EntityType<E>>}.
         *
         * @param parentEntityTypeTags The {@link TagKey<EntityType<E>>} with which this ETPW's
         *                             parent {@link EntityType<E>} will be tagged. May only be of
         *                             type {@link EntityType<E>}.
         *
         * @return {@code this} (builder method).
         */
        public ETPWBuilder<E> withTag(Supplier<TagKey<EntityType<E>>> parentEntityTypeTags) {
            this.parentTags.add(parentEntityTypeTags);
            return this;
        }

        /**
         * Tags this ETPWBuilder's parent {@link EntityType<E>} with the provided {@linkplain TagKey<EntityType<E>> Tags}.
         * Appends to the existing list.
         *
         * @param parentEntityTypeTags The {@linkplain TagKey<EntityType<E>> TagKeys} with which this ETPW's
         *                             parent {@link EntityType<E>} will be tagged. May only be of type {@link EntityType<E>}.
         *
         * @return {@code this} (builder method).
         *
         * @see #withSetTags(List)
         */
        public ETPWBuilder<E> withTags(List<Supplier<TagKey<EntityType<E>>>> parentEntityTypeTags) {
            this.parentTags.addAll(parentEntityTypeTags);
            return this;
        }

        /**
         * Tags this ETPWBuilder's parent entity type with the provided {@linkplain TagKey<EntityType<E>> Tags}.
         * Overwrites the existing list.
         *
         * @param parentEntityTypeTags The {@linkplain TagKey<EntityType<E>> TagKeys} with which this ETPW's
         *                             parent {@link EntityType<E>} will be tagged. May only be of type {@link EntityType<E>}.
         *
         * @return {@code this} (builder method).
         *
         * @see #withTags(List)
         */
        public ETPWBuilder<E> withSetTags(List<Supplier<TagKey<EntityType<E>>>> parentEntityTypeTags) {
            this.parentTags.clear();
            this.parentTags.addAll(parentEntityTypeTags);
            return this;
        }

        /**
         * Specifies the attributes for the owner entity type.
         *
         * @param attributeBuilder The attribute builder for the owner entity type.
         *
         * @return {@code this} (builder method).
         */
        public ETPWBuilder<E> withAttributes(Supplier<AttributeSupplier.Builder> attributeBuilder) {
            this.attribBuilder = attributeBuilder;
            return this;
        }

        /**
         * Determines whether this ETPW instance should be entirely excluded from Nexus' native datagen.
         * <br></br>
         * Fundamentally, all this does is flag this instance as not needing a data entry to be mapped to it. You may
         * choose to generate data for it yourself if needed, since Nexus won't handle datagen for this particular object.
         * <br></br>
         * If an entity type-specific data provider has {@link ModDataProvider#validateAllEntries()} set to {@code true}, this
         * instance (and its children, so long as this value isn't modified) will still be excluded from datagen, and thus
         * an exception won't be thrown for it.
         *
         * @param excludeFromNativeDatagen Whether this instance's data should be passed into Nexus' native datagen for
         *                                 data generation.
         *
         * @return {@code this} (builder method).
         *
         * @see #excludeFromNativeDatagen()
         * @see #requiresDatagenEntry(ProviderType, boolean)
         */
        public ETPWBuilder<E> excludeFromNativeDatagen(boolean excludeFromNativeDatagen) {
            this.excludeFromNativeDatagen = excludeFromNativeDatagen;
            return this;
        }

        /**
         * Determines whether this ETPW instance is required to generate necessary block-related data based on the
         * {@link ProviderType} passed in.
         * <br></br>
         * By default, unmapped providers will not require an entry for this ETPW to be generated unless
         * {@link ModDataProvider#validateAllEntries()} is set to {@code true}.
         * <br></br>
         * Mapping the related provider passed in here to {@code requiresDatagenEntry}, set to {@code true}, will flag
         * this ETPW instance for requiring related data regardless of what
         * {@link ModDataProvider#validateAllEntries()} is set to.
         *
         * @param targetProviderType The {@link ProviderType} to modify the data entry requirement for.
         * @param requiresDatagenEntry Whether this ETPW should require data related to the specified
         *                             {@code targetProviderType} to be present.
         *
         * @return {@code this} (builder method).
         *
         * @see #requiresDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(Map)
         * @see #excludeFromNativeDatagen(boolean)
         */
        public ETPWBuilder<E> requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry) {
            mappedProviderRequisites.put(targetProviderType, requiresDatagenEntry);
            return this;
        }

        /**
         * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
         * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}.
         *
         * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
         *                            entry requirements for.
         * @param requiresDatagenEntry Whether this BlockPropertyWrapperBuilder should require data related to each of the
         *                             specified {@code targetProviderTypes} to be present.
         *
         * @return {@code this} (builder method).
         *
         * @see #requiresDatagenEntry(ProviderType, boolean)
         * @see #requiresSetDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(Map)
         * @see #excludeFromNativeDatagen(boolean)
         */
        public ETPWBuilder<E> requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
            targetProviderTypes.forEach(type -> requiresDatagenEntry(type, requiresDatagenEntry));
            return this;
        }

        /**
         * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
         * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
         * {@link Map}.
         *
         * @param targetProviderTypes The {@link List} of {@linkplain ProviderType ProviderTypes} to modify the data
         *                            entry requirements for.
         * @param requiresDatagenEntry Whether this BlockPropertyWrapperBuilder should require data related to each of the
         *                             specified {@code targetProviderTypes} to be present.
         *
         * @return {@code this} (builder method).
         *
         * @see #requiresDatagenEntry(ProviderType, boolean)
         * @see #requiresDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(Map)
         * @see #excludeFromNativeDatagen(boolean)
         */
        public ETPWBuilder<E> requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
            mappedProviderRequisites.clear();
            targetProviderTypes.forEach(type -> requiresDatagenEntry(type, requiresDatagenEntry));
            return this;
        }

        /**
         * Overloaded variant of {@link #requiresDatagenEntry(ProviderType, boolean)}. Maps each of the
         * {@linkplain ProviderType ProviderTypes} passed in to {@code requiresDatagenEntry}. Overrides the existing
         * {@link Map}.
         *
         * @param mappedProviderRequisites The {@link Map} of provider requisites to override the existing {@link Map}
         *                                 with.
         *
         * @return {@code this} (builder method).
         *
         * @see #requiresDatagenEntry(ProviderType, boolean)
         * @see #requiresDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(List, boolean)
         * @see #excludeFromNativeDatagen(boolean)
         */
        public ETPWBuilder<E> requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites) {
            this.mappedProviderRequisites.clear();
            this.mappedProviderRequisites.putAll(mappedProviderRequisites);
            return this;
        }

        /**
         * Builds a new {@link EntityTypePropertyWrapper<E>} using this builder's data. Also maps the owner
         * {@link EntityTypePropertyWrapper<E>} to the parent {@linkplain EntityType<E>} if the owner is not a template.
         *
         * @return The newly data-populated {@link EntityTypePropertyWrapper<E>}.
         *
         * @see EntityTypePropertyWrapper#isTemplate()
         */
        public EntityTypePropertyWrapper<E> build() {
            if (!ownerWrapper.isTemplate) MAPPED_ETPWS.putIfAbsent(ownerWrapper.entityTypeRegName == null ? ownerWrapper.parentEntityType : () -> BuiltInRegistries.ENTITY_TYPE.get(ownerWrapper.entityTypeRegName), ownerWrapper);
            return ownerWrapper;
        }
    }
}
