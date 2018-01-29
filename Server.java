package game;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server {
	private ServerSocket server;
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private int[] move = new int[5];

	public Server() {
	}

	public int[] getMove() throws ClassNotFoundException, IOException {
		move = (int[]) input.readObject();
		return move;
}

	public void host() {
		JLabel IP = new JLabel();
		try {
			// show server IP
			IP.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		JTextField Port = new JTextField("1337");
		JLabel invalid = new JLabel("your port was invalid");
		invalid.setForeground(Color.red);
		Object[] message = {"IP:", IP, "Port:", Port};

		int option = JOptionPane.showConfirmDialog(null, message, "host a game",
				JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			int port = Integer.valueOf(Port.getText());
			// check if port is valid
			while (port < 1 || port > 65535) {
				Object[] errormessage = {invalid, "IP:", IP, "Port:", Port};
				option = JOptionPane.showConfirmDialog(null, errormessage,
						"host a game", JOptionPane.OK_CANCEL_OPTION);
				port = Integer.valueOf(Port.getText());
			}
			// try to establish a connection
			try {
				server = new ServerSocket(port);
				// establish a connection
				socket = server.accept();
				// send board to client
				output = new ObjectOutputStream(socket.getOutputStream());
				output.flush();
				// get InputStream from client
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
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
