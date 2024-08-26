package net.irisshaders.iris.vertices.sodium;

import net.caffeinemc.mods.sodium.api.memory.MemoryIntrinsics;
import net.caffeinemc.mods.sodium.api.util.NormI8;
import net.caffeinemc.mods.sodium.api.vertex.format.common.EntityVertex;
import net.caffeinemc.mods.sodium.api.vertex.serializer.VertexSerializer;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.NormalHelper;
import org.lwjgl.system.MemoryUtil;

public class EntityToTerrainVertexSerializer implements VertexSerializer {
	@Override
	public void serialize(long src, long dst, int vertexCount) {
		// Only accept quads, to be safe
		int quadCount = vertexCount / 4;
		for (int i = 0; i < quadCount; i++) {
			int normal = MemoryUtil.memGetInt(src + 32);
			int tangent = NormalHelper.computeTangent(NormI8.unpackX(normal), NormI8.unpackY(normal), NormI8.unpackZ(normal), MemoryUtil.memGetFloat(src), MemoryUtil.memGetFloat(src + 4), MemoryUtil.memGetFloat(src + 8), MemoryUtil.memGetFloat(src + 16), MemoryUtil.memGetFloat(src + 20),
				MemoryUtil.memGetFloat(src + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 4 + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 8 + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 16 + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 20 + EntityVertex.STRIDE),
				MemoryUtil.memGetFloat(src + EntityVertex.STRIDE + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 4 + EntityVertex.STRIDE + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 8 + EntityVertex.STRIDE + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 16 + EntityVertex.STRIDE + EntityVertex.STRIDE), MemoryUtil.memGetFloat(src + 20 + EntityVertex.STRIDE + EntityVertex.STRIDE));
			float midU = 0, midV = 0;
			for (int vertex = 0; vertex < 4; vertex++) {
				midU += MemoryUtil.memGetFloat(src + 16 + (EntityVertex.STRIDE * vertex));
				midV += MemoryUtil.memGetFloat(src + 20 + (EntityVertex.STRIDE * vertex));
			}

			midU /= 4;
			midV /= 4;

			for (int j = 0; j < 4; j++) {
				MemoryIntrinsics.copyMemory(src, dst, 24);
				MemoryUtil.memPutInt(dst + 24, MemoryUtil.memGetInt(src + 28L));
				MemoryUtil.memPutInt(dst + 28, normal);
				MemoryUtil.memPutShort(dst + 32, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedEntity());
				MemoryUtil.memPutShort(dst + 34, (short) CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity());
				MemoryUtil.memPutFloat(dst + 36, midU);
				MemoryUtil.memPutFloat(dst + 40, midV);
				MemoryUtil.memPutInt(dst + 44, tangent);
				MemoryUtil.memPutInt(dst + 48, 0);

				src += EntityVertex.STRIDE;
				dst += IrisVertexFormats.TERRAIN.getVertexSize();
			}
		}
	}
}
