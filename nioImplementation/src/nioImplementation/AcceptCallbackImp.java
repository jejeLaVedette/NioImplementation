package nioImplementation;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class AcceptCallbackImp implements AcceptCallback {
	
	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		// TODO Auto-generated method stub
		System.out.println("Succesfully connected to the port " + ns.getPort() + " to " + nc.getRemoteAddress());
		
	}
 
	@Override 
	public void closed(NioChannel arg0) {
		// TODO Auto-generated method stub
		System.out.println("NioChannel closed");
	}
}