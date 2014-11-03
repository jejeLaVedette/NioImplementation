package multiCast.server.kernel.callbackServer;

import nio.engine.ConnectCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerConnectCallbackImp implements ConnectCallback{

	@Override
	public void closed(NioChannel nioChannel) {
		System.out.println("Server : NioChannel at " + nioChannel.getRemoteAddress() + " is now closed");
	}

	@Override
	public void connected(NioChannel nioChannel) {
		//System.out.println("in connected");
		System.out.println("Server : Connected to : " + nioChannel.getRemoteAddress());
		String message = " Serveur : ping pong!";
		//we send the message
		nioChannel.send(message.getBytes(), 0, message.getBytes().length);
	}

}
