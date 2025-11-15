package com.mememan.nexus.template.object.item.entity.boat;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface BoatType extends StringRepresentable {
    BoatType OAK = register("oak", () -> Blocks.OAK_PLANKS, false);
    BoatType SPRUCE = register("spruce", () -> Blocks.SPRUCE_PLANKS, false);
    BoatType BIRCH = register("birch", () -> Blocks.BIRCH_PLANKS, false);
    BoatType JUNGLE = register("jungle", () -> Blocks.JUNGLE_PLANKS, false);
    BoatType ACACIA = register("acacia", () -> Blocks.ACACIA_PLANKS, false);
    BoatType CHERRY = register("cherry", () -> Blocks.CHERRY_PLANKS, false);
    BoatType DARK_OAK = register("dark_oak", () -> Blocks.DARK_OAK_PLANKS, false);
    BoatType MANGROVE = register("mangrove", () -> Blocks.MANGROVE_PLANKS, false);

    Supplier<Block> getPlanks();

    boolean isRaft();

    default ResourceLocation getResourceFriendlyId() {
        return new ResourceLocation(getSerializedName().contains("-") ? getSerializedName().replace('-', ':') : getSerializedName());
    }

    static BoatType register(String typeName, Supplier<Block> associatedPlanks, boolean isRaft) {
        return BoatTypesContainer.trackBoatType(new BoatType() {

            @Override
            public @NotNull String getSerializedName() {
                return typeName;
            }

            @Override
            public Supplier<Block> getPlanks() {
                return associatedPlanks;
            }

            @Override
            public boolean isRaft() {
                return isRaft;
            }
        });
    }

    static BoatType register(String typeName, Supplier<Block> associatedPlanks) {
        return register(typeName, associatedPlanks, false);
    }

    static ImmutableSet<BoatType> getKnownBoatTypes() {
        return ImmutableSet.copyOf(BoatTypesContainer.KNOWN_BOAT_TYPES);
    }

    class BoatTypesContainer {
        private static final ObjectOpenHashSet<BoatType> KNOWN_BOAT_TYPES = new ObjectOpenHashSet<>();

        private BoatTypesContainer() {
            throw new IllegalAccessError("Attempted to construct instance of container class! (BoatTypesContainer)");
        }

        public static BoatType trackBoatType(BoatType typeToTrack) {
            KNOWN_BOAT_TYPES.add(typeToTrack);
            return typeToTrack;
        }

        public static void untrackBoatType(BoatType typeToUntrack) {
            KNOWN_BOAT_TYPES.remove(typeToUntrack);
        }
    }
}
