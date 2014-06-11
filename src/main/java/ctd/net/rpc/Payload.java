package ctd.net.rpc;

import java.io.Serializable;

public class Payload implements Serializable{
	private static final long serialVersionUID = -3984392509278535275L;
	private transient byte compression;
	private transient int contentLength;
	
	private long correlationId;
	
	public long getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(long correlationId) {
		this.correlationId = correlationId;
	}
	
	public byte getCompression() {
		return compression;
	}
	
	public void setCompression(byte compression) {
		this.compression = compression;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	
}
