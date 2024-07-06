package com.constanzee

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.entity.Entity
import net.minecraft.text.Text

import net.minecraft.server.command.*
import net.minecraft.server.command.CommandManager.*
import net.minecraft.util.math.Vec3d

object VelocityMultiplierCommand {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(literal("velocitymultiplier")
            .requires { source -> source.hasPermissionLevel(2) }
            .then(literal("default")
                .then(argument("value", Vec3ArgumentType.vec3())
                    .executes { context ->
                        executeDefault(
                            Vec3ArgumentType.getVec3(context, "value"),
                            context
                        )
                    }
                )
            )
            .then(argument("targets", EntityArgumentType.entities())
                .then(argument("value", Vec3ArgumentType.vec3())
                    .executes { context ->
                        execute(
                            EntityArgumentType.getEntities(context, "targets"),
                            Vec3ArgumentType.getVec3(context, "value"),
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
        VelocityMultiplier.velocityMultiplierDefaultValue = value
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
                target.velocityMultiplierOverrideValue = value
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