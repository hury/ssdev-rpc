package ctd.spring.parse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import ctd.spring.AppDomainContext;

public class ApplicationDomainBeanDefinitionParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element el, ParserContext context) {
		 RootBeanDefinition def = new RootBeanDefinition();
		 def.setBeanClass(AppDomainContext.class);
		 
		 String name = el.getAttribute("name");
		 AppDomainContext.setName(name);
		 
		 String id = el.getAttribute("id");
		 if(StringUtils.isEmpty(id)){
			 id = name;
		 }
		 boolean enableLogger = Boolean.parseBoolean(el.getAttribute("enableLogger"));
		 
		 String registryAddress = el.getAttribute("registryAddress");
		 AppDomainContext.setRegistryAddress(registryAddress);
		 AppDomainContext.setEnableLogger(enableLogger);
		 
		 context.getRegistry().registerBeanDefinition("$domain-" + name, def);
		 
		return def;
	}

}
