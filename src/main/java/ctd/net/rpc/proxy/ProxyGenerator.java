package ctd.net.rpc.proxy;

import java.util.concurrent.atomic.AtomicInteger;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;


public class ProxyGenerator {
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
			createImplCtMethod(serviceName,cc,m);
		}
		return (T)cc.toClass().newInstance();
	}
	
	private static void createImplCtMethod(String serviceName,CtClass cc,CtMethod m) throws NotFoundException, CannotCompileException{
		StringBuffer body = new StringBuffer("public ").append(m.getReturnType().getName()).append(" ").append(m.getName()).append("(");
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
		
		body.append("{Object[] parameters = ");
		if(pCount == 0){
			body.append("null;");
		}
		else{
			body.append("new Object[]{");
			for(int i = 0; i < pCount; i ++){
				CtClass p = parameters[i];
				if(p.isPrimitive()){
					body.append(getPrimitiveBigTypeName(p)).append(".valueOf(arg").append(i).append(")");
				}
				else{
					body.append("arg").append(i);
				}
				if(i < pCount - 1){
					body.append(",");
				}
			}
			body.append("};");
		}
		String methodName = m.getName();
		String returnType = m.getReturnType().getName();
		if(!returnType.equals("void")){
			body.append("return (").append(returnType).append(")");
		}	
		body.append("ctd.net.rpc.Client.rpcInvoke(\"").append(serviceName).append("\",\"").append(methodName).append("\",parameters);}");
		
		CtMethod cm = CtNewMethod.make(body.toString(), cc);
		cc.addMethod(cm);
	}
	
	private static String getPrimitiveBigTypeName(CtClass c){
		Class<?> b = null;
		String cName = c.getName();
		if(cName.equals("int"))
			b = Integer.class;
		else if(cName.equals("boolean"))
			b = Boolean.class;
		else  if(cName.equals("long"))
			b = Long.class;
		else if(cName.equals("float"))
			b = Float.class;
		else if(cName.equals("double"))
			b = Double.class;
		else if(cName.equals("char"))
			b = Character.class;
		else if(cName.equals("byte"))
			b = Byte.class;
		else if(cName.equals("short"))
			b = Short.class;
		if(b == null){
			return null;
		}
		return b.getName();
	}

}
