package task4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SendStartingWord {
	static Map<String, String> ipName = new HashMap<>();
	static BufferedReader fileRead;	
	
	public static void main(String[] args) {
		try {
			fileRead = new BufferedReader(new FileReader(new File("src/task3/names.txt")));
			while (fileRead.ready()) {
				String line = fileRead.readLine();
				
				int spaceIndex = line.indexOf(' ');
				
				ipName.put(line.substring(0, spaceIndex), line.substring(spaceIndex + 1));
			}
			fileRead.close();
		} catch (IOException e1) {
			System.out.println("FileWrite error....");
			return;
		}
		
		try {
			Socket socket = new Socket("10.0.82.15", 8888);
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("Odmah");
			writer.newLine();
			writer.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("Could not send the starting message.");
			System.exit(1);
		}
		
		try {
			ServerSocket server = new ServerSocket(8888);
			System.out.println("Server started!");
			
			while (true) {							
				(new Thread(new ClientOp(server.accept()))).start();
			}
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
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = reader.readLine();
				
				System.out.println(ipName.get(ip) + ": " + msg);
			} catch (IOException e) {
				System.out.println("IP " + ip + " ima problema.");
				return;
			}
			
		}
		
	}

}
