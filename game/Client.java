package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Client {
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private int[] move = new int[4];

	public Client() {
		
	}

	public int[] getMove() throws ClassNotFoundException, IOException {
			move = (int[]) input.readObject();
			return move;
	}

	public void join() {
		JTextField IP = new JTextField("localhost");
		JTextField Port = new JTextField("1337");
		Object[] message = { "IP:", IP, "Port:", Port };

		int option = JOptionPane.showConfirmDialog(null, message, "host a game", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			int port = Integer.valueOf(Port.getText());
			String ip = IP.getText();
			// try to establish a connection
			try {
				socket = new Socket(ip, port);
				// get OutputStream to server
				output = new ObjectOutputStream(socket.getOutputStream());
				// get InputStream from server
				input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMove(int x1, int y1, int x2, int y2, int type) {
		move[0]=x1;
		move[1]=y1;
		move[2]=x2;
		move[3]=y2;
		move[4]=type;
		try {
			output.writeObject(move);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
