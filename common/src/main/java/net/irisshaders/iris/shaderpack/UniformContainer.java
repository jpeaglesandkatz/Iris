package net.irisshaders.iris.shaderpack;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

public class UniformContainer implements Iterable<UniformContainer.Uniform<?>> {
	@SerializedName("uniforms")
	List<Uniform<?>> uniforms;

	@Override
	public @NotNull Iterator<Uniform<?>> iterator() {
		return uniforms.iterator();
	}

	public static class Uniform<T> {
		@SerializedName("uniformName")
		public String uniformName;

		@SerializedName("settingName")
		public String settingName;

		@SerializedName("type")
		public Class<?> type;

		@SerializedName("values")
		public T[] values;

		@SerializedName("default")
		public T defaultValue;
	}


	public static class UniformTypeAdapter implements JsonDeserializer<Uniform<?>> {
		@Override
		public Uniform<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String type = jsonObject.get("type").getAsString();
			String uniformName = jsonObject.get("uniformName").getAsString();
			String settingName = jsonObject.get("settingName").getAsString();
			JsonArray valuesArray = jsonObject.has("values") ? jsonObject.get("values").getAsJsonArray() : null;
			JsonElement valueMin = jsonObject.get("valueMin");
			JsonElement valueMax = jsonObject.get("valueMax");
			JsonElement interval = jsonObject.get("interval");
			JsonElement defaultValue = jsonObject.get("default");

			// Create the appropriate Uniform type based on the "type" field
			switch (type) {
				case "float": {
					Uniform<Float> uniform = new Uniform<>();
					uniform.uniformName = uniformName;
					uniform.settingName = settingName;
					uniform.type = Float.class;
					if (valuesArray != null) {
						uniform.values = new Float[valuesArray.size()];
						for (int i = 0; i < valuesArray.size(); i++) {
							uniform.values[i] = valuesArray.get(i).getAsFloat();
						}
					} else if (valueMax != null) {
						float valueMaxF = valueMax.getAsFloat();
						float valueMinF = valueMin.getAsFloat();
						float intervalF = interval.getAsFloat();

						int decimalCount = getDecimalCount(intervalF);

						uniform.values = new Float[(int) Math.ceil((valueMaxF - valueMinF) / intervalF) + 1];
						int e = 0;
						for (float i = valueMinF; i <= valueMaxF; i += intervalF) {
							uniform.values[e] = roundToDecimals(i, decimalCount);
							e++;
						}

					}

					uniform.defaultValue = defaultValue.getAsFloat();

					return uniform;
				}
				case "int": {
					Uniform<Integer> uniform = new Uniform<>();
					uniform.uniformName = uniformName;
					uniform.settingName = settingName;
					uniform.type = Integer.class;
					if (valuesArray != null) {
						uniform.values = new Integer[valuesArray.size()];
						for (int i = 0; i < valuesArray.size(); i++) {
							uniform.values[i] = valuesArray.get(i).getAsInt();
						}
					} else if (valueMax != null) {
						int valueMaxI = valueMax.getAsInt();
						int valueMinI = valueMin.getAsInt();
						int intervalI = interval.getAsInt();

						uniform.values = new Integer[(int) (double) ((valueMaxI - valueMinI) / intervalI) + 1];
						int e = 0;
						for (int i = valueMinI; i <= valueMaxI; i += intervalI) {
							uniform.values[e] = i;
							e++;
						}
					}

					uniform.defaultValue = defaultValue.getAsInt();

					return uniform;
				}
				case "bool": {
					Uniform<Boolean> uniform = new Uniform<>();
					uniform.uniformName = uniformName;
					uniform.settingName = settingName;
					uniform.type = Boolean.class;
					uniform.values = new Boolean[2];
					uniform.values[0] = true;
					uniform.values[1] = false;
					uniform.defaultValue = defaultValue.getAsBoolean();
					return uniform;
				}
				default: {
					throw new JsonParseException("Unknown uniform type: " + type);
				}
			}
		}

		// Method to round a value to a specified number of decimal places
		private static float roundToDecimals(float value, int decimalPlaces) {
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(decimalPlaces, RoundingMode.UP);
			return bd.floatValue();
		}

		private static int getDecimalCount(float value) {
			String text = Float.toString(Math.abs(value));
			int decimalIndex = text.indexOf('.');
			return decimalIndex < 0 ? 0 : text.length() - decimalIndex - 1;
		}
	}
}
