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
        this.server.getChatServer().deliver("Server accepte Call Back : "+server);
    }

    @Override
	public void deliver(NioChannel nc, ByteBuffer bb) {
        this.server.getChatServer().deliver("Server : Message received from : " + nc.getRemoteAddress() + " : " + new String(bb.array()));

        String m = new String(bb.array());

        if (m.contains("[OKADD]")) {
            int port = Integer.parseInt(m.split("\\[")[2].split("\\]")[0]);
            this.server.getMapChannelPort().put(nc, port);

            if (server.getClientList().size() == this.server.getMaxClientRoom()) {
                NioChannel channel;
                //pour chaque client
                for (int i = 0; i < server.getClientList().size(); i++) {
                    //on lui envoi la liste des clients apres lui dans la liste
                    for (int j = i + 1; j < server.getClientList().size(); j++) {
                        channel = server.getClientList().get(j);
                        String msgRetour = "[LISTE]" +
                                "[" + channel.getRemoteAddress().getHostName() + "]" +
                                "["+ server.getMapChannelPort().get(channel) +"]";
                        server.getClientList().get(i).send(msgRetour.getBytes(), 0, msgRetour.getBytes().length);
                    }
                }
            }

        }
    }

}