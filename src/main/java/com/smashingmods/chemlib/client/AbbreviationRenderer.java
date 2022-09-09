package com.smashingmods.chemlib.client;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.Config;
import com.smashingmods.chemlib.api.Chemical;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AbbreviationRenderer extends BlockEntityWithoutLevelRenderer {

	public static final Supplier<BlockEntityWithoutLevelRenderer> INSTANCE = Suppliers.memoize(
			() -> new AbbreviationRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())
	);
	public static final IItemRenderProperties RENDERER = new IItemRenderProperties()
	{
		@Override
		public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
			return INSTANCE.get();
		}
	};

	public AbbreviationRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
		super(pBlockEntityRenderDispatcher, pEntityModelSet);
	}

	@Override
	public void renderByItem(ItemStack pStack, ItemTransforms.TransformType pTransformType, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

		boolean gui = pTransformType == ItemTransforms.TransformType.GUI;
		boolean frame = pTransformType == ItemTransforms.TransformType.FIXED;

		ModelResourceLocation modelResourceLocation = null;

		if (pStack.getItem() instanceof ElementItem elementItem) {
			switch (elementItem.getMatterState()) {
				case LIQUID -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_liquid_model"), "inventory");
				case GAS -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_gas_model"), "inventory");
				default -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_solid_model"), "inventory");
			}
		} else if (pStack.getItem() instanceof ChemicalItem chemicalItem) {
			switch (chemicalItem.getItemType()) {
				case DUST -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_dust_model"), "inventory");
				case NUGGET -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_nugget_model"), "inventory");
				case INGOT -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_ingot_model"), "inventory");
				case PLATE -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_plate_model"), "inventory");
			}
		}

		if (modelResourceLocation != null) {

			BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(modelResourceLocation);

			pPoseStack.pushPose();
			pPoseStack.translate(0.5D, 0.5D, 0D);
			if (gui) {
				Lighting.setupForFlatItems();
			}
			pPoseStack.pushPose();

			switch (pTransformType) {
				case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
					pPoseStack.translate(0, -0.2D, 0.45D);
				}
				case FIRST_PERSON_LEFT_HAND -> {
					pPoseStack.translate(-0.025D, -0.025D, 0.75D);
					pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(25));
					pPoseStack.mulPose(Vector3f.XN.rotationDegrees(45));
					pPoseStack.mulPose(Vector3f.YN.rotationDegrees(80));
				}
				case FIRST_PERSON_RIGHT_HAND -> {
					pPoseStack.translate(-0.20D, -0.05D, 0.75D);
					pPoseStack.mulPose(Vector3f.ZN.rotationDegrees(25));
					pPoseStack.mulPose(Vector3f.XP.rotationDegrees(45));
					pPoseStack.mulPose(Vector3f.YP.rotationDegrees(100));
					pPoseStack.mulPose(Vector3f.ZN.rotationDegrees(45));
				}
				case HEAD -> {
					pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180));
					pPoseStack.translate(0, -0.75D, -0.75D);
				}
				case GROUND -> {
					pPoseStack.translate(0, -0.25D, 0.5D);
					pPoseStack.scale(1.5F, 1.5F, 1.5F);
				}
				case FIXED -> {
					pPoseStack.mulPose(Vector3f.YN.rotationDegrees(180));
					pPoseStack.translate(0, 0, -0.5D);
				}
			}

			Minecraft.getInstance().getItemRenderer().render(
					pStack,
					pTransformType,
					false,
					pPoseStack,
					pBuffer,
					gui ? 0xF000F0 : pPackedLight,
					gui ? OverlayTexture.NO_OVERLAY : pPackedOverlay,
					ForgeHooksClient.handleCameraTransforms(pPoseStack, bakedModel, pTransformType, false));
			pPoseStack.popPose();

			if (gui || frame) {
				pPoseStack.pushPose();
				pPoseStack.mulPose(Vector3f.XN.rotation(180));
				pPoseStack.translate(-0.16D, 0, -0.55D);
				pPoseStack.scale(0.05F, 0.08F, 0.08F);

				if (frame) {
					pPoseStack.mulPose(Vector3f.YN.rotationDegrees(180));
					pPoseStack.mulPose(Vector3f.XN.rotationDegrees(53));
					pPoseStack.translate(-8D, -1D, 1.7D);
					pPoseStack.scale(1F, 0.65F, 1F);
				}

				Consumer<Chemical> renderAbbreviation = (chemical) -> Minecraft.getInstance().font.drawShadow(pPoseStack, chemical.getAbbreviation(), -5, 0, 0xFFFFFF);

				if (pStack.getItem() instanceof ElementItem elementItem) {
					if (Config.Common.renderElementAbbreviations.get()) {
						renderAbbreviation.accept(elementItem);
					}
				} else if (pStack.getItem() instanceof ChemicalItem chemicalItem) {
					switch (chemicalItem.getItemType()) {
						case DUST -> {
							if (Config.Common.renderDustAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case NUGGET -> {
							if (Config.Common.renderNuggetAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case INGOT -> {
							if (Config.Common.renderIngotAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
						case PLATE -> {
							if (Config.Common.renderPlateAbbreviations.get()) {
								renderAbbreviation.accept(chemicalItem);
							}
						}
					}
				}
				pPoseStack.popPose();
			}
			pPoseStack.popPose();
		}
	}
}
