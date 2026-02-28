package com.example.modid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, dependencies = "required-after-client:crgltf;", useMetadata = true)
public class Example {

	@SidedProxy(clientSide = Reference.PACKAGE + ".ClientProxy", serverSide = Reference.PACKAGE + ".ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void onEvent(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(proxy);
		GameRegistry.registerTileEntity(TileEntityExample.class, new ResourceLocation(Reference.MOD_ID, "example_tileentity"));
		proxy.onEvent(event);
	}

	@EventHandler
	public void onEvent(FMLInitializationEvent event) {
		proxy.onEvent(event);
	}

}
