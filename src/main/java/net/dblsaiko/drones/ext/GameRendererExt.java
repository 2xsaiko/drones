package net.dblsaiko.drones.ext;

import net.minecraft.client.render.GameRenderer;

import net.dblsaiko.drones.entity.DroneEntity;

public interface GameRendererExt {

    void setDronePov(DroneEntity entity);

    DroneEntity getDronePov();

    static GameRendererExt from(GameRenderer self) {
        return (GameRendererExt) self;
    }

}
