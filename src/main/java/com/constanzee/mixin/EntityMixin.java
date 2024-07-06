package com.constanzee.mixin;

import com.constanzee.SetVelocityMultiplierPayload;
import com.constanzee.VelocityMultiplier;
import com.constanzee.VelocityMultiplierOverridable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements VelocityMultiplierOverridable {
    private Vec3d velocityMultiplierOverrideValue = VelocityMultiplier.Companion.getVelocityMultiplierDefaultValue();

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract EntityType<?> getType();

    @NotNull
    @Override
    public Vec3d getVelocityMultiplierOverrideValue() {
        return velocityMultiplierOverrideValue;
    }

    @Override
    public void setVelocityMultiplierOverrideValue(@NotNull Vec3d value) {
        this.velocityMultiplierOverrideValue = value;

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

    @Override
    public void velocityMultiplierOnDefaultMultiplierChanged(@NotNull Vec3d before, @NotNull Vec3d after) {
        if (velocityMultiplierOverrideValue == before) {
            setVelocityMultiplierOverrideValue(after);
        }
    }

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    public Vec3d modifyVelocity(Vec3d original, MovementType movementType) {
        if (movementType != MovementType.SELF) {
            return original;
        }

        return original.multiply(this.velocityMultiplierOverrideValue);
    }
}
