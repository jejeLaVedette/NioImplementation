package nioImplementation;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class AcceptCallbackImp implements AcceptCallback {
	
	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		// TODO Auto-generated method stub
		System.out.println("Succesfully connected to the server on the port : "+ns.getPort());
		
	}
 
	@Override 
	public void closed(NioChannel arg0) {
		// TODO Auto-generated method stub
		System.out.println("NioChannel closed");
	}
}