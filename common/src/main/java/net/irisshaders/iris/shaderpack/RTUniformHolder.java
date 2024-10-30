package net.irisshaders.iris.shaderpack;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.option.values.MutableOptionValues;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;

public class RTUniformHolder implements Iterable<RTUniforms<?>> {
	private final ShaderPack pack;
	private Path propertyPath;
	private Map<String, String> changed;

	private List<RTUniforms<?>> uniformList;

	private Object2ObjectMap<String, RTUniforms<?>> uniformMap = new Object2ObjectOpenHashMap<>();
	private Path shaderPackConfigFile;

	public RTUniformHolder(ShaderPack pack, Path shaderPackConfigFile, Map<String, String> properties, List<UniformContainer.Uniform<?>> uniforms) throws IOException {
		this.pack = pack;
		uniformList = new ArrayList<>();
		this.shaderPackConfigFile = shaderPackConfigFile;

		System.out.println(properties);

		this.changed = new Object2ObjectOpenHashMap<>();
		for (UniformContainer.Uniform<?> uniform : uniforms) {
			if (uniform.type.equals(Boolean.class)) {
				RTUniforms.BooleanUniform booleanUniform = new RTUniforms.BooleanUniform(uniform.uniformName, uniform.settingName, Boolean.parseBoolean((String) properties.getOrDefault(uniform.settingName, Boolean.toString((boolean) uniform.defaultValue))));
				uniformMap.put(uniform.settingName, booleanUniform);
				uniformList.add(booleanUniform);

				if (!properties.containsKey(uniform.settingName)) changed.put(uniform.settingName, String.valueOf(booleanUniform.getCurrentValue()));
			} else if (uniform.type.equals(Float.class)) {
				RTUniforms.FloatUniform floatUniform = new RTUniforms.FloatUniform(uniform.uniformName, uniform.settingName, Float.parseFloat((String) properties.getOrDefault(uniform.settingName, Float.toString((float) uniform.defaultValue))), (Float[]) uniform.values);
				uniformMap.put(uniform.settingName, floatUniform);
				uniformList.add(floatUniform);

				if (!properties.containsKey(uniform.settingName)) changed.put(uniform.settingName, String.valueOf(floatUniform.getCurrentValue()));
			} else if (uniform.type.equals(Integer.class)) {
				RTUniforms.IntUniform intUniform = new RTUniforms.IntUniform(uniform.uniformName, uniform.settingName, Integer.parseInt((String) properties.getOrDefault(uniform.settingName, Integer.toString((int) uniform.defaultValue))), (Integer[]) uniform.values);
				uniformMap.put(uniform.settingName, intUniform);
				uniformList.add(intUniform);

				if (!properties.containsKey(uniform.settingName)) changed.put(uniform.settingName, String.valueOf(intUniform.getCurrentValue()));
			}
		}

		uniformList.forEach(s -> System.out.println(s.toString()));
	}

	public List<RTUniforms<?>> getUniformList() {
		return uniformList;
	}

	public void save() throws IOException {
		MutableOptionValues changedConfigsValues = pack.getShaderPackOptions().getOptionValues().mutableCopy();

		// Store changed values from those currently in use by the shader pack
		Properties configsToSave = new Properties();
		changedConfigsValues.getBooleanValues().forEach((k, v) -> configsToSave.setProperty(k, Boolean.toString(v)));
		changedConfigsValues.getStringValues().forEach(configsToSave::setProperty);
		forAllUniforms(configsToSave::setProperty);

		tryUpdateConfigPropertiesFile(shaderPackConfigFile, configsToSave);
	}

	private static void tryUpdateConfigPropertiesFile(Path path, Properties properties) {
		try {
			if (properties.isEmpty()) {
				// Delete the file or don't create it if there are no changed configs
				if (Files.exists(path)) {
					Files.delete(path);
				}

				return;
			}

			try (OutputStream out = Files.newOutputStream(path)) {
				properties.store(out, null);
			}
		} catch (IOException e) {
			Iris.logger.error("Tried to update config but failed!", e);
		}
	}

	private static Properties readProperties(Path shaderPath, String name) {
		try {
			// ID maps should be encoded in ISO_8859_1.
			Properties props = new Properties();
			props.load(new StringReader(Files.readString(shaderPath.resolve(name), StandardCharsets.ISO_8859_1)));
			return props;
		} catch (NoSuchFileException e) {
			Iris.logger.debug("An " + name + " file was not found in the current shaderpack");

			return new Properties();
		} catch (IOException e) {
			Iris.logger.error("An IOException occurred reading " + name + " from the current shaderpack", e);

			return new Properties();
		}
	}

	@Override
	public @NotNull Iterator<RTUniforms<?>> iterator() {
		return this.uniformList.iterator();
	}

	public RTUniforms<?> getUniformByName(String name) {
		return uniformMap.get(name);
	}

	public Iterable<String> getUniformNames() {
		return uniformMap.keySet();
	}

	public void forAllUniforms(BiConsumer<String, String> saveFunction) {
		for (RTUniforms<?> uniform : uniformList) {
			uniform.save(saveFunction);
		}
	}
}
