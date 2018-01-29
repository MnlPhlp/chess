package game;

import java.io.Serializable;

public class Piece implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1740725596398840823L;
	private PieceType type = PieceType.DEFAULT;
	private String color = "";

	public Piece(PieceType type, String color) {
		this.type = type;
		this.color = color;
	}

	public void setType(PieceType type) {
		this.type = type;

	}

	public void setType(int i) {
		switch (i) {
			case 0 :
				this.type = PieceType.BISHOP;
				break;
			case 1 :
				this.type = PieceType.KING;
				break;
			case 2 :
				this.type = PieceType.KNIGHT;
				break;
			case 3 :
				this.type = PieceType.PAWN;
				break;
			case 4 :
				this.type = PieceType.QUEEN;
				break;
			case 5 :
				this.type = PieceType.ROOK;
				break;
			case -1:
				this.type=PieceType.DEFAULT;
		}

	}

	public int getTypeInt() {
		int i =-1;
		switch (this.type) {
			case BISHOP :
				i=0;
				break;
			case KING :
				i=1;
				break;
			case KNIGHT :
				i=2;
				break;
			case PAWN :
				i=3;
				break;
			case QUEEN :
				i=4;
				break;
			case ROOK :
				i=5;
				break;
			case DEFAULT :
				i=-1;
				break;

		}
		return i;
	}

	public PieceType getType() {
		return type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
