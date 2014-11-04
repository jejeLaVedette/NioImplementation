package multiCast.client;

import multiCast.client.gui.ChatException;
import multiCast.client.gui.ChatGUI;
import multiCast.client.gui.IChatRoom;
import multiCast.client.kernel.ACK;
import multiCast.client.kernel.Message;
import multiCast.client.kernel.callbackClient.ClientAcceptCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
import multiCast.server.kernel.callbackServer.ServerConnectCallbackImp;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by augustin on 30/10/14.
 */
public class Client implements Runnable, IChatRoom{
	
	private int identity;
	private int clock;
    private int listenPort;

    private ArrayList<Message> messageList;
    private ArrayList<ACK> bufferACKList;
    //client list
    private ArrayList<NioChannel> clientList;
    private NioEngineImp nioEngine;
    private IChatListener chat;
	
    public Client(int identity, int clock){
    	this.identity=identity;
    	this.clock=clock;
        this.messageList = new ArrayList<>();
        this.bufferACKList = new ArrayList<>();
        this.clientList = new ArrayList<>();
        new ChatGUI("Client"+this.identity,this);
    }



    public void run() {
        NioServer nioServer;
		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), 6667,new ServerConnectCallbackImp(this));
		} catch (IOException e) {
			NioEngine.panic("Error during the connection attempt of the client");
		}

        nioServer = nioEngine.listen(new ClientAcceptCallbackImp(this));

        if(nioServer == null){
            NioEngine.panic("failed listen port with a client number : "+this.identity);
        }

        this.listenPort = nioEngine.getListenPort();
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

       this.chat.deliver(data);
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
        String data = "[ack]["+this.identity+"]["+this.clock+"]"+m;
        for(int i = 0; i < clientList.size(); i++){
            clientList.get(i).send(data.getBytes(), 0, data.getBytes().length);
        }
    }

    public ArrayList<NioChannel> getClientList(){
        return this.clientList;
    }

    public int getListenPort(){
        return this.listenPort;
    }

    public NioEngineImp getNioEngine(){
        return this.nioEngine;
    }

    @Override
    public void enter(String clientName, IChatListener l) throws ChatException {
        if(this.chat == null){
            this.chat = l;
        }

        this.chat.joined(clientName);
    }

    @Override
    public void leave() throws ChatException {

    }

    @Override
    public void send(String msg) throws ChatException {
       this.nioEngine.getSelector().wakeup();
       sendMessageToEveryBody(msg);

    }

    private void sendMessageToEveryBody(String m){
        String data = "["+this.identity+"]["+this.clock+"]"+m;
        System.out.println("send a message from : "+this.identity+" to everybody");
        for(int i = 0; i < clientList.size(); i++){
            clientList.get(i).send(data.getBytes(), 0, data.getBytes().length);
            this.clock++;
        }
    }
}
