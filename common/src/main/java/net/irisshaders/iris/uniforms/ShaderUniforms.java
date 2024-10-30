package net.irisshaders.iris.uniforms;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.gl.uniform.UniformUpdateFrequency;
import net.irisshaders.iris.shaderpack.RTUniformHolder;
import net.irisshaders.iris.shaderpack.RTUniforms;

public class ShaderUniforms {
	public static void add(UniformHolder holder, RTUniformHolder uniforms) {
		for (RTUniforms<?> uniform : uniforms) {
			if (uniform instanceof RTUniforms.BooleanUniform) {
				holder.uniform1b(UniformUpdateFrequency.PER_FRAME, uniform.getUniformName(), () -> (boolean) uniform.getCurrentValue());
			} else if (uniform instanceof RTUniforms.FloatUniform) {
				holder.uniform1f(UniformUpdateFrequency.PER_FRAME, uniform.getUniformName(), () -> (float) uniform.getCurrentValue());
			} else if (uniform instanceof RTUniforms.IntUniform) {
				holder.uniform1i(UniformUpdateFrequency.PER_FRAME, uniform.getUniformName(), () -> (int) uniform.getCurrentValue());
			}
		}
	}
}
