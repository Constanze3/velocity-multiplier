package com.constanzee

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient

class VelocityMultiplierClient : ClientModInitializer {
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SetVelocityMultiplierPayload.ID) { payload, context ->
            val client = MinecraftClient.getInstance()
            val entity = client.world!!.getEntityById(payload.entityId)

            entity?.let {
                val velocityMultiplierOverridable = it as VelocityMultiplierOverridable
                velocityMultiplierOverridable.velocitymultiplierOverrideValue = payload.value
            }
        }
    }
}