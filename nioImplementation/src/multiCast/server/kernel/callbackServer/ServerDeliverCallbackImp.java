package multiCast.server.kernel.callbackServer;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

import java.nio.ByteBuffer;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerDeliverCallbackImp implements DeliverCallback{

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
	}

}