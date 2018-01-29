package game;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Rules {
	private Piece[][] board;
	private Piece defaultPiece = new Piece(PieceType.DEFAULT, "none");

	public Rules(Piece[][] board) {
		// store given board in order to check requested moves
		this.board = board;
	}

	public void setupGame() {
		// empty the board (fill with default pieces)
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = defaultPiece;
			}
		}
		// create and arrange the pieces
		// create Pawns
		for (int i = 0; i < 8; i++) {
			board[i][1] = new Piece(PieceType.PAWN, "black");
			board[i][6] = new Piece(PieceType.PAWN, "white");
		}
		// create Rooks
		board[0][0] = new Piece(PieceType.ROOK, "black");
		board[7][0] = new Piece(PieceType.ROOK, "black");
		board[0][7] = new Piece(PieceType.ROOK, "white");
		board[7][7] = new Piece(PieceType.ROOK, "white");
		// create Knights
		board[1][0] = new Piece(PieceType.KNIGHT, "black");
		board[6][0] = new Piece(PieceType.KNIGHT, "black");
		board[1][7] = new Piece(PieceType.KNIGHT, "white");
		board[6][7] = new Piece(PieceType.KNIGHT, "white");
		// create Bishops
		board[2][0] = new Piece(PieceType.BISHOP, "black");
		board[5][0] = new Piece(PieceType.BISHOP, "black");
		board[2][7] = new Piece(PieceType.BISHOP, "white");
		board[5][7] = new Piece(PieceType.BISHOP, "white");
		// create Queens
		board[3][0] = new Piece(PieceType.QUEEN, "black");
		board[3][7] = new Piece(PieceType.QUEEN, "white");
		// create Kings
		board[4][0] = new Piece(PieceType.KING, "black");
		board[4][7] = new Piece(PieceType.KING, "white");
	}

	public boolean validateMove(int x1, int y1, int x2, int y2) {
		// get the type of the piece at position x1,y1
		PieceType type1 = board[x1][y1].getType();
		// get the color of the piece at position x1,y1
		String color1 = board[x1][y1].getColor();
		// get the color of the piece at position x1,y1
		String color2 = board[x2][y2].getColor();
		// check if the move is allowed
		boolean allowed = false;
		// check if the move is allowed according to the pieces moves
		if (color1 != color2) {
			switch (type1) {
				case PAWN :
					// Hitting another player diagonally
					if (Math.abs(x1 - x2) == 1 && color2 != "none") {
						if (color1 == "white" && (y1 - y2) == 1) {
							allowed = true;
						} else if (color1 == "black" && (y1 - y2) == -1) {
							allowed = true;
						}
					}

					// moving forward normally
					if (color2 == "none") {
						if (x1 == x2) {
							if (color1 == "white" && (y1 - y2) == 1) {
								allowed = true;
							} else if (color1 == "black" && (y1 - y2) == -1) {
								allowed = true;
							}
						}
						// allow to move two fields at the beginning
						if (x1 == x2) {
							if (color1 == "white" && (y1 - y2) == 2 && y1 == 6) {
								allowed = true;
							}
							if (color1 == "black" && (y1 - y2) == -2 && y1 == 1) {
								allowed = true;
							}
						}
					}

					break;
				case ROOK :
					if (y1 == y2) {
						allowed = checkHorizontal(x1, y1, x2, y2);
					}
					if (x1 == x2) {
						allowed = checkVertical(x1, y1, x2, y2);
					}
					break;
				case KNIGHT :
					if (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 2) {
						allowed = true;
					}
					if (Math.abs(y1 - y2) == 1 && Math.abs(x1 - x2) == 2) {
						allowed = true;
					}

					break;
				case BISHOP :
					if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
						allowed = checkDiagonal(x1, y1, x2, y2);
					}
					break;
				case QUEEN :
					// allow diagonal moves
					if (Math.abs(x1 - x2) == Math.abs(y1 - y2)) {
						allowed = checkDiagonal(x1, y1, x2, y2);
					}
					// allow vertical/horizontal moves
					if (y1 == y2) {
						allowed = checkHorizontal(x1, y1, x2, y2);
					}
					if (x1 == x2) {
						allowed = checkVertical(x1, y1, x2, y2);
					}
					break;
				case KING :
					// limit movement to one field in each direction
					if (Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1) {
						allowed = true;
					}
					break;
				case DEFAULT :
					break;
			}
		}
		return allowed;
	}

	// enable horizontal movement
	private boolean checkHorizontal(int x1, int y1, int x2, int y2) {
		boolean allowed = false;
		boolean freepath = true;
		int i = 0;
		// check if piece is standing in the way
		// check for moving right
		while (i < (x2 - x1) && freepath) {
			if (board[x1 + i][y1].getColor() != "none" && board[x1 + i][y1].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 + i][y1].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// check for moving left
		while (i < x1 - x2 && freepath) {
			if (board[x1 - i][y1].getColor() != "none" && board[x1 - i][y1].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 - i][y1].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// if path is free allow the move
		if (freepath) {
			allowed = true;
		}
		return allowed;
	}

	// enable vertical movement
	private boolean checkVertical(int x1, int y1, int x2, int y2) {
		boolean allowed = false;
		boolean freepath = true;
		int i = 0;
		// check if piece is standing in the way
		// check for moving down
		while (i < (y2 - y1) && freepath) {
			if (board[x1][y1 + i].getColor() != "none" && board[x1][y1 + i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1][y1 + i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// check for moving up
		while (i < y1 - y2 && freepath) {
			if (board[x1][y1 - i].getColor() != "none" && board[x1][y1 - i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1][y1 - i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// if path is free allow the move
		if (freepath) {
			allowed = true;
		}
		return allowed;
	}

	// enable diagonal movement
	private boolean checkDiagonal(int x1, int y1, int x2, int y2) {
		boolean allowed = false;
		boolean freepath = true;
		int i = 0;
		// check if piece is standing in the way
		// check for moving down right
		while (i < (y2 - y1) && i < (x2 - x1) && freepath) {
			if (board[x1 + i][y1 + i].getColor() != "none" && board[x1 + i][y1 + i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 + i][y1 + i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// check for moving up right
		while (i < (y1 - y2) && i < (x2 - x1) && freepath) {
			if (board[x1 + i][y1 - i].getColor() != "none" && board[x1 + i][y1 - i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 + i][y1 - i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// check for moving up left
		while (i < (y1 - y2) && i < (x1 - x2) && freepath) {
			if (board[x1 - i][y1 - i].getColor() != "none" && board[x1 - i][y1 - i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 - i][y1 - i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// check for moving down left
		while (i < (y2 - y1) && i < (x1 - x2) && freepath) {
			if (board[x1 - i][y1 + i].getColor() != "none" && board[x1 - i][y1 + i].getColor() != board[x1][y1].getColor()) {
				freepath = false;
			}
			i++;
			if (board[x1 - i][y1 + i].getColor() == board[x1][y1].getColor()) {
				freepath = false;
			}
		}

		// if path is free allow the move
		if (freepath) {
			allowed = true;
		}
		return allowed;
	}

	public void exchangePawn(int x1, int y1) {
		// check if a pawn reached end of the board
		if (board[x1][y1].getType() == PieceType.PAWN) {
			int response = -1;
			// exchange white pawn on the top end of the board
			if (board[x1][y1].getColor() == "white" && y1 == 0) {
				String[] options = new String[]{"Queen", "Bishop", "Rook", "Knight"};
				response = JOptionPane.showOptionDialog(null, "select a piece:", "Piece", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			}
			// exchange black pawn on the bottom end of the board
			if (board[x1][y1].getColor() == "black" && y1 == 7) {
				String[] options = new String[]{"Queen", "Bishop", "Rook", "Knight"};
				response = JOptionPane.showOptionDialog(null, "select a piece:", "Piece", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			}
			// change Pawn to selected piece
			switch (response) {
				case 0 :
					board[x1][y1].setType(PieceType.QUEEN);
					break;
				case 1 :
					board[x1][y1].setType(PieceType.BISHOP);
					break;
				case 2 :
					board[x1][y1].setType(PieceType.ROOK);
					break;

				case 3 :
					board[x1][y1].setType(PieceType.KNIGHT);
					break;
			}
		}
	}

	// check whether King can get hit in opponent's next move
	public boolean hitKing(String color, JLabel[][] field) {
		boolean search = true;
		boolean check = false;
		int x1 = 0;
		int y1 = 0;
		// search for King
		while (search) {
			// rest x and ad 1 to y if reaching the end of a line
			if (x1 == 8) {
				x1 = 0;
				y1++;
			}
			// end search if king is at current position
			if (board[x1][y1].getColor() == color && board[x1][y1].getType() == PieceType.KING) {
				search = false;
			} else {
				x1++;
			}
		}
		// check if piece at x2 y2 could hit king
		for (int x2 = 0; x2 < 8; x2++) {
			for (int y2 = 0; y2 < 8; y2++) {
				if (board[x2][y2].getColor() != color && validateMove(x2, y2, x1, y1)) {
					// mark field of the King as red
					field[y1][x1 + 1].setBackground(Color.red);
					check = true;
				}

			}
		}
		return check;
	}

}
