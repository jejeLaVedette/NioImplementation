package launch;

import java.io.IOException;
import java.net.InetAddress;

import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nioImplementation.ConnectCallbackImplementation;
import nioImplementation.NioEngineImp;

public class ClientNio implements Runnable {

	public ClientNio(){

	}

	@Override
	public void run(){
		NioEngineImp nioEngine = null;
		ConnectCallback cc = new ConnectCallbackImplementation(); 

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), cc);
		} catch (IOException e) {
			NioEngine.panic("Error during the connection attempt of the client");
		}

		System.out.println("client launch");

		nioEngine.mainloop();

		System.out.println("client after mainloop");
	}
}
