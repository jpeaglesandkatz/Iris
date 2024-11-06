package net.irisshaders.iris.mixinterface;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemStackHolder {
	@Nullable ItemStack iris$getItemStack();

	void iris$setItemStack(@Nullable ItemStack itemStack);
}
