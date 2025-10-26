package com.mememan.nexus.mixins.forge.datagen;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.platform.NexusServices;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

/**
 * Mixin {@code class} responsible for discreetly running Nexus API's data gatherer without necessarily appending it to
 * the same generator as its dependant mod.
 * <br></br>
 * In essence, it's running Nexus' data gatherer on its own alongside whatever mod(s) depend(s) on it without flipping
 * {@link GatherDataEvent.DataGeneratorConfig#isFlat()}, though it handles that edge case correctly as well (why would a
 * developer even want to run several data gatherers from external dependencies within their workspace?).
 *
 * @see NexusServices#DATA_GENERATOR
 */
@Mixin(value = DatagenModLoader.class, remap = false)
public abstract class DatagenModLoaderMixin {

    private DatagenModLoaderMixin() {
        throw new IllegalAccessError("Attempted to construct Mixin Class! (DatagenModLoaderMixin)");
    }

    @Definition(id = "dataGeneratorConfig", field = "Lnet/minecraftforge/data/loading/DatagenModLoader;dataGeneratorConfig:Lnet/minecraftforge/data/event/GatherDataEvent$DataGeneratorConfig;")
    @Definition(id = "getMods", method = "Lnet/minecraftforge/data/event/GatherDataEvent$DataGeneratorConfig;getMods()Ljava/util/Set;")
    @Definition(id = "contains", method = "Ljava/util/Set;contains(Ljava/lang/Object;)Z")
    @Definition(id = "mc", local = @Local(type = ModContainer.class, argsOnly = true))
    @Definition(id = "getModId", method = "Lnet/minecraftforge/fml/ModContainer;getModId()Ljava/lang/String;")
    @Expression("dataGeneratorConfig.getMods().contains(mc.getModId())")
    @ModifyExpressionValue(method = "lambda$begin$2", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean nexus$generateDataFromNexus(boolean original, ModContainer mc) {
        return original || (Objects.equals(mc.getModId(), NexusConstants.MOD_ID) && !NexusServices.DATA_GENERATOR.getModDatagenConfigs().isEmpty());
    }
}
