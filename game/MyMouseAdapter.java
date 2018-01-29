package game;

import java.awt.event.MouseAdapter;

public class MyMouseAdapter extends MouseAdapter {
	private int x = 0;
	private int y = 0;

	public MyMouseAdapter(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
