package nioImplementation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

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
	
	
	// List of ByteBuffer
	ArrayList<ByteBuffer> listBuffer;

	private NioEngineImp nioEngineImp;

	static final int READING_LENGTH = 1;
	static final int READING_MSG = 2;
	static final int READING_DONE = 3;
	int currentStateRead = READING_DONE;

	static final int WRITING_LENGTH = 1;
	static final int WRITING_MSG = 2;
	static final int WRITING_DONE = 3;
	int currentStateWrite = WRITING_DONE;
	

	public NioChannelImp(SocketChannel socketChannel, NioEngineImp nioEngineImp) {
		// TODO Auto-generated constructor stub
		this.channel = socketChannel;
		this.nioEngineImp = nioEngineImp;
		listBuffer = new ArrayList<ByteBuffer>();
		lengthBufferRead = ByteBuffer.allocate(4);
		lengthBufferWrite = ByteBuffer.allocate(4);
		callback = new DeliverCallbackImp(); 
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
		listBuffer.add(buffer);
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
		
		if(currentStateRead == READING_DONE){
			bufferRead = null;
			lengthBufferRead.position(0);
			currentStateRead = READING_LENGTH;
		}
		
		if(currentStateRead == READING_LENGTH){	
			try{
				nb = channel.read(lengthBufferRead);		
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
				lengthBufferRead.position(0);
				int length = lengthBufferRead.getInt(0);
				bufferRead = ByteBuffer.allocate(length);
				currentStateRead = READING_MSG;
			}

		} if(currentStateRead == READING_MSG){	

			try{

				nb = channel.read(bufferRead);
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

			//we read all the buffer so we send
			if(bufferRead.remaining() == 0){
				//we duplicate because the buffer will be remplace by a new buffer
				// on the next step, so mb we will lost our message
				callback.deliver(this, bufferRead.duplicate());
				currentStateRead = READING_DONE;
			}
		}

	}

	public boolean write() throws IOException {
		// TODO Auto-generated method stub
		SelectionKey key = this.channel.keyFor(this.nioEngineImp.getSelector());
		
		if(currentStateWrite == WRITING_DONE){	
			if(listBuffer.size() > 0){
				bufferWrite = listBuffer.get(0);
				listBuffer.remove(0);
				bufferWrite.position(0);
				lengthBufferWrite.position(0);
				lengthBufferWrite.putInt(bufferWrite.capacity());
				lengthBufferWrite.position(0);
				currentStateWrite = WRITING_LENGTH;
			}
		}


		if(currentStateWrite == WRITING_LENGTH){

			try{
				channel.write(lengthBufferWrite);
			}catch(IOException e){
				key.cancel(); 
				channel.close(); 
			}

			if(lengthBufferWrite.remaining() == 0){
				currentStateWrite = WRITING_MSG;
			}

		} if(currentStateWrite == WRITING_MSG){

			if(bufferWrite.remaining() > 0){

				try{
					channel.write(bufferWrite);
				}catch(IOException e){
					key.cancel(); 
					channel.close(); 
				}
			}

			if(bufferWrite.remaining() == 0){
				currentStateWrite = WRITING_DONE;
			}

		}
		return (listBuffer.size() == 0);
	}
}