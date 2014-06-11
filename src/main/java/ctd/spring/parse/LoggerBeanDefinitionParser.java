package ctd.spring.parse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import ctd.net.rpc.logger.LoggerManager;
import ctd.spring.AppDomainContext;
import ctd.util.converter.ConversionUtils;

public class LoggerBeanDefinitionParser implements BeanDefinitionParser {

		@Override
		public BeanDefinition parse(Element el, ParserContext context) {
			 RootBeanDefinition def = new RootBeanDefinition();
			 def.setBeanClass(LoggerManager.class);
			 def.setInitMethodName("startWork");
			 def.setDestroyMethodName("shutdown");
			 
			 String serviceId = el.getAttribute("service");
			 if(StringUtils.isEmpty(serviceId)){
				 throw new IllegalArgumentException("ssdev:logger must not has empty [service] attribute");
			 }
			 
			 MutablePropertyValues pv = def.getPropertyValues(); 
			 String domain = AppDomainContext.getName();
			 String beanName = domain + "-logger";
			 
			 RuntimeBeanReference reference = new RuntimeBeanReference(serviceId);
			 pv.add("loggerService", reference);
			 
			 
			 String[] attrNames = {"sendRetryTimes","sendRetryDelayTime","sendCheckDelayTime","sendBatchSize","sendMaxStayTime","statCheckDelayTime"};
			 for(String nm : attrNames){
				 String attr = el.getAttribute(nm);
				 if(!StringUtils.isEmpty(attr)){
					 int val = ConversionUtils.convert(attr,int.class);
					 if(val > 0){
						 pv.add(nm, val);
					 }
				 }
			 }
			 context.getRegistry().registerBeanDefinition(beanName, def);
			return def;
		}
}
