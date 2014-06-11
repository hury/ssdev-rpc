package ctd.net.rpc.registry;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.net.rpc.config.ProviderUrlConfig;
import ctd.net.rpc.config.ServiceConfig;
import ctd.net.rpc.exception.RpcException;
import ctd.net.rpc.transport.ServerUrl;
import ctd.spring.AppDomainContext;
import ctd.util.acl.ACListType;
import ctd.util.lock.KeyEntityRWLockManager;
import ctd.util.store.StoreConstants;
import ctd.util.store.StoreException;
import ctd.util.store.ActiveStore;
import ctd.util.store.listener.NodeListener;
import ctd.util.store.listener.StateListener;

public class ServiceRegistry implements StateListener,Runnable{
	private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
	private static final int PUBLISH_CHECK_DELAY = 5;
	private static final int RETRY_DELAY = 5;
	public static final String CHARSET = "UTF-8";
	private static volatile boolean disable;
	private static List<ServerUrl> serverUrls = new ArrayList<ServerUrl>();
	
	private ConcurrentHashMap<String,ServiceConfig> serviceStore = new ConcurrentHashMap<String,ServiceConfig>();
	private static ConcurrentLinkedQueue<ServiceConfig> uploadQueue = new ConcurrentLinkedQueue<ServiceConfig>();
	protected static CopyOnWriteArraySet<ServiceConfig> deployedSet = new CopyOnWriteArraySet<ServiceConfig>();
	
	private volatile boolean running = true;
	private Thread t;
	private ActiveStore store;
	private String domainServiceRoot;
	private final KeyEntityRWLockManager lockManager = new KeyEntityRWLockManager();
	
	
	public static void publish(ServiceConfig service){
		if(!disable){
			uploadQueue.add(service);
		}
	}

	public static void setDisable(boolean disable) {
		ServiceRegistry.disable = disable;
	}
	
	public static void addServerUrl(ServerUrl url){
		serverUrls.add(url);
	}
	
	public ServiceConfig find(String beanName) throws RpcException{
		if(disable){
			throw new RpcException(RpcException.REGISTRY_DISABLED,"serviceRegistry is disabled.");
		}
		store.connectingAwait();
		boolean isLocked = lockManager.tryReadLock(beanName);
		try{
			if(serviceStore.containsKey(beanName)){
				return serviceStore.get(beanName);
			}
		}
		finally{
			if(isLocked){
				lockManager.readUnlock(beanName);
			}
		}

		lockManager.writeLock(beanName);
		try{
			if(serviceStore.containsKey(beanName)){
				return serviceStore.get(beanName);
			}
			else{
				String domain = StringUtils.substringBefore(beanName, ".");
				String servicePath = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain , "/" , beanName);
				
				boolean exist = false;
				try{
					exist = store.isPathExist(servicePath);
				}
				catch(StoreException e){
					exist = false;
				}
				
				if(exist){
					ServiceConfig o = load(beanName,servicePath);
					if(o == null){
						throw new RpcException(RpcException.UNKNOWN,"load service[" + beanName + "] from registry falied.");
					}
					startServiceRegistryWatch(beanName,servicePath);
					return o;
				}
				else{
					throw new RpcException(RpcException.SERVICE_NOT_REGISTED, "beanName[" + beanName + "] not found on server registry");
				}
			}
		}
		finally{
			lockManager.writeUnlock(beanName);
		}
	}
	
	private ServiceConfig load(String beanName,String path){
		try {
			byte[] data = store.getData(path);
			ServiceConfig o = ServiceConfig.parse(new String(data,CHARSET));
			serviceStore.put(beanName, o);
			
			String providerUrlsRoot = buildPathStr(path , "/" , StoreConstants.SERVICE_PROVIDERS);
			startProviderUrlsWatch(beanName,providerUrlsRoot);
			
			return o;
		} 
		catch (Exception e) {
			serviceStore.remove(beanName);
			logger.error("load service[" + beanName + "] falied.", e);
		} 
		return null;
	}
	
	private void startServiceRegistryWatch(final String beanName,final String path){
		try{
			store.isPathExist(path, new NodeListener(){
				
				@Override
				public void onDeleted(String path){
					try{
						lockManager.writeLock(beanName);
						serviceStore.remove(beanName);
						logger.info("service[" + beanName + "] unregistered.");
					}
					finally{
						lockManager.writeUnlock(beanName);
					}
				}
				
				@Override
				public void onDataChanged(String path){
					try{
						lockManager.writeLock(beanName);
						serviceStore.remove(beanName);
						logger.info("service[" + beanName + "] updated,clear local cache.");
					}
					finally{
						lockManager.writeUnlock(beanName);
					}
				}
				
			});
			
		}
		catch(StoreException e){
			logger.error(e.getMessage(),e);
			try {
				TimeUnit.SECONDS.sleep(RETRY_DELAY);
			} 
			catch (InterruptedException e1) {
				Thread.interrupted();
			}
			startServiceRegistryWatch(beanName,path);
		}
		
	}
	
	private void startProviderUrlsWatch(final String beanName,final String path){
		startProviderUrlsWatch(beanName,path,false);
	}
	
	private void startProviderUrlsWatch(final String beanName,final String path,boolean justWactchEvent){
		try{
			List<String> ls = store.getChildren(path, new NodeListener(){
				@Override
				public void onChildrenChanged(String path){
					startProviderUrlsWatch(beanName,path);
				}
				
				@Override
				public void onDataChanged(String path){
					startProviderUrlsWatch(beanName,path,true);
				}
			});
			if(justWactchEvent){
				return;
			}
			ServiceConfig service = serviceStore.get(beanName);
			if(service != null && ls != null){
				List<ProviderUrlConfig> urls = new ArrayList<ProviderUrlConfig>();
				for(String s : ls){
					String url;
					try {
						url = URLDecoder.decode(s, CHARSET);
						urls.add(new ProviderUrlConfig(url));
					}
					catch (UnsupportedEncodingException e) {}
				}
				service.updateProviderUrls(urls);
			}
		}
		catch(StoreException e){
			logger.error(e.getMessage(),e);
			try {
				TimeUnit.SECONDS.sleep(RETRY_DELAY);
			} 
			catch (InterruptedException e1) {
			}
			startProviderUrlsWatch(beanName,path);
		}
	}
	
	public void undeployService(String beanName) throws StoreException{
		int i = beanName.indexOf(".");
		String domain = beanName.substring(0,i);
		String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME , "/" ,domain);
		String servicePath = buildPathStr(domainServiceRoot , "/" , beanName) ;
		store.delete(servicePath);
	}
	
	public void deployService(String beanName,String serviceDesc,boolean overwrite) throws StoreException{
		int i = beanName.indexOf(".");
		String domain = beanName.substring(0,i);
		deployService(domain,beanName,serviceDesc,overwrite);
	}
	
	public void deployService(String domain,String beanName,String serviceDesc,boolean overwrite) throws StoreException {
		String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain);
		String servicePath = buildPathStr(domainServiceRoot , "/" , beanName) ;
		
		if(!store.isPathExist(domainServiceRoot)){
			store.createPath(domainServiceRoot,null);
		}
		
		try {
			byte[] serviceData = serviceDesc.getBytes(CHARSET);
			if(!store.isPathExist(servicePath)){
				store.createPath(servicePath, serviceData);
				store.createPath(buildPathStr(servicePath , "/" , StoreConstants.SERVICE_ACL), null);
				store.createPath(buildPathStr(servicePath , "/" , StoreConstants.SERVICE_PROVIDERS), null);
				logger.info("service[" + beanName + "] path created.");
			}
			else{

				byte[] data = store.getData(servicePath);
				String serverSideDesc = new String(data,CHARSET);
				
				ServiceConfig L = ServiceConfig.parse(serviceDesc);
				ServiceConfig S = ServiceConfig.parse(serverSideDesc);
				
				if(!L.equals(S)){
					if(overwrite){
						store.setData(servicePath, serviceData);
						logger.info("service[" + beanName + "] overwrited.");
					}
					else{
						throw new IllegalStateException("service[" + beanName + "] is not compatible with the registry one,deploy failed.");
					}
				}

			}
		} 
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void deployProviderUrl(ServiceConfig service) throws StoreException{
		String domain = service.getAppDomain();
		if(StringUtils.isEmpty(domain)){
			domain = AppDomainContext.getName();
		}
		String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain);
		
		String beanName = service.getId();	
		String providerUrlsRoot = buildPathStr(domainServiceRoot,"/",beanName,"/",StoreConstants.SERVICE_PROVIDERS);
		
		if(!store.isPathExist(providerUrlsRoot)){
			store.createPath(providerUrlsRoot,null);
		}
		
		for(ServerUrl url : serverUrls){
			
			String providerUrlPath = buildPathStr(providerUrlsRoot , "/" , url.getEncodeUrl());
			if(store.isPathExist(providerUrlPath)){
				store.delete(providerUrlPath);
			}
			store.createTempPath(providerUrlPath, null);
		}
		deployedSet.add(service);
		startWatchACL(service);
		
		logger.info("service[" + beanName + "] online.");
	}
	
	public void undeployProviderUrl(ServiceConfig service) throws StoreException{
		String domain = service.getAppDomain();
		if(StringUtils.isEmpty(domain)){
			domain = AppDomainContext.getName();
		}
		String domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain);
		
		String beanName = service.getId();
		for(ServerUrl url : serverUrls){
			String providerUrlPath = buildPathStr(domainServiceRoot,"/",beanName,"/",StoreConstants.SERVICE_PROVIDERS,"/",url.getUrl());
			store.delete(providerUrlPath);
			deployedSet.remove(service);
		}
		
		logger.info("service[" + beanName + "] offline");
	}
	
	public void addServiceACLItem(String beanName,String aclStr) throws StoreException{
		String domain = StringUtils.substringBefore(beanName, ".");
		String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain , "/" , beanName , "/" , StoreConstants.SERVICE_ACL);
		if(!store.isPathExist(ACLPath)){
			store.createPath(ACLPath, null);
		}
		String ACLItemPath = buildPathStr(ACLPath , "/" , aclStr);
		if(!store.isPathExist(ACLItemPath)){
			store.createPath(ACLItemPath, null);
		}
	}
	
	public void removeServiceACLItem(String beanName,String aclStr) throws StoreException{
		String domain = StringUtils.substringBefore(beanName, ".");
		String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain , "/" , beanName , "/" , StoreConstants.SERVICE_ACL);
		if(!store.isPathExist(ACLPath)){
			return;
		}
		String ACLItemPath = buildPathStr(ACLPath , "/" , aclStr);
		if(store.isPathExist(ACLItemPath)){
			store.delete(ACLItemPath);
		}
	}
	
	public List<String> getServiceACL(String beanName) throws StoreException{
		int i = beanName.indexOf(".");
		String domain = beanName.substring(0,i);
		String ACLPath = buildPathStr(StoreConstants.SERVICES_HOME , "/" , domain , "/" , beanName , "/" , StoreConstants.SERVICE_ACL);
		if(!store.isPathExist(ACLPath)){
			return null;
		}
		return store.getChildren(ACLPath);
	}
	
	private void startWatchACL(final ServiceConfig service){
		String beanName = service.getId();
		
		if(!deployedSet.contains(service)){
			logger.info("service[" + beanName + "] stopping watch ACL as be offlined.");
			return;
		}
		
		String path = buildPathStr(domainServiceRoot, "/" , beanName , "/" , StoreConstants.SERVICE_ACL);
		try{
			List<String> ls = store.getChildren(path, new NodeListener(){
				@Override
				public void onChildrenChanged(String path){
					startWatchACL(service);
				}
			});
			service.updateACL(ACListType.whiteList, ls);
		}
		catch(StoreException e){
			logger.error(e.getMessage(),e);
			try {
				TimeUnit.SECONDS.sleep(RETRY_DELAY);
			} 
			catch (InterruptedException e1) {
			}
			startWatchACL(service);
		}
	}
	
	public void deployDomainServerNode() throws StoreException{
		String domain = AppDomainContext.getName();
		String serverNodesHome = StoreConstants.SERVERNODES_HOME;
		
		if(!store.isPathExist(serverNodesHome)){
			store.createPath(serverNodesHome, null);
		}
		
		String domainPath = buildPathStr(serverNodesHome , "/" , domain);
		if(!store.isPathExist(domainPath)){
			store.createPath(domainPath, null);
		}

		for(ServerUrl url : serverUrls){
			String serverHost = buildPathStr(url.getHost(),":",String.valueOf(url.getPort()));
			String serverNodePath = buildPathStr(domainPath , "/" ,serverHost,"-");
			if(store.isPathExist(serverNodePath)){
				store.delete(serverNodePath);
			}
			store.createSeqTempPath(serverNodePath, null);
			logger.info("serverNode[" + serverHost + "] deployed for domain[" + domain + "]");
		}
	}
	
	public void stop(){
		running = false;
		t = null;
	}
	
	public void start(){
		if(t == null || t.isAlive()){
			running = true;
			t = new Thread(this,"ctd-serviceRegistry");
			//t.setDaemon(true);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}
	}
	
	public  void setStore(ActiveStore store){
		this.store = store;
		store.addStateListener(this);
	}
	
	public ActiveStore getStore(){
		return store;
	}
	
	@Override
	public void onConnected(){
		
	}
	
	@Override
	public void onExpired(){
		try {
			deployDomainServerNode();
		} 
		catch (StoreException e) {
			logger.info("redeploy domainServerNode falied:" + e.getMessage());
		}
		for(ServiceConfig service : deployedSet){
			publish(service);
		}
		logger.info("store is rebuilded,redeploy local services.");
	}

	@Override
	public void run() {
		try{
			prepareStore();
			deployDomainServerNode();
			while(running && !Thread.currentThread().isInterrupted()){
				try{
					deployLocalServices();
					TimeUnit.SECONDS.sleep(PUBLISH_CHECK_DELAY);
				}
				catch(StoreException e){
					logger.error("ServiceRegistry thread error.",e);
				}
				catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
				
			}
		}
		catch(Exception e){
			System.out.println("Registry main thread shutdown now because has exception:" + e.getMessage());
		}
		
		System.out.println("ServiceRegistry shutdown");
	}
	
	
	
	private void deployLocalServices() throws StoreException{
		
		ServiceConfig service = null;
		String domain = AppDomainContext.getName();
		while((service = uploadQueue.poll()) != null){
			if(serverUrls.isEmpty()){
				throw new StoreException(StoreException.UNKNOWN,"serverUrls is empty,deploy abort.");
			}
			String beanName = service.getId();
			String serviceDesc = service.desc();
			boolean overwrite = service.getProperty("master", boolean.class,false);

			deployService(domain,beanName,serviceDesc,overwrite);
			deployProviderUrl(service);
			
		}
	}
	
	
	private void prepareStore() throws StoreException{
		ActiveStore store = getStore();
		
		if(store == null){
			throw new IllegalStateException("ActiveStore is not inited.registry start failed.");
		}
		store.connect();
		
		if(!store.isPathExist(StoreConstants.ROOT_DIR)){
			store.createPath(StoreConstants.ROOT_DIR, null);
		}
		if(!store.isPathExist(StoreConstants.SERVICES_HOME)){
			store.createPath(StoreConstants.SERVICES_HOME, null);
		}
		String domain = AppDomainContext.getName();
		domainServiceRoot = buildPathStr(StoreConstants.SERVICES_HOME , "/" ,domain);

		if(!store.isPathExist(domainServiceRoot)){
			store.createPath(domainServiceRoot,null);
		}
		
		logger.info("registry is running,connected to server[" + store.getServerAddress() + "]");
	}
	
	private String buildPathStr(String ...strings){
		StringBuilder sb = new StringBuilder();
		for(String s : strings){
			sb.append(s);
		}
		return sb.toString();
	}

	@Override
	public void onDisconnected() {
		
	}
}
