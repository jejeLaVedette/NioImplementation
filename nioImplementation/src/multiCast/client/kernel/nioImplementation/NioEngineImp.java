package multiCast.client.kernel.nioImplementation;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;

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

	private HashMap<ServerSocketChannel, NioServer> nioServers;
	private HashMap<SocketChannel, NioChannelImp> nioChannels;
	private HashMap<SocketChannel, ConnectCallback> nioChannelCallback;


	public NioEngineImp() throws Exception {
		//we create the selector
		selector = SelectorProvider.provider().openSelector();
		//we keep the link between SC and multiCast.server
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
                this.port = portTest;
            } catch (IOException ex){
                System.out.println("impossible de se connecter avec le port : "+portTest);
                isConnected = false;
            }
            i++;
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
            selectedKeys.remove();
            if (key.isConnectable()) {
                System.out.println("test connexion");
                handleConnection(key);
                break;
            }
        }

	}

    public NioServer listen(AcceptCallback cc){
        int portTest = portBegin;
        int i = 0;
        boolean isListeaning = false;
        NioServer server = null;

        while((portTest < portBegin + portMargin)&&(!isListeaning)){
            portTest = portBegin + i;
            try{
                server = listen(portTest, cc);
                isListeaning = true;
                this.port = portTest;
            } catch (IOException ex){
                System.out.println("impossible de se connecter avec le port : "+portTest);
                isListeaning = false;
            }
            i++;
        }

        return server;
    }

	@Override
	public NioServer listen(int port, AcceptCallback ac) throws IOException{

		NioServer server;


		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", port);
		serverSocketChannel.socket().bind(inetSocketAddress);

		//will notify when there is incoming data
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//we fix the multiCast.server with the AC and the SC
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
			e.printStackTrace();
		}

	}

	/**
	 * Finish to establish a connection
	 * @param key of the channel on which a connection is requested
	 */
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