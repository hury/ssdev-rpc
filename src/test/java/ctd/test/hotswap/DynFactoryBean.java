package ctd.test.hotswap;

import java.io.IOException;

import org.springframework.beans.factory.FactoryBean;

import ctd.util.ReflectUtil;

public class DynFactoryBean implements FactoryBean<DynBeanInterface> {
	private DynBeanInterface ref;

	@Override
	public DynBeanInterface getObject() throws Exception {
		if(ref == null){
			reload();
		}
		return ref;
	}

	@Override
	public Class<?> getObjectType() {
		if(ref!=null){
			return ref.getClass();
		}
		return Object.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void reload() throws IOException, InstantiationException, IllegalAccessException{
		String path = ReflectUtil.getCodeBase(DynBean.class);
		HotswapClassLoader loader = new HotswapClassLoader(null);

		Class<DynBean> clz = (Class<DynBean>)loader.loadClass(path,"ctd.test.hotswap.DynBean");
		ref =(DynBeanInterface)clz.newInstance();
		System.out.println("reload");
	}

}
