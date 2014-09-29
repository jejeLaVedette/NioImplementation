package nioImplementation;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

public class AcceptCallbackImp implements AcceptCallback {

	@Override
	public void accepted(NioServer arg0, NioChannel arg1) {
		// TODO Auto-generated method stub
		System.out.println("Succesfully connected to the port " + arg0.getPort() + " to " + arg1.getRemoteAddress());
	}

	@Override
	public void closed(NioChannel arg0) {
		// TODO Auto-generated method stub
		System.out.println("NioChannel closed");
	}
}