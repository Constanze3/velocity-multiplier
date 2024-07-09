package com.constanzee.mixin;

import com.constanzee.VelocityMultiplierOverridable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private Vec3d modifyVelocity(Vec3d original) {
        VelocityMultiplierOverridable velocityMultiplierOverridable = (VelocityMultiplierOverridable)this;
        return original.multiply(velocityMultiplierOverridable.getVelocitymultiplierOverrideValue());
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    )
    private void modifySetVelocity(PersistentProjectileEntity instance, Vec3d velocity, Operation<Void> original) {
        VelocityMultiplierOverridable vmo = (VelocityMultiplierOverridable)this;

        Vec3d scaled =  velocity.multiply(vmo.getVelocitymultiplierOverrideValueInverse());
        original.call(instance, scaled);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isCritical()Z"
            )
    )
    private boolean modifyIsCritical(boolean original) {
        VelocityMultiplierOverridable vmo = (VelocityMultiplierOverridable)this;
        return original && 1.7320 <= vmo.getVelocitymultiplierOverrideValue().length();
    }
}