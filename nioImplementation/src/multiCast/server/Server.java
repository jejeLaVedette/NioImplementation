package multiCast.server;

import multiCast.Entities;
import multiCast.nioImplementation.NioEngineImp;
import multiCast.server.kernel.callbackServer.ServerAcceptCallbackImp;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Server extends Entities implements Runnable{

	private ArrayList<NioChannel> clientList;
	private int maxClientRoom;
    private HashMap<NioChannel,Integer> mapChannelPort;

    public Server(int identity){
        this.identity = identity;
        this.clock = 0;
        this.addressHashMap = new HashMap<>();
        this.integerHashMap = new HashMap<>();
        this.mapChannelPort = new HashMap<>();
        clientList = new ArrayList<>();
        maxClientRoom = 3;
        }

   
    public void run(){
        NioEngineImp nioEngine = null;

        try{
            nioEngine = new NioEngineImp();
        }catch (Exception e) {
            NioEngine.panic("Error during the creation of the server");
        }

        try {
            nioEngine.listen(6667,new ServerAcceptCallbackImp(this));
        } catch (IOException e) {
            NioEngine.panic("creation server failed");
        }

        nioEngine.mainloop();
    }


	public ArrayList<NioChannel> getClientList() {
		return this.clientList;
	}

    public HashMap<NioChannel, Integer> getMapChannelPort(){
        return this.mapChannelPort;
    }

    public int getMaxClientRoom() {
        return maxClientRoom;
    }
}
