package multiCast.server.kernel.callbackServer;

import multiCast.server.Server;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

import java.nio.ByteBuffer;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerDeliverCallbackImp implements DeliverCallback{

    private Server server;

    public ServerDeliverCallbackImp(Server server) {
        this.server = server;
        System.out.println("Server accepteCB : "+server);
    }

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		System.out.println("Server : Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
	}

}