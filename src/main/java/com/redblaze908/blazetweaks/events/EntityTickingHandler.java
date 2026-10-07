package com.redblaze908.blazetweaks.events;

import com.redblaze908.blazetweaks.BlazeTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityTickingHandler {

    public static final Map<UUID, Vec3d> SAVED_MOTIONS = new HashMap<>();
    private static final Map<UUID, Long> LAST_PUSH_TICK = new HashMap<>();

    // Factor di smorzamento per compensare la distanza di collisione statica
    // (Distanza d = 0)
    private static final double STATIC_IMPULSE_FACTOR = 0.75;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.world == null)
            return;
        processEntities(event.world.loadedEntityList);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.player == null)
            return;
        processEntities(mc.world.loadedEntityList);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isSingleplayer() || event.getEntity() == null || event.getEntity() == mc.player)
            return;

        Entity entity = event.getEntity();
        if (shouldFreezeEntity(entity)) {
            freezeEntity(entity);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote) {
            BlazeTweaks.isEntityTickingDisabled = false;
            BlazeTweaks.isFallingBlockTickingDisabled = false;
            SAVED_MOTIONS.clear();
            LAST_PUSH_TICK.clear();
        }
    }

    private void processEntities(Iterable<Entity> entities) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isSingleplayer())
            return;

        for (Entity entity : entities) {
            if (entity == mc.player)
                continue;

            if (shouldFreezeEntity(entity)) {
                freezeEntity(entity);
            } else if (entity.updateBlocked) {
                unfreezeEntity(entity);
            }
        }
    }

    private boolean shouldFreezeEntity(Entity entity) {
        if (BlazeTweaks.isEntityTickingDisabled)
            return true;
        if (BlazeTweaks.isFallingBlockTickingDisabled && entity instanceof EntityFallingBlock)
            return true;
        return false;
    }

    private void freezeEntity(Entity entity) {
        long currentTick = entity.world.getTotalWorldTime();
        UUID id = entity.getUniqueID();

        double rawMotionX = entity.motionX;
        double rawMotionY = entity.motionY;
        double rawMotionZ = entity.motionZ;

        // 1. PRIMO TICK DI CONGELAMENTO (Inerzia di volo preservata al 100%)
        if (!entity.updateBlocked) {
            if (rawMotionX != 0.0 || rawMotionY != 0.0 || rawMotionZ != 0.0) {
                SAVED_MOTIONS.put(id, new Vec3d(rawMotionX, rawMotionY, rawMotionZ));
                LAST_PUSH_TICK.put(id, currentTick);
            }
        }
        // 2. SPINTA RICEVUTA MENTRE È GIÀ CONGELATO (TNT / Pistoni da fermo)
        else if (rawMotionX != 0.0 || rawMotionY != 0.0 || rawMotionZ != 0.0) {
            Long lastTick = LAST_PUSH_TICK.get(id);

            if (lastTick == null || lastTick == currentTick || (currentTick - lastTick) > 2) {
                Vec3d existing = SAVED_MOTIONS.getOrDefault(id, Vec3d.ZERO);

                // Normalizziamo l'impulso statico con lo smorzamento per simulare
                // l'allontanamento Vanilla
                Vec3d calibratedImpulse = new Vec3d(
                        rawMotionX * STATIC_IMPULSE_FACTOR,
                        rawMotionY * STATIC_IMPULSE_FACTOR,
                        rawMotionZ * STATIC_IMPULSE_FACTOR);

                SAVED_MOTIONS.put(id, existing.add(calibratedImpulse));
                LAST_PUSH_TICK.put(id, currentTick);
            }
        }

        entity.updateBlocked = true;

        // Azzeramento motion e posizioni
        entity.motionX = 0;
        entity.motionY = 0;
        entity.motionZ = 0;

        entity.lastTickPosX = entity.posX;
        entity.lastTickPosY = entity.posY;
        entity.lastTickPosZ = entity.posZ;

        entity.prevPosX = entity.posX;
        entity.prevPosY = entity.posY;
        entity.prevPosZ = entity.posZ;
    }

    private void unfreezeEntity(Entity entity) {
        entity.updateBlocked = false;

        UUID id = entity.getUniqueID();
        Vec3d savedMotion = SAVED_MOTIONS.remove(id);
        LAST_PUSH_TICK.remove(id);

        if (savedMotion != null) {
            entity.motionX = savedMotion.x;
            entity.motionY = savedMotion.y;
            entity.motionZ = savedMotion.z;
        }
    }
}