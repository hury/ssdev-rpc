package ctd.test.registry;

import ctd.net.rpc.registry.ServiceRegistry;
import ctd.util.store.StoreException;
import ctd.util.store.support.ZooKeeperActiveStore;

public class RegistryTester {

	/**
	 * @param args
	 * @throws StoreException 
	 */
	public static void main(String[] args) throws StoreException {
		ZooKeeperActiveStore store = new ZooKeeperActiveStore("127.0.0.1:2181");
		ServiceRegistry registry = new ServiceRegistry();
		registry.setStore(store);
		registry.undeployService("esb.roleWatcher");
		registry.undeployService("esb.userWatcher");
		registry.undeployService("esb.schemaWatcher");
		registry.undeployService("esb.dictionaryWatcher");
		registry.undeployService("esb.applicationWatcher");
		registry.undeployService("esb.categoryNodeWatcher");
		registry.undeployService("esb.hello");
	}

}
