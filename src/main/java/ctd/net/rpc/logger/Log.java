package ctd.net.rpc.logger;

public abstract class Log {

	private String sourceDomain;
	private String destDomain;
	private String clientAddress;
	private String remoteAddress;
	
	private String protocol;
	private String beanName;
	private String methodDesc;
	
	public String getSourceDomain() {
		return sourceDomain;
	}
	public void setSourceDomain(String sourceDomain) {
		this.sourceDomain = sourceDomain;
	}
	
	public String getDestDomain() {
		return destDomain;
	}
	
	public void setDestDomain(String destDomain) {
		this.destDomain = destDomain;
	}
	
	public String getClientAddress() {
		return clientAddress;
	}
	
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	
	public String getRemoteAddress() {
		return remoteAddress;
	}
	
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getMethodDesc() {
		return methodDesc;
	}
	
	public void setMethodDesc(String methodDesc) {
		this.methodDesc = methodDesc;
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

}
