package ctd.test.dynloader;




import ctd.util.annotation.RpcService;

public class DynBean implements DynBeanInterface{
	private String name = "nobody";
	
	@RpcService
	public String sayHello(){
		DynBeanSupport support = new DynBeanSupport();
		return "hello world20,I'm " + name + ",and my age is:" + support.getAge();
	}
	
	public void setName(String nm){
		this.name = nm;
	}
	
	public String getName(){
		return name;
	}
	

}
