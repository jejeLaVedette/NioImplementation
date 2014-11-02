package multiCast.client.kernel.callbackClient;

import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.client.kernel.Message;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

import java.nio.ByteBuffer;

/**
 * 
 * @author Jérôme
 *
 */

public class DeliverCallbackImp implements DeliverCallback{
    private EntitiesClientImpl entities;

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		//System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
        String m = new String(arg1.array());

        if(m.contains("[ack]")){
            int clockACK = extractClock(m);
            int clockMessage = 0;
            int identityMessage = extractIdentity(m);
            int identityOver =0;

            entities.updateClock(clockACK);
            entities.receiveACK(identityMessage, identityOver, clockMessage);
            entities.checkAndPrintMessage();
        } else{
            String data = extractData(m);
            int clock = extractClock(m);
            int identity = extractIdentity(m);

            Message message = new Message(clock, identity, data, 5);
            entities.putMessage(message);
            entities.updateClock(clock);
            sendACKToEveryBody(m);

        }

		//arg0.send(ping.getBytes(),0,ping.getBytes().length);

	}

    private void sendACKToEveryBody(String m){
        String data = "[ack]["+entities.getIdentity()+"]["+entities.getClock()+"]"+m;
    }

    private String extractData(String m){
        String data = "";
        return data;
    }

    private int extractIdentity(String m){
        int identity = 0;
        return identity;
    }

    private int extractClock(String m){
        int clock = 0;
        return clock;
    }

}