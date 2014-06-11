package ctd.spring.parse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import ctd.net.rpc.registry.ServiceRegistry;
import ctd.net.rpc.transport.ServerUrl;
import ctd.net.rpc.transport.http.HttpServer;
import ctd.net.rpc.transport.sockect.SocketServer;
import ctd.util.converter.ConversionUtils;

public class ConnectorBeanDefinitionParser implements BeanDefinitionParser {

		@Override
		public BeanDefinition parse(Element el, ParserContext context) {
			
			String url = el.getAttribute("url");
			if(StringUtils.isEmpty(url)){
				throw new IllegalArgumentException("ssdev:connector must not has empty url attribute");
			}
			ServerUrl serverUrl = new ServerUrl(url);
			Class<?> clz = serverUrl.getProtocol().equals("http") ? HttpServer.class : SocketServer.class;
			
			RootBeanDefinition def = new RootBeanDefinition();
			def.setBeanClass(clz);
			def.setInitMethodName("start");
			def.setDestroyMethodName("shutdown");
			def.getConstructorArgumentValues().addGenericArgumentValue(serverUrl);
			
			if(el.hasAttribute("threads")){
				int threads = ConversionUtils.convert(el.getAttribute("threads"),int.class);
				def.getPropertyValues().add("threads", threads);
			}
			
			if(el.hasAttribute("queues")){
				int queues = ConversionUtils.convert(el.getAttribute("queues"),int.class);
				def.getPropertyValues().add("queues", queues);
			}
			
			context.getRegistry().registerBeanDefinition("rpcserver-" + serverUrl.getPort(), def);
			
			ServiceRegistry.addServerUrl(serverUrl);
			
			return def;
		}
}
