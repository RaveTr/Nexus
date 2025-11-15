package com.mememan.nexus.template.object.entity.misc.vehicle;

import com.mememan.nexus.template.object.item.entity.boat.BoatType;

import java.util.Optional;

public interface DefaultableBoatType {

    Optional<BoatType> getBoatType();

    void setBoatType(BoatType boatType);
}
