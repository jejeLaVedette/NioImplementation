package multiCast;

import java.io.IOException;
import java.net.InetAddress;

import multiCast.nioImplementation.AcceptCallbackImp;
import multiCast.nioImplementation.ConnectCallbackImp;
import multiCast.nioImplementation.NioEngineImp;
import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;
import nio.engine.NioServer;


/**
 * Class for the test of ping-pong
 * @author Jérôme
 *
 */
public class TestNio {
	public static void server(){
		NioEngineImp nioEngine = null;
		AcceptCallback acImp = new AcceptCallbackImp();

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the server");
		}

        NioServer server = nioEngine.listen(acImp);
        if(server == null){
            NioEngine.panic("creation server failed");
            System.exit(-1);
        }

		System.out.println("server launch");
		nioEngine.mainloop();
		System.out.println("server after mainloop");


	}

	public static void client(){
		NioEngineImp nioEngine = null;
		ConnectCallback cc = new ConnectCallbackImp();

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), cc);
		} catch (IOException e) {
			NioEngine.panic("Error during the connection attempt of the client");
		}

		System.out.println("client launch");

		nioEngine.mainloop();

		System.out.println("client after mainloop");

	}

	public static void main(String args[]){
		
//		Scanner portServer = new Scanner(System.in);
//		System.out.println("Veuillez saisir le port du server :");
//		final String str = portServer.nextLine();
//		System.out.println("Vous avez saisi : " + str);
		
		new Thread(new Runnable() {
			public void run() {
				server();
			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

//		Scanner newPort = new Scanner(System.in);
//		System.out.println("Veuillez saisir le port du/des client(s) :");
//		final String str2 = newPort.nextLine();
//		System.out.println("Vous avez saisi : " + str2);

		new Thread(new Runnable() {
			public void run() {
				client();
			}
		}).start();
	}
}
