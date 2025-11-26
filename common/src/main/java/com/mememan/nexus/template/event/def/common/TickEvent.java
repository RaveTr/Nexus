package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.function.BooleanSupplier;

public abstract class TickEvent extends BaseEvent {
    protected final ModSide logicalSide;
    protected final Phase tickPhase;

    public TickEvent(ModSide logicalSide, Phase tickPhase) {
        this.logicalSide = logicalSide;
        this.tickPhase = tickPhase;
    }

    public ModSide getLogicalSide() {
        return logicalSide;
    }

    public Phase getPhase() {
        return tickPhase;
    }

    public static class CommonTickEvent extends TickEvent {

        public CommonTickEvent(ModSide logicalSide, Phase tickPhase) {
            super(logicalSide, tickPhase);
        }
    }

    public static class ClientTickEvent extends CommonTickEvent {

        public ClientTickEvent(Phase tickPhase) {
            super(ModSide.CLIENT, tickPhase);
        }
    }

    public static class ServerTickEvent extends CommonTickEvent {
        protected final MinecraftServer curServer;
        protected final BooleanSupplier hasTime;

        public ServerTickEvent(Phase tickPhase, MinecraftServer curServer, BooleanSupplier hasTime) {
            super(ModSide.SERVER, tickPhase);
            this.curServer = curServer;
            this.hasTime = hasTime;
        }

        public MinecraftServer getServer() {
            return curServer;
        }

        public BooleanSupplier hasTime() {
            return hasTime;
        }
    }

    public static class LevelTickEvent extends CommonTickEvent {
        protected final Level targetLevel;

        public LevelTickEvent(ModSide logicalSide, Phase tickPhase, Level targetLevel) {
            super(logicalSide, tickPhase);
            this.targetLevel = targetLevel;
        }

        public Level getLevel() {
            return targetLevel;
        }
    }

    public static class ClientLevelTickEvent extends LevelTickEvent {

        public ClientLevelTickEvent(Phase tickPhase, Level targetLevel) {
            super(ModSide.CLIENT, tickPhase, targetLevel);
        }
    }

    public static class ServerLevelTickEvent extends LevelTickEvent {

        public ServerLevelTickEvent(Phase tickPhase, Level targetLevel) {
            super(ModSide.SERVER, tickPhase, targetLevel);
        }
    }

    public enum Phase {
        START,
        END;
    }
}
