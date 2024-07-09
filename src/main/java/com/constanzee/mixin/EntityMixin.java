package com.constanzee.mixin;

import com.constanzee.SetVelocityMultiplierPayload;
import com.constanzee.VelocityMultiplier;
import com.constanzee.VelocityMultiplierOverridable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements VelocityMultiplierOverridable {
    private Vec3d velocitymultiplierOverrideValue = VelocityMultiplier.Companion.getVelocitymultiplierDefaultValue();
    private Vec3d velocitymultiplierOverrideValueInverse = VelocityMultiplier.Companion.invertMultiplier(
            velocitymultiplierOverrideValue
    );

    @Shadow
    public abstract World getWorld();

    @Shadow @Final private static Logger LOGGER;

    @NotNull
    @Override
    public Vec3d getVelocitymultiplierOverrideValue() {
        return velocitymultiplierOverrideValue;
    }

    @Override
    public void setVelocitymultiplierOverrideValue(@NotNull Vec3d value) {
        this.velocitymultiplierOverrideValue = value;
        this.velocitymultiplierOverrideValueInverse = VelocityMultiplier.Companion.invertMultiplier(
                velocitymultiplierOverrideValue
        );

        if (!getWorld().isClient) {
            Entity entity = (Entity)(Object)this;
            int entityId = entity.getId();

            List<ServerPlayerEntity> playerList = VelocityMultiplier.Companion.getServer()
                    .getPlayerManager()
                    .getPlayerList();

            for (ServerPlayerEntity player : playerList) {
                ServerPlayNetworking.send(player, new SetVelocityMultiplierPayload(entityId, value));
            }
        }
    }

    @NotNull
    @Override
    public Vec3d getVelocitymultiplierOverrideValueInverse() {
        return velocitymultiplierOverrideValueInverse;
    }

    @Override
    public void velocitymultiplierOnDefaultMultiplierChanged(@NotNull Vec3d before, @NotNull Vec3d after) {
        if (getVelocitymultiplierOverrideValue().equals(before)) {
            setVelocitymultiplierOverrideValue(after);
        } else {
            Logger logger = VelocityMultiplier.Companion.getLOGGER();
            logger.info(getVelocitymultiplierOverrideValue().toString());
            logger.info(before.toString());
        }
    }

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    public Vec3d modifyVelocity(Vec3d original, MovementType movementType) {
        return original.multiply(getVelocitymultiplierOverrideValue());
    }
}
