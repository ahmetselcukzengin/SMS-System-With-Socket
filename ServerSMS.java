import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSMS extends Thread {

	private static DatagramSocket socket;
	private static boolean running=true;
	private static byte[] buf = new byte[256];
	private static int PORT_NUMBER = 4445;

	public static void main(String[] args) throws Exception {
		ServerSMS Server = new ServerSMS();
		Server.start();
		
		
	}

	public ServerSMS() throws Exception {
		socket = new DatagramSocket(PORT_NUMBER);
		System.out.println("Port " + PORT_NUMBER +" is listening...");
	}
	public void run() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> messages = new ArrayList<String>();
		Map<String, Boolean> isLogged = new HashMap<String, Boolean>();
		boolean logged=true;
		
		DatagramPacket outPacket;
		try {
		while(running) {
			DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
			Arrays.fill(buf, (byte)0);
			socket.receive(inPacket);
			
			InetAddress address = inPacket.getAddress();
			int port = inPacket.getPort();
			String received = new String(inPacket.getData(), 0, inPacket.getLength());
			
			
			
			if(received.contains("BEGIN_SESSION")) {
				String user_name = received.split(" ", 2)[1];
				
				if(isLogged.get(user_name)==null ||isLogged.get(user_name)!=true) {
				isLogged.put(user_name, logged);
				int messages_count;
				if(map.containsKey(user_name)) {
					messages_count=map.get(user_name).size();
					String server_message="%d adet SMS mesajýnýz bulunuyor.".formatted(messages_count);
					byte[] outBuf = server_message.getBytes();
					outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
					socket.send(outPacket);
					continue;
				}
					map.put(user_name, messages);
					messages_count=map.get(user_name).size();
					String server_message="%d adet SMS mesajýnýz bulunuyor.".formatted(messages_count);
					byte[] outBuf = server_message.getBytes();
					outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
					socket.send(outPacket);
			}
			}
	
			if(received.contains("SEND_SMS")) {
				List<String> received_list = new ArrayList<String>(Arrays.asList(received.split(" ")));
				String user_name = received_list.get(received_list.size()-1).toString();
				String receiver_name = received_list.get(1).toString();
				if( received_list.contains("SEND_SMS")) {
				received_list.remove(received_list.size()-1);
				received_list.remove(received_list.indexOf("SEND_SMS"));
				received_list.remove(received_list.indexOf(receiver_name));
				
				String message=String.join(" ", received_list);
				
				message=user_name+": "+message;
				
				if(map.containsKey(receiver_name)) {
					
					messages=map.get(receiver_name);
					messages.add(message);
					String server_message="Mesajýnýz alýndý.";
					byte[] outBuf = server_message.getBytes();
					outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
					socket.send(outPacket);
					continue;
				}
					messages.add(message);
					map.put(receiver_name, messages);
					String server_message="Mesajýnýz alýndý.";
					byte[] outBuf = server_message.getBytes();
					outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
					socket.send(outPacket);
				}
			
		        
				
			}
			if(received.contains("POP_SMS")) {
				String user_name = received.split(" ", 2)[1];
				if(map.containsKey(user_name)) {
					int messages_count=map.get(user_name).size();
					if(messages_count>0) {
			            List<String> values = map.get(user_name);
			            byte[] outBuf = values.get(0).getBytes();
			            values.remove(0);
						outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
						socket.send(outPacket);
						
						continue;
					}
				}
				byte[] outBuf = "SMS mesajýnýz bulunmuyor".getBytes();
				outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
				socket.send(outPacket);
				
			}
			if(received.contains("END_SESSION")) {
				String user_name = received.split(" ", 2)[1];
				if(isLogged.get(user_name)) {
				logged=false;
				isLogged.put(user_name, logged);
				}
			}
			if(!received.contains("BEGIN_SESSION") && !received.contains("END_SESSION") && !received.contains("SEND_SMS") && !received.contains("POP_SMS")) {
				String server_message="Mesaj anlaþýlmadý.";
				byte[] outBuf = server_message.getBytes();
				outPacket = new DatagramPacket(outBuf, outBuf.length, address, port);
				socket.send(outPacket);
			}
		}
		socket.close();
	} catch (Exception e) {
		System.out.println("Exception: " + e.getMessage());
	}
}

}


