package com.example.modid;

import com.timlee9024.crgltf.api.v0.CRglTFApi;
import com.timlee9024.crgltf.api.v0.CompiledGltfModel;
import com.timlee9024.crgltf.api.v0.UniversalGltfRenderer;
import com.timlee9024.crgltf.api.v0.UniversalGltfRendererListener;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AnimationModel;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.Animation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class RenderEntityExample extends Render<EntityExample> implements UniversalGltfRendererListener {

	public RenderEntityExample(RenderManager renderManager) {
		super(renderManager);
	}

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
	protected ResourceLocation getEntityTexture(EntityExample entity) {
		return entity.getProfessionForge().getSkin();
	}

	@Override
	public boolean shouldRender(EntityExample livingEntity, ICamera camera, double camX, double camY, double camZ) {
		if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
			return true;
		} else if (livingEntity.getLeashed() && livingEntity.getLeashHolder() != null) {
			Entity entity = livingEntity.getLeashHolder();
			return camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox());
		} else {
			return false;
		}
	}

	@Override
	public void doRender(EntityExample entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (!isModelLoaded) return;

		if (lastFrameTime > 0) {
			float time = Animation.getWorldTime(entity.world, partialTicks);
			gltfRenderer.playAnimation(modelId, animation, time % lastFrameTime);
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks), 0.0F, 1.0F, 0.0F);
		gltfRenderer.render(modelId, 0);
		GL11.glPopMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
		float f;

		for (f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) ;

		while (f >= 180.0F) f -= 360.0F;

		return prevYawOffset + partialTicks * f;
	}

}
