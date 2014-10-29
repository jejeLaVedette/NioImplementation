package nioImplementation;

import java.util.ArrayList;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class AcceptCallbackImp implements AcceptCallback {
	
	ArrayList<NioChannel> listChannel = new ArrayList<NioChannel>();

	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		// TODO Auto-generated method stub
		System.out.println("Succesfully connected to the server on the port : "+ns.getPort()+ " to " + nc.getRemoteAddress());
		//String askPort = "ADD^" + listChannel.size();
		
		listChannel.add(nc);
		System.out.println("liste taille : "+listChannel.size());
		for(int i=0;i<listChannel.size();i++){
			System.out.println("liste : "+listChannel.get(i));			
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		//nioChannel.send(askPort.getBytes(), 0, askPort.getBytes().length);
	}
 
	@Override 
	public void closed(NioChannel arg0) {
		// TODO Auto-generated method stub
		System.out.println("NioChannel closed");
	}
}