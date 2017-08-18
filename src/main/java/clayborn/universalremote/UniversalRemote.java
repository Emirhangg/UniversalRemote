package clayborn.universalremote;

import clayborn.universalremote.network.FMLNetworkHandlerInjector;
import clayborn.universalremote.network.OpenGuiWrapper;
import clayborn.universalremote.network.OpenRemoteGuiMessage;
import clayborn.universalremote.network.UniversalRemotePacketHandler;
import clayborn.universalremote.proxy.ISidedProxy;
import clayborn.universalremote.registrar.Registrar;
import clayborn.universalremote.settings.Strings;
import clayborn.universalremote.util.Logger;
import clayborn.universalremote.util.Util;
import clayborn.universalremote.version.UniversalRemoteVersionProvider;
import clayborn.universalremote.version.VersionTracker;
import clayborn.universalremote.world.PlayerWorldSyncServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Strings.MODID, version = Strings.VERSION)
public class UniversalRemote
{

	// Proxy
    @SidedProxy(clientSide = Strings.CLIENTPROXY, serverSide = Strings.SERVERPROXY)
    public static ISidedProxy proxy;    
    
    // Here be initialization events!
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	Util.logger = new Logger(event.getModLog());
    	
    	// find the fields we need regardless of obs
    	OpenGuiWrapper.findFields();

    	// register event handlers
    	MinecraftForge.EVENT_BUS.register(new Registrar());
    	MinecraftForge.EVENT_BUS.register(new VersionTracker());
    	MinecraftForge.EVENT_BUS.register(PlayerWorldSyncServer.INSTANCE);    	
    	
    	FMLNetworkHandlerInjector.preInit(event);
    	
    	VersionTracker.register(new UniversalRemoteVersionProvider());
    	
    	proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
		Util.logger.info("Starting initalization...");
		
    	
		
    	// register our packet
    	UniversalRemotePacketHandler.INSTANCE.registerMessage(
    			OpenRemoteGuiMessage.OpenRemoteGuiMessageHandler.class,
    			OpenRemoteGuiMessage.class, UniversalRemotePacketHandler.getNextId(), Side.CLIENT);
    	
    	// get version data from the net
    	VersionTracker.downloadVersions();
    	
    	proxy.init(event);
    	
    	Util.logger.info("Initalization complete!");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
    	proxy.postInit(event);
    }    
    
}
