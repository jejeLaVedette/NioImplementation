package nioImplementation;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Hashtable;
import java.util.Iterator;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;

public abstract class NioEngineImp extends NioEngine{

	private Selector selector;
	private NioChannelImp nioChannel;
	private ServerSocketChannel serverSocketChannel;

	Hashtable<SocketChannel, ByteBuffer> lengthBuffersWrite;

	InetAddress hostAddress;
	
	Hashtable<ServerSocketChannel, NioServerImp> nioServers;
	Hashtable<SocketChannel, NioChannelImp> nioChannels;

	/* Variable de l'automate */
	State readState = State.READING_LENGTH;
	State writeState = State.WRITING_LENGTH;

	public NioEngineImp() throws Exception {
		super();

		//Création du selector
		selector = SelectorProvider.provider().openSelector();

	}

	@Override
	public void connect(InetAddress address, int port, ConnectCallback arg2)
			throws UnknownHostException, SecurityException, IOException{

		nioChannel = new NioChannelImp(SocketChannel.open());
		nioChannel.getChannel().configureBlocking(false);

		nioChannel.getChannel().register(selector, SelectionKey.OP_CONNECT);
		nioChannel.getChannel().connect(new InetSocketAddress(address, port));

		//arg2.methodNotify();
	}

	@Override
	public NioServer listen(int arg0, AcceptCallback arg1) throws IOException{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);


		InetSocketAddress addressToBind = new InetSocketAddress(hostAddress, arg0);
		serverSocketChannel.socket().bind(addressToBind);

		return null;

	}


	@Override
	public void mainloop() {
		while (true) {
			try {
				selector.select();
				Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();

				while (selectedKeys.hasNext()) {

					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						System.out.println("The key is not valid");
						continue;
					} else if (key.isAcceptable()) {
						handleAccept(key);
					} else if (key.isReadable()) {
						handleRead(key);
					} else if (key.isWritable()) {
						handleWrite(key);
					} else if (key.isConnectable()) {
						handleConnection(key);
					} else {
						System.out.println("Unknown key");
					}
				}
			} catch (Exception e) {
				panic("Error during the selection of a key");
			}
		}
	}

	public Selector getSelector(){
		return this.selector;
	}

	public NioChannelImp getNioChannel(){
		return this.nioChannel; 
	}


	/**
	 * Accept a connection and make it non-blocking
	 * @param the key of the channel on which a connection is requested
	 */
	public void handleAccept(SelectionKey key){
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(this.selector, SelectionKey.OP_READ);

			//AcceptCallback callback = nioServers.get(serverSocketChannel).getCallback();
			//NioChannelImp nioChannel = new NioChannelImp(socketChannel, this);
			//nioChannels.put(socketChannel, nioChannel);	
			

			//callback.accepted(nioServers.get(serverSocketChannel), nioChannel);
		} catch (IOException e) {
			// as if there was no accept done
			nioChannel.close();
		}
	}

	public void handleRead(SelectionKey key){
		
	}



	public void handleWrite(SelectionKey key){
		
	}

	/**
	 * Finish to establish a connection
	 * @param the key of the channel on which a connection is requested
	 */
	public void handleConnection(SelectionKey key){
		nioChannel.setChannel((SocketChannel) key.channel());

		try {
			nioChannel.getChannel().finishConnect();
		} catch (IOException e) {
			// cancel the channel's registration with our selector 
			System.out.println(e);
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_READ);	
	}
	
	public void wantToWrite(NioChannelImp nioChannel)
	{
		try {
				nioChannel.getChannel().register(selector, SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}