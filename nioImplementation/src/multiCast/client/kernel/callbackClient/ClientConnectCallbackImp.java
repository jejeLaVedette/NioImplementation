package multiCast.client.kernel.callbackClient;

import multiCast.client.Client;
import nio.engine.ConnectCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jerome
 *
 */

public class ClientConnectCallbackImp implements ConnectCallback{

    private Client client;

    public ClientConnectCallbackImp(Client client){
        this.client=client;
    }

	@Override
	public void closed(NioChannel nioChannel) {
		System.out.println("NioChannel at " + nioChannel.getRemoteAddress() + " is now closed");
	}

	@Override
	public void connected(NioChannel nioChannel) {
		System.out.println("Client "+client.getIdentity()+" connected to : " + nioChannel.getRemoteAddress());
//		String message = "Client "+client.getIdentity()+" : ping pong!";
//		//we send the message
//		nioChannel.send(message.getBytes(), 0, message.getBytes().length);
	}

}
