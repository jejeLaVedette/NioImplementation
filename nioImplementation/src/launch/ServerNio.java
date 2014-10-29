package launch;

import java.util.ArrayList;

import nio.engine.AcceptCallback;
import nio.engine.NioChannel;
import nio.engine.NioEngine;
import nio.engine.NioServer;
import nioImplementation.AcceptCallbackImp;
import nioImplementation.NioEngineImp;

public class ServerNio implements Runnable{
			
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
	}

}
