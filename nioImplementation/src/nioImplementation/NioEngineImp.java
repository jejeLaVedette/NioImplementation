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

public class NioEngineImp extends NioEngine{

	private Selector selector;
    private final int portBegin;
    private final int portMargin;
    private int port;

	HashMap<SocketChannel, ByteBuffer> lengthBuffersWrite;	
	HashMap<ServerSocketChannel, NioServer> nioServers;
	HashMap<SocketChannel, NioChannelImp> nioChannels;
	HashMap<SocketChannel, ConnectCallback> nioChannelCallback;

	/* Variable de l'automate */
	State readState = State.READING_LENGTH;
	State writeState = State.WRITING_LENGTH;

	public NioEngineImp() throws Exception {
		//we create the selector
		selector = SelectorProvider.provider().openSelector();
		//we keep the link between SC and server
		nioServers= new HashMap<ServerSocketChannel, NioServer>();
		//we keep the link between SC and channel
		nioChannels= new HashMap<SocketChannel, NioChannelImp>();
		//we keep the link between SC and CB
		nioChannelCallback = new HashMap<SocketChannel, ConnectCallback>();
        portBegin = 6667;
        portMargin = 100;
	}

    public void connect(InetAddress address, ConnectCallback cc){
        int portTest = portBegin;
        int i = 0;
        boolean isConnected = false;
        while((portTest < portBegin+portMargin)&&(!isConnected)){
            portTest = portBegin + i;
            try{
                connect(address, portTest, cc);
                isConnected = true;
            } catch (IOException ex){
                System.out.println("impossible de se connecter avec le port : "+portTest);
                isConnected = false;
            }
            System.out.println(i);
            i++;
        }
        if (isConnected){
            this.port = portTest;
        }
    }

	@Override
	public void connect(InetAddress address, int port, ConnectCallback cc)
			throws SecurityException, IOException{

		//create the SC
		SocketChannel socketChannel = SocketChannel.open();
		//say to it noBlocking
		socketChannel.configureBlocking(false);

		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		//and we "try" to connect 
		socketChannel.connect(new InetSocketAddress(address, port));

		nioChannelCallback.put(socketChannel, cc);

        selector.select();
        Iterator<?> selectedKeys = this.selector.selectedKeys().iterator();


        while (selectedKeys.hasNext()) {
            SelectionKey key = (SelectionKey) selectedKeys.next();
            if (key.isConnectable()) {
                System.out.println("test connexion");
                handleConnection(key);
                break;
            }
        }

	}

	@Override
	public NioServer listen(int port, AcceptCallback ac) throws IOException{

		NioServerImp server = null;


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
					} else if (key.isConnectable()) {
						handleConnection(key);
					} else if (key.isReadable()) {
						handleRead(key);
					} else if (key.isWritable()) {
						handleWrite(key);
					} else if (key.isAcceptable()) {
						handleAccept(key);
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

			AcceptCallback acceptCallback = ((NioServerImp) nioServers.get(serverSocketChannel)).getCallback();

			NioChannelImp nioChannel = new NioChannelImp(socketChannel,this);

			nioChannels.put(socketChannel,nioChannel);
			acceptCallback.accepted(nioServers.get(serverSocketChannel), nioChannel);


		} catch (IOException e) {
			//we close the connection................... ??????
			//panic("Error during the handleAccept");
			System.out.println(e);

		}
	}

	public void handleRead(SelectionKey key){

		SocketChannel socketChannel = (SocketChannel) key.channel();

		try {
			this.nioChannels.get(socketChannel).read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			NioEngine.panic("error in handleRead");
		}
	}



	public void handleWrite(SelectionKey key) throws IOException{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		NioChannelImp nc = this.nioChannels.get(socketChannel);
		//finish = true if we don't have anything to write
		boolean finish = nc.write();
		
		if (finish) key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

		try {
			nc.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Finish to establish a connection
	 * @param key of the channel on which a connection is requested
	 */
//	public void handleConnection(SelectionKey key){
//		SocketChannel sc = (SocketChannel) key.channel();
//
//		try {
//			sc.finishConnect();
//		} catch (IOException e) {
//			// cancel the channel's registration with our selector
//			System.out.println(e);
//			key.cancel();
//			return;
//		}
//		key.interestOps(SelectionKey.OP_READ);
//
//		NioChannelImp nioChannel = new NioChannelImp(sc, this);
//		nioChannels.put(sc, nioChannel);
//		nioChannelCallback.get(sc).connected(nioChannel);
//
//	}

    public void handleConnection(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        sc.finishConnect();
        key.interestOps(SelectionKey.OP_READ);

        NioChannelImp nioChannel = new NioChannelImp(sc, this);
        nioChannels.put(sc, nioChannel);
        nioChannelCallback.get(sc).connected(nioChannel);
    }

	public void wantToWrite(NioChannelImp nChannel)
	{
		try {
			nChannel.getChannel().register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			panic("Impossible to ask writing");
		}
	}

}