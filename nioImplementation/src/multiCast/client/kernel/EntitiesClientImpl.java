package multiCast.client.kernel;

import multiCast.Entities;
import nio.engine.NioEngine;

import java.util.ArrayList;

/**
 * Created by augustin on 02/11/14.
 */
public class EntitiesClientImpl extends Entities{
    private ArrayList<Message> messageList;
    private ArrayList<ACK> bufferACKList;

    public EntitiesClientImpl(int identity, int clock){
        this.identity = identity;
        this.clock = clock;
        this.messageList = new ArrayList<>();
        this.bufferACKList = new ArrayList<>();
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
        this.clock = Math.max(this.clock, clockReceive) +1;
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
            NioEngine.panic("we receive an ack, but we didn't receive the message. " +
                    "The ack is put in an other list to wait the message");
            ACK ack = new ACK(identityMessage, identityACK, clockMessage);
            this.bufferACKList.add(ack);
        } else {
            this.messageList.get(i).receiveACK(identityACK);
        }
    }

    public int getIdentity(){
        return this.identity;
    }

    public int getClock(){
        return this.clock;
    }

}
