package com.mememan.nexus.template.event.def.common;

import com.mememan.nexus.event.object.BaseEvent;
import com.mememan.nexus.loader.ModSide;
import net.minecraft.world.entity.Entity;

public class EntityEvent extends BaseEvent {
    protected final Entity targetEntity;

    public EntityEvent(ModSide targetSide, Entity targetEntity) {
        super(targetSide);

        this.targetEntity = targetEntity;
    }

    public EntityEvent(Entity targetEntity) {
        this(ModSide.COMMON, targetEntity);
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }
}
