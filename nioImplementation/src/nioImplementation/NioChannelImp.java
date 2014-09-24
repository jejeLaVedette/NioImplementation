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
	// Buffer for reading the length
	ByteBuffer lengthBufferRead = ByteBuffer.allocate(4);
	// Buffer for reading the incomming data
	ByteBuffer bufferRead = null;
	//The callBack
	private DeliverCallback callback;
	
	private NioChannelImp nioChannelImp;
	
	private ByteBuffer out_buffer;
	

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
	public void send(byte[] bytes, int offset, int length) {
		// TODO Auto-generated method stub
		while (length != 0 ){
			this.out_buffer.put(bytes[length]);
			offset++;
			length--;
		}

		//faire la demande d'écriture ?
	}

	@Override
	public void setDeliverCallback(DeliverCallback arg0) {
		// TODO Auto-generated method stub
		this.callback = arg0;
	}

	

}