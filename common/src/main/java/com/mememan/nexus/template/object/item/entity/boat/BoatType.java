package com.mememan.nexus.template.object.item.entity.boat;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface BoatType extends StringRepresentable { //TODO Abstract into a special registrar type once support for that's added

    Supplier<Block> getPlanks();

    static BoatType register(String typeName, Supplier<Block> associatedPlanks) {
        return BoatTypesContainer.trackBoatType(new BoatType() {

            @Override
            public @NotNull String getSerializedName() {
                return typeName;
            }

            @Override
            public Supplier<Block> getPlanks() {
                return associatedPlanks;
            }
        });
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
