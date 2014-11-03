package multiCast.server.kernel.callbackServer;

import multiCast.client.Client;
import multiCast.server.Server;
import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

import java.nio.ByteBuffer;

/**
 * 
 * @author Jérôme
 *
 */

public class ServerDeliverCallbackImp implements DeliverCallback{

    private Server server;

    public ServerDeliverCallbackImp(Server server) {
        this.server = server;
        System.out.println("Server accepteCB : "+server);
    }

    @Override
	public void deliver(NioChannel nc, ByteBuffer bb) {
        System.out.println("Server : Message received from : " + nc.getRemoteAddress() + " : " + new String(bb.array()));

        String m = new String(bb.array());
        String[] msg = m.split("\\[");

        if (m.contains("[OKADD]")) {
            if (server.getClientList().size() == Server.getMaxClientRoom()) {
                //pour chaque client
                for (int i = 0; i < server.getClientList().size(); i++) {
                    //on lui envoi la liste des clients apres lui dans la liste
                    for (int j = i + 1; j < server.getClientList().size(); j++) {
                        String msgRetour = "[LISTE]" + "[" + server.getClientList().get(j).getRemoteAddress().getHostName() + "]";
                        server.getClientList().get(i).send(msgRetour.getBytes(), 0, msgRetour.getBytes().length);
                    }
                }
            }

        }
    }

}