package multiCast.client.kernel.callbackClient;

import multiCast.client.Client;
import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class ClientAcceptCallbackImp implements AcceptCallback {
	//CA SERA TOUT DANS CLIENT APRES HAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	private Client client;

	public ClientAcceptCallbackImp(Client client) {
		this.client = client;
	}

	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		System.out.println("Client : " + this.client.getIdentity()+" : Succesfully connected to the server on the port : "+ns.getPort());
		client.getClientList().add(nc);
        nc.setDeliverCallback(new ClientDeliverCallbackImp(client));

    }

	@Override 
	public void closed(NioChannel arg0) {
		System.out.println("NioChannel closed");
	}
}