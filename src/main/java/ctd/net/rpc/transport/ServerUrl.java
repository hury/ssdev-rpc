package ctd.net.rpc.transport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ctd.util.NetUtils;

public class ServerUrl {
	private static final Pattern pattern = Pattern.compile("([a-zA-Z]+)://([a-zA-Z\\.0-9]+)\\:([1-9][0-9]+)/{0,1}\\?codec=([a-zA-Z]+)");
	public static final String CHARSET = "UTF-8";
	
	private String url;
	private String protocol;
	private String host;
	private int port;
	private String codec;
	
	public ServerUrl(String url){
		url = url.replace("localhost", NetUtils.getLocalHost());
		
		Matcher m = pattern.matcher(url);
		if(m.find() && m.groupCount() == 4){
			protocol = m.group(1);
			host = m.group(2);
			port = Integer.valueOf(m.group(3));
			codec = m.group(4);
			this.url = url;
		}
		else{
			throw new IllegalArgumentException("ServerUrl invalid:" + url);
		}
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getCodec() {
		return codec;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getEncodeUrl(){
		try {
			return URLEncoder.encode(url,CHARSET);
		} 
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public int hashCode(){
		return url.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(o != null && o instanceof ServerUrl){
			return ((ServerUrl)o).getUrl().equals(url);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return url;
	}
	
}
