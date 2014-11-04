package multiCast.server.kernel.callbackServer;

import multiCast.client.Client;
import multiCast.client.kernel.callbackClient.ClientDeliverCallbackImp;
import multiCast.server.Server;
import nio.engine.ConnectCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerConnectCallbackImp implements ConnectCallback{

    private Server server;
    public ServerConnectCallbackImp(Server server) {
        this.server=server;
    }

    @Override
	public void closed(NioChannel nioChannel) {
		System.out.println("Server : NioChannel at " + nioChannel.getRemoteAddress() + " is now closed");
	}

	@Override
	public void connected(NioChannel nioChannel) {
		System.out.println("Server : Connected to : " + nioChannel.getRemoteAddress());
        nioChannel.setDeliverCallback(new ServerDeliverCallbackImp(server));
	}

}
