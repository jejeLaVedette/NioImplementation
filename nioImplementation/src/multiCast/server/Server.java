package multiCast.server;

import multiCast.Entities;
import multiCast.nioImplementation.NioEngineImp;
import multiCast.server.kernel.callbackServer.ServerAcceptCallbackImp;
import nio.engine.NioEngine;
import nio.engine.NioServer;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Server extends Entities{

    public Server(int identity){
        this.identity = identity;
        this.clock = 0;
        this.addressHashMap = new HashMap<String, InetAddress>();
        this.integerHashMap = new HashMap<String, Integer>();
    }

   
    public void run(){
        NioEngineImp nioEngine = null;

        try{
            nioEngine = new NioEngineImp();
        }catch (Exception e) {
            NioEngine.panic("Error during the creation of the server");
        }

        NioServer server = nioEngine.listen(new ServerAcceptCallbackImp());
        if(server == null){
            NioEngine.panic("creation server failed");
            System.exit(-1);
        }

        System.out.println("server launch");
        nioEngine.mainloop();
    }
}
