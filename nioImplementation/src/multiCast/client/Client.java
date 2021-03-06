package multiCast.client;

import multiCast.client.gui.ChatException;
import multiCast.client.gui.ChatGUI;
import multiCast.client.gui.IChatRoom;
import multiCast.client.kernel.ACK;
import multiCast.client.kernel.Message;
import multiCast.client.kernel.callbackClient.ClientAcceptCallbackImp;
import multiCast.client.kernel.callbackClient.ClientConnectCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
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
        new ChatGUI("Client "+this.identity,this);
    }


    /**
     * on se place ensuite en écoute afin de pouvoir recevoir des connexions d'autres clients (on transmettra ensuite notre port et notre ip au serveur
     * pour qu'il puisse la transmettre aux autres clients
     */
    public void run() {
        NioServer nioServer;
        try{
            nioEngine = new NioEngineImp();
        }catch (Exception e) {
            NioEngine.panic("Error during the creation of the client");
        }

        // On se connecte d'abord au serveur

        try {
            nioEngine.connect(InetAddress.getByName("localhost"), 6667,new ClientConnectCallbackImp(this));
        } catch (IOException e) {
            NioEngine.panic("Error during the connection attempt of the client");
        }

        //

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

    /**
     *
     * @param message est le message qu'on vient de recevoir.
     *                Le message est placé suivant son horloge dans la liste des messages
     *                On check ensuite si on n'a pas reçu d'ack correspondant à ce message.
     *                Si c'est le cas, on prend soin de retirer l'ack de la liste des ack en attentes, et de mettre à jour le tableau de boolean du message.
     */
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

    /**
     * Cette methode va permettre de vérifier qu'il n'y a pas de message à afficher.
     * On devra appeler cette methode à chaque fin de traitement de la reception d'un ack ou d'un message
     */
    public String checkAndPrintMessage(){
        int i = 0;
        int size = messageList.size();
        String data = "";

        while((i < size)&&(this.messageList.get(0).receveidAllACK())){
            data = messageList.remove(0).toString();
            this.chat.deliver(data);
            i++;
        }

        return data;
    }

    /**
     *
     * @param identityMessage il s'agit de l'id du client qui a envoyé le message
     * @param identityACK il s'agit de l'id du client qui a envoyé l'ack
     * @param clockMessage il s'agit de l'horloge du message (et non pas de l'ack)
     *
     * Lors de la reception d'un ack, cette methode va permettre de savoir si on a reçu un message correspond à ce message
     * Si c'est le cas, alors on mettra à jour le tableau de booleen.
     * Sinon on mettra l'ack dans une liste temporaire en attendant l'arriver du message.
     */
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
        sendMessageToEveryBody(msg);
        this.nioEngine.getSelector().wakeup();

    }

    private void sendMessageToEveryBody(String m){
        int clockTemp = clock;
        String data = "["+this.identity+"]["+clockTemp+"]"+m;
        System.out.println("send a message from : "+this.identity+" to everybody");

        for(int i = 0; i < clientList.size(); i++){
            clientList.get(i).send(data.getBytes(), 0, data.getBytes().length);
            this.clock++;
        }

        Message message = new Message(clockTemp, identity, m, getClientList().size());
        putMessage(message);
        updateClock(clock);
        sendACKToEveryBody(data);
        //on simule le fait qu'on a reçu notre propre ack
        receiveACK(identity,getIdentity(),clock);
    }
}
