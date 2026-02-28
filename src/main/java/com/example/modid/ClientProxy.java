package com.example.modid;

import com.timlee9024.crgltf.api.v0.CRglTFApi;
import com.timlee9024.crgltf.api.v0.UniversalGltfRendererListener;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientProxy extends CommonProxy {

	private ItemRendererExample itemRenderer;
	private TileEntitySpecialRendererExample tesr;
	private RenderEntityExample renderEntity;

	@Override
	public void onEvent(FMLPreInitializationEvent event) {
		itemRenderer = new ItemRendererExample();
		tesr = new TileEntitySpecialRendererExample();

		//According to Forge Doc "Each mod should only have one instance of a custom TEISR/ISTER/BEWLR.", due to creating an instance will also initiate unused fields inside the class which waste a lots of memory.
		TileEntityItemStackRenderer teisr = new TileEntityItemStackRenderer() {

			@Override
			public void renderByItem(ItemStack itemStackIn) {
				Item currentItem = itemStackIn.getItem();
				if (currentItem == item) {
					itemRenderer.renderWithItemCameraTransformsHelper();
				} else if (currentItem == itemBlock) {
					tesr.renderByItem(itemStackIn);
				}
			}

		};

		item.setTileEntityItemStackRenderer(teisr);
		itemBlock.setTileEntityItemStackRenderer(teisr);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityExample.class, tesr);

		RenderingRegistry.registerEntityRenderingHandler(EntityExample.class, manager -> {
			renderEntity = new RenderEntityExample(manager);
			return renderEntity;
		});
	}

	@Override
	public void onEvent(FMLInitializationEvent event) {
		List<Map.Entry<ResourceLocation, UniversalGltfRendererListener>> entries = new ArrayList<>(3);
		entries.add(new AbstractMap.SimpleEntry<>(new ResourceLocation(Reference.MOD_ID, "models/item/water_bottle.gltf"), itemRenderer));
		entries.add(new AbstractMap.SimpleEntry<>(new ResourceLocation(Reference.MOD_ID, "models/block/boom_box.gltf"), tesr));
		entries.add(new AbstractMap.SimpleEntry<>(new ResourceLocation(Reference.MOD_ID, "models/entity/cesium_man.gltf"), renderEntity));
		CRglTFApi.getInstance().getUniversalGltfRenderer().addShareableModels(entries);
		itemRenderer.lastFrameTime = itemRenderer.tempLastFrameTime;
		tesr.lastFrameTime = tesr.tempLastFrameTime;
		renderEntity.lastFrameTime = renderEntity.tempLastFrameTime;
	}

	@SubscribeEvent
	public void onEvent(ModelRegistryEvent event) {
		ItemCameraTransformsHelper.registerDummyModelToAccessCurrentTransformTypeForTEISR(item);

		//The regular way of register base model for TEISR, use this if you don't need perspective-wise rendering.
		ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(Reference.MOD_ID + ":example_item_block#inventory"));
	}

}
