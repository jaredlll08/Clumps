package com.blamejared.clumps.proxy;

import com.blamejared.clumps.events.CommonEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	
	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
	}
	
	public void registerRenders() {
		
	}
}
