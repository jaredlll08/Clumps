package com.blamejared.clumps;

import com.blamejared.clumps.proxy.CommonProxy;
import com.blamejared.clumps.reference.Reference;
import com.teamacronymcoders.base.BaseModFoundation;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDECIES)
public class Clumps extends BaseModFoundation<Clumps> {
	
	
	@Mod.Instance(Reference.MODID)
	public static Clumps INSTANCE;
	
	@SidedProxy(clientSide = "com.blamejared.clumps.proxy.ClientProxy", serverSide = "com.blamejared.clumps.proxy.CommonProxy")
	public static CommonProxy PROXY;
	
	public Clumps() {
		super(Reference.MODID, Reference.NAME, Reference.VERSION, null);
	}
	
	@Override
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	
	
	@Override
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
		PROXY.registerRenders();
		PROXY.registerEvents();
	}
	
	@Override
	public Clumps getInstance() {
		return INSTANCE;
	}
}
