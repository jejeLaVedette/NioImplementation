package multiCast.client;

import multiCast.Entities;
import multiCast.client.kernel.ACK;
import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.client.kernel.Message;
import multiCast.nioImplementation.ConnectCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
import multiCast.server.kernel.callbackServer.ServerConnectCallbackImp;
import nio.engine.NioChannel;
import nio.engine.NioEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by augustin on 30/10/14.
 */
public class Client extends NioEngineImp implements Runnable{
	
	private int identity;
	private int clock;

    private ArrayList<Message> messageList;
    private ArrayList<ACK> bufferACKList;
    //client list
    private ArrayList<NioChannel> clientList;
	
    public Client(int identity, int clock) throws Exception{
		super(identity, clock);
		// TODO Auto-generated constructor stub
    	this.identity=identity;
    	this.clock=clock;
        this.messageList = new ArrayList<>();
        this.bufferACKList = new ArrayList<>();
        this.clientList = new ArrayList<>();
    }

	private NioEngineImp nioEngine;

    public void run() {

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), new ServerConnectCallbackImp(this));
		} catch (IOException e) {
			NioEngine.panic("Error during the connection attempt of the client");
		}

		//System.out.println("client launch");

		nioEngine.mainloop();
    }

    public int getIdentity() {
        return identity;
    }

    public void putMessage(Message message){
        int i = 0, j = 0;
        int size = this.messageList.size();
        ACK ack;

        while((i < size) && (message.getClock() > this.messageList.get(i).getClock())){
            i++;
        }

        this.messageList.add(i,message);

        i = 0;
        size = this.bufferACKList.size();
        ArrayList<ACK> clone = (ArrayList<ACK>)this.bufferACKList.clone();

        while(i < size){
            ack = clone.get(i);

            if((ack.getIdentityMessage() == message.getIdentity())
                    &&(ack.getClock() == message.getClock())){

                message.receiveACK(ack.getIdentityACK());
                this.bufferACKList.remove(j);
            } else {
                j++;
            }
            i++;
        }
    }

    public void updateClock(int clockReceive){
        this.clock = Math.max(this.clock, clockReceive) + 1;
    }

    public void checkAndPrintMessage(){
        int i = 0;
        int size = messageList.size();
        String data = "";

        while((i < size)&&(this.messageList.get(0).receveidAllACK())){
            data = data + messageList.remove(0).toString();
            i++;
        }

        System.out.println(data);
    }

    public void receiveACK(int identityMessage, int identityACK, int clockMessage){
        int i = 0;
        int size = this.messageList.size();

        while((i < size)
                &&(identityMessage != this.messageList.get(i).getIdentity())
                &&(clockMessage != this.messageList.get(i).getClock())){
            i++;
        }

        if(i == size){
            ACK ack = new ACK(identityMessage, identityACK, clockMessage);
            this.bufferACKList.add(ack);
        } else {
            this.messageList.get(i).receiveACK(identityACK);
        }
    }


    public int getClock(){
        return this.clock;
    }

    public void sendACKToEveryBody(String m){
        String data = "[ack]["+this.identity+"]["+this.identity+"]"+m;
        for(int i = 0; i < clientList.size(); i++){
            clientList.get(i).send(m.getBytes(), 0, m.getBytes().length);
        }
    }

    public ArrayList<NioChannel> getClientList(){
        return this.clientList;
    }

}
