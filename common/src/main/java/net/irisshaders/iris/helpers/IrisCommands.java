package net.irisshaders.iris.helpers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.RTUniforms;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class IrisCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("iris")
			.then(Commands.argument("name", StringArgumentType.word()).suggests((s, t) -> SharedSuggestionProvider.suggest(Iris.getCurrentPack().get().getShaderUniformList().getUniformNames(), t)).then(Commands.argument("value", StringArgumentType.word())
				.suggests((cc, b) -> {
					RTUniforms<?> uniform = Iris.getCurrentPack().get().getShaderUniformList().getUniformByName(StringArgumentType.getString(cc, "name"));
					if (uniform instanceof RTUniforms.BooleanUniform) {
						b.suggest("true");
						b.suggest("false");
					} else if (uniform instanceof RTUniforms.FloatUniform fu) {
						for (Float v : fu.getValues()) {
							b.suggest(String.valueOf(v));
						}
					} else if (uniform instanceof RTUniforms.IntUniform iu) {
						for (Integer i : iu.getValues()) {
							b.suggest(String.valueOf(i));
						}
					}

					return b.buildFuture();
				})
				.executes(cc -> {
				try {
					Iris.logger.warn("Setting " + StringArgumentType.getString(cc, "name") + " to " + StringArgumentType.getString(cc, "value"));
					Iris.getCurrentPack().get().getShaderUniformList().getUniformByName(StringArgumentType.getString(cc, "name"))
						.setValueFromString(StringArgumentType.getString(cc, "value"));
					Iris.getCurrentPack().get().getShaderUniformList().save();
					Minecraft.getInstance().player.displayClientMessage(Component.literal("Saved option " + StringArgumentType.getString(cc, "name") + "."), true);
				} catch (IOException e) {
					Iris.logger.error("Failed to save uniforms!", e);
					Minecraft.getInstance().player.displayClientMessage(Component.literal("Failed to save option!"), false);
				}

				return 1;
			}))));
	}
}
