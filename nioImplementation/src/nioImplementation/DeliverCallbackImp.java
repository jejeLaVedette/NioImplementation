package nioImplementation;

import java.nio.ByteBuffer;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jérôme
 *
 */

public class DeliverCallbackImp implements DeliverCallback{

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
		
	}

}