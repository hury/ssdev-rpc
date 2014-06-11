package ctd.spring.parse;


import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import ctd.net.rpc.beans.ReferenceBean;


public class ReferenceBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element el, ParserContext context) {
		 RootBeanDefinition def = new RootBeanDefinition();
		 def.setBeanClass(ReferenceBean.class);
		 
		 String beanName = el.getAttribute("id");
		 String interfaceClassName = el.getAttribute("interface");

		 MutablePropertyValues pv = def.getPropertyValues(); 
	
		 context.getRegistry().registerBeanDefinition(beanName, def);
		 pv.add("id", beanName);
		 pv.add("interface", interfaceClassName);
		 
		return def;
	}
	

}
