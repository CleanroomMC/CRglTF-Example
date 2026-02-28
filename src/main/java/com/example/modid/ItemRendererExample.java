package com.example.modid;

import com.timlee9024.crgltf.api.v0.CRglTFApi;
import com.timlee9024.crgltf.api.v0.CompiledGltfModel;
import com.timlee9024.crgltf.api.v0.UniversalGltfRenderer;
import com.timlee9024.crgltf.api.v0.UniversalGltfRendererListener;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AnimationModel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.animation.Animation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemRendererExample implements UniversalGltfRendererListener {

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

	/**
	 * Require {@link ItemCameraTransformsHelper#registerDummyModelToAccessCurrentTransformTypeForTEISR(net.minecraft.item.Item) ItemCameraTransformsHelper#registerDummyModelToAccessCurrentTransformTypeForTEISR(yourItem)} during
	 * {@link net.minecraftforge.client.event.ModelRegistryEvent ModelRegistryEvent} to make {@link ItemCameraTransformsHelper#getCurrentTransformType()} work.
	 */
	public void renderWithItemCameraTransformsHelper() {
		if (!isModelLoaded) return;

		if (lastFrameTime > 0) {
			float time = Animation.getWorldTime(Minecraft.getMinecraft().world, Animation.getPartialTickTime());
			gltfRenderer.playAnimation(modelId, animation, time % lastFrameTime);
		}
		switch (ItemCameraTransformsHelper.getCurrentTransformType()) {
			case GUI:
				gltfRenderer.render(modelId, 0);
				break;
			default:
				gltfRenderer.render(modelId, 0);
				break;
		}
	}

}
