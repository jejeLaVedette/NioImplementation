package nioImplementation;

//test de push
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;

public class NioChannelImp extends NioChannel{

	SocketChannel channel;

	public NioChannelImp(SocketChannel ch) {
		this.channel = ch;
	}

	@Override
	public void close() {
		try{
			channel.close();
		} catch (IOException e) {
			System.out.println("The channel is already closed");
		}

		// Il manque encore une ligne quand à l'annulation par cancel du selector.
	}

	@Override
	public SocketChannel getChannel() {
		return channel;
	}

	public void setChannel(SocketChannel ch){
		this.channel = ch;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {

		InetSocketAddress result = null;

		try {
			result = (InetSocketAddress) channel.getRemoteAddress();
		} catch (IOException e) {
			NioEngine.panic("Impossible to get the remote address");
		}

		return result;
	}

	@Override
	public void send(ByteBuffer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDeliverCallback(DeliverCallback arg0) {
		// TODO Auto-generated method stub

	}

}