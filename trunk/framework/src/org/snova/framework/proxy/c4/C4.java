/**
 * 
 */
package org.snova.framework.proxy.c4;

import org.arch.util.ListSelector;
import org.arch.util.NetworkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snova.framework.event.UserLoginEvent;
import org.snova.framework.proxy.RemoteProxyHandler;
import org.snova.framework.proxy.RemoteProxyManager;
import org.snova.framework.proxy.RemoteProxyManagerHolder;

/**
 * @author qiyingwang
 * 
 */
public class C4
{
	protected static Logger logger = LoggerFactory.getLogger(C4.class);
	public static boolean enable;
	static ListSelector<C4ServerAuth> servers = new ListSelector<C4ServerAuth>();

	public static class C4RemoteProxyManager implements RemoteProxyManager
	{
		@Override
		public String getName()
		{
			return "C4";
		}

		@Override
		public RemoteProxyHandler createProxyHandler(String[] attr)
		{
			
			return new C4RemoteHandler(servers.select());
		}
	}

	public static boolean init()
	{
		if (!C4Config.init())
		{
			return false;
		}
		if (C4Config.appids.isEmpty())
		{
			return false;
		}
		logger.info("C4 init.");
		for (C4ServerAuth server : C4Config.appids)
		{
			UserLoginEvent ev = new UserLoginEvent();
			ev.user = NetworkHelper.getMacAddress();
			new C4RemoteHandler(server).requestEvent(ev);
			servers.add(server);
		}

		RemoteProxyManagerHolder
		        .registerRemoteProxyManager(new C4RemoteProxyManager());
		enable = true;
		return true;
	}
}
