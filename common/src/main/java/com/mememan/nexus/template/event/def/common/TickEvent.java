package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.BooleanSupplier;

public abstract class TickEvent extends BaseEvent {
    protected final TickEventType eventType;
    protected final ModSide logicalSide;
    protected final Phase tickPhase;

    public TickEvent(TickEventType eventType, ModSide logicalSide, Phase tickPhase) {
        super(logicalSide);

        this.eventType = eventType;
        this.logicalSide = logicalSide;
        this.tickPhase = tickPhase;
    }

    public TickEventType getTickEventType() {
        return eventType;
    }

    @Override
    public ModSide getEventSide() {
        return logicalSide;
    }

    public Phase getPhase() {
        return tickPhase;
    }

    public static class CommonTickEvent extends TickEvent {

        public CommonTickEvent(ModSide logicalSide, Phase tickPhase) {
            super(TickEventType.STANDARD, logicalSide, tickPhase);
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
            super(!curServer.isDedicatedServer() ? ModSide.COMMON : ModSide.SERVER, tickPhase);

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

        @Override
        public TickEventType getTickEventType() {
            return TickEventType.LEVEL;
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
        protected final BooleanSupplier hasTime;

        public ServerLevelTickEvent(Phase tickPhase, ServerLevel targetLevel, BooleanSupplier hasTime) {
            super(!targetLevel.getServer().isDedicatedServer() ? ModSide.COMMON : ModSide.SERVER, tickPhase, targetLevel);
            this.hasTime = hasTime;
        }

        @Override
        public ServerLevel getLevel() {
            return (ServerLevel) super.getLevel();
        }

        public BooleanSupplier hasTime() {
            return hasTime;
        }
    }

    public static class RenderTickEvent extends ClientTickEvent {
        protected final boolean shouldRenderLevel;
        protected final float partialTick;

        public RenderTickEvent(Phase tickPhase, boolean shouldRenderLevel, float partialTick) {
            super(tickPhase);

            this.shouldRenderLevel = shouldRenderLevel;
            this.partialTick = partialTick;
        }

        @Override
        public TickEventType getTickEventType() {
            return TickEventType.RENDER;
        }

        public boolean shouldRenderLevel() {
            return shouldRenderLevel;
        }

        public float getPartialTick() {
            return partialTick;
        }
    }

    public static class EntityTickEvent extends CommonTickEvent {
        protected final Entity targetEntity;

        public EntityTickEvent(Phase tickPhase, Entity targetEntity) {
            super(ModSide.COMMON, tickPhase);

            this.targetEntity = targetEntity;
        }

        @Override
        public TickEventType getTickEventType() {
            return TickEventType.ENTITY;
        }

        public Entity getEntity() {
            return targetEntity;
        }
    }

    public static class LivingEntityTickEvent extends EntityTickEvent {

        public LivingEntityTickEvent(Phase tickPhase, LivingEntity targetLivingEntity) {
            super(tickPhase, targetLivingEntity);
        }

        @Override
        public LivingEntity getEntity() {
            return (LivingEntity) super.getEntity();
        }
    }

    public static class PlayerTickEvent extends EntityTickEvent {

        public PlayerTickEvent(Phase tickPhase, Player targetPlayer) {
            super(tickPhase, targetPlayer);
        }

        @Override
        public Player getEntity() {
            return (Player) super.getEntity();
        }
    }

    public enum Phase {
        START,
        END;
    }

    public enum TickEventType {
        STANDARD,
        LEVEL,
        RENDER,
        ENTITY;

        TickEventType() {

        }

        public boolean isStandard() {
            return this == STANDARD;
        }

        public boolean isLevel() {
            return this == LEVEL;
        }

        public boolean isRender() {
            return this == RENDER;
        }

        public boolean isEntity() {
            return this == ENTITY;
        }
    }
}
