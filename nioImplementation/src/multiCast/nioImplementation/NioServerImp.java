package multiCast.nioImplementation;

import nio.engine.AcceptCallback;
import nio.engine.NioServer;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

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
		try {
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public int getPort() {
		return serverSocketChannel.socket().getLocalPort();
	}

	public AcceptCallback getCallback() {
		return acceptCallback;
	}

}
