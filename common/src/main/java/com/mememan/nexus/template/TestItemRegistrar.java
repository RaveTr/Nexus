package com.mememan.nexus.template;

import com.mememan.nexus.asm.annotations.RegistrarEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

@RegistrarEntry
public class TestItemRegistrar {
    private static final ObjectArrayList<Supplier<Item>> ITEMS = new ObjectArrayList<>();


}
