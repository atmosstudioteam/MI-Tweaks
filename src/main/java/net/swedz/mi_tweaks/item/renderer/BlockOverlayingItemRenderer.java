package net.swedz.mi_tweaks.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

public final class BlockOverlayingItemRenderer extends BlockEntityWithoutLevelRenderer
{
	private final ModelResourceLocation itemModelLocation;

	public BlockOverlayingItemRenderer(
			ModelResourceLocation itemModelLocation,
			Function<ItemStack, Optional<Block>> blockFromItemStackGetter
	)
	{
		super(null, null);

		this.itemModelLocation = itemModelLocation;
	}

	@Override
	public void renderByItem(
			ItemStack stack,
			ItemDisplayContext displayContext,
			PoseStack poseStack,
			MultiBufferSource buffer,
			int packedLight,
			int packedOverlay
	)
	{
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

		poseStack.pushPose();

		poseStack.translate(0.5, 0.5, 0.5);

		BakedModel model = renderer
				.getItemModelShaper()
				.getModelManager()
				.getModel(itemModelLocation);

		renderer.render(
				stack,
				displayContext,
				isLeftHand(displayContext),
				poseStack,
				buffer,
				packedLight,
				packedOverlay,
				model
		);

		poseStack.popPose();
	}

	private static boolean isLeftHand(ItemDisplayContext context)
	{
		return context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ||
			   context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}
}
