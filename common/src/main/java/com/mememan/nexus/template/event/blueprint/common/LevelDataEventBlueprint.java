package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcreteEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.LevelDataEvent;

public class LevelDataEventBlueprint<LDE extends LevelDataEvent> extends ConcreteEventBlueprint<LDE> {
    public static final SaveLevelDataEventBlueprint SAVE_LEVEL_DATA = new SaveLevelDataEventBlueprint();
    public static final LoadLevelDataEventBlueprint LOAD_LEVEL_DATA = new LoadLevelDataEventBlueprint();

    protected LevelDataEventBlueprint(Class<LDE> eventInterface, LDE defaultResult, ModSide eventSide) {
        super(eventInterface, defaultResult, false, eventSide);
    }

    public static class SaveLevelDataEventBlueprint extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent> {

        protected SaveLevelDataEventBlueprint() {
            super(LevelDataEvent.SaveLevelDataEvent.class, null, ModSide.COMMON);
        }
    }

    public static class LoadLevelDataEventBlueprint extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent> {

        protected LoadLevelDataEventBlueprint() {
            super(LevelDataEvent.LoadLevelDataEvent.class, null, ModSide.COMMON);
        }
    }
}
