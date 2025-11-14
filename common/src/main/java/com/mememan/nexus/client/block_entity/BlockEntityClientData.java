package com.mememan.nexus.client.block_entity;

import com.google.common.base.Suppliers;
import com.mememan.nexus.client.entity.EntityClientData;
import com.mememan.nexus.property_wrapper.def.block_entity.BlockEntityTypePropertyWrapper;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Pseudo-side-safe holder {@code record} designed to store basic block entity renderer data.
 * <br></br>
 * <b>Important</b>: While it's safe to reference {@code static} instances of this {@code record} behind a lambda expression,
 * directly constructing one in common code will likely lead to runtime exceptions being thrown due to client-side-only
 * classes being stripped from the physical server JAR at classloading time.
 * <br></br>
 * For instance:
 * <pre>
 *     {@code
 *          public class SomeClientOnlyClass {
 *              public static final BlockEntityClientData<YourBlockEntity> YOUR_BLOCK_ENTITY_DATA = new BlockEntityClientData<>(YourBlockEntityRenderer::new); // Functionally takes one BlockEntityRendererProvider.Context and returns a BlockEntityRenderer<BE> instance
 *          }
 *
 *          @RegistrarEntry
 *          public class YourSideSafeEntityTypeRegistrar {
 *
 *              public static final Supplier<BlockEntityType<YourBlockEntity>> YOUR_BLOCK_ENTITY = new BlockEntityTypePropertyWrapper<>(EntityTypePropertyWrapperTemplates.registerEntityType(new ResourceLocation("your_mod_id", "your_block_entity"), () -> ...), "your_mod_id")
 *                      .builder()
 *                      .withClientData(() -> SomeClientOnlyClass.YOUR_BLOCK_ENTITY_DATA) // This works, because lambda expressions don't create direct class references in bytecode. You can also wrap the original field in a Supplier, and it will functionally work, but be aware that doing so may cause weird stacktraces to pop up (particularly in the case of Fabric datagen) and is thus not recommended.
 *                      .buildAndGet();
 *          }
 *     }
 * </pre>
 *
 * Now, you may be wondering: Why not just allow for direct construction of this {@code record} and hide all the unsafe
 * stuff behind suppliers and such?
 * <br></br>
 * Well, the short answer is that side-stripping checks are done during classloading time, not initialization/linking,
 * so even import statements that point to side-stripped classes are unsafe due to the fact that they create unresolvable
 * references inside your class's constant pool when the JVM attempts to resolve them from the classloader's cache.
 *
 * @param blockEntityRendererMapper A mapping {@link Function} whose input is the current
 *                                  {@linkplain BlockEntityRendererProvider.Context render context}, and whose output is
 *                                  a {@link BlockEntityRenderer} whose generic type is {@link BE}.
 * @param blockEntitySheetDataMapper An optional mapping {@link Function}, whose input is typically some
 *                                   {@code Supplier<BlockEntityType<BE>>} mapped to this BECD instance through
 *                                   {@link BlockEntityTypePropertyWrapper}, used to  define  different data mapped in
 *                                   {@link Sheets}. Primarily useful in cases such as signs, where sign textures have
 *                                   to be stitched in their respective atlas in order to be displayed properly in-game.
 * @param mappedModelLayerDefinitions A {@link Supplier} of a {@link Pair} containing a collection of
 *                                    {@linkplain ModelLayerLocation ModelLayerLocations} and a {@link LayerDefinition},
 *                                    often paired with the provided {@code blockEntityRendererMapper} to define the
 *                                    block entity's model data. The reason you can map multiple layer locations to a
 *                                    singular {@link LayerDefinition}, unlike in {@link EntityClientData}, is due to the
 *                                    fact that assigning multiple different textures to a single block entity based on
 *                                    some given data is quite unconventional/not typically possible otherwise.
 *                                    <br></br>
 *                                    Not required in all cases, but should at least return a {@link Supplier} whose
 *                                    output is {@code null} if another model layer definition is present (Example of
 *                                    such an edge case can be seen in
 *                                    {@link ZombieRenderer#ZombieRenderer(EntityRendererProvider.Context)}), so leaving
 *                                    this empty may be necessary if working with a block entity type whose layers are
 *                                    already registered elsewhere. It should also be noted that of course, depending
 *                                    on the implementation, you may not even need a layer definition at all.
 *
 * @param <BE> Any {@link BlockEntity} type.
 *
 * @see <a href="https://blogs.oracle.com/javamagazine/post/java-class-file-constant-pool">Oracle Blog: Java Class File Constant Pool</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.11">JLS 17: The Class File Format</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-5.html">JLS 17: Loading, Linking, and Initializing</a>
 */
public record BlockEntityClientData<BE extends BlockEntity>(Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper, @Nullable Function<Supplier<BlockEntityType<BE>>, Collection<BlockEntitySheetData>> blockEntitySheetDataMapper, @Nullable Supplier<Pair<Collection<ModelLayerLocation>, LayerDefinition>> mappedModelLayerDefinitions) {

    /**
     * Overloaded constructor whose {@code blockEntitySheetDataMapper} is set to {@code null}.
     *
     * @param blockEntityRendererMapper A mapping {@link Function} whose input is the current
     *                                  {@linkplain BlockEntityRendererProvider.Context render context}, and whose output
     *                                  is a {@link BlockEntityRenderer} whose generic type is {@link BE}.
     *
     * @see BlockEntityClientData
     */
    public BlockEntityClientData(Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper) {
        this(blockEntityRendererMapper, null, Suppliers.ofInstance(null));
    }

    public BlockEntityClientData(Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper, @Nullable Function<Supplier<BlockEntityType<BE>>, Collection<BlockEntitySheetData>> blockEntitySheetDataMapper) {
        this(blockEntityRendererMapper, blockEntitySheetDataMapper, Suppliers.ofInstance(null));
    }

    public BlockEntityClientData(Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<BE>> blockEntityRendererMapper, @Nullable Supplier<Pair<Collection<ModelLayerLocation>, LayerDefinition>> mappedModelLayerDefinitions) {
        this(blockEntityRendererMapper, null, mappedModelLayerDefinitions);
    }
}
