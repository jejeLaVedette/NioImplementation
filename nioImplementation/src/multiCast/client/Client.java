package multiCast.client;

import multiCast.Entities;
import multiCast.nioImplementation.ConnectCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
import nio.engine.NioEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Client extends Entities{
    private NioEngineImp nioEngine;

    public Client(int identity){
        this.identity = identity;
        this.clock = 0;
        this.addressHashMap = new HashMap<String, InetAddress>();
        this.integerHashMap = new HashMap<String, Integer>();
    }

    public void run() {

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), new ConnectCallbackImp());
		} catch (IOException e) {
			NioEngine.panic("Error during the connection attempt of the client");
		}

		System.out.println("client launch");

		nioEngine.mainloop();
    }
}
