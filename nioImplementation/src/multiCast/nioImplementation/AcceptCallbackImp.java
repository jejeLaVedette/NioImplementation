package multiCast.nioImplementation;

import multiCast.client.Client;
import multiCast.client.kernel.EntitiesClientImpl;
import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class AcceptCallbackImp implements AcceptCallback {
	//CA SERA TOUT DANS CLIENT APRES HAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	private EntitiesClientImpl nioClient;

	public AcceptCallbackImp(EntitiesClientImpl nioClient) {
		this.nioClient = nioClient; 
	}

	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		// TODO Auto-generated method stub
		System.out.println("Client : Succesfully connected to the multiCast.server on the port : "+ns.getPort());
		nioClient.getClientList().add(nc);
	}

	@Override 
	public void closed(NioChannel arg0) {
		// TODO Auto-generated method stub
		System.out.println("NioChannel closed");
	}
}