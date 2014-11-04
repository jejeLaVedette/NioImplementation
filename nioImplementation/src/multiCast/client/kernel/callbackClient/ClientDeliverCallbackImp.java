package multiCast.client.kernel.callbackClient;

import multiCast.client.Client;
import multiCast.client.kernel.EntitiesClientImpl;
import multiCast.client.kernel.Message;
import multiCast.server.kernel.callbackServer.ServerConnectCallbackImp;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Jérôme
 *
 */

public class ClientDeliverCallbackImp implements DeliverCallback{
    private Client client;

    public ClientDeliverCallbackImp(Client client){
        this.client=client;
    }

	@Override
	public void deliver(NioChannel nc, ByteBuffer bb) {
		//System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));
        String m = new String(bb.array());


        if(m.contains("[ack]")){
            int clockACK = extractClockACK(m);
            int identityACK = extractIdentityACK(m);
            int clockMessage = extractClockMessage(true, m);
            int identityMessage = extractIdentityMessage(true ,m);


            client.updateClock(clockACK);
            client.receiveACK(identityMessage, identityACK, clockMessage);
            client.checkAndPrintMessage();
        } else if(m.contains("[ADD")){ //le client sait que le server la add a la liste
            //le client previent le server qu'il est pret à recevoir la liste complete et il fournit son port d'écoute
            System.out.println("Client "+this.client.getIdentity() +" receive msg ADD");

            String msgRetour = "[OKADD]["+this.client.getListenPort()+"]";
            nc.send(msgRetour.getBytes(), 0, msgRetour.getBytes().length);

        } else if(m.contains("[LISTE]")) {
            System.out.println("Client " + this.client.getIdentity() + " receive msg from server about complete list");
            String ip = m.split("\\[")[2].split("\\]")[0];
            int port = Integer.parseInt(m.split("\\[")[3].split("\\]")[0]);

            try {
                this.client.getNioEngine().connect(InetAddress.getByName(ip), port, new ServerConnectCallbackImp(client));
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
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