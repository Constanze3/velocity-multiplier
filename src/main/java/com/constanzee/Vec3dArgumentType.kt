package com.constanzee

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import java.util.concurrent.CompletableFuture

class Vec3dArgumentType : ArgumentType<Vec3d> {
    override fun parse(reader: StringReader?): Vec3d {
        val reader = reader!!

        val start = reader.cursor
        val x = parseDouble(reader, start, false)
        val y = parseDouble(reader, start, false)
        val z = parseDouble(reader, start, true)

        return Vec3d(x, y, z)
    }

    private fun parseDouble(reader: StringReader, start: Int, isEnd: Boolean): Double {
        val d = DoubleArgumentType.doubleArg().parse(reader)

        if (!isEnd) {
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip()
            } else {
                reader.cursor = start
                throw INCOMPLETE_EXCEPTION.createWithContext(reader)
            }
        }

        return d
    }

    override fun getExamples(): MutableCollection<String> {
        return EXAMPLES
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>?,
        builder: SuggestionsBuilder?
    ): CompletableFuture<Suggestions> {
        if (!(context!!.source is CommandSource)) {
            return Suggestions.empty()
        } else {
            val remaining = builder!!.remaining

            return CommandSource.suggestPositions(
                remaining, SUGGESTION, builder, CommandManager.getCommandValidator(this::parse)
            )
        }
    }

    companion object {
        val EXAMPLES = mutableListOf("0 0 0", "0.1 -0.5 .9")
        val SUGGESTION: Collection<CommandSource.RelativePosition> = listOf(
            CommandSource.RelativePosition("1", "1", "1")
        )
        val INCOMPLETE_EXCEPTION = SimpleCommandExceptionType(Text.translatable("argument.pos3d.incomplete"))

        fun vec3d(): Vec3dArgumentType {
            return Vec3dArgumentType()
        }

        fun getVec3d(context: CommandContext<ServerCommandSource>, name: String): Vec3d {
            return context.getArgument(name, Vec3d::class.java)
        }
    }
}