package multiCast.server.kernel.callbackServer;

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
		if(server.getClientList().size() < this.server.getMaxClientRoom()){
			String msg = "[ADD] cheat msg";
			this.server.getChatServer().deliver("Server : connection accepted on port : "+ns.getPort());

			server.getClientList().add(nc);

            this.server.getChatServer().deliver("Server : size list = "+server.getClientList().size());
			nc.send(msg.getBytes(), 0, msg.getBytes().length);
			nc.setDeliverCallback(new ServerDeliverCallbackImp(server));
		}
	}

	@Override 
	public void closed(NioChannel arg0) {
		System.out.println("NioChannel closed");
	}
}