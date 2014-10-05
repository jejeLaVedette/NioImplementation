package nioImplementation;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import nio.engine.AcceptCallback;
import nio.engine.NioServer;

/**+ * 
 * @author Jérôme
 *
 */
public class NioServerImp extends NioServer{

	private ServerSocketChannel serverSocketChannel;
	private AcceptCallback acceptCallback;
	
	public NioServerImp(ServerSocketChannel serverSocketChannel){
		this.serverSocketChannel = serverSocketChannel;
	}

	public NioServerImp(ServerSocketChannel serverSocketChannel, AcceptCallback acceptCallback) {
		this.serverSocketChannel = serverSocketChannel;
		this.acceptCallback = acceptCallback;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		try {
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return serverSocketChannel.socket().getLocalPort();
	}

	public AcceptCallback getCallback() {
		// TODO Auto-generated method stub
		return acceptCallback;
	}

}
