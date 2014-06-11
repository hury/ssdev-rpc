package ctd.spring.parse;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class NamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("applicationDomain", new ApplicationDomainBeanDefinitionParser());
		registerBeanDefinitionParser("service",new ServiceBeanDefinitionParser());
		registerBeanDefinitionParser("reference",new ReferenceBeanDefinitionParser());
		registerBeanDefinitionParser("logger",new LoggerBeanDefinitionParser());
		registerBeanDefinitionParser("connector",new ConnectorBeanDefinitionParser());
	}

}
