package multiCast.server.kernel.callbackServer;

import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.server.Server;
import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioServer;

import java.io.IOException;

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
		System.out.println("Server accepteCB : "+server);
	}

	@Override
	public void accepted(NioServer ns, NioChannel nc) {
        if(server.getClientList().size()<Server.getMaxClientRoom()){
            try {
                System.out.println("Server : connection accepted on port : "+ns.getPort() +" on channel :"+nc.getChannel().getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.getClientList().add(nc);
            System.out.println("Server : taille liste = "+server.getClientList().size());

            nc.setDeliverCallback(new ServerDeliverCallbackImp(server));

        }

	}

	@Override 
	public void closed(NioChannel arg0) {
		System.out.println("NioChannel closed");
	}
}