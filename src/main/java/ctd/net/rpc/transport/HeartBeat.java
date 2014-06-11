package ctd.net.rpc.transport;


import ctd.net.rpc.Payload;

public class HeartBeat extends Payload{
	public static final HeartBeat PING = new HeartBeat();
	public static final HeartBeat PONG = new HeartBeat();
	private static final long serialVersionUID = 1L;
	
	
}
