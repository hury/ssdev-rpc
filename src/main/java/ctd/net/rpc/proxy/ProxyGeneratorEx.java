package ctd.net.rpc.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import ctd.util.annotation.RpcService;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;


public class ProxyGeneratorEx {
	private static AtomicInteger counter = new AtomicInteger(0);
	
	@SuppressWarnings("unchecked")
	public static <T> T createProxyBean(String serviceName,Class<T> interfaceClass) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException{
		
		String interfaceName = interfaceClass.getName();
		
		StringBuffer className = new StringBuffer(interfaceName);
			className.append("Impl").append(counter.incrementAndGet());
		
		ClassPool cpool = ClassPool.getDefault();
		cpool.insertClassPath(new ClassClassPath(interfaceClass)); 

		CtClass cc = cpool.makeClass(className.toString()); 
		CtClass ci = cpool.get(interfaceName);
		
		if(!ci.isInterface()){
			throw new IllegalAccessException("class[" + interfaceClass.getName() + "] is not a interface");
		}
		cc.addInterface(ci);
		
		CtMethod[] ctMethods =  ci.getMethods();
		for(CtMethod m : ctMethods){
			if(!m.hasAnnotation(ctd.util.annotation.RpcService.class)){
				continue;
			}
			createImplCtMethod(serviceName,cc,m,cpool);
		}
		return (T)cc.toClass().newInstance();
	}
	
	private static void createImplCtMethod(String serviceName,CtClass cc,CtMethod m,ClassPool cp) throws NotFoundException, CannotCompileException{
		
		StringBuilder body = new StringBuilder("public ").append(m.getReturnType().getName()).append(" ").append(m.getName()).append("(");
		CtClass[] parameters = m.getParameterTypes();
		int pCount = parameters.length;
		for(int i = 0; i < pCount ; i ++){
			CtClass p = parameters[i];
			body.append(p.getName()).append(" ").append("arg").append(i);
			if(i < pCount - 1){
				body.append(",");
			}
		}
		body.append(")");
		
		CtClass[] cExceptions = m.getExceptionTypes();
		int eCount =  cExceptions.length;
		if(eCount > 0){
			body.append("throws ");
			for(int i = 0; i < eCount ; i ++){
				CtClass c = cExceptions[i];
				body.append(c.getName());
				if(i < eCount - 1){
					body.append(",");
				}
			}
		}
		
		body.append("{System.out.println($args);");
		
		String returnType = m.getReturnType().getName();
		if(!returnType.equals("void")){
			body.append("return null;");
		}	
		body.append("}");
		
		System.out.println(body.toString());
		CtMethod cm = CtNewMethod.make(body.toString(), cc);
		
		
		ConstPool constpool = cm.getMethodInfo().getConstPool();
		AnnotationsAttribute at = new AnnotationsAttribute(constpool,AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation("ctd.util.annotation.RpcService", constpool);
		at.addAnnotation(annot);
		cm.getMethodInfo().addAttribute(at);
		cc.addMethod(cm);
	}
	
	public static void main(String[] args){
		try {
			TestInterface t = createProxyBean("test",TestInterface.class);
			for(Method m:  t.getClass().getMethods()){
				System.out.println(m.getName() + ":" + m.isAnnotationPresent(RpcService.class));
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
