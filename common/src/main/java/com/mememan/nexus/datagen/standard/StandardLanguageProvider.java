package com.mememan.nexus.datagen.standard;

import com.google.gson.JsonObject;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.block.standard.BlockPropertyWrapper;
import com.mememan.nexus.datagen.DuplicateDataPolicy;
import com.mememan.nexus.datagen.NexusProviderTypes;
import com.mememan.nexus.datagen.ProviderType;
import com.mememan.nexus.enchantment.standard.EnchantmentPropertyWrapper;
import com.mememan.nexus.entity.standard.EntityTypePropertyWrapper;
import com.mememan.nexus.item.standard.ItemPropertyWrapper;
import com.mememan.nexus.mob_effect.standard.MobEffectPropertyWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.core.registries.BuiltInRegistries;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class StandardLanguageProvider implements ModDataProvider {
    protected final Object2ObjectRBTreeMap<String, String> localizationEntries = new Object2ObjectRBTreeMap<>();
    protected final PackOutput output;
    protected final String modId;
    protected final String locale;
    protected final boolean validateAllEntries;
    protected final DuplicateDataPolicy dupeStrat;
    protected final Path outputPath;
    protected final Object2ObjectOpenHashMap<Supplier<Block>, BlockPropertyWrapper> mappedModBPWs;
    protected final Object2ObjectOpenHashMap<Supplier<Item>, ItemPropertyWrapper> mappedModIPWs;
    protected final Object2ObjectOpenHashMap<Supplier<? extends EntityType<?>>, EntityTypePropertyWrapper<?>> mappedModETPWs;
    protected final Object2ObjectOpenHashMap<Supplier<Enchantment>, EnchantmentPropertyWrapper> mappedModEPWs;
    protected final Object2ObjectOpenHashMap<Supplier<MobEffect>, MobEffectPropertyWrapper> mappedModMEPWs;

    public StandardLanguageProvider(PackOutput output, String modId, String locale, boolean validateAllEntries, DuplicateDataPolicy dupeStrat) {
        this.output = output;
        this.modId = modId;
        this.locale = locale;
        this.validateAllEntries = validateAllEntries;
        this.dupeStrat = dupeStrat;

        this.outputPath = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modId).resolve("lang").resolve(locale + ".json");

        //TODO Probably refactor this sometime down the road (Seriously, ts is so painful to look at 🥀)
        this.mappedModBPWs = BlockPropertyWrapper.getMappedBpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.BLOCK.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .peek(curEntry -> {
                    BlockPropertyWrapper curBPW = curEntry.getValue();

                    if (validateAllEntries() || curBPW.getProviderTypeRequisites().getOrDefault(NexusProviderTypes.LANGUAGE_PROVIDER, false)) {
                        if (curBPW.bypassDefaultTranslation() && (curBPW.getManuallyLocalizedBlockName() == null || curBPW.getManuallyLocalizedBlockName().isEmpty())) {
                            throw new NullPointerException(String.format("Missing %s locale entry for block %s, required by mod: %s, either because validateAllEntries is set to true or the block itself requires validation through BlockPropertyWrapper#getProviderTypeRequisites() (BlockPropertyWrapper#bypassDefaultTranslation() is set to true, but BlockPropertyWrapper#getManuallyLocalizedBlockName() is %s)", locale, curEntry.getKey().get().getDescriptionId(), modId, curBPW.getManuallyLocalizedBlockName() == null ? "null" : "empty"));
                        }
                    }
                })
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
        this.mappedModIPWs = ItemPropertyWrapper.getMappedIpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.ITEM.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .peek(curEntry -> {
                    ItemPropertyWrapper curIPW = curEntry.getValue();

                    if (validateAllEntries() || curIPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                        if (curIPW.bypassDefaultTranslation() && (curIPW.getManuallyLocalizedItemName() == null || curIPW.getManuallyLocalizedItemName().isEmpty())) {
                            throw new NullPointerException(String.format("Missing %s locale entry for item %s, required by mod: %s, either because validateAllEntries is set to true or the item itself requires validation through ItemPropertyWrapper#getProviderTypeRequisites() (ItemPropertyWrapper#bypassDefaultTranslation() is set to true, but ItemPropertyWrapper#getManuallyLocalizedItemName() is %s)", locale, curEntry.getKey().get().getDescriptionId(), modId, curIPW.getManuallyLocalizedItemName() == null ? "null" : "empty"));
                        }
                    }
                })
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
        this.mappedModETPWs = EntityTypePropertyWrapper.getMappedEtpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.ENTITY_TYPE.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .peek(curEntry -> {
                    EntityTypePropertyWrapper<?> curETPW = curEntry.getValue();

                    if (validateAllEntries() || curETPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                        if (curETPW.bypassDefaultTranslation() && (curETPW.getManuallyLocalizedEntityTypeName() == null || curETPW.getManuallyLocalizedEntityTypeName().isEmpty())) {
                            throw new NullPointerException(String.format("Missing %s locale entry for entity type %s, required by mod: %s, either because validateAllEntries is set to true or the entity type itself requires validation through EntityTypePropertyWrapper#getProviderTypeRequisites() (EntityTypePropertyWrapper#bypassDefaultTranslation() is set to true, but EntityTypePropertyWrapper#getManuallyLocalizedEntityTypeName() is %s)", locale, curEntry.getKey().get().getDescriptionId(), modId, curETPW.getManuallyLocalizedEntityTypeName() == null ? "null" : "empty"));
                        }
                    }
                })
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
        this.mappedModEPWs = EnchantmentPropertyWrapper.getMappedEpws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.ENCHANTMENT.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .peek(curEntry -> {
                    EnchantmentPropertyWrapper curEPW = curEntry.getValue();

                    if (validateAllEntries() || curEPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                        if (curEPW.bypassDefaultTranslation() && (curEPW.getManuallyLocalizedEnchantmentName() == null || curEPW.getManuallyLocalizedEnchantmentName().isEmpty())) {
                            throw new NullPointerException(String.format("Missing %s locale entry for enchantment %s, required by mod: %s, either because validateAllEntries is set to true or the enchantment itself requires validation through EnchantmentPropertyWrapper#getProviderTypeRequisites() (EnchantmentPropertyWrapper#bypassDefaultTranslation() is set to true, but EnchantmentPropertyWrapper#getManuallyLocalizedEnchantmentName() is %s)", locale, curEntry.getKey().get().getDescriptionId(), modId, curEPW.getManuallyLocalizedEnchantmentName() == null ? "null" : "empty"));
                        }
                    }
                })
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
        this.mappedModMEPWs = MobEffectPropertyWrapper.getMappedMepws().entrySet()
                .stream()
                .filter(curEntry -> BuiltInRegistries.MOB_EFFECT.getKey(curEntry.getKey().get()).getNamespace().equals(modId))
                .filter(curEntry -> !curEntry.getValue().excludeFromNativeDatagen())
                .peek(curEntry -> {
                    MobEffectPropertyWrapper curMEPW = curEntry.getValue();

                    if (validateAllEntries() || curMEPW.getProviderTypeRequisites().getOrDefault(getProviderType(), false)) {
                        if (curMEPW.bypassDefaultTranslation() && (curMEPW.getManuallyLocalizedMobEffectName() == null || curMEPW.getManuallyLocalizedMobEffectName().isEmpty())) {
                            throw new NullPointerException(String.format("Missing %s locale entry for mob effect %s, required by mod: %s, either because validateAllEntries is set to true or the mob effect itself requires validation through MobEffectPropertyWrapper#getProviderTypeRequisites() (MobEffectPropertyWrapper#bypassDefaultTranslation() is set to true, but MobEffectPropertyWrapper#getManuallyLocalizedMobEffectName() is %s)", locale, curEntry.getKey().get().getDescriptionId(), modId, curMEPW.getManuallyLocalizedMobEffectName() == null ? "null" : "empty"));
                        }
                    }
                })
                .collect(Object2ObjectOpenHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
    }

    protected void addTranslations() {
        // Native types
        handleBlockTranslations();
        handleEnchantmentTranslations();
        handleEntityTypeTranslations();
        handleItemTranslations();
        handleMobEffectTranslations();

        // Misc. types
        
    }

    protected void handleBlockTranslations() {

    }

    protected void handleEnchantmentTranslations() {

    }

    protected void handleEntityTypeTranslations() {

    }

    protected void handleItemTranslations() {

    }

    protected void handleMobEffectTranslations() {

    }

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
