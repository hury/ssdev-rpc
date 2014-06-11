package ctd.net.rpc;

import java.io.Serializable;

public class Payload implements Serializable{
	private static final long serialVersionUID = -3984392509278535275L;
	private static final int DEFAULT_COMPRESSION_MIN_SIZE = 2048;
	private transient byte compression;
	private transient int compressionMinSize = DEFAULT_COMPRESSION_MIN_SIZE;
	
	private long correlationId;
	
	public byte getCompression() {
		return compression;
	}
	
	public void setCompression(byte compression) {
		this.compression = compression;
	}

	public int getCompressionMinSize() {
		return compressionMinSize;
	}

	public void setCompressionMinSize(int compressionMinSize) {
		this.compressionMinSize = compressionMinSize;
	}

	public long getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(long correlationId) {
		this.correlationId = correlationId;
	}
	
}
