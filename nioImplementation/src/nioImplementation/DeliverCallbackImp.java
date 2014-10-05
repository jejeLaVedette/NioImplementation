package nioImplementation;

import java.nio.ByteBuffer;

import nio.engine.DeliverCallback;
import nio.engine.NioChannel;

/**
 * 
 * @author Jérôme
 *
 */

public class DeliverCallbackImp implements DeliverCallback{

	@Override
	public void deliver(NioChannel arg0, ByteBuffer arg1) {
		System.out.println("Message received from : " + arg0.getRemoteAddress() + " : " + new String(arg1.array()));

		//String ping="ping-pong";
		String ping = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum eget posuere odio, ut dapibus "
				+ "urna. Donec posuere lectus est, vel porttitor massa bibendum ut. Pellentesque interdum tincidunt ipsum "
				+ "nec euismod. Maecenas consectetur lacinia lacus, eu pretium ipsum rutrum in. Sed id dolor libero. "
				+ "Vestibulum ex felis, porta convallis maximus sed, varius a nisi. Praesent efficitur dolor et enim "
				+ "bibendum rutrum. Sed non turpis ligula. Integer odio arcu, luctus quis pretium ac, accumsan at lacus. "
				+ "Sed aliquet aliquam pellentesque. Morbi vitae efficitur dui, at lacinia tellus. Donec quis luctus felis, "
				+ "quis dapibus odio. Cras pharetra mattis enim, id vestibulum magna. Sed fringilla quam sit amet accumsan "
				+ "efficitur. Aenean sodales odio vel ante dictum pharetra.";
		
		arg0.send(ping.getBytes(),0,ping.getBytes().length);

	}

}