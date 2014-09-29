package nioImplementation;

import java.io.IOException;
import java.net.InetAddress;

import nio.engine.AcceptCallback;
import nio.engine.ConnectCallback;
import nio.engine.NioEngine;

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


	}

	public static void client(int port){
		NioEngineImp nioEngine = null;
		ConnectCallback cc = new ConnectCallbackImplementation(); 


		try{
			nioEngine = new NioEngineImp();
		}catch (Exception e) {
			NioEngine.panic("Error during the creation of the client");
		}

		try {
			nioEngine.connect(InetAddress.getByName("localhost"), port, cc);
		} catch (IOException e) {
			NioEngine.panic("Error during the conenction attempt of the client");
		}

		System.out.println("client launch");

		nioEngine.mainloop();

	}

	public static void main(String args[]){
		new Thread(new Runnable() {
			public void run() {
				server(4212);
			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}


		new Thread(new Runnable() {
			public void run() {
				client(4212);
			}
		}).start();
	}
}
