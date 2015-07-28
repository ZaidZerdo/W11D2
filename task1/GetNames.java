package task1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GetNames {
	static Map<String, String> ipName = new HashMap<>();
	static BufferedWriter fileWrite;	
	
	// 10.0.82.98
	public static void main(String[] args) {		
		try {
			fileWrite = new BufferedWriter(new FileWriter(new File("names.txt"), true));
		} catch (IOException e1) {
			System.out.println("FileWrite error....");
			return;
		}
		try {
			ServerSocket server = new ServerSocket(8888);
			System.out.println("Server started!");
			
			while (ipName.size() < 26) {							
				(new Thread(new ClientOp(server.accept()))).start();
			}
			
			server.close();
			System.out.println("Everyone sent their name!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Server crashed -_-");
			return;				
		}
	}
	
	static class ClientOp implements Runnable {

		private Socket socket;
		
		public ClientOp(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			String ip = socket.getInetAddress().getHostAddress();
			BufferedReader reader = null;
			BufferedWriter writer = null;
			String name = "???";
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	
				name = reader.readLine();
			} catch (IOException e) {
				System.out.println("IP " + ip + " ima problema.");
				return;
			}
			
			try {
				if (ipName.containsKey(ip)) {
					writer.write("Vec si poslao svoje ime!");
					writer.newLine();
					writer.close();
					System.out.println(name + " je vec poslao ime. Dosta jedno!");
				} else {
					writer.write("Hvala ti, " + name + ".");
					writer.newLine();
					writer.close();
					
					fileWrite.write(ip + " " + name);
					fileWrite.newLine();
					fileWrite.flush();
					
					ipName.put(ip, name);
					System.out.println(name + " je poslao!");
				}
			} catch (IOException e) {
				System.out.println(name + " nije primio na kraju poruku.");
				return;
			}
		}
		
	}

}
