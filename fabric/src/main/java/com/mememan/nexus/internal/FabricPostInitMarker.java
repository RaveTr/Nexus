package com.mememan.nexus.internal;

import com.mememan.nexus.asm.annotations.PostInit;
import com.mememan.nexus.mixins.registries.MappedRegistryMixin;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Somewhat fragile internal Fabric implementation to mark post-initialization status exclusively for Nexus. Only really
 * used in {@link MappedRegistryMixin} to prevent early classloading (see references below).
 * <br></br>
 * This'll probably exist for a while since it's not exactly a practical endeavor to try and unify mod-loading stages
 * across loaders for now beyond generic markers, such as this one.
 * <br></br>
 * <b>Dependants: DO NOT use this class.</b>
 *
 * @see MappedRegistryMixin#nexus$getValueThroughAppellations(ResourceLocation)
 */
@PostInit
public final class FabricPostInitMarker {

    static {
        MarkerContainer.IS_POST_INIT.set(true);
    }

    /**
     * Internal container {@code class} used to query {@link #IS_POST_INIT} safely without initializing
     * {@link FabricPostInitMarker}.
     * <br></br>
     * (And in case you were wondering, no, initializing an inner {@code static} {@code class} doesn't affect the
     * encapsulating {@code class} cuz they're still independent of each other, but you probably knew that. What are you
     * even doing here? 🤨) <- (YOU CAN USE EMOJIS IN JAVADOCS 💀💀💀)
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-12.html">JLS: 12.4.2. Initialization of Classes and Interfaces</a>
     */
    public static final class MarkerContainer {
        private static final AtomicBoolean IS_POST_INIT = new AtomicBoolean(false);

        private MarkerContainer() {
            throw new IllegalAccessError("Attempted to construct instance of container class! (MarkerContainer)");
        }

        /**
         * Checks whether {@link #IS_POST_INIT} has been set to {@code true} in {@link FabricPostInitMarker}'s
         * {@code <clinit>}.
         *
         * @return {@link #IS_POST_INIT}.
         */
        public static boolean hasPostInitialized() {
            return MarkerContainer.IS_POST_INIT.get();
        }
    }
}
