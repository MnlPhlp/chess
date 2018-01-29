package game;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Control {
	Board board = new Board();
	private Rules rules = new Rules(board.getBoard());
	private JFrame frame = new JFrame();
	private String playing = "white";
	int x = 0;
	int y = 0;
	private Server server;
	private Client client;
	boolean hosted = false;
	boolean joined = false;

	public Control() {
		rules.setupGame();
	}

	public Board getBoard() {
		return board;
	}

	public boolean move(int x1, int y1, int x2, int y2, JLabel[][] field) {
		boolean moved = false;
		// check if right player is moving
		if (playing == board.getPiece(x1, y1).getColor()) {
			// check whether move is allowed
			if (rules.validateMove(x1, y1, x2, y2)) {
				// check if King is hit
				if (board.getPiece(x2, y2).getType() == PieceType.KING) {
					JOptionPane.showMessageDialog(frame, "Player " + board.getPiece(x1, y1).getColor() + " won the game");
					setupGame(field);
				} else {
					// move Piece from position x1,y1 to x2,y2
					board.move(x1, y1, x2, y2);
					// if player moves his king on a spot where it can be hit
					// reset the move and show a warning
					if (rules.hitKing(playing, field)) {
						board.move(x2, y2, x1, y1);
						JOptionPane.showMessageDialog(frame, "protect your King");
					} else {
						// check whether a pawn reached the end of the field
						rules.exchangePawn(x2, y2);
						moved = true;
						changePlayer();
					}
					// update the Label field for the GUI
					updateBoard(field);
				}

			} else {
				updateBoard(field);
			}
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Color old = field[y1][x1 + 1].getBackground();
						field[y1][x1 + 1].setBackground(Color.red);
						Thread.sleep(200);
						field[y1][x1 + 1].setBackground(old);

					} catch (InterruptedException e) {
					}
				}
			}).start();
		}
		return moved;
	}

	public char getSymbol(int x, int y) {
		// get the type of the piece at position c1,y1
		PieceType type = board.getPiece(x, y).getType();
		// get the color of the piece at position c1,y1
		String color = board.getPiece(x, y).getColor();
		// get unicode symbol
		int i = 0;
		switch (type) {
			case PAWN :
				i = 5;
				break;
			case ROOK :
				i = 2;
				break;
			case KNIGHT :
				i = 4;
				break;
			case BISHOP :
				i = 3;
				break;
			case QUEEN :
				i = 1;
				break;
			case KING :
				i = 0;
				break;
			case DEFAULT :
				return ' ';
		}

		switch (color) {
			case "white" :
				i = i + 9812;
				break;
			case "black" :
				i = i + 9818;

		}
		return (char) i;
	}

	public void setupGame(JLabel[][] field) {
		rules.setupGame();
		playing = "white";
		updateBoard(field);

	}

	public void showMoves(int x, int y, JLabel[][] field) {
		// reset other changes
		updateBoard(field);
		// change color of clicked field
		if (field[y][x + 1].getBackground() == Color.white) {
			field[y][x + 1].setBackground(Color.yellow);
		}
		if (field[y][x + 1].getBackground() == Color.gray) {
			field[y][x + 1].setBackground(Color.orange);
		}
		// check for every field if piece could move there
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (rules.validateMove(x, y, i, j)) {
					// change color of reachable fields according to default
					// color
					if (field[j][i + 1].getBackground() == Color.white) {
						field[j][i + 1].setBackground(Color.yellow);
					}
					if (field[j][i + 1].getBackground() == Color.gray) {
						field[j][i + 1].setBackground(Color.orange);
					}
				}
			}
		}
	}

	// returns playing color
	public String getPlayer() {
		return playing;
	}

	// changes playing color
	private void changePlayer() {
		if (playing == "white") {
			playing = "black";
		} else if (playing == "black") {
			playing = "white";
		}

	}

	public void updateBoard(JLabel[][] field) {
		for (int i = 0; i < 8; i++) {
			for (int j = 1; j < 9; j++) {
				// get unicode char for chess piece at given position from
				// control
				field[i][j].setText(String.valueOf(getSymbol(j - 1, i)));
				// create dark squares of chess board
				if (i % 2 == 0 && j % 2 == 0 || i % 2 != 0 && j % 2 != 0) {
					field[i][j].setBackground(Color.gray);
					field[i][j].setHorizontalAlignment(0);
				}
				// create light squares of chess board
				else {
					field[i][j].setBackground(Color.white);
					field[i][j].setHorizontalAlignment(0);
				}
			}
		}
		// mark Kings that could be hit in next Move
		rules.hitKing("white", field);
		rules.hitKing("black", field);

	}

	public void hostGame(JLabel[][] field) {
		rules.setupGame();
		updateBoard(field);
		server = new Server();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// block moving until client has connected
				playing = "opponent";
				// connect to client
				server.host();
				// enable moving again
				playing = "white";
				updateBoard(field);

			}
		}).start();
		hosted = true;
	}

	public void joinGame(JLabel[][] field) {
		rules.setupGame();
		updateBoard(field);
		client = new Client();
		client.join();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// block moving until opponent moved
					playing = "opponent";
					
					// get move from Opponent
					int[] move = client.getMove();
					// move piece
					board.move(move[0], move[1], move[2], move[3]);
					//change Type (pawn exchange)
					board.getPiece(move[2], move[3]).setType(move[4]);

				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
				playing = "black";
				updateBoard(field);

			}
		}).start();
		joined = true;
	}

	public void stopNetwork(JLabel[][] field) {
		if (hosted) {
			hosted = false;
			server.stop();
			setupGame(field);
		}
		if (joined) {
			joined = false;
			client.stop();
			setupGame(field);
		}
	}

	public void networking(int x1, int y1, int x2, int y2, JLabel[][] field) {
		// send move to opponent
		if (hosted) {
			server.sendMove(x1, y1, x2, y2, board.getPiece(x2, y2).getTypeInt());

		}
		if (joined) {
			client.sendMove(x1, y1, x2, y2, board.getPiece(x2, y2).getTypeInt());
		}

		updateBoard(field);
		// block moving until opponent moved on his computer
		playing = "opponent";
		// get moves from Opponent
		if (joined) {
			try {
				int[] move = client.getMove();
				board.move(move[0], move[1], move[2], move[3]);
				updateBoard(field);
				board.getPiece(move[2], move[3]).setType(move[4]);
				updateBoard(field);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			playing = "black";
		}

		if (hosted) {
			try {
				int[] move = server.getMove();
				board.move(move[0], move[1], move[2], move[3]);
				updateBoard(field);
				board.getPiece(move[2], move[3]).setType(move[4]);
				updateBoard(field);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			playing = "white";
		}

	}
	
	public void save() {
		try(FileOutputStream fos = new FileOutputStream ("ChessBoard");
	    ObjectOutputStream oos = new ObjectOutputStream (fos)){
			oos.writeObject(board.getBoard());
			oos.writeUTF(playing);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(frame,"Board stored in: "+System.getProperty("user.dir"));
	}
	
	public void load(JLabel[][] field) {
		try(FileInputStream fis = new FileInputStream ("ChessBoard");
			    ObjectInputStream ois = new ObjectInputStream (fis)){
					board.setBoard((Piece[][]) ois.readObject());
					playing=ois.readUTF();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		updateBoard(field);
	}

}