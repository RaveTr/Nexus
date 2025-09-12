package com.mememan.nexus.block.standard;

import com.google.common.collect.ImmutableMap;
import com.mememan.nexus.block.data.BlockModelDefinition;
import com.mememan.nexus.block.data.BlockStateDefinition;
import com.mememan.nexus.client.block.WrappedBlockColor;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.datagen.standard.ModDataProvider;
import com.mememan.nexus.platform.NexusServices;
import it.unimi.dsi.fastutil.ints.IntIntMutablePair;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A wrapper {@code class} used to store information referenced in datagen to simplify creating data entries for blocks,
 * as well as the modification of hardcoded block settings.
 */
public class BlockPropertyWrapper {
    private static final Object2ObjectLinkedOpenHashMap<Supplier<Block>, BlockPropertyWrapper> MAPPED_BPWS = new Object2ObjectLinkedOpenHashMap<>(); // Preserve object insertion order
    @Nullable
    private final ResourceLocation blockRegName;
    private final Supplier<Block> parentBlock;
    private final boolean isTemplate;
    @Nullable
    private BPWBuilder builder;

    private BlockPropertyWrapper(@Nullable ResourceLocation blockRegName, Supplier<Block> parentBlock) {
        this.blockRegName = blockRegName;
        this.parentBlock = parentBlock;
        this.isTemplate = false;
    }

    private BlockPropertyWrapper(Supplier<Block> parentBlock) {
        this(null, parentBlock);
    }

    private BlockPropertyWrapper() {
        this.blockRegName = null;
        this.parentBlock = null;
        this.isTemplate = true; // Otherwise can't set with constructor overloading
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance. This is usually where you'll begin chaining
     * {@link #builder()} method calls if needed. Use this variant if you want to directly create a BPW during a
     * registration call rather than after it/without storing it.
     *
     * @param blockRegName The registry name of the {@code parentBlock}. Used when access to the {@code parentBlock}
     *                     returns an air delegate (I.E. It's too early to access the parent block).
     * @param parentBlock The parent {@code Supplier<Block>} stored in the newly-initialized BPW instance.
     *
     * @return A new {@link BlockPropertyWrapper} instance.
     *
     * @apiNote You shouldn't have to use this method for 95% of cases since you'd be inlining a registration call for
     * {@link #parentBlock}, see {@link #create(Supplier)} instead.
     *
     * @see #create(Supplier)
     * @see #of(Supplier, Supplier)
     * @see #of(ResourceLocation, Supplier)
     * @see #of(BlockPropertyWrapper, Supplier)
     */
    public static BlockPropertyWrapper create(ResourceLocation blockRegName, Supplier<Block> parentBlock) {
        return new BlockPropertyWrapper(blockRegName, parentBlock);
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance. This is usually where you'll begin chaining
     * {@link #builder()} method calls if needed. Use this variant if you want to create a BPW instance with a stored
     * registration call, such that its parent {@code Supplier<Block>} is not an air delegate.
     *
     * @param parentBlock The parent {@code Supplier<Block>} stored in the newly-initialized BPW instance.
     *
     * @return A new {@link BlockPropertyWrapper} instance.
     *
     * @see #create(ResourceLocation, Supplier)
     * @see #of(Supplier, Supplier)
     * @see #of(ResourceLocation, Supplier)
     * @see #of(BlockPropertyWrapper, Supplier)
     */
    public static BlockPropertyWrapper create(Supplier<Block> parentBlock) {
        return new BlockPropertyWrapper(parentBlock);
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance as a template. Template BPWs are not stored in
     * {@link #MAPPED_BPWS} and do not store a parent {@link Block}. They're particularly useful for re-using across
     * multiple {@linkplain Block Blocks}.
     *
     * @return A new {@link BlockPropertyWrapper} instance, set as a template.
     *
     * @see #of(BlockPropertyWrapper, Supplier)
     * @see #isTemplate()
     * @see #ofTemplate(BlockPropertyWrapper)
     */
    public static BlockPropertyWrapper createTemplate() {
        return new BlockPropertyWrapper();
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance as a template, inheriting data from the provided BPW template.
     * Template BPWs are not stored in {@link #MAPPED_BPWS} and do not store a parent {@link Block}. They're
     * particularly useful for re-using across multiple {@linkplain Block Blocks}.
     *
     * @param parentTemplateWrapper The parent {@link BlockPropertyWrapper} template from which {{@link #builder()}}
     *                              data should be copied.
     *
     * @return A new {@link BlockPropertyWrapper} instance, set as a template, inheriting from the provided BPW template.
     * If the provided BPW template is {@code null}, returns {@link #createTemplate()}.
     *
     * @see #createTemplate()
     * @see #isTemplate()
     */
    public static BlockPropertyWrapper ofTemplate(BlockPropertyWrapper parentTemplateWrapper) {
        if (parentTemplateWrapper != null) {
            BlockPropertyWrapper newTemplateWrapper = new BlockPropertyWrapper();

            return copyProperties(parentTemplateWrapper, newTemplateWrapper);
        } else return createTemplate();
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance based on the provided {@link BlockPropertyWrapper}. If the
     * provided BPW instance is {@code null}, returns {@link #create(Supplier)}. You'd typically use this if you have a
     * BPW template you want multiple registered {@linkplain Block Blocks} to inherit from.
     *
     * @param parentWrapper The parent {@link BlockPropertyWrapper} instance from which {@link #builder()} should be
     *                      copied.
     * @param newBlock The new registry entry to use for the newly constructed BPW instance.
     *
     * @return A new {@link BlockPropertyWrapper} instance with copied properties based on the provided BPW, or an
     * entirely new/clean instance if the provided BPW is {@code null}.
     *
     * @see #of(Supplier, Supplier)
     * @see #of(ResourceLocation, Supplier)
     * @see #create(Supplier)
     * @see #create(ResourceLocation, Supplier)
     * @see #createTemplate()
     */
    public static BlockPropertyWrapper of(BlockPropertyWrapper parentWrapper, Supplier<Block> newBlock) {
        if (parentWrapper != null) {
            BlockPropertyWrapper newWrapper = new BlockPropertyWrapper(newBlock);

            return copyProperties(parentWrapper, newWrapper);
        } else return create(newBlock);
    }

    /**
     * Creates a new {@link BlockPropertyWrapper} instance from an existing {@link BlockPropertyWrapper} instance based
     * on the provided {@code Supplier<Block>}. If no such existing BPW instance exists, returns {@link #create(Supplier)}.
     *
     * @param parentBlock The parent {@code Supplier<Block>} stored in {@link #MAPPED_BPWS}. Copies its BPW
     *                    instance's {@link BPWBuilder} properties if it exists, or creates a clean new BPW instance if
     *                    it doesn't.
     * @param newBlock The new registry entry to use for the newly constructed BPW instance.
     *
     * @return A new {@link BlockPropertyWrapper} instance with copied properties based on the provided
     * {@code Supplier<Block>}, or an entirely new/clean instance if no such BPW exists.
     *
     * @see #create(Supplier)
     * @see #create(ResourceLocation, Supplier)
     * @see #of(ResourceLocation, Supplier)
     * @see #of(BlockPropertyWrapper, Supplier)
     */
    public static BlockPropertyWrapper of(Supplier<Block> parentBlock, Supplier<Block> newBlock) {
        if (MAPPED_BPWS.containsKey(parentBlock)) {
            BlockPropertyWrapper originalWrapper = MAPPED_BPWS.get(parentBlock);
            BlockPropertyWrapper newWrapper = new BlockPropertyWrapper(newBlock);

            return copyProperties(originalWrapper, newWrapper);
        } else return create(newBlock);
    }

    /**
     * Overloaded variant of {@link #of(Supplier, Supplier)} that copies the parent block's
     * {@link BlockBehaviour.Properties}.
     *
     * @param newBlockRegLoc The new registry name by which the newly-constructed {@code Supplier<Block>} instance will
     *                       be stored.
     * @param parentBlock The parent {@link Supplier<Block>} stored in {@link #MAPPED_BPWS}.
     *
     * @return A new {@link BlockPropertyWrapper} instance with copied properties (including
     * {@link BlockBehaviour.Properties}) based on the provided {@code Supplier<Block>}, or an entirely new/clean
     * instance if no such BPW exists.
     *
     * @see #of(BlockPropertyWrapper, Supplier)
     * @see #of(Supplier, Supplier)
     * @see #create(Supplier)
     * @see #create(ResourceLocation, Supplier)
     */
    public static BlockPropertyWrapper of(ResourceLocation newBlockRegLoc, Supplier<Block> parentBlock) {
        return of(parentBlock, NexusServices.REGISTRAR.registerObject(newBlockRegLoc, () -> new Block(BlockBehaviour.Properties.copy(parentBlock.get())), BuiltInRegistries.BLOCK));
    }

    /**
     * Shortcut utility method centered around copying builder properties over from one BPW instance to another.
     *
     * @param from The BPW instance to copy properties from.
     * @param to The BPW instance to copy properties to.
     *
     * @return The provided BPW instance with copied properties.
     */
    public static BlockPropertyWrapper copyProperties(BlockPropertyWrapper from, BlockPropertyWrapper to) {
        if (from.builder == null) return to;
        return to.builder()
                .withCustomName(from.builder.manuallyLocalizedBlockName)
                .literalTranslation(from.builder.literalTranslation)
                .bypassDefaultTranslation(from.builder.bypassDefaultTranslation)
                .withCustomSeparatorWords(from.builder.definedSeparatorWords)
                .withLocalization(from.builder.blockTranslationFunc)
                .withSetTags(List.copyOf(from.builder.parentTags))
                .withSetBlockTags(List.copyOf(from.builder.blockTags))
                .withSetItemTags(List.copyOf(from.builder.itemTags))
                .withLootTable(from.builder.blockLootTableBuilder)
                .withCustomModelDefinitions(from.builder.bmdMappingFunc)
                .withBlockStateDefinition(from.builder.blockStateDefinition)
                .withRecipe(from.builder.recipeBuilderFunction)
                .withSetParentCreativeModeTabs(List.copyOf(from.builder.parentTabs))
                .withBlockColor(from.builder.blockColorMappingFunc)
                .withFlammability(from.builder.flammabilityMappingFunc)
                .withAxeStripping(from.builder.blockStrippingMappingFunc)
                .withHoeTilling(from.builder.blockTillingMappingFunc)
                .withShovelFlattening(from.builder.blockFlatteningMappingFunc)
                .withOxidization(from.builder.blockOxidizationMappingFunc)
                .asWaxable(from.builder.blockWaxingMappingFunc)
                .asCompostable(from.builder.blockCompostingMappingFunc)
                .asFuel(from.builder.blockFuelMappingFunc)
                .excludeFromNativeDatagen(from.builder.excludeFromNativeDatagen)
                .requiresSetDatagenEntries(Map.copyOf(from.builder.mappedProviderRequisites))
                .build(); // Direct setting of the builder would copy the entire object itself, which would in-turn overwrite it if any calls are made to the copied BPW afterward
    }

    /**
     * Constructs a builder chain in which certain datagen/hardcoded properties can be assigned and re-built with in this
     * BlockPropertyWrapper instance. Also sets this BPW instance's {@link #builder} to the newly-constructed
     * {@link BPWBuilder} instance.
     * <br></br>
     * <b>NOTE: THIS WILL OVERRIDE {@link #builder} ENTIRELY EVEN IF IT'S NOT {@code null} (e.g. you're inheriting from a
     * template, see {@link #of(BlockPropertyWrapper, Supplier)}).</b>
     *
     * @return A new {@link BPWBuilder} instance from the {@link #builder} field.
     *
     * @see #cachedBuilder()
     */
    public BPWBuilder builder() {
        return this.builder = new BPWBuilder(this, parentBlock);
    }

    /**
     * Gets the cached {@link BPWBuilder} instance from the {@link #builder} if it exists. May be {@code null}. Useful
     * for overriding specific properties after having copied another BPW instance/already set a BlockPropertyWrapperBuilder.
     *
     * @return The cached {@link BPWBuilder} instance, or {@code null} if the {@link #builder} is {@code null}.
     *
     * @see #of(ResourceLocation, Supplier)
     * @see #of(Supplier, Supplier)
     * @see #builder()
     */
    @Nullable
    public BPWBuilder cachedBuilder() {
        return builder;
    }

    /**
     * Gets the parent {@code Supplier<Block>} of this BPW instance.
     *
     * @return The parent {@code Supplier<Block>} stored in this BPW instance.
     */
    public Supplier<Block> getParentBlock() {
        return parentBlock;
    }

    /**
     * Gets the manually localized block name from the {@link #builder()} if the builder exists.
     *
     * @return The manually localized block name, or an empty {@code String} if the {@link #builder()} is {@code null}.
     */
    public String getManuallyLocalizedBlockName() {
        return builder == null ? "" : builder.manuallyLocalizedBlockName;
    }

    /**
     * Gets whether this BPW instance bypasses default translation corrections.
     *
     * @return Whether this BPW instance bypasses default translation corrections.
     */
    public boolean hasLiteralTranslation() {
        return builder != null && builder.literalTranslation;
    }

    /**
     * Gets whether this BPW instance bypasses default translation altogether.
     *
     * @return Whether this BPW instance bypasses default translation altogether.
     */
    public boolean bypassDefaultTranslation() {
        return builder != null && builder.bypassDefaultTranslation;
    }

    /**
     * Gets the defined separator words from the {@link #builder()} if the builder exists.
     *
     * @return The defined separator words, or an empty {@link ObjectArrayList} if the {@link #builder()} is
     * {@code null}.
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
    public Function<String, String> getBlockTranslationFunc() {
        return builder == null ? null : builder.blockTranslationFunc;
    }

    /**
     * Gets the {@code Function<Supplier<Block>, LootTable.Builder>} from the {@link #builder()} if the builder exists,
     * and it is defined within said builder. May be {@code null}.
     *
     * @return The loot table builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, LootTable.Builder> getBlockLootTableMappingFunction() {
        return builder == null ? null : builder.blockLootTableBuilder;
    }

    /**
     * Gets the defined parent {@linkplain Supplier<TagKey> Tags} from the {@link #builder()} if the builder exists.
     *
     * @return The defined parent {@linkplain Supplier<TagKey> Tags}, or an empty {@link ObjectArrayList} if the
     * {@link #builder()} is {@code null}.
     */
    public List<Supplier<TagKey<?>>> getParentTags() {
        return builder == null ? ObjectArrayList.of() : builder.parentTags;
    }

    /**
     * Gets the defined parent {@linkplain Supplier<TagKey<Block>> Tags} from the {@link #builder()} if the builder exists.
     *
     * @return The defined parent {@linkplain Supplier<TagKey<Block>> Tags}, or an empty {@link ObjectArrayList} if the
     * {@link #builder()} is {@code null}.
     */
    public List<Supplier<TagKey<Block>>> getParentBlockTags() {
        return builder == null ? ObjectArrayList.of() : builder.blockTags;
    }

    /**
     * Gets the defined parent {@linkplain Supplier<TagKey< Item >> Tags} from the {@link #builder()} if the builder
     * exists.
     *
     * @return The defined parent {@linkplain Supplier<TagKey<Item>> Tags}, or an empty {@link ObjectArrayList} if the
     * {@link #builder()} is {@code null}.
     */
    public List<Supplier<TagKey<Item>>> getParentItemTags() {
        return builder == null ? ObjectArrayList.of() : builder.itemTags;
    }

    /**
     * Gets the {@code List<BlockModelDefinition>} {@code Function<Supplier<Block>, List<BlockModelDefinition>>} from
     * the {@link #builder()} if the builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block model builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, List<BlockModelDefinition>> getBMDMappingFunction() {
        return builder == null ? null : builder.bmdMappingFunc;
    }

    /**
     * Gets the {@link BlockStateDefinition} {@code Function<Supplier<Block>, BlockStateDefinition>} from the
     * {@link #builder()} if the builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The {@code Function<Supplier<Block>, BlockStateDefinition>}, or {@code null} if the {@link #builder()}
     * is {@code null} || it isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, BlockStateDefinition> getBlockStateDefinitionMappingFunction() {
        return builder == null ? null : builder.blockStateDefinition;
    }

    /**
     * Gets the recipe {@code Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>>>} from the
     * {@link #builder()} if the builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The recipe builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> getRecipeMappingFunction() {
        return builder == null ? null : builder.recipeBuilderFunction;
    }

    /**
     * Gets the {@link List} of parent {@linkplain CreativeModeTab CreativeModeTabs} from the {@link #builder()} if the
     * builder exists, and it is defined within said builder. May be empty.
     *
     * @return The {@link List} of parent {@linkplain CreativeModeTab CreativeModeTabs}, or an empty
     * {@link ObjectArrayList} if the {@link #builder()} is {@code null}.
     */
    public List<Supplier<CreativeModeTab>> getParentCreativeModeTabs() {
        return builder == null ? ObjectArrayList.of() : builder.parentTabs;
    }

    /**
     * Gets the {@link WrappedBlockColor} {@code Function<Supplier<Block>, WrappedBlockColor>} from the {@link #builder()} if the
     * builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The {@code Function<Supplier<Block>, WrappedBlockColor>}, or {@code null} if the {@link #builder()} is
     * {@code null} || it isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, WrappedBlockColor> getBlockColorMappingFunc() {
        return builder == null ? null : builder.blockColorMappingFunc;
    }

    /**
     * Gets the flammability {@code Function<Supplier<Block>, IntIntMutablePair>} from the {@link #builder()} if the
     * builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The flammability options builder function, or {@code null} if the {@link #builder()} is {@code null} ||
     * it isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, IntIntMutablePair> getFlammabilityMappingFunc() {
        return builder == null ? null : builder.flammabilityMappingFunc;
    }

    /**
     * Gets the stripping {@code Function<Supplier<Block>, Supplier<Block>>} from the {@link #builder()} if the builder
     * exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block stripping builder function, or {@code null} if the {@link #builder()} is {@code null} || it
     * isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, Supplier<Block>> getBlockStrippingMappingFunc() {
        return builder == null ? null : builder.blockStrippingMappingFunc;
    }

    /**
     * Gets the tilling {@code Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>>}
     * from the {@link #builder()} if the builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block tilling builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>> getBlockTillingMappingFunc() {
        return builder == null ? null : builder.blockTillingMappingFunc;
    }

    /**
     * Gets the flattening {@code Function<Supplier<Block>, BlockState>} from the {@link #builder()} if the builder
     * exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block flattening builder function, or {@code null} if the {@link #builder()} is {@code null} || it
     * isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, BlockState> getBlockFlatteningMappingFunc() {
        return builder == null ? null : builder.blockFlatteningMappingFunc;
    }

    /**
     * Gets the oxidization {@code Function<Supplier<Block>, Supplier<Block>>} from the {@link #builder()} if the
     * builder exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block oxidization builder function, or {@code null} if the {@link #builder()} is {@code null} || it
     * isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, Supplier<Block>> getBlockOxidizationMappingFunc() {
        return builder == null ? null : builder.blockOxidizationMappingFunc;
    }

    /**
     * Gets the waxing {@code Function<Supplier<Block>, Supplier<Block>>} from the {@link #builder()} if the builder
     * exists, and it is defined within said builder. May be {@code null}.
     *
     * @return The block waxing builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, Supplier<Block>> getBlockWaxingMappingFunc() {
        return builder == null ? null : builder.blockOxidizationMappingFunc;
    }

    /**
     * Gets the composting {@code Function<Supplier<Block>, Float>} from the {@link #builder()} if the builder exists,
     * and it is defined within said builder. May be {@code null}.
     *
     * @return The block composting builder function, or {@code null} if the {@link #builder()} is {@code null} || it
     * isn't defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, Float> getBlockCompostingMappingFunc() {
        return builder == null ? null : builder.blockCompostingMappingFunc;
    }

    /**
     * Gets the fuel {@code Function<Supplier<Block>, Integer>} from the {@link #builder()} if the builder exists, and
     * it is defined within said builder. May be {@code null}.
     *
     * @return The block fuel builder function, or {@code null} if the {@link #builder()} is {@code null} || it isn't
     * defined within said builder.
     */
    @Nullable
    public Function<Supplier<Block>, Integer> getBlockFuelMappingFunc() {
        return builder == null ? null : builder.blockFuelMappingFunc;
    }

    /**
     * Whether data present in {@link #builder()} (if not {@code null}) should automatically be handled/generated by
     * Nexus API.
     *
     * @return {@code true} if {@link #builder} isn't {@code null} and {@link BPWBuilder#excludeFromNativeDatagen} is set
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
     * Whether this BPW instance is a template. Templates are not stored in {@link #getMappedBpws()} and have no parent
     * {@link Block}.
     *
     * @return Whether this BPW instance is a template.
     *
     * @see #of(BlockPropertyWrapper, Supplier)
     * @see #createTemplate()
     */
    public boolean isTemplate() {
        return isTemplate;
    }

    /**
     * Gets an immutable view (via {@link ImmutableMap}) of {@link #MAPPED_BPWS}.
     *
     * @return An immutable view (via {@link ImmutableMap}) of {@link #MAPPED_BPWS}.
     */
    public static ImmutableMap<Supplier<Block>, BlockPropertyWrapper> getMappedBpws() {
        return ImmutableMap.copyOf(MAPPED_BPWS);
    }

    /**
     * A builder class used to construct certain block-related data for datagen and other data related to hardcoded
     * block settings, such as flammability.
     */
    public static class BPWBuilder {
        private final BlockPropertyWrapper ownerWrapper;
        private final Supplier<Block> parentBlock;
        private String manuallyLocalizedBlockName = "";
        private List<String> definedSeparatorWords = ObjectArrayList.of();
        @Nullable
        private Function<Supplier<Block>, LootTable.Builder> blockLootTableBuilder;
        private final List<Supplier<TagKey<?>>> parentTags = ObjectArrayList.of();
        private final List<Supplier<TagKey<Block>>> blockTags = ObjectArrayList.of();
        private final List<Supplier<TagKey<Item>>> itemTags = ObjectArrayList.of();
        @Nullable
        private Function<Supplier<Block>, BlockStateDefinition> blockStateDefinition;
        @Nullable
        private Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> recipeBuilderFunction;
        @Nullable
        private Function<Supplier<Block>, List<BlockModelDefinition>> bmdMappingFunc;
        private final List<Supplier<CreativeModeTab>> parentTabs = new ObjectArrayList<>();
        @Nullable
        private Function<Supplier<Block>, WrappedBlockColor> blockColorMappingFunc;
        @Nullable
        private Function<Supplier<Block>, IntIntMutablePair> flammabilityMappingFunc;
        @Nullable
        private Function<Supplier<Block>, Supplier<Block>> blockStrippingMappingFunc;
        @Nullable
        private Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>> blockTillingMappingFunc;
        @Nullable
        private Function<Supplier<Block>, BlockState> blockFlatteningMappingFunc;
        @Nullable
        private Function<Supplier<Block>, Supplier<Block>> blockOxidizationMappingFunc;
        @Nullable
        private Function<Supplier<Block>, Supplier<Block>> blockWaxingMappingFunc;
        @Nullable
        private Function<Supplier<Block>, Float> blockCompostingMappingFunc;
        @Nullable
        private Function<Supplier<Block>, Integer> blockFuelMappingFunc;
        @Nullable
        private Function<String, String> blockTranslationFunc;
        private boolean literalTranslation = false;
        private boolean bypassDefaultTranslation = false;
        private boolean excludeFromNativeDatagen = false;
        private final Map<ProviderType, Boolean> mappedProviderRequisites = new Object2BooleanOpenHashMap<>();

        private BPWBuilder(BlockPropertyWrapper ownerWrapper, Supplier<Block> parentBlock) {
            this.ownerWrapper = ownerWrapper;
            this.parentBlock = parentBlock;
        }

        /**
         * Assigns a custom translation key for datagen. By default, a basic regex algorithm is used to automatically localize
         * the block name into something more legible (I.E. The names you see in-game). This property is simply an override
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
         *              String unlocalizedName = "block.mymodid.my_material_block"; // The registry name/initial unlocalized name
         *
         *              // Steps
         *              AlgorithmLanguageProvider.validateNullity(unlocalizedName); // Checks whether the provided 'unlocalizedName' is empty/all whitespaces/you get the point
         *              AlgorithmLanguageProvider.validateRegex(unlocalizedName); // Checks whether the provided 'unlocalizedName' has the signature registry name separator character "."
         *              AlgorithmLanguageProvider.formatCaps(unlocalizedName); // Output: "Block.Mymodid.My_Material_Block" <-- Capitalizes the first letter of each word based on regex-checks for special separators ("." and "_") (First character all the way to the left is always capitalized (duh), not that it matters)
         *              AlgorithmLanguageProvider.formatSeparators(unlocalizedName); // Output: "Block.Mymodid.My_Material_Block" <-- Any defined "separator" Strings are lowercased, see #withCustomSeparatorWords(List)
         *              AlgorithmLanguageProvider.formatSpecialSeparators(unlocalizedName); // Output: "Block of My Material" <-- All characters preceding the last "." are substringed/removed, and then any "_" characters are replaced with whitespaces
         *
         *              // End result
         *              System.out.println(unlocalizedName); // Output: "Block of My Material"
         *          }
         *      }
         *     }
         * </pre>
         *
         * @param manuallyLocalizedBlockName The name override used to localize the parent {@linkplain Block Block's} registry name.
         *
         * @return {@code this} (builder method).
         *
         * @apiNote Block registry names ending with "_block" (e.g. "block.mymodid.my_material_block")
         * have the "_block" part pruned and the result string is prepended with "Block of" during the translation
         * process (the registry name stays the same, of course). You may use this method (or
         * {@link #literalTranslation(boolean)} and its overloaded variant(s)) to bypass that step if needed. Otherwise,
         * block names are literally translated.
         *
         * @see #withCustomSeparatorWords(List)
         * @see #withLocalization(Function)
         * @see #literalTranslation(boolean)
         * @see #bypassDefaultTranslation(boolean)
         */
        public BPWBuilder withCustomName(String manuallyLocalizedBlockName) {
            this.manuallyLocalizedBlockName = manuallyLocalizedBlockName;
            return this;
        }

        /**
         * Marks this builder as using literal translations, meaning that corrections (like the one seen in the example
         * provided by {@link #withCustomName(String)}) are not applied.
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
        public BPWBuilder literalTranslation(boolean literalTranslation) {
            this.literalTranslation = literalTranslation;
            return this;
        }

        /**
         * A custom {@link Function} to apply miscellaneous modifications to the resulting localized block name. This is
         * influenced by {@link #withCustomName(String)} and {@link #literalTranslation(boolean)}, where applicable.
         *
         * @param blockTranslationFunc The {@link Function} responsible for directly modifying the resulting localized
         *                             block name.
         *
         * @return {@code this} (builder method).
         *
         * @see #withCustomName(String)
         * @see #literalTranslation(boolean)
         */
        public BPWBuilder withLocalization(Function<String, String> blockTranslationFunc) {
            this.blockTranslationFunc = blockTranslationFunc;
            return this;
        }

        /**
         * Overloaded variant of {@link #literalTranslation(boolean)} which marks this builder as using literal translations.
         *
         * @return {@code this} (builder method).
         *
         * @see #literalTranslation(boolean)
         */
        public BPWBuilder literalTranslation() {
            return literalTranslation(true);
        }

        /**
         * Whether this BlockPropertyWrapperBuilder instance should skip the translation process altogether.
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
        public BPWBuilder bypassDefaultTranslation(boolean bypassDefaultTranslation) {
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
        public BPWBuilder bypassDefaultTranslation() {
            return bypassDefaultTranslation(true);
        }

        /**
         * Assigns a {@link List} of custom separator words which are lowercased during the algorithm's de-localization
         * process. This is ignored if {@link #manuallyLocalizedBlockName} is defined, {@link #literalTranslation} is
         * {@code true}, or if {@link #blockTranslationFunc} is non-null.
         *
         * @param definedSeparatorWords The {@link List} of custom separator words to lowercase while the algorithm is
         *                              running.
         *
         * @return {@code this} (builder method).
         *
         * @apiNote The default entries for this are {"Of", "And"}. This {@link List} is appended to the default
         * separator definitions rather than replacing them.
         *
         * @see #withCustomName(String)
         * @see #withLocalization(Function)
         * @see #literalTranslation(boolean)
         */
        public BPWBuilder withCustomSeparatorWords(List<String> definedSeparatorWords) {
            this.definedSeparatorWords = definedSeparatorWords;
            return this;
        }

        /**
         * Assigns a given {@link LootTable.Builder} to this builder via the input function. Can be {@code null}.
         *
         * @param blockLootTableBuilder The mapping {@code Function<Supplier<Block>, LootTable.Builder>}
         *                              used to build this BlockPropertyWrapperBuilder's parent block's loot table in datagen.
         *
         * @return {@code this} (builder method).
         *
         * @see LootUtil
         */
        public BPWBuilder withLootTable(Function<Supplier<Block>, LootTable.Builder> blockLootTableBuilder) {
            this.blockLootTableBuilder = blockLootTableBuilder;
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@link TagKey<?>}.
         *
         * @param parentBlockTag The {@link TagKey<?>} with which this BPW's parent {@link Block} will be tagged. May
         *                       generally be of types {@link Item} or {@link Block}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withTag(Supplier<TagKey<?>> parentBlockTag) {
            this.parentTags.add(parentBlockTag);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<?> Tags}. Appends to the
         * existing list.
         *
         * @param parentBlockTags The {@linkplain TagKey<?> TagKeys} with which this BPW's parent {@link Block} will be
         *                        tagged. May generally be of types {@link Item} or {@link Block}.
         *
         * @return {@code this} (builder method).
         *
         * @see #withSetTags(List)
         */
        public BPWBuilder withTags(List<Supplier<TagKey<?>>> parentBlockTags) {
            this.parentTags.addAll(parentBlockTags);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<?> Tags}. Overwrites the
         * existing list.
         *
         * @param parentBlockTags The {@linkplain TagKey<?> TagKeys} with which this BPW's parent {@link Block} will be
         *                        tagged. May generally be of types {@link Item} or {@link Block}.
         *
         * @return {@code this} (builder method).
         *
         * @see #withTags(List)
         */
        public BPWBuilder withSetTags(List<Supplier<TagKey<?>>> parentBlockTags) {
            this.parentTags.clear();
            this.parentTags.addAll(parentBlockTags);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@link TagKey<Block>}. Useful for bypassing
         * Java's generic type inference.
         *
         * @param parentBlockTag The {@link TagKey<Block>} with which this BPW's parent {@link Block} will be tagged.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withBlockTag(Supplier<TagKey<Block>> parentBlockTag) {
            this.blockTags.add(parentBlockTag);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<Block> Tags}. Appends to the
         * existing list. Useful for bypassing Java's generic type inference.
         *
         * @param parentBlockTags The {@linkplain TagKey<Block> TagKeys} with which this BPW's parent {@link Block} will
         *                        be tagged.
         *
         * @return {@code this} (builder method).
         *
         * @see #withSetBlockTags(List)
         */
        public BPWBuilder withBlockTags(List<Supplier<TagKey<Block>>> parentBlockTags) {
            this.blockTags.addAll(parentBlockTags);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<Block> Tags}. Overwrites the
         * existing list. Useful for bypassing Java's generic type inference.
         *
         * @param parentBlockTags The {@linkplain TagKey<Block> TagKeys} with which this BPW's parent {@link Block} will
         *                        be tagged.
         *
         * @return {@code this} (builder method).
         *
         * @see #withBlockTags(List)
         */
        public BPWBuilder withSetBlockTags(List<Supplier<TagKey<Block>>> parentBlockTags) {
            this.blockTags.clear();
            this.blockTags.addAll(parentBlockTags);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@link TagKey<Item>}. Useful for bypassing
         * Java's generic type inference.
         *
         * @param parentItemTag The {@link TagKey<Item>} with which this BPW's parent {@link Block} will be tagged.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withItemTag(Supplier<TagKey<Item>> parentItemTag) {
            this.itemTags.add(parentItemTag);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<Item> Tags}. Appends to the
         * existing list. Useful for bypassing Java's generic type inference.
         *
         * @param parentItemTags The {@linkplain TagKey<Item> TagKeys} with which this BPW's parent {@link Block} will
         *                       be tagged.
         *
         * @return {@code this} (builder method).
         *
         * @see #withSetItemTags(List)
         */
        public BPWBuilder withItemTags(List<Supplier<TagKey<Item>>> parentItemTags) {
            this.itemTags.addAll(parentItemTags);
            return this;
        }

        /**
         * Tags this BlockPropertyWrapperBuilder's parent {@link Block} with the provided {@linkplain TagKey<Item> Tags}. Overwrites the
         * existing list. Useful for bypassing Java's generic type inference.
         *
         * @param parentItemTags The {@linkplain TagKey<Item> TagKeys} with which this BPW's parent {@link Block} will
         *                       be tagged.
         *
         * @return {@code this} (builder method).
         *
         * @see #withItemTags(List)
         */
        public BPWBuilder withSetItemTags(List<Supplier<TagKey<Item>>> parentItemTags) {
            this.itemTags.clear();
            this.itemTags.addAll(parentItemTags);
            return this;
        }

        /**
         * Sets the {@link #bmdMappingFunc} of this BlockPropertyWrapperBuilder. This is used in order to generate models for the parent
         * BPW's {@link Block}.
         *
         * @param bmdMappingFunc The mapping {@link Function} used to build this BlockPropertyWrapperBuilder's parent block's model in
         *                       datagen.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withCustomModelDefinitions(Function<Supplier<Block>, List<BlockModelDefinition>> bmdMappingFunc) {
            this.bmdMappingFunc = bmdMappingFunc;
            return this;
        }

        /**
         * Defines a custom {@link BlockStateDefinition} mapping function to this builder. By default, blockstate datagen
         * is handled based on a series of type checks (E.G. Doors, walls, fences, rotatable blocks, etc.). You can use
         * this method if your custom block requires a different blockstate definition that isn't natively handled.
         *
         * @param bsdMappingFunction The {@link BlockStateDefinition} mapping function used to build this BlockPropertyWrapperBuilder's
         *                           parent block's blockstate in datagen.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withBlockStateDefinition(Function<Supplier<Block>, BlockStateDefinition> bsdMappingFunction) {
            this.blockStateDefinition = bsdMappingFunction;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} recipe. BPWBuilders
         * accepting more than 1 recipe function assume that each recipe has a unique recipe ID, and thus recipes are
         * generated under that constraint.
         *
         * @param recipeBuilderFunction The mapping function accepting a representation of the parent
         *                              {@linkplain Block Block's} recipe. Input is the recipe, output is the parent BPW's
         *                              {@link Block}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withRecipe(Function<Consumer<FinishedRecipe>, Consumer<Supplier<Block>>> recipeBuilderFunction) {
            this.recipeBuilderFunction = recipeBuilderFunction;
            return this;
        }

        /**
         * Appends a parent {@link CreativeModeTab} for the parent BPW's {@link Block} to show up in.
         *
         * @param parentTab The {@link CreativeModeTab} under which the parent BPW's {@link Block} will be listed/show
         *                  up.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withParentCreativeModeTab(Supplier<CreativeModeTab> parentTab) {
            this.parentTabs.add(parentTab);
            return this;
        }

        /**
         * Appends a {@link List} of parent {@linkplain CreativeModeTab CreativeModeTabs} for the parent BPW's
         * {@link Block} to show up in.
         *
         * @param parentTabs A {@link List} of {@linkplain CreativeModeTab CreativeModeTabs} under which the parent
         *                   BPW's {@link Block} will be listed/show up.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withParentCreativeModeTabs(List<Supplier<CreativeModeTab>> parentTabs) {
            this.parentTabs.addAll(parentTabs);
            return this;
        }

        /**
         * Sets (does NOT append) a {@link List} of parent {@linkplain CreativeModeTab CreativeModeTabs} for the parent
         * BPW's {@link Block} to show up in.
         *
         * @param parentTabs A {@link List} of {@linkplain CreativeModeTab CreativeModeTabs} under which the parent
         *                   BPW's {@link Block} will be listed/show up.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withSetParentCreativeModeTabs(List<Supplier<CreativeModeTab>> parentTabs) {
            this.parentTabs.clear();
            this.parentTabs.addAll(parentTabs);
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional {@link WrappedBlockColor}.
         *
         * @param blockColorMappingFunc The mapping function accepting a representation of the parent
         *                              {@linkplain Block Block's} optional {@link WrappedBlockColor}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withBlockColor(Function<Supplier<Block>, WrappedBlockColor> blockColorMappingFunc) {
            this.blockColorMappingFunc = blockColorMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional flammability
         * settings.
         *
         * @param flammabilityMappingFunc The mapping function accepting a representation of the parent
         *                                {@linkplain Block Block's} optional flammability settings, with the
         *                                {@link IntIntMutablePair} representing the burn time (in ticks) and spread
         *                                respectively.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withFlammability(Function<Supplier<Block>, IntIntMutablePair> flammabilityMappingFunc) {
            this.flammabilityMappingFunc = flammabilityMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional axe stripping
         * to another {@link Block}.
         *
         * @param blockStrippingMappingFunc The mapping function accepting a representation of the
         *                                  parent {@linkplain Block Block's} optional axe stripping to another
         *                                  {@link Block}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withAxeStripping(Function<Supplier<Block>, Supplier<Block>> blockStrippingMappingFunc) {
            this.blockStrippingMappingFunc = blockStrippingMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional hoe tilling,
         * applying the behaviour specified by the output {@link ObjectObjectMutablePair}.
         *
         * @param blockTillingMappingFunc The mapping function accepting a representation of the parent
         *                                {@linkplain Block Block's} optional hoe tilling, applying the behaviour
         *                                specified by the output {@link ObjectObjectMutablePair}. The
         *                                {@code Predicate<UseOnContext>} effectively determines whether the supplied
         *                                {@link Block} can be tilled, and the {@code Consumer<UseOnContext>} applies
         *                                the tilling behaviour if that predicate returns {@code true}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withHoeTilling(Function<Supplier<Block>, ObjectObjectMutablePair<Predicate<UseOnContext>, Consumer<UseOnContext>>> blockTillingMappingFunc) {
            this.blockTillingMappingFunc = blockTillingMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional shovel
         * flattening to another {@link BlockState}.
         *
         * @param blockFlatteningMappingFunc The mapping function accepting a representation of the parent
         *                                   {@linkplain Block Block's} optional shovel flattening to another
         *                                   {@link BlockState}.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withShovelFlattening(Function<Supplier<Block>, BlockState> blockFlatteningMappingFunc) {
            this.blockFlatteningMappingFunc = blockFlatteningMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional oxidation to
         * another {@link Block}.
         *
         * @param blockOxidizationMappingFunc The mapping function accepting a representation of the parent
         *                                    {@linkplain Block Block's} optional oxidation to another {@link Block},
         *                                    like the copper block's oxidization over time if not waxed.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder withOxidization(Function<Supplier<Block>, Supplier<Block>> blockOxidizationMappingFunc) {
            this.blockOxidizationMappingFunc = blockOxidizationMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional waxing to
         * another {@link Block}.
         *
         * @param blockWaxingMappingFunc The mapping function accepting a representation of the parent
         *                               {@linkplain Block Block's} optional waxing to another {@link Block}, like the
         *                               copper block getting waxed, using honey combs.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder asWaxable(Function<Supplier<Block>, Supplier<Block>> blockWaxingMappingFunc) {
            this.blockWaxingMappingFunc = blockWaxingMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional composting
         * chances.
         *
         * @param blockCompostMappingFunc The mapping function accepting a representation of the parent
         *                                {@linkplain Block Block's} composting to another {@link Block}. The output
         *                                {@link Float} value in the provided {@link Function} represents composting
         *                                chance. {@code null}/0 values are ignored. Negative values are abs'd.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder asCompostable(Function<Supplier<Block>, Float> blockCompostMappingFunc) {
            this.blockCompostingMappingFunc = blockCompostMappingFunc;
            return this;
        }

        /**
         * Defines a custom mapping function representing the parent {@linkplain Block Block's} optional fuel value.
         *
         * @param fuelMappingFunc The mapping function accepting a representation of the parent {@linkplain Block Block's}
         *                        optional cook time value, in ticks. {@code null}/0 values are treated as none. Negative
         *                        values are abs'd.
         *
         * @return {@code this} (builder method).
         */
        public BPWBuilder asFuel(Function<Supplier<Block>, Integer> fuelMappingFunc) {
            this.blockFuelMappingFunc = fuelMappingFunc;
            return this;
        }

        /**
         * Determines whether this BlockPropertyWrapperBuilder instance should be entirely excluded from Nexus' native datagen.
         * <br></br>
         * Fundamentally, all this does is flag this instance as not needing a data entry to be mapped to it. You may
         * choose to generate data for it yourself if needed, since Nexus won't handle datagen for this particular object.
         * <br></br>
         * If a block-specific data provider has {@link ModDataProvider#validateAllEntries()} set to {@code true}, this
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
        public BPWBuilder excludeFromNativeDatagen(boolean excludeFromNativeDatagen) {
            this.excludeFromNativeDatagen = excludeFromNativeDatagen;
            return this;
        }

        /**
         * Determines whether this BlockPropertyWrapperBuilder instance is required to generate necessary block-related data based on the
         * {@link ProviderType} passed in.
         * <br></br>
         * By default, unmapped providers will not require an entry for this BlockPropertyWrapperBuilder to be generated unless
         * {@link ModDataProvider#validateAllEntries()} is set to {@code true}.
         * <br></br>
         * Mapping the related provider passed in here to {@code requiresDatagenEntry}, set to {@code true}, will flag
         * this BlockPropertyWrapperBuilder instance for requiring related data regardless of what
         * {@link ModDataProvider#validateAllEntries()} is set to.
         *
         * @param targetProviderType The {@link ProviderType} to modify the data entry requirement for.
         * @param requiresDatagenEntry Whether this BlockPropertyWrapperBuilder should require data related to the specified
         *                             {@code targetProviderType} to be present.
         *
         * @return {@code this} (builder method).
         *
         * @see #requiresDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(List, boolean)
         * @see #requiresSetDatagenEntries(Map)
         * @see #excludeFromNativeDatagen(boolean)
         */
        public BPWBuilder requiresDatagenEntry(ProviderType targetProviderType, boolean requiresDatagenEntry) {
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
        public BPWBuilder requiresDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
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
        public BPWBuilder requiresSetDatagenEntries(List<ProviderType> targetProviderTypes, boolean requiresDatagenEntry) {
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
        public BPWBuilder requiresSetDatagenEntries(Map<ProviderType, Boolean> mappedProviderRequisites) {
            this.mappedProviderRequisites.clear();
            this.mappedProviderRequisites.putAll(mappedProviderRequisites);
            return this;
        }

        /**
         * Builds a new {@link BlockPropertyWrapper} using this builder's data. Also maps the owner
         * {@link BlockPropertyWrapper} to the parent {@linkplain Block} if the owner is not a template.
         *
         * @return The newly data-populated {@link BlockPropertyWrapper}.
         *
         * @see BlockPropertyWrapper#isTemplate()
         */
        public BlockPropertyWrapper build() {
            if (!ownerWrapper.isTemplate) MAPPED_BPWS.putIfAbsent(ownerWrapper.blockRegName == null ? ownerWrapper.parentBlock : () -> BuiltInRegistries.BLOCK.get(ownerWrapper.blockRegName), ownerWrapper);
            return ownerWrapper;
        }
    }
}
