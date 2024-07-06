package com.constanzee

import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

data class SetVelocityMultiplierPayload(val entityId: Int, val value: Vec3d) : CustomPayload {
    constructor(buf: PacketByteBuf) : this(buf.readInt(), buf.readVec3d())

    fun write(buf: PacketByteBuf) {
        buf.writeInt(entityId)
        buf.writeVec3d(value)
    }

    companion object {
        val ID: Id<SetVelocityMultiplierPayload> = Id(
            Identifier.of(VelocityMultiplier.MODID, "set-player-velocity-multiplier")
        )

        val CODEC = CustomPayload.codecOf(
            SetVelocityMultiplierPayload::write,
            ::SetVelocityMultiplierPayload
        )
    }

    override fun getId(): Id<out CustomPayload> {
        return ID
    }
}