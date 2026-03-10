package com.mememan.nexus.template.event.blueprint.common;

import com.mememan.nexus.event.blueprint.ConcretePropagatingEventBlueprint;
import com.mememan.nexus.loader.ModSide;
import com.mememan.nexus.template.event.def.common.LevelDataEvent;

public class LevelDataEventBlueprint<LDE extends LevelDataEvent> extends ConcretePropagatingEventBlueprint<LDE> {
    public static final SaveLevelDataEventBlueprint SAVE_LEVEL_DATA = new SaveLevelDataEventBlueprint();
    public static final SaveLevelDataEventBlueprint.PreLoader SAVE_LEVEL_DATA_PRE_LOADER = new SaveLevelDataEventBlueprint.PreLoader();
    public static final SaveLevelDataEventBlueprint.PostLoader SAVE_LEVEL_DATA_POST_LOADER = new SaveLevelDataEventBlueprint.PostLoader();
    public static final SaveLevelDataEventBlueprint.PreVanilla SAVE_LEVEL_DATA_PRE_VANILLA = new SaveLevelDataEventBlueprint.PreVanilla();
    public static final SaveLevelDataEventBlueprint.PostVanilla SAVE_LEVEL_DATA_POST_VANILLA = new SaveLevelDataEventBlueprint.PostVanilla();
    public static final LoadLevelDataEventBlueprint LOAD_LEVEL_DATA = new LoadLevelDataEventBlueprint();
    public static final LoadLevelDataEventBlueprint.PreLoader LOAD_LEVEL_DATA_PRE_LOADER = new LoadLevelDataEventBlueprint.PreLoader();
    public static final LoadLevelDataEventBlueprint.PostLoader LOAD_LEVEL_DATA_POST_LOADER = new LoadLevelDataEventBlueprint.PostLoader();
    public static final LoadLevelDataEventBlueprint.PreVanilla LOAD_LEVEL_DATA_PRE_VANILLA = new LoadLevelDataEventBlueprint.PreVanilla();
    public static final LoadLevelDataEventBlueprint.PostVanilla LOAD_LEVEL_DATA_POST_VANILLA = new LoadLevelDataEventBlueprint.PostVanilla();

    protected LevelDataEventBlueprint(Class<LDE> eventInterface, LDE defaultResult, ModSide eventSide) {
        super(eventInterface, defaultResult, false, eventSide);
    }

    public static class SaveLevelDataEventBlueprint extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent> {

        protected SaveLevelDataEventBlueprint() {
            super(LevelDataEvent.SaveLevelDataEvent.class, null, ModSide.COMMON);
        }

        public static class PreLoader extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent.PreLoader> {

            protected PreLoader() {
                super(LevelDataEvent.SaveLevelDataEvent.PreLoader.class, null, ModSide.COMMON);
            }
        }

        public static class PostLoader extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent.PostLoader> {

            protected PostLoader() {
                super(LevelDataEvent.SaveLevelDataEvent.PostLoader.class, null, ModSide.COMMON);
            }
        }

        public static class PreVanilla extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent.PreVanilla> {

            protected PreVanilla() {
                super(LevelDataEvent.SaveLevelDataEvent.PreVanilla.class, null, ModSide.COMMON);
            }
        }

        public static class PostVanilla extends LevelDataEventBlueprint<LevelDataEvent.SaveLevelDataEvent.PostVanilla> {

            protected PostVanilla() {
                super(LevelDataEvent.SaveLevelDataEvent.PostVanilla.class, null, ModSide.COMMON);
            }
        }
    }

    public static class LoadLevelDataEventBlueprint extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent> {

        protected LoadLevelDataEventBlueprint() {
            super(LevelDataEvent.LoadLevelDataEvent.class, null, ModSide.COMMON);
        }

        public static class PreLoader extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent.PreLoader> {

            protected PreLoader() {
                super(LevelDataEvent.LoadLevelDataEvent.PreLoader.class, null, ModSide.COMMON);
            }
        }

        public static class PostLoader extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent.PostLoader> {

            protected PostLoader() {
                super(LevelDataEvent.LoadLevelDataEvent.PostLoader.class, null, ModSide.COMMON);
            }
        }

        public static class PreVanilla extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent.PreVanilla> {

            protected PreVanilla() {
                super(LevelDataEvent.LoadLevelDataEvent.PreVanilla.class, null, ModSide.COMMON);
            }
        }

        public static class PostVanilla extends LevelDataEventBlueprint<LevelDataEvent.LoadLevelDataEvent.PostVanilla> {

            protected PostVanilla() {
                super(LevelDataEvent.LoadLevelDataEvent.PostVanilla.class, null, ModSide.COMMON);
            }
        }
    }
}
