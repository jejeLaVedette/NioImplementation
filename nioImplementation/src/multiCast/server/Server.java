package multiCast.server;

import multiCast.nioImplementation.NioEngineImp;
import multiCast.server.gui.ChatException;
import multiCast.server.gui.ChatGUI;
import multiCast.server.gui.IChatRoom;
import multiCast.server.kernel.callbackServer.ServerAcceptCallbackImp;
import nio.engine.NioChannel;
import nio.engine.NioEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Server implements Runnable, IChatRoom {

	private ArrayList<NioChannel> clientList;
	private int maxClientRoom;
    private int identity;
    private HashMap<NioChannel,Integer> mapChannelPort;
    private IChatListener chatServer;

    public Server(int identity, int maxClientRoom){
        this.identity = identity;
        this.mapChannelPort = new HashMap<>();
        clientList = new ArrayList<>();
        this.maxClientRoom = maxClientRoom;
        new ChatGUI("Serveur",this);
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

    @Override
    public void enter(String clientName, IChatListener l) throws ChatException {
        if(this.chatServer == null){
            this.chatServer = l;
        }

        this.chatServer.joined(clientName);
    }

    @Override
    public void leave() throws ChatException {

    }

    @Override
    public void send(String msg) throws ChatException {

    }

    public IChatListener getChatServer(){
        return this.chatServer;
    }
}
