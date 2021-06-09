import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;/*
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;*/

public class ClientSMS {
	private static DatagramSocket socket;
	private static InetAddress address;
	private static boolean running=true;
	private static boolean logged=false;
	private static String user_name;
	public static void main(String[] args) throws Exception {
		ClientSMS client=null;
		while(running) {
			/*@SuppressWarnings("resource")
			Scanner myinput = new Scanner(System.in);
			String inputtext = myinput.nextLine();*/
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String inputtext = reader.readLine();

			if(inputtext.contains("BEGIN_SESSION") && !logged) {//Bir kullanýcýnýn ayný anda 2 farklý oturum açmasýný engelleyebilir misiniz?
				logged=true;
				user_name = inputtext.split(" ", 2)[1];
				client = new ClientSMS();
				String output =client.send(inputtext);
				System.out.println(output);
			}
			if(inputtext.contains("END_SESSION")&& logged) {
				if(client!=null) {
					logged = false;
					running = false;
					client.send(inputtext+" "+user_name);
					client.close();
				}
			}
			if(inputtext.contains("SEND_SMS")) {
				if(logged) {
					String output =client.send(inputtext +" "+ user_name);
					System.out.println(output);
				}
				else {
				System.out.println("Oturum Açýnýz.");
				}
				
			}
			if(inputtext.contains("POP_SMS")) {
				if(logged) {
					String output =client.send(inputtext +" "+ user_name);
					System.out.println(output);
				}
				else {
				System.out.println("Oturum Açýnýz.");
				}
				
			}
			else if(!inputtext.contains("BEGIN_SESSION") && !inputtext.contains("END_SESSION") && !inputtext.contains("SEND_SMS") && !inputtext.contains("POP_SMS")) {
				String output =client.send(inputtext +" "+ user_name);
				System.out.println(output);
			}
		}
	}

	public ClientSMS() throws Exception {
		socket = new DatagramSocket();
		address = InetAddress.getByName("localhost");
	}

	public String send (String msg) throws Exception {
		byte[] outBuf = msg.getBytes();
		DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, 4445);
		socket.send(outPacket);

		byte[] inBuf = new byte[256];
		DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
		socket.receive(inPacket);
		
		String received = new String(inPacket.getData(), 0, inPacket.getLength());
		return received;
	}

	public void close() {
		socket.close();
	}
}