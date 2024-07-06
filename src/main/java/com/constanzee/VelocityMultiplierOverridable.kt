package com.constanzee

import net.minecraft.util.math.Vec3d

interface VelocityMultiplierOverridable {
    var velocityMultiplierOverrideValue: Vec3d
    fun velocityMultiplierOnDefaultMultiplierChanged(before: Vec3d, after: Vec3d)
}