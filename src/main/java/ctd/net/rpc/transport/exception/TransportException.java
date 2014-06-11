package ctd.net.rpc.transport.exception;

import ctd.net.rpc.exception.RpcException;

public class TransportException extends RpcException {	
	private static final long serialVersionUID = 8364626071787450848L;
	public static final int CONNECT_FALIED = 501;
	public static final int CONNECT_NOT_READY = 502;
	public static final int TASK_TIMEOUT = 503;
	public static final int INTERRUPTED = 504;
	
	public TransportException() {
		super();
	}

	public TransportException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransportException(String message) {
		super(message);
	}

	public TransportException(Throwable cause) {
		super(cause);
	}

	public TransportException(int code) {
		super(code);
	}

	public TransportException(int code, String message, Throwable cause) {
		super(code, message, cause);
	}

	public TransportException(int code, String message) {
		super(code, message);
	}

	public TransportException(int code, Throwable cause) {
		super(code, cause);
	}

}
