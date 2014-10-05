package nioImplementation;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;


/**
 * Class for the test of ping-pong
 * @author Jérôme
 *
 */
public class TestNio {
	public static void server(int port){
		NioEngineImp nioEngine = null;
		AcceptCallback acImp = new AcceptCallbackImp();

		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the server");
		}

		try {  
			nioEngine.listen(port, acImp);
		} catch (IOException e) {
			NioEngine.panic("Error during the listening of the server");
		}

		System.out.println("server launch");
		nioEngine.mainloop();
		System.out.println("server after mainloop");


	}

	public static void client(){
		NioEngineImp nioEngine = null;
		ConnectCallback cc = new ConnectCallbackImplementation(); 

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
		
		Scanner portServer = new Scanner(System.in);
		System.out.println("Veuillez saisir le port du server :");
		final String str = portServer.nextLine();
		System.out.println("Vous avez saisi : " + str);
		
		new Thread(new Runnable() {
			public void run() {
				server(Integer.parseInt(str));
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
