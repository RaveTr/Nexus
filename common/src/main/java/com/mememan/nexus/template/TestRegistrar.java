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
    public static final WoodenBlockGroup APPLE = RegistryUtil.registerStandardWoodFamily(NexusConstants.prefix("apple"), APPLE_LOGS, null, BLOCKS, null, null);

    @DatagenRegistrarEntry
    public static class Data {
        public static final ModDatagenConfig CONFIG = NexusServices.DATA_GENERATOR.registerConfigForMod(ModDatagenConfig.defaultConfig(NexusConstants.MOD_ID));
    }
}
