package multiCast.client;

import multiCast.Entities;
import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.nioImplementation.ConnectCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
import nio.engine.NioEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Client extends NioEngineImp implements Runnable{
	
	private int identity;
	private int clock;
	
    public Client(int identity, int clock) throws Exception{
		//super(identity, clock);
		// TODO Auto-generated constructor stub
    	this.identity=identity;
    	this.clock=clock;
    }

	private NioEngineImp nioEngine;

    public void run() {

		try{
			System.out.println("in run");
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
