package com.mememan.nexus.property_wrapper;

import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapper;
import com.mememan.nexus.property_wrapper.impl.generic.BaseDataGenPropertyWrapperBuilder;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BlockPropertyWrapper<B extends Block> extends BaseDataGenPropertyWrapper<B, BlockPropertyWrapper<B>, BlockPropertyWrapper.BPWBuilder<B>> {

    public static class BPWBuilder<B extends Block> extends BaseDataGenPropertyWrapperBuilder<B, BPWBuilder<B>, BlockPropertyWrapper<B>> {

        public BPWBuilder(@NotNull BlockPropertyWrapper<B> ownerWrapper) {
            super(ownerWrapper);
        }
    }
}