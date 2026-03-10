package com.mememan.nexus.internal.services;

import com.mememan.nexus.asm.ClassFinder;
import com.mememan.nexus.internal.loader.ForgeGamePathWrapper;
import com.mememan.nexus.internal.loader.ForgeModData;
import com.mememan.nexus.loader.EnvironmentSide;
import com.mememan.nexus.loader.GamePathWrapper;
import com.mememan.nexus.loader.ModData;
import com.mememan.nexus.loader.ModLoader;
import com.mememan.nexus.platform.services.PlatformManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Forge-specific implementation of {@link PlatformManager}.
 */
public class ForgePlatformManager implements PlatformManager {
    private static final ForgeGamePathWrapper FORGE_GAME_PATH_WRAPPER = new ForgeGamePathWrapper();
    private static final ObjectOpenHashSet<ModData> MOD_DATA_CACHE = ModList.get().getMods().stream()
            .map(ForgeModData::new)
            .collect(Collectors.toCollection(ObjectOpenHashSet::new));

    @Override
    public ModLoader getPlatform() {
        return ModLoader.FORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public List<Class<?>> discoverAnnotatedClasses(Class<? extends Annotation> annotationTypeClazz, @Nullable Comparator<String> classLoadingSorter, @Nullable List<String> validModIds, @Nullable Predicate<String> initFilter, @Nullable Consumer<String> beforeClassInitConsumer) {
        Type targetAnnotType = Type.getType(annotationTypeClazz); // Micro-optimization: Cache the annotation class' type in a local field

        if (validModIds == null || validModIds.isEmpty() || validModIds.stream().noneMatch(curModId -> ModList.get().isLoaded(curModId))) {
            return ModList.get().getAllScanData().stream()
                    .map(ModFileScanData::getAnnotations)
                    .flatMap(Collection::stream)
                    .filter(annotationData -> Objects.equals(annotationData.annotationType(), targetAnnotType))
                    .map(ModFileScanData.AnnotationData::clazz)
                    .map(Type::getClassName)
                    .sorted(classLoadingSorter != null ? classLoadingSorter : String::compareTo)
                    .filter(name -> initFilter == null || initFilter.test(name))
                    .peek(name -> {
                        if (beforeClassInitConsumer != null) beforeClassInitConsumer.accept(name);
                    })
                    .map(ClassFinder::forName)
                    .collect(Collectors.toCollection(ObjectArrayList::new));
        } else return ModList.get().getMods().stream() // Instead of layering stream operations through #getModData, we can directly stream everything through here as a little overhead shortcut
                .filter(curModInfo -> validModIds.contains(curModInfo.getModId()))
                .map(IModInfo::getOwningFile)
                .map(IModFileInfo::getFile)
                .map(IModFile::getScanResult)
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(annotationData -> Objects.equals(annotationData.annotationType(), targetAnnotType))
                .map(ModFileScanData.AnnotationData::clazz)
                .map(Type::getClassName)
                .sorted(classLoadingSorter != null ? classLoadingSorter : String::compareTo)
                .filter(name -> initFilter == null || initFilter.test(name))
                .peek(name -> {
                    if (beforeClassInitConsumer != null) beforeClassInitConsumer.accept(name);
                })
                .map(ClassFinder::forName)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    @Override
    public Set<ModData> getModData() {
        return MOD_DATA_CACHE;
    }

    @Override
    public GamePathWrapper getGamePathWrapper() {
        return FORGE_GAME_PATH_WRAPPER;
    }

    @Override
    public @Nullable MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean isRunningDataGen() {
        return DatagenModLoader.isRunningDataGen();
    }

    @Override
    public EnvironmentSide getEnvironmentSide() {
        return FMLEnvironment.dist == Dist.CLIENT ? EnvironmentSide.CLIENT : EnvironmentSide.DEDICATED_SERVER;
    }
}
