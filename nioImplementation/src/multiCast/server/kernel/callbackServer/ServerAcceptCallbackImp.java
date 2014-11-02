package multiCast.server.kernel.callbackServer;

import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.server.Server;
import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerAcceptCallbackImp implements AcceptCallback {

	//CA SERA TOUT DANS CLIENT APRES HAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	private Server server;

	public ServerAcceptCallbackImp(Server server) {
		this.server = server; 
	}

	@Override
	public void accepted(NioServer ns, NioChannel nc) {
		System.out.println("Succesfully connected to the server on the port : "+ns.getPort());
		server.getClientList().add(nc);

	}

	@Override 
	public void closed(NioChannel arg0) {
		System.out.println("NioChannel closed");
	}
}