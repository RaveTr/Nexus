package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcretePropagatingEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.TickEvent;

public class TickEventBlueprint<TE extends TickEvent> extends ConcretePropagatingEventBlueprint<TE> {
    public static final CommonTickEventBlueprint COMMON_TICK = new CommonTickEventBlueprint();
    public static final ClientTickEventBlueprint CLIENT_TICK = new ClientTickEventBlueprint();
    public static final ServerTickEventBlueprint SERVER_TICK = new ServerTickEventBlueprint();
    public static final LevelTickEventBlueprint LEVEL_TICK = new LevelTickEventBlueprint();
    public static final ClientLevelTickEventBlueprint CLIENT_LEVEL_TICK = new ClientLevelTickEventBlueprint();
    public static final ServerLevelTickEventBlueprint SERVER_LEVEL_TICK = new ServerLevelTickEventBlueprint();
    public static final RenderTickEventBlueprint RENDER_TICK = new RenderTickEventBlueprint();
    public static final EntityTickEventBlueprint ENTITY_TICK = new EntityTickEventBlueprint();
    public static final LivingEntityTickEventBlueprint LIVING_ENTITY_TICK = new LivingEntityTickEventBlueprint();
    public static final PlayerTickEventBlueprint PLAYER_TICK = new PlayerTickEventBlueprint();

    protected TickEventBlueprint(Class<TE> eventInterface, TE defaultResult, boolean isCancellable, ModSide eventSide) {
        super(eventInterface, defaultResult, isCancellable, eventSide);
    }

    public static class CommonTickEventBlueprint extends TickEventBlueprint<TickEvent.CommonTickEvent> {

        protected CommonTickEventBlueprint() {
            super(TickEvent.CommonTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class ClientTickEventBlueprint extends TickEventBlueprint<TickEvent.ClientTickEvent> {

        protected ClientTickEventBlueprint() {
            super(TickEvent.ClientTickEvent.class, null, true, ModSide.CLIENT);
        }
    }

    public static class ServerTickEventBlueprint extends TickEventBlueprint<TickEvent.ServerTickEvent> {

        protected ServerTickEventBlueprint() {
            super(TickEvent.ServerTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class LevelTickEventBlueprint extends TickEventBlueprint<TickEvent.LevelTickEvent> {

        protected LevelTickEventBlueprint() {
            super(TickEvent.LevelTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class ClientLevelTickEventBlueprint extends TickEventBlueprint<TickEvent.ClientLevelTickEvent> {

        protected ClientLevelTickEventBlueprint() {
            super(TickEvent.ClientLevelTickEvent.class, null, true, ModSide.CLIENT);
        }
    }

    public static class ServerLevelTickEventBlueprint extends TickEventBlueprint<TickEvent.ServerLevelTickEvent> {

        protected ServerLevelTickEventBlueprint() {
            super(TickEvent.ServerLevelTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class RenderTickEventBlueprint extends TickEventBlueprint<TickEvent.RenderTickEvent> {

        protected RenderTickEventBlueprint() {
            super(TickEvent.RenderTickEvent.class, null, true, ModSide.CLIENT);
        }
    }

    public static class EntityTickEventBlueprint extends TickEventBlueprint<TickEvent.EntityTickEvent> {

        protected EntityTickEventBlueprint() {
            super(TickEvent.EntityTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class LivingEntityTickEventBlueprint extends TickEventBlueprint<TickEvent.LivingEntityTickEvent> {

        protected LivingEntityTickEventBlueprint() {
            super(TickEvent.LivingEntityTickEvent.class, null, true, ModSide.COMMON);
        }
    }

    public static class PlayerTickEventBlueprint extends TickEventBlueprint<TickEvent.PlayerTickEvent> {

        protected PlayerTickEventBlueprint() {
            super(TickEvent.PlayerTickEvent.class, null, true, ModSide.COMMON);
        }
    }
}
