package task3;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerChat extends JFrame {
	static Map<String, String> ipName = new HashMap<>();
	static BufferedReader fileRead;	
	private JTextArea chat = new JTextArea();
	
	public ServerChat() {
		add(new JScrollPane(chat));
		chat.setFont(new Font("Serif", Font.PLAIN, 40));
				
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	// 10.0.82.98
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
		ServerChat window = new ServerChat();
		try {
			ServerSocket server = new ServerSocket(8888);
			System.out.println("Server started!");
			
			while (true) {							
				(new Thread(new ClientOp(server.accept(), window))).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Server crashed -_-");
			return;				
		}
	}
	
	static class ClientOp implements Runnable {

		private Socket socket;
		private ServerChat window;
		
		public ClientOp(Socket socket, ServerChat window) {
			this.socket = socket;
			this.window = window;
		}
		
		@Override
		public void run() {
			String ip = socket.getInetAddress().getHostAddress();
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = reader.readLine();
				
				window.chat.setText(window.chat.getText() + "\n" + ipName.get(ip) + ": " + msg);
			} catch (IOException e) {
				System.out.println("IP " + ip + " ima problema.");
				return;
			}
			
		}
		
	}

}