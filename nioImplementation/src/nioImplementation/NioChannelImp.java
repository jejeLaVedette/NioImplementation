package nioImplementation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;

/**
 * 
 * @author Jérôme
 *
 */

public class NioChannelImp extends NioChannel{

	private SocketChannel channel;
	// Buffer for reading the length
	private ByteBuffer lengthBufferRead;
	// Buffer for reading the incoming data
	private ByteBuffer bufferRead;

	// Buffer for writing the length
	private ByteBuffer lengthBufferWrite;
	// Buffer for reading the incoming data
	private ByteBuffer bufferWrite;

	//The callBack
	private DeliverCallback callback;

	private NioChannelImp nioChannelImp;

	private ByteBuffer out_buffer;
	private SocketChannel socketChannel;
	private NioEngineImp nioEngineImp;

	static final int READING_LENGTH = 1;
	static final int READING_MSG = 2;
	int currentStateRead = READING_LENGTH;

	static final int WRITING_LENGTH = 1;
	static final int WRITING_MSG = 2;
	int currentStateWrite = WRITING_LENGTH;
 

	public NioChannelImp(SocketChannel socketChannel, NioEngineImp nioEngineImp) {
		// TODO Auto-generated constructor stub
		this.socketChannel = socketChannel;
		this.nioEngineImp = nioEngineImp;
		lengthBufferRead = ByteBuffer.allocate(4);
		lengthBufferWrite = ByteBuffer.allocate(4);
		bufferRead = null;
		bufferWrite = null;
	}

	@Override
	public void close() {
		try{
			channel.close();
		} catch (IOException e) {
			System.out.println("The channel is already closed");
		}

		//selector annulation.
		channel.keyFor(this.nioEngineImp.getSelector()).cancel();

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
	public void send(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		nioEngineImp.wantToWrite(this);
	}

	@Override
	public void send(byte[] bytes, int offset, int length) {
		// TODO Auto-generated method stub

		ByteBuffer buff = ByteBuffer.allocate(length);

		while (length != 0 ){
			buff.put(bytes[offset]);
			offset++;
			length--;
		}
		send(buff);
	}

	@Override
	public void setDeliverCallback(DeliverCallback dc) {
		// TODO Auto-generated method stub
		this.callback = dc;
	}


	public void read() throws IOException {
		// TODO Auto-generated method stub

		//we need to get the current key about our channel
		//The key returned when this channel was last registered with the given selector, or null if this channel is not 
		//currently registered with that selector 
		SelectionKey key = this.channel.keyFor(this.nioEngineImp.getSelector());
		lengthBufferRead.clear();
		int nb = 0;
		if(currentStateRead == READING_LENGTH){
			try{
				nb = socketChannel.read(lengthBufferRead);
			}catch(IOException e){
				key.channel().close();
				key.cancel();
				return;
			}

			// -1 case : we close the connection
			if(nb == -1){
				key.channel().close();
				key.cancel();
				NioEngine.panic("nb = -1 : error during reading length");
				return;
			}


			if(lengthBufferRead.remaining() == 0){
				int length = lengthBufferRead.getInt(0);
				out_buffer = ByteBuffer.allocate(length);
				lengthBufferRead.position(0);
				currentStateRead = READING_MSG;
			}

		} else if(currentStateRead == READING_MSG){

			try{
				nb = socketChannel.read(out_buffer);
			}catch(IOException e){
				key.channel().close();
				key.cancel();
				NioEngine.panic("nb = -1 : error during reading message");
				return;
			}

			// cas avec -1 fermeture de connection
			if(nb == -1){
				key.channel().close();
				key.cancel();
				return;
			}

			if(out_buffer.remaining() == 0){
				currentStateRead = READING_LENGTH;
			}
		}

	}

	public void write() throws IOException {
		// TODO Auto-generated method stub
		SelectionKey key = this.channel.keyFor(this.nioEngineImp.getSelector());


		if(currentStateWrite == WRITING_LENGTH){

			try{
				socketChannel.write(lengthBufferWrite);
			}catch(IOException e){
				key.cancel(); 
				socketChannel.close(); 
				return; 
			}

			if(lengthBufferWrite.remaining() == 0){
				currentStateWrite = WRITING_MSG;
			}

		} else if(currentStateWrite == WRITING_MSG){

			if(lengthBufferWrite.remaining() > 0){

				try{
					socketChannel.write(lengthBufferWrite);
				}catch(IOException e){
					key.cancel(); 
					socketChannel.close(); 
					return; 
				}
			}

			if(lengthBufferWrite.remaining() == 0){
				currentStateWrite = WRITING_LENGTH;
			}
		}
	}
}