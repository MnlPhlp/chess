package game;
import java.io.Serializable;

public class Board  implements Serializable{

	private static final long serialVersionUID = 6421607357301178617L;
	// create 2d array to store the pieces and their position
	// it symbolizes the chess board
	private Piece[][] board = new Piece[8][8];
	private Piece defaultPiece = new Piece(PieceType.DEFAULT, "none");

	public Board() {
	}

	
	public Piece getPiece(int x, int y){
		return board[x][y];
	}
	
	public void setPiece(Piece p, int x, int y) {
		board[x][y] = p;
	}

	
	public void move(int x1, int y1, int x2, int y2) {
		Piece p = getPiece(x1, y1);
		setPiece(defaultPiece, x1, y1);
		setPiece(p, x2, y2);
	}
	
	public Piece[][] getBoard(){
		return board;
	}


	public void setBoard(Piece[][] b) {
		board=b;
		
	}

}
