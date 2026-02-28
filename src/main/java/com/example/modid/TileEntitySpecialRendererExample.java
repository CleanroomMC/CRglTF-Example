package com.example.modid;

import com.timlee9024.crgltf.api.v0.CRglTFApi;
import com.timlee9024.crgltf.api.v0.CompiledGltfModel;
import com.timlee9024.crgltf.api.v0.UniversalGltfRenderer;
import com.timlee9024.crgltf.api.v0.UniversalGltfRendererListener;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AnimationModel;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.Animation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TileEntitySpecialRendererExample extends TileEntitySpecialRenderer<TileEntityExample> implements UniversalGltfRendererListener {

	protected UniversalGltfRenderer gltfRenderer = CRglTFApi.getInstance().getUniversalGltfRenderer();
	protected boolean isModelLoaded;
	protected int modelId;
	protected int animation = 0; //Play the animation 0 on glTF model.
	protected float tempLastFrameTime;
	protected float lastFrameTime;

	@Override
	public @Nullable CompiledGltfModel onModelIdAssigned(int modelId, @Nullable CompiledGltfModel compiledGltfModel) {
		if (compiledGltfModel == null) {
			isModelLoaded = false;
			return null;
		} else isModelLoaded = true;

		List<AnimationModel> animationModel = compiledGltfModel.getGltfModel().getAnimationModels();
		if (!animationModel.isEmpty()) {
			AccessorFloatData data = (AccessorFloatData) animationModel.get(animation).getChannels().get(0).getSampler().getInput().getAccessorData();
			tempLastFrameTime = data.get(data.getNumElements() - 1, 0);
		} else tempLastFrameTime = 0;
		this.modelId = modelId;
		return null;
	}

	@Override
	public void onModelRefreshComplete() {
		lastFrameTime = tempLastFrameTime;
	}

	@Override
	public void render(TileEntityExample te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (!isModelLoaded) return;

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);

		World world = te.getWorld();
		if (world != null) {
			GL11.glTranslatef(0.5F, 0.0F, 0.5F); //Make sure it is in the center of the block
			switch (world.getBlockState(te.getPos()).getValue(BlockHorizontal.FACING)) {
				case DOWN:
					break;
				case UP:
					break;
				case NORTH:
					break;
				case SOUTH:
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
					break;
				case WEST:
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					break;
				case EAST:
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
					break;
			}
		}
		if (lastFrameTime > 0) {
			float time = Animation.getWorldTime(world, partialTicks);
			gltfRenderer.playAnimation(modelId, animation, Math.fma(lastFrameTime, te.randomOffset, time) % lastFrameTime);
		}
		gltfRenderer.render(modelId, 0);

		GL11.glPopMatrix();
	}

	public void renderByItem(ItemStack itemStackIn) {
		if (lastFrameTime > 0) {
			float time = Animation.getWorldTime(Minecraft.getMinecraft().world, Animation.getPartialTickTime());
			gltfRenderer.playAnimation(modelId, animation, time % lastFrameTime);
		}
		gltfRenderer.render(modelId, 0);
	}
}
