package com.mrinformatic.refinedsimd;

import com.mrinformatic.refinedsimd.proxy.ProxyCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = RefinedSIMD.MODID, name = RefinedSIMD.NAME, version = RefinedSIMD.VERSION, dependencies = RefinedSIMD.DEPENDENCIES)
public final class RefinedSIMD
{
    public static final String MODID = "refinedsimd";
    public static final String NAME = "Refined SIMD";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "required-after:refinedstorage@[1.6.16,);";

    @SidedProxy(clientSide = "com.mrinformatic.refinedsimd.proxy.ProxyClient", serverSide = "com.mrinformatic.refinedsimd.proxy.ProxyCommon")
    public static ProxyCommon PROXY;
    @Mod.Instance
    public static RefinedSIMD INSTANCE;

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        PROXY.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

}
