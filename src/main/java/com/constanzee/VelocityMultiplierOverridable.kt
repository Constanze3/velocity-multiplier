package com.constanzee

import net.minecraft.util.math.Vec3d

interface VelocityMultiplierOverridable {
    var velocitymultiplierOverrideValue: Vec3d
    var velocitymultiplierOverrideValueInverse: Vec3d
    fun velocitymultiplierOnDefaultMultiplierChanged(before: Vec3d, after: Vec3d)
}