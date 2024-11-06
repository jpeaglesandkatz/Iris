package net.irisshaders.iris.mixin.entity_render_context;

import com.mojang.blaze3d.vertex.PoseStack;
import net.irisshaders.iris.mixinterface.ItemStackHolder;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemStackRenderState.LayerRenderState.class, priority = 1010)
public abstract class MixinItemRenderer {
	@Shadow
	@Final
	ItemStackRenderState field_55345;

	@Unique
	private int previousBeValue;

	@Inject(method = "render", at = @At(value = "HEAD"))
	private void changeId(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
		iris$setupId(((ItemStackHolder) this.field_55345).iris$getItemStack());
	}

	@Unique
	private void iris$setupId(ItemStack pItemRenderer0) {
		if (WorldRenderingSettings.INSTANCE.getItemIds() == null || pItemRenderer0 == null) return;

		if (pItemRenderer0.getItem() instanceof BlockItem blockItem && !(pItemRenderer0.getItem() instanceof SolidBucketItem)) {
			if (WorldRenderingSettings.INSTANCE.getBlockStateIds() == null) return;

			previousBeValue = CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity();
			CapturedRenderingState.INSTANCE.setCurrentBlockEntity(1);

			//System.out.println(WorldRenderingSettings.INSTANCE.getBlockStateIds().getInt(blockItem.getBlock().defaultBlockState()));
			CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getBlockStateIds().getOrDefault(blockItem.getBlock().defaultBlockState(), 0));
		} else {
			ResourceLocation location = BuiltInRegistries.ITEM.getKey(pItemRenderer0.getItem());

			CapturedRenderingState.INSTANCE.setCurrentRenderedItem(WorldRenderingSettings.INSTANCE.getItemIds().applyAsInt(new NamespacedId(location.getNamespace(), location.getPath())));
		}
	}

	@Inject(method = "render", at = @At(value = "RETURN"))
	private void changeId3(CallbackInfo ci) {
		CapturedRenderingState.INSTANCE.setCurrentRenderedItem(0);
		CapturedRenderingState.INSTANCE.setCurrentBlockEntity(previousBeValue);
		previousBeValue = 0;
	}
}
