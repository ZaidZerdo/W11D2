package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SendingNameFile {
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
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(7777);
			System.out.println("Server started!");
			
			while (true) {							
				(new Thread(new ClientOp(server.accept()))).start();
			}			
		} catch (IOException e) {
			System.out.println("Server crashed -_-");
			try {
				server.close();
			} catch (IOException e1) {
				System.out.println("Could not close server...");
			}
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
			OutputStream writer = null;
			FileInputStream fis = null;
			
			try {
				fis = new FileInputStream(new File("src/task3/names.txt"));
				writer = socket.getOutputStream();
				
				System.out.println("Sending file to " + ipName.get(ip) + "...");
				byte[] fileBytes = new byte[1024];
				int readBytes;
				while ((readBytes = fis.read(fileBytes, 0, fileBytes.length)) > 0) {
					writer.write(fileBytes, 0, readBytes);
					writer.flush();
				}
				System.out.println("File sent to " + ipName.get(ip) + ".");
				
				writer.close();
				fis.close();
			} catch (IOException e) {
				System.out.println(ipName.get(ip) + " had a problem.");
				return;
			}
			
			
		}
		
	}

}