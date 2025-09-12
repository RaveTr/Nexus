package com.mememan.nexus.mixins.fabric_api.datagen;

import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin {@code class} that allows Nexus API to bypass Fabric's default mod ID filter for creating and running data
 * generators from entrypoints, allowing dependant mods to let Nexus do all the heavy lifting.
 * <br></br>
 * Additionally, helps improves determinism since you can still stop Nexus from running entirely via {@link ModDatagenConfig}
 * if needed.
 *
 * @see FabricDataGenHelper#MOD_ID_FILTER
 * @see NexusServices#DATA_GENERATOR
 */
@Mixin(value = FabricDataGenHelper.class, remap = false)
public abstract class FabricDataGenHelperMixin {

    private FabricDataGenHelperMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (FabricDataGenHelperMixin)");
    }

    private static void nexus$generateDataFromNexus() {

    }
}
