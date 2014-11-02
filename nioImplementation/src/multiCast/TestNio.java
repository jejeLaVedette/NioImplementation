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

		new Thread(new Server(1999)).start();

		System.out.println("Server launch");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		new Thread(new Client(1,0)).start();

		System.out.println("Client 1 launch");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		new Thread(new Client(1,0)).start();

		System.out.println("Client 2 launch");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		new Thread(new Client(1,0)).start();

		System.out.println("Client 3 launch");

	}

}
