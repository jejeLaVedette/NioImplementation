package multiCast.client.kernel.callbackClient;

import multiCast.client.Client;
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
    private Client client;

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		//System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
        String m = new String(arg1.array());

        if(m.contains("[ack]")){
            int clockACK = extractClockACK(m);
            int identityACK = extractIdentityACK(m);
            int clockMessage = extractClockMessage(true ,m);
            int identityMessage = extractIdentityMessage(true ,m);


            client.updateClock(clockACK);
            client.receiveACK(identityMessage, identityACK, clockMessage);
            client.checkAndPrintMessage();
        } else{
            String data = extractData(m);
            int clock = extractClockMessage(false, m);
            int identity = extractIdentityMessage(false, m);

            Message message = new Message(clock, identity, data, 5);
            client.putMessage(message);
            client.updateClock(clock);
            client.sendACKToEveryBody(m);

        }

		//arg0.send(ping.getBytes(),0,ping.getBytes().length);

	}

    /*private void sendACKToEveryBody(String m){
        String data = "[ack]["+entities.getIdentity()+"]["+entities.getClock()+"]"+m;
    }*/

    private String extractData(String m){
        String[] buff = m.split("\\[");
        return buff[buff.length-1].split("\\]")[1];
    }

    private int extractIdentityMessage(boolean isACKMessage,String m){
        int identity = 0;
        String[] buffer = m.split("\\[");

        if(isACKMessage){
            identity = Integer.parseInt(buffer[4].split("\\]")[0]);
        } else{
            identity = Integer.parseInt(buffer[1].split("\\]")[0]);
        }
        return identity;
    }

    private int extractIdentityACK(String m){
        String[] buffer = m.split("\\[");
        return Integer.parseInt(buffer[2].split("\\]")[0]);
    }

    private int extractClockMessage(boolean isACKMessage, String m){
        int clock = 0;
        String[] buffer = m.split("\\[");

        if(isACKMessage){
            clock = Integer.parseInt(buffer[5].split("\\]")[0]);
        } else{
            clock = Integer.parseInt(buffer[2].split("\\]")[0]);
        }
        return clock;
    }

    private int extractClockACK(String m){
        String[] buffer = m.split("\\[");
        return Integer.parseInt(buffer[3].split("\\]")[0]);
    }

}