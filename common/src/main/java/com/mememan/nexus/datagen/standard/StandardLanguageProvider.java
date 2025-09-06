package com.mememan.nexus.datagen.standard;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.property_wrapper.base.generic.PropertyWrapper;
import com.mememan.nexus.property_wrapper.base.specialised.language.LanguageBasedPropertyWrapper;
import com.mememan.nexus.util.StringUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Standard loader-agnostic mod-specific language provider in Nexus API. Instanced based on the provided mod ID. Handles
 * localization of all different object types whose property wrappers implement {@link LanguageBasedPropertyWrapper}.
 *
 * @apiNote As it currently stands, this provider only supports {@code "en_us"} localization. Support for other locales
 * will be added sometime in the future.
 */
public class StandardLanguageProvider implements ModDataProvider {
    protected final Object2ObjectRBTreeMap<String, String> localizationEntries = new Object2ObjectRBTreeMap<>();
    protected final PackOutput output;
    protected final String modId;
    protected final String locale;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final Path outputPath;
    protected final List<? extends LanguageBasedPropertyWrapper<?, ?, ?>> mappedLanguagePWs;

    public StandardLanguageProvider(PackOutput output, String modId, String locale, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        this.output = output;
        this.modId = modId;
        this.locale = locale;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.outputPath = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modId).resolve("lang").resolve(locale + ".json");
        this.mappedLanguagePWs = PropertyWrapper.PropertyWrappersContainer.getInferrableWrappersOfType(LanguageBasedPropertyWrapper.class, modId);
    }

    /**
     * Backing method responsible for populating {@link #localizationEntries} with translations from
     * {@link #mappedLanguagePWs}, if applicable. Handles missing translation entries appropriately.
     */
    protected void addTranslations() {
        if (!mappedLanguagePWs.isEmpty()) {
            mappedLanguagePWs.forEach(curPW -> {
                Optional<String> localizedValue = curPW.getLocalizedObjectKey((locVal, postMappedVal) -> NexusConstants.LOGGER.debug("[{}] [Applying Post-Translation Mapping for {}]: '{}' -> '{}' -> '{}'", modId, curPW.getParentObject().get().getClass().getSimpleName(), curPW.getObjectDescriptionId(), locVal, postMappedVal));
                String objectClassName = curPW.getParentObject().get().getClass().getSimpleName();

                localizedValue.ifPresentOrElse(locVal -> {
                    String unlocalizedKey = curPW.getObjectDescriptionId();

                    NexusConstants.LOGGER.debug("[{}] [Generating Translation for {}]: '{}' -> '{}'", modId, objectClassName, unlocalizedKey, locVal);

                    add(unlocalizedKey, locVal);
                }, () -> {
                    if (validateAllEntries() || curPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                        throw new NullPointerException(String.format("Missing localized key for %s: %s, required by mod: %s, either because validateAllEntries is set to true for this provider or the object itself requires validation through DataGenBasedPropertyWrapper#getProviderTypeRequisites().", objectClassName, curPW.getObjectDescriptionId(), modId));
                    }
                });

                Map<String, Function<String, String>> additionalAssociatedTranslations = curPW.getAdditionalLocalizationKeys();

                if (!additionalAssociatedTranslations.isEmpty()) {
                    additionalAssociatedTranslations.forEach((unlocalizedKey, customTranslationMapper) -> {
                        String localizedAdditionalValue = customTranslationMapper == null
                                ? StringUtil.literallyLocalize(unlocalizedKey, curPW.getCustomSeparatorWords())
                                : customTranslationMapper.apply(unlocalizedKey);

                        NexusConstants.LOGGER.debug("[{}] [Generating Translation for Additional Key Associated with {}]: '{}' -> '{}'", modId, objectClassName, unlocalizedKey, localizedAdditionalValue);

                        add(unlocalizedKey, localizedAdditionalValue);
                    });
                }
            });
        }
    }

    /**
     * Handles populating the language provider with translations, then directly serializing the newly-filled {@link Map}
     * to JSON.
     *
     * @param cachedOutput The {@link CachedOutput} instance to use for saving generated data to disk.
     *
     * @return {@link DataProvider#saveStable(CachedOutput, JsonElement, Path)} if the {@link Map} is not empty, otherwise
     * returns an empty {@link CompletableFuture#allOf(CompletableFuture[])}.
     */
    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput cachedOutput) {
        addTranslations();

        if (!localizationEntries.isEmpty()) { // Effectively taken from Forge (well, rest of the provider is rewritten to fit our purposes in this case)
            JsonObject targetJson = new JsonObject();

            localizationEntries.forEach(targetJson::addProperty);

            return DataProvider.saveStable(cachedOutput, targetJson, outputPath);
        }

        return CompletableFuture.allOf();
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public @NotNull String getName() {
        return String.format("Language [%s] [%s]", getLocale(), getModId());
    }

    @Override
    public boolean validateAllEntries() {
        return validateAllEntries;
    }

    @Override
    public @NotNull ProviderType getProviderType() {
        return NexusProviderTypes.LANGUAGE_PROVIDER;
    }

    @Override
    public @NotNull DuplicateDataPolicy getDuplicateDataPolicy() {
        return dupeStrat;
    }

    public String getLocale() {
        return locale;
    }

    public void addBlock(Supplier<? extends Block> targetBlockSup, String localizedBlockName) {
        addBlock(targetBlockSup.get(), localizedBlockName);
    }

    public void addBlock(Block targetBlock, String localizedBlockName) {
        add(targetBlock.getDescriptionId(), localizedBlockName);
    }

    public void addEnchantment(Supplier<? extends Enchantment> targetEnchantmentSup, String localizedEnchantmentName) {
        addEnchantment(targetEnchantmentSup.get(), localizedEnchantmentName);
    }

    public void addEnchantment(Enchantment targetEnchantment, String localizedEnchantmentName) {
        add(targetEnchantment.getDescriptionId(), localizedEnchantmentName);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> targetEntityTypeSup, String localizedEntityTypeName) {
        addEntityType(targetEntityTypeSup.get(), localizedEntityTypeName);
    }

    public void addEntityType(EntityType<?> targetEntityType, String localizedEntityTypeName) {
        add(targetEntityType.getDescriptionId(), localizedEntityTypeName);
    }

    public void addItem(Supplier<? extends Item> targetItemSup, String localizedItemName) {
        addItem(targetItemSup.get(), localizedItemName);
    }

    public void addItem(Item targetItem, String localizedItemName) {
        add(targetItem.getDescriptionId(), localizedItemName);
    }

    public void addMobEffect(Supplier<? extends MobEffect> targetMobEffectSup, String localizedMobEffectName) {
        addMobEffect(targetMobEffectSup.get(), localizedMobEffectName);
    }

    public void addMobEffect(MobEffect targetMobEffect, String localizedMobEffectName) {
        add(targetMobEffect.getDescriptionId(), localizedMobEffectName);
    }

    /**
     * Adds a translation entry to {@link #localizationEntries}. Handles duplicate key resolution based on the
     * specified {@link #dupeStrat}.
     *
     * @param unlocalizedKey The unlocalized key to add.
     * @param localizedValue The corresponding localized value to add.
     */
    public void add(String unlocalizedKey, String localizedValue) {
        boolean isAlreadyMapped = localizationEntries.containsKey(unlocalizedKey);

        if (isAlreadyMapped) {
            switch (getDuplicateDataPolicy()) {
                case CRASH -> throw new IllegalStateException(String.format("Attempted to localize duplicate translation key (original: %s -> %s | duplicate: %s -> %s) from mod of ID %s, specified DuplicateDataPolicy is CRASH.", unlocalizedKey, localizationEntries.get(unlocalizedKey), unlocalizedKey, localizedValue, getModId()));
                case EXCLUDE_WARN -> NexusConstants.LOGGER.warn("Attempted to localize duplicate translation key (original: {} -> {} | duplicate: {} -> {}) from mod of ID {}, specified DuplicateDataPolicy is EXCLUDE_WARN. Skipping...", unlocalizedKey, localizationEntries.get(unlocalizedKey), unlocalizedKey, localizedValue, getModId());
                case EXCLUDE_SILENT -> {}
                case OVERRIDE_WARN -> {
                    NexusConstants.LOGGER.warn("Overriding duplicate translation key (original: {} -> {} | duplicate (new): {} -> {}) from mod of ID {}, specified DuplicateDataPolicy is OVERRIDE_WARN.", unlocalizedKey, localizationEntries.get(unlocalizedKey), unlocalizedKey, localizedValue, getModId());

                    localizationEntries.put(unlocalizedKey, localizedValue);
                }
                case OVERRIDE_SILENT -> localizationEntries.put(unlocalizedKey, localizedValue);
            }
        } else localizationEntries.put(unlocalizedKey, localizedValue);
    }
}
