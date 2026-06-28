package com.mememan.nexus.template.object.attribute;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.RegistrarEntry;
import com.mememan.nexus.platform.NexusServices;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

/**
 * Registry {@code class} containing some common attribute definitions for use by dependant mods. Some of these entries
 * gracefully remap to their loader-specific equivalents, if any, to ensure proper compatibility and deterministic
 * behaviour.
 *
 * @apiNote The "graceful registration" behaviour isn't included as an API feature atm due to the indeterministic
 * nature of performing such an operation (i.e. substituting a {@code static} field or assigning it some other registry
 * value).
 * <br></br>
 * More specifically: we're relying on the assumption here that this {@code class} will not be statically-initialized
 * early enough to cause any issues. This assumption holds true as long as end-developers do not reference anything in
 * this {@code class} before registries are done being populated, which in and of itself is an unnatural and effectively
 * pointless thing to do in the first place.
 * <br></br>
 * This may later be introduced in some form as a convenient shortcut, but as it stands, it remains an internal library
 * mechanism for now.
 */
@RegistrarEntry(priority = -1000)
public final class NexusAttributes {

    public static final Supplier<Attribute> BLOCK_REACH = gracefullyRegisterAttribute(
            NexusConstants.prefix("block_reach"),
            () -> new RangedAttribute(String.format("attribute.name.generic.%s.block_reach", NexusConstants.MOD_ID), 4.5D, 0.0D, 1024.0D).setSyncable(true),
            new ResourceLocation("forge", "block_reach"),
            new ResourceLocation("reach-entity-attributes", "reach")
    );
    public static final Supplier<Attribute> ENTITY_REACH = gracefullyRegisterAttribute(
            NexusConstants.prefix("entity_reach"),
            () -> new RangedAttribute(String.format("attribute.name.generic.%s.entity_reach", NexusConstants.MOD_ID), 3.0D, 0.0D, 1024.0D).setSyncable(true),
            new ResourceLocation("forge", "entity_reach"),
            new ResourceLocation("reach-entity-attributes", "attack_range")
    );

    private static <A extends Attribute> Supplier<A> gracefullyRegisterAttribute(ResourceLocation id, Supplier<A> attributeSup, ResourceLocation... potentialFallbackIds) {
        Supplier<A> registeredAttrSup = NexusServices.REGISTRAR.registerObject(id, attributeSup, BuiltInRegistries.ATTRIBUTE);

        return () -> {
            for (ResourceLocation fallbackId : potentialFallbackIds) {
                if (BuiltInRegistries.ATTRIBUTE.containsKey(fallbackId)) return (A) BuiltInRegistries.ATTRIBUTE.get(fallbackId);
            }

            return registeredAttrSup.get();
        };
    }
}
