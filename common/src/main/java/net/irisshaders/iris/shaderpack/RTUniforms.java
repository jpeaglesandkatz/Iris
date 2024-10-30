package net.irisshaders.iris.shaderpack;

import java.io.Serializable;
import java.util.Properties;
import java.util.function.BiConsumer;

public interface RTUniforms<T> extends Serializable {
	T getCurrentValue();

	void setValue(T value);

	T[] getValues();

	String getUniformName();

	String getSettingName();

	void setValueFromString(String value);

	class BooleanUniform implements RTUniforms<Boolean> {
		private final String uniformName;
		private final String settingName;
		private boolean value;

		public BooleanUniform(String uniformName, String settingName, boolean value) {
			this.uniformName = uniformName;
			this.settingName = settingName;
			this.value = value;
		}

		@Override
		public Boolean getCurrentValue() {
			return value;
		}

		@Override
		public void setValue(Boolean value) {
			this.value = value;
		}

		@Override
		public Boolean[] getValues() {
			return new Boolean[]{true, false};
		}

		@Override
		public String getUniformName() {
			return uniformName;
		}

		@Override
		public String getSettingName() {
			return settingName;
		}

		@Override
		public void setValueFromString(String value) {
			setValue(Boolean.parseBoolean(value));
		}

		@Override
		public String toString() {
			return "Bool uniform: value " + value + " " + uniformName;
		}
	}

	class FloatUniform implements RTUniforms<Float> {
		private final String uniformName;
		private final String settingName;
		private float value;
		private Float[] values;

		public FloatUniform(String uniformName, String settingName, float value, Float[] values) {
			this.uniformName = uniformName;
			this.settingName = settingName;
			this.value = value;
			this.values = values;
		}

		@Override
		public Float getCurrentValue() {
			return value;
		}

		@Override
		public void setValue(Float value) {
			this.value = value;
		}

		@Override
		public Float[] getValues() {
			return values;
		}

		@Override
		public String getUniformName() {
			return uniformName;
		}

		@Override
		public String getSettingName() {
			return settingName;
		}

		@Override
		public void setValueFromString(String value) {
			setValue(Float.parseFloat(value));
		}

		@Override
		public String toString() {
			return "Float uniform: value " + value + " " + uniformName;
		}
	}

	class IntUniform implements RTUniforms<Integer> {
		private final String uniformName;
		private final String settingName;
		private int value;
		private Integer[] values;

		public IntUniform(String uniformName, String settingName, int value, Integer[] values) {
			this.uniformName = uniformName;
			this.settingName = settingName;
			this.value = value;
			this.values = values;
		}

		@Override
		public Integer getCurrentValue() {
			return value;
		}

		@Override
		public void setValue(Integer value) {
			this.value = value;
		}

		@Override
		public Integer[] getValues() {
			return values;
		}

		@Override
		public String getUniformName() {
			return uniformName;
		}

		@Override
		public String getSettingName() {
			return settingName;
		}

		@Override
		public void setValueFromString(String value) {
			setValue(Integer.parseInt(value));
		}

		@Override
		public String toString() {
			return "Integer uniform: value " + value + " " + uniformName;
		}
	}

	default void save(BiConsumer<String, String> saveFunction) {
		saveFunction.accept(getSettingName(), String.valueOf(getCurrentValue()));
	}
}
