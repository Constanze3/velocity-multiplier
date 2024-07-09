package com.constanzee

import com.constanzee.SetVelocityMultiplierPayload.Companion.CODEC
import com.constanzee.SetVelocityMultiplierPayload.Companion.ID
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.slf4j.LoggerFactory

class VelocityMultiplier : ModInitializer {
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register {
            server = it
        }

        PayloadTypeRegistry.playS2C().register(ID, CODEC)

        ArgumentTypeRegistry.registerArgumentType(
            Identifier.of(MODID, "vec3d"),
            Vec3dArgumentType::class.java,
            ConstantArgumentSerializer.of(Vec3dArgumentType::vec3d)
        )

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            VelocityMultiplierCommand.register(dispatcher)
        }

        LOGGER.info("Velocitymultiplier is ready!")
    }

    companion object {
        const val MODID = "velocitymultiplier"
        val LOGGER = LoggerFactory.getLogger(MODID)
        lateinit var server: MinecraftServer
            private set

        var velocitymultiplierDefaultValue = Vec3d(1.0, 1.0, 1.0)
            set(value) {
                for (world in server.worlds) {
                    for (entity in world.iterateEntities()) {
                        if (entity is VelocityMultiplierOverridable) {
                            entity.velocitymultiplierOnDefaultMultiplierChanged(
                                velocitymultiplierDefaultValue,
                                value
                            )
                        }
                    }
                }

                field = value
            }

        fun invertMultiplier(multiplier: Vec3d): Vec3d {
            val x = if (multiplier.x == 0.0) 0.0 else 1 / multiplier.x
            val y = if (multiplier.x == 0.0) 0.0 else 1 / multiplier.y
            val z = if (multiplier.x == 0.0) 0.0 else 1 / multiplier.z
            return Vec3d(x, y, z)
        }
    }
}