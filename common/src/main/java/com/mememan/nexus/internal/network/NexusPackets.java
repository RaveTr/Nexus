package com.mememan.nexus.internal.network;

import com.mememan.nexus.NexusConstants;
import com.mememan.nexus.asm.annotations.NetworkRegistrarEntry;
import com.mememan.nexus.internal.network.packets.s2c.DatapackEntriesSyncPacket;
import com.mememan.nexus.network.BasePacket;
import com.mememan.nexus.network.NetworkSide;
import com.mememan.nexus.platform.NexusServices;

@NetworkRegistrarEntry
public final class NexusPackets {

    public static final BasePacket<DatapackEntriesSyncPacket> DATAPACK_ENTRIES_SYNC_PACKET = NexusServices.NETWORK_MANAGER.registerPacket(new BasePacket<>(NexusConstants.MOD_ID, DatapackEntriesSyncPacket.class, DatapackEntriesSyncPacket::encode, DatapackEntriesSyncPacket::decode, DatapackEntriesSyncPacket::handlePacket, NetworkSide.S2C));
}
