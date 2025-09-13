package com.mememan.nexus.mixins.fabric_api.datagen;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.datagen.ModDatagenConfig;
import com.mememan.nexus.platform.NexusServices;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

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

    @Definition(id = "id", local = @Local(type = String.class))
    @Definition(id = "equals", method = "Ljava/lang/String;equals(Ljava/lang/Object;)Z")
    @Definition(id = "MOD_ID_FILTER", field = "Lnet/fabricmc/fabric/impl/datagen/FabricDataGenHelper;MOD_ID_FILTER:Ljava/lang/String;")
    @Expression("id.equals(MOD_ID_FILTER)")
    @ModifyExpressionValue(method = "runInternal", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean nexus$generateDataFromNexus(boolean original, @Local(name = "id") String modId) {
        return original || (Objects.equals(modId, NexusConstants.MOD_ID) && !NexusServices.DATA_GENERATOR.getModDatagenConfigs().isEmpty());
    }
}
