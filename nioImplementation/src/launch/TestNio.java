package launch;

/**
 * Class for the test of ping-pong
 * @author Jérôme
 *
 */
public class TestNio {

	
	public static void main(String args[]){
		
//		Scanner portServer = new Scanner(System.in);
//		System.out.println("Veuillez saisir le port du server :");
//		final String str = portServer.nextLine();
//		System.out.println("Vous avez saisi : " + str);
		
		new Thread(new ServerNio()).start();

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
		
		new Thread(new ClientNio()).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		new Thread(new ClientNio()).start();
	}
}
