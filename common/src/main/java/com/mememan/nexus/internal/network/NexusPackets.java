package com.mememan.nexus.internal.network;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.NetworkRegistrarEntry;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncChunkPacket;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import com.mememan.nexus.platform.NexusServices;

@NetworkRegistrarEntry
public final class NexusPackets {

    public static final BasePacket<DatapackEntriesSyncChunkPacket> DATAPACK_ENTRIES_SYNC_PACKET = NexusServices.NETWORK_MANAGER.registerPacket(new BasePacket<>(NexusConstants.MOD_ID, DatapackEntriesSyncChunkPacket.class, DatapackEntriesSyncChunkPacket::encode, DatapackEntriesSyncChunkPacket::decode, DatapackEntriesSyncChunkPacket::handlePacket, NetworkSide.S2C));
}
