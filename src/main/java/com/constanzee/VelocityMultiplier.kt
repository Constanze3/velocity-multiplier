package com.constanzee

import com.constanzee.SetVelocityMultiplierPayload.Companion.CODEC
import com.constanzee.SetVelocityMultiplierPayload.Companion.ID
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.*
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.Vec3d
import org.slf4j.LoggerFactory

class VelocityMultiplier : ModInitializer {
    override fun onInitialize() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            VelocityMultiplierCommand.register(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            server = it
        }

        LOGGER.info("Velocity Multiplier is ready!")
    }

    companion object {
        const val MODID = "velocitymultiplier"
        val LOGGER = LoggerFactory.getLogger(MODID)
        lateinit var server: MinecraftServer
            private set

        var velocityMultiplierDefaultValue = Vec3d(1.0, 1.0, 1.0)
            set(value) {
                for (world in server.worlds) {
                    for (entity in world.iterateEntities()) {
                        if (entity is VelocityMultiplierOverridable) {
                            entity.velocityMultiplierOnDefaultMultiplierChanged(
                                velocityMultiplierDefaultValue,
                                value
                            )
                        }
                    }
                }

                field = value
            }
    }
}