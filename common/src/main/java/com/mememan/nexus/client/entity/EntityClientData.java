package com.mememan.nexus.client.entity;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Pseudo-side-safe holder {@code record} designed to store basic entity model and renderer data.
 * <br></br>
 * <b>Important</b>: While it's safe to reference {@code static} instances of this {@code record} behind a lambda expression,
 * directly constructing one in common code will likely lead to runtime exceptions being thrown due to client-side-only
 * classes being stripped from the physical server JAR at classloading time.
 * <br></br>
 * For instance:
 * <pre>
 *     {@code
 *          public class SomeClientOnlyClass {
 *              public static final EntityClientData<YourEntity> YOUR_ENTITY_DATA = new EntityClientData<>(YourEntityRenderer::new); // Functionally takes one EntityRendererProvider.Context and returns an EntityRenderer<E> instance
 *          }
 *
 *          @RegistrarEntry
 *          public class YourSideSafeEntityTypeRegistrar {
 *
 *              public static final Supplier<EntityType<YourEntity>> YOUR_ENTITY = new EntityTypePropertyWrapper<>(EntityTypePropertyWrapperTemplates.registerEntityType(new ResourceLocation("your_mod_id", "your_entity"), () -> ...), "your_mod_id")
 *                      .builder()
 *                      .withAttributes(YourEntity::createAttributeMethodReference)
 *                      .withClientData(() -> SomeClientOnlyClass.YOUR_ENTITY_DATA) // This works, because lambda expressions don't create direct class references in bytecode
 *                      .buildAndGet();
 *          }
 *     }
 * </pre>
 *
 * Now, you may be wondering: Why not just allow for direct construction of this {@code record} and hide all the unsafe
 * stuff behind suppliers and such?
 * <br></br>
 * Well, the short answer is that side-stripping checks are done during classloading time, not initialization/linking,
 * so even import statements are unsafe due to the fact that they create unresolvable references inside your class's
 * constant pool when the JVM attempts to resolve them from the classloader's cache.
 *
 * @param entityRendererMapper A mapping {@link Function} whose input is the current
 *                             {@linkplain EntityRendererProvider.Context render context}, and whose output is a
 *                             {@link EntityRenderer} whose generic type is the same as {@code E}.
 * @param mappedModelLayerDefinition A {@link Supplier} of a {@link Pair} containing a {@link ModelLayerLocation} and a
 *                                   {@link LayerDefinition}, often paired with the provided {@code entityRendererMapper}
 *                                   to define the entity's model data.
 *                                   <br></br>
 *                                   Not required in all cases, but should at least return a {@link Supplier} whose
 *                                   output is {@code null} if another model layer definition is present (Example of
 *                                   such an edge case can be seen in
 *                                   {@link ZombieRenderer#ZombieRenderer(EntityRendererProvider.Context)}), so leaving
 *                                   this empty may be necessary if working with an entity type whose layers are already
 *                                   registered elsewhere.
 *
 * @param <E> Any {@link Entity} type.
 *
 * @see <a href="https://blogs.oracle.com/javamagazine/post/java-class-file-constant-pool">Oracle Blog: Java Class File Constant Pool</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-4.html#jvms-4.11">JLS 17: The Class File Format</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jvms/se17/html/jvms-5.html">JLS 17: Loading, Linking, and Initializing</a>
 */
public record EntityClientData<E extends Entity>(Function<EntityRendererProvider.Context, EntityRenderer<E>> entityRendererMapper, Supplier<Pair<ModelLayerLocation, LayerDefinition>> mappedModelLayerDefinition) {

    /**
     * Overloaded constructor for when the entity renderer mapper is the only thing needed (see {@link EntityClientData}
     * for more info).
     *
     * @param entityRendererMapper A mapping {@link Function} whose input is the current
     *                             {@linkplain EntityRendererProvider.Context render context}, and whose output is a
     *                             {@link EntityRenderer} whose generic type is the same as {@code E}.
     */
    public EntityClientData(Function<EntityRendererProvider.Context, EntityRenderer<E>> entityRendererMapper) {
        this(entityRendererMapper, Suppliers.ofInstance(null));
    }
}