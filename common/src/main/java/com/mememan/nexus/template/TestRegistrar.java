package com.mememan.nexus.template;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.DatagenRegistrarEntry;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;
import com.mememan.nexus.template.object.block.misc.WoodenBlockGroup;
import com.mememan.nexus.template.property_wrapper.TagPropertyWrapperTemplates;
import com.mememan.nexus.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

@RegistrarEntry
public class TestRegistrar {
    static final ObjectArrayList<Supplier<Block>> BLOCKS = new ObjectArrayList<>();

    public static final Supplier<TagKey<Item>> APPLE_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("apple_logs"));
    public static final Supplier<TagKey<Item>> SKYWOOD_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("skywood_logs"));
    public static final Supplier<TagKey<Item>> ELM_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("elm_logs"));
    public static final Supplier<TagKey<Item>> ASH_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("ash_logs"));
    public static final Supplier<TagKey<Item>> WILLOW_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("willow_logs"));
    public static final Supplier<TagKey<Item>> PINE_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("pine_logs"));
    public static final Supplier<TagKey<Item>> MAPLE_LOGS = TagPropertyWrapperTemplates.registerTagKey(Registries.ITEM, NexusConstants.prefix("maple_logs"));

    public static final WoodenBlockGroup APPLE = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("apple"), APPLE_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup SKYWOOD = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("skywood"), SKYWOOD_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup ELM = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("elm"), ELM_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup ASH = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("ash"), ASH_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup WILLOW = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("willow"), WILLOW_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup PINE = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("pine"), PINE_LOGS, null, BLOCKS, null, null);
    public static final WoodenBlockGroup MAPLE = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("maple"), MAPLE_LOGS, null, BLOCKS, null, null);

    @DatagenRegistrarEntry
    public static class Data {
        public static final ModDatagenConfig CONFIG = NexusServices.DATA_GENERATOR.registerConfigForMod(ModDatagenConfig.defaultConfig(NexusConstants.MOD_ID));
    }
}
