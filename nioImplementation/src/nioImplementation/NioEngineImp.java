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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;

/**
 * This class will listen incoming connection
 * and connect to remote ports
 * @author Jérôme
 *
 */

public abstract class NioEngineImp extends NioEngine{

	private Selector selector;
	private NioChannelImp nioChannel;

	HashMap<SocketChannel, ByteBuffer> lengthBuffersWrite;	
	HashMap<ServerSocketChannel, NioServerImp> nioServers;
	HashMap<SocketChannel, NioChannelImp> nioChannels;

	/* Variable de l'automate */
	State readState = State.READING_LENGTH;
	State writeState = State.WRITING_LENGTH;

	public NioEngineImp() throws Exception {
		super();

		//we create the selector
		selector = SelectorProvider.provider().openSelector();
		nioServers= new HashMap<ServerSocketChannel, NioServerImp>();
		nioChannels= new HashMap<SocketChannel, NioChannelImp>();
	}

	@Override
	public void connect(InetAddress address, int port, ConnectCallback arg2)
			throws UnknownHostException, SecurityException, IOException{
		try {

			//create the SC
			SocketChannel socketChannel = SocketChannel.open();
			//say to it noBlocking
			socketChannel.configureBlocking(false);

			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			//and we "try" to connect 
			socketChannel.connect(new InetSocketAddress(address, port));
		} catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}

	}

	@Override
	public NioServer listen(int port, AcceptCallback ac) throws IOException{
		
		NioServerImp server = null;
		
		try{
			
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);

			InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", port);
			serverSocketChannel.socket().bind(inetSocketAddress);

			//will notify when there is incoming data
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			//we fix the server with the AC and the SC
			server = new NioServerImp(serverSocketChannel, ac);
			// store it
			nioServers.put(serverSocketChannel, server);

		} catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		return server;
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
	 * will notify when there is connection and accept it and make it non blocking
	 * @param key
	 */
	public void handleAccept(SelectionKey key){
		SocketChannel socketChannel = null;
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		try {
			socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			//will notify when there is incoming data
			socketChannel.register(this.selector, SelectionKey.OP_READ);
			//
			AcceptCallback acceptCallback = (nioServers.get(serverSocketChannel)).getCallback();

			NioChannelImp nioChannel = new NioChannelImp(socketChannel,this);
			
			nioChannels.put(socketChannel,nioChannel);
			acceptCallback.accepted(nioServers.get(serverSocketChannel), nioChannel);
			
		} catch (IOException e) {
			//we close the connection................... ??????
			System.out.println(e);
			nioChannel.close();
			//or panic("Error during the handleAccept");

		}
	}

	public void handleRead(SelectionKey key){
		
		SocketChannel socketChannel = (SocketChannel) key.channel();
		//need to do the read method
		try {
			this.nioChannels.get(socketChannel).read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NioEngine.panic("error in handleRead");
		}
	}



	public void handleWrite(SelectionKey key){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			this.nioChannels.get(socketChannel).write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NioEngine.panic("error in handleWrite");
		}
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

}