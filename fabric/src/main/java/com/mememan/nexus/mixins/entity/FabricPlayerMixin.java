package com.mememan.nexus.mixins.entity;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mememan.nexus.mixins.NexusFabricMixinConfigPlugin;
import com.mememan.nexus.template.object.attribute.NexusAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin {@code class} responsible for adding Nexus' fallback attributes to players, in case they don't already have
 * any loader/api-substituted variants.
 *
 * @see NexusAttributes
 * @see NexusFabricMixinConfigPlugin
 */
@Mixin(Player.class)
public abstract class FabricPlayerMixin extends LivingEntity {

    private FabricPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Definition(id = "distanceToSqr", method = "Lnet/minecraft/world/entity/player/Player;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D")
    @Definition(id = "livingEntity", local = @Local(type = LivingEntity.class))
    @Expression("this.distanceToSqr(livingEntity) < 9.0")
    @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean nexus$modifyPlayerAttackDistance(boolean original, @Local(ordinal = 0) LivingEntity livingTarget) {
        return distanceToSqr(livingTarget) < Math.pow(getAttributeValue(NexusAttributes.ENTITY_REACH.get()), 2.0D);
    }

    @ModifyReturnValue(method = "createAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder nexus$attachFallbackAttributes(AttributeSupplier.Builder original) {
        if (!original.builder.containsKey(NexusAttributes.ENTITY_REACH.get())) original.add(NexusAttributes.ENTITY_REACH.get());
        if (!original.builder.containsKey(NexusAttributes.BLOCK_REACH.get())) original.add(NexusAttributes.BLOCK_REACH.get());

        return original;
    }
}
