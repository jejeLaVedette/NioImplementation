package multiCast;

import multiCast.client.Client;
import multiCast.server.Server;

/**
 * Class for the test of ping-pong
 * @author Jérôme
 *
 */
public class TestNio {

	public static void main(String args[]) throws Exception {
        int nbMaxClient = 3;
        int i = 1;

		new Thread(new Server(42, nbMaxClient)).start();

		System.out.println("Server launch");

        while(i <= nbMaxClient){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            new Thread(new Client(i,0)).start();

            System.out.println("Client " +i+ "launch");
            i++;
        }

	}

}
