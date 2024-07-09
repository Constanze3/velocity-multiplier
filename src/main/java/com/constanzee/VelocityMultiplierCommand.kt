package com.constanzee

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.Entity
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

object VelocityMultiplierCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("velocitymultiplier")
            .requires { source -> source.hasPermissionLevel(2) }
            .then(literal("default")
                .then(argument("multiplier", Vec3dArgumentType.vec3d())
                    .executes { context ->
                        executeDefault(
                            Vec3dArgumentType.getVec3d(context, "multiplier"),
                            context
                        )
                    }
                )
            )
            .then(argument("targets", EntityArgumentType.entities())
                .then(argument("multiplier", Vec3dArgumentType.vec3d())
                    .executes { context ->
                        execute(
                            EntityArgumentType.getEntities(context, "targets"),
                            Vec3dArgumentType.getVec3d(context, "multiplier"),
                            context
                        )
                    }
                )
            )
        )
    }

    private fun executeDefault(
        value: Vec3d, context: CommandContext<ServerCommandSource>
    ): Int {
        VelocityMultiplier.velocitymultiplierDefaultValue = value
        context.source.sendFeedback({ Text.of("Set the default velocity multiplier to $value") }, false)

        return 1
    }

    private fun execute(
        targets: Collection<Entity>, value: Vec3d, context: CommandContext<ServerCommandSource>
    ): Int {
        var firstTargetName: Text? = null
        for (target in targets) {
            if (firstTargetName == null) {
                firstTargetName = target.name
            }

            if (target is VelocityMultiplierOverridable) {
                target.velocitymultiplierOverrideValue = value
            }
        }

        val message = if (targets.size == 1) {
            val name = firstTargetName!!.string
            "Set the velocity multiplier of $name to $value"
        } else {
            "Set the velocity multiplier of ${targets.size} entities to $value"
        }

        context.source.sendFeedback({ Text.of(message) }, false)

        return 1
    }
}