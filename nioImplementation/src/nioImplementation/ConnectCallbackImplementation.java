package nioImplementation;

import nio.engine.ConnectCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jérôme
 *
 */

public class ConnectCallbackImplementation implements ConnectCallback{

	@Override
	public void closed(NioChannel nioChannel) {
		System.out.println("NioChannel at " + nioChannel.getRemoteAddress() + " is now closed");
	}

	@Override
	public void connected(NioChannel nioChannel) {
		//System.out.println("in connected");
		System.out.println("Connected to : " + nioChannel.getRemoteAddress());
		String message = "ping pong V1.0";
		//we send the message
		//nioChannel.send(message.getBytes(), 0, message.getBytes().length);
	}

}
