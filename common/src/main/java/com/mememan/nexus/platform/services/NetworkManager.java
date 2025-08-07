package com.mememan.nexus.platform.services;

import com.mememan.nexus.Nexus;
import com.mememan.nexus.asm.annotations.NetworkRegistrarEntry;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

/**
 * A loader-agnostic {@code interface} used for dynamically registering and sending cross-loader packets without needing
 * multiple separate methods, classes, or redundant loader-specific setup.
 *
 * @see BasePacket
 * @see <a href="https://github.com/RaveTr/Nexus/wiki/networking">Nexus Wiki: Networking</a>
 */
public interface NetworkManager {

    /**
     * Main method for this service interface, called in {@link Nexus} in order to load it and its loader-specific
     * implementations accordingly.
     * <br></br>
     * Functionally speaking, all this method does is properly load {@link NetworkRegistrarEntry}-annotated classes
     * in lexicographical order.
     * <br></br>
     * Should <b>NOT</b> be called anywhere else!
     *
     * @apiNote Classloading order of annotated network registrar entries shouldn't necessarily matter, since packet
     * registration is standalone. If you need any special functionality, you shouldn't be using the annotation to
     * load your packet registrar class(es).
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    void setupNetworkManager();

    /**
     * Method for registering S2C/C2S packets (based on the provided {@linkplain BasePacket BasePacket's}
     * {@link NetworkSide} definition).
     * <br></br>
     * You'd usually call this method like so:
     * <pre>
     *     {@code
     *         @NetworkRegistrarEntry // Optional; you can use bootstrap methods or some other way to statically initialize this class
     *         public class MyPacketRegistrarClass {
     *
     *             public static final BasePacket<MyPacket> MY_PACKET = registerPacket(new BasePacket<>(new ResourceLocation("my_modid", "my_packet"), MyPacket.class, MyPacket::encode, MyPacket::decode, MyPacket::handle, NetworkSide.C2S));
     *
     *             private static <MSGT> BasePacket<MSGT> registerPacket(BasePacket<MSGT> packet) {
     *                  return NexusServices.NETWORK_MANAGER.registerPacket(packet);
     *             }
     *         }
     *
     *         // ...
     *
     *         public class MyPacket {
     *              private final int someInt;
     *              private final String someString;
     *
     *              public MyPacket(int someInt, String someString) {
     *                  this.someInt = someInt;
     *                  this.someString = someString;
     *              }
     *
     *              public MyPacket(FriendlyByteBuf buf) { // ALT: You can use this overloaded constructor for decoding instead
     *                  this(buf.readInt(), buf.readUtf());
     *              }
     *
     *              public static MyPacket decode(FriendlyByteByf buf) {
     *                  return new MyPacket(buf.readInt(), buf.readUtf());
     *              }
     *
     *              public void encode(FriendlyByteBuf buf) {
     *                  buf.writeInt(this.someInt);
     *                  buf.writeUtf(this.someString);
     *              }
     *
     *              public static PacketContext handle(MyPacket myPacketObj) {
     *                  return (nullablePlayerOwner, currentLevel, currentConnection, currentSide) -> {
     *                      // ... (Do stuff)
     *                  };
     *              }
     *         }
     *     }
     * </pre>
     *
     * @param packet The wrapped packet object to register.
     *
     * @return The registered packet.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> BasePacket<MSGT> registerPacket(BasePacket<MSGT> packet);

    /**
     * Sends a C2S packet (client -> server) from the current client.
     *
     * @param c2sPacket The packet object to send.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToServer(MSGT c2sPacket);

    /**
     * Sends an S2C packet (server -> client) to the current client.
     *
     * @param s2cPacket The packet object to send.
     *
     * @param <MSGT> The type of the packet object.
     *
     * @implNote This method has no functional implementation on Fabric due to how Fabric's networking API works. It is
     * recommended to use {@link #sendToClient(Object, ServerPlayer)} instead.
     */
    <MSGT> void sendToClient(MSGT s2cPacket);

    /**
     * Sends an S2C packet (server -> client) to all connected clients on the current server.
     *
     * @param s2cPacket The packet object to send.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToAllClients(MSGT s2cPacket);

    /**
     * Sends an S2C packet (server -> client) to all connected clients tracking the specified {@code trackedEntity},
     * as well as the {@code trackedEntity} itself if it's a {@link Player} and {@code ignoreSelf} is set to
     * {@code false}.
     *
     * @param s2cPacket The packet object to send.
     * @param trackedEntity The tracked {@link Entity}.
     * @param ignoreSelf Whether to ignore (I.E. not send a packet to) the {@code trackedEntity} (if it's
     *                   a {@link Player}).
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToTrackingClients(MSGT s2cPacket, Entity trackedEntity, boolean ignoreSelf);

    /**
     * Overloaded variant of {@link #sendToTrackingClients(MSGT, Entity, boolean)} with {@code ignoreSelf} set to
     * {@code false}.
     *
     * @param s2cPacket The packet object to send.
     * @param trackedEntity The tracked {@link Entity}.
     *
     * @param <MSGT> The type of the packet object.
     */
    default <MSGT> void sendToTrackingClients(MSGT s2cPacket, Entity trackedEntity) {
        sendToTrackingClients(s2cPacket, trackedEntity, false);
    }

    /**
     * Sends an S2C packet (server -> client) to all connected clients tracking the specified {@code trackedChunk}.
     *
     * @param s2cPacket The packet object to send.
     * @param trackedChunk The tracked {@linkplain LevelChunk Chunk}.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToTrackingClients(MSGT s2cPacket, LevelChunk trackedChunk);

    /**
     * Sends an S2C packet (server -> client) to all connected clients in the specified {@code targetDim}.
     *
     * @param s2cPacket The packet object to send.
     * @param targetDim The target dimension's {@link ResourceKey}.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToClientsInDimension(MSGT s2cPacket, ResourceKey<Level> targetDim);

    /**
     * Sends an S2C packet (server -> client) to all connected clients within the specified {@code range} of the
     * specified {@code originPos} in the specified {@code targetDim}.
     *
     * @param s2cPacket The packet object to send.
     * @param targetDim The target dimension's {@link ResourceKey}, in which the range check will be validated.
     * @param originPos The origin {@link BlockPos} from which range will be counted.
     * @param range The range from the specified {@code originPos} within which clients will receive the packet.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToClientsWithinRange(MSGT s2cPacket, ResourceKey<Level> targetDim, BlockPos originPos, double range);

    /**
     * Sends an S2C packet (server -> client) to the specified {@code targetPlayer}.
     *
     * @param s2cPacket The packet object to send.
     * @param targetPlayer The target {@link ServerPlayer}.
     *
     * @param <MSGT> The type of the packet object.
     */
    <MSGT> void sendToClient(MSGT s2cPacket, ServerPlayer targetPlayer);
}
