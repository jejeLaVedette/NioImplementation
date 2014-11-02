package multiCast.client.kernel;

import multiCast.Entities;
import nio.engine.NioEngine;

import java.util.ArrayList;

/**
 * Created by augustin on 02/11/14.
 */
public class EntitiesClientImpl extends Entities{
    private ArrayList<Message> messageList;

    public EntitiesClientImpl(int identity, int clock){
        this.identity = identity;
        this.clock = clock;
        this.messageList = new ArrayList<>();
    }

    public void putMessage(Message message){
        int i = 0;
        int size = this.messageList.size();

        while((i < size) && (message.getClock() > this.messageList.get(i).getClock())){
            i++;
        }

        this.messageList.add(i,message);
    }

    public void updateClock(int clockReceive){
        this.clock = Math.max(this.clock, clockReceive) +1;
    }

    public void checkAndPrintMessage(){
        int i = 0;
        int size = messageList.size();
        String data = "";

        while((i < size)&&(this.messageList.get(i).receveidAllACK())){
            data = data + messageList.remove(i).toString();
            i++;
        }

        System.out.println(data);
    }

    public void receiveACK(int identityMessage, int identityOver, int clock){
        int i = 0;
        int size = this.messageList.size();

        while((i < size)
                &&(identityMessage != this.messageList.get(i).getIdentity())
                &&(clock != this.messageList.get(i).getClock())){
            i++;
        }

        if(i == size){
            NioEngine.panic("we receive an ack, but we didn't receive the message");
            System.exit(-1);
        }

        this.messageList.get(i).receiveACK(identityOver);
    }

    public int getIdentity(){
        return this.identity;
    }

    public int getClock(){
        return this.clock;
    }

}
