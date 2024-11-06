package net.irisshaders.iris.mixin.entity_render_context;

import net.irisshaders.iris.mixinterface.ItemStackHolder;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public class MixinItemStackRenderState implements ItemStackHolder {
	@Unique
	@Nullable
	private ItemStack itemStack;

	@Override
	public @Nullable ItemStack iris$getItemStack() {
		return itemStack;
	}

	@Override
	public void iris$setItemStack(@Nullable ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Inject(method = "clear", at = @At("RETURN"))
	private void iris$clearItemStack(CallbackInfo ci) {
		itemStack = null;
	}
}
