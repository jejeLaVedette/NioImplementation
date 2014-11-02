package multiCast.server.kernel.callbackServer;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerAcceptCallbackImp implements AcceptCallback {
	
	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		System.out.println("Succesfully connected to the server on the port : "+ns.getPort());
		
	}
 
	@Override 
	public void closed(NioChannel arg0) {
		System.out.println("NioChannel closed");
	}
}