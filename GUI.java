package game;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI {

	private JFrame frame;
	private JLabel[][] field = new JLabel[9][9];
	private Control control = new Control();
	private boolean moving = false;
	private int x1 = 0;
	private int x2 = 0;
	private int y1 = 0;
	private int y2 = 0;
	private boolean network = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(460, 40, 700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(9, 9, 1, 1));
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				// create restart button in the down left corner
				if (i == 8 && j == 0) {
					JButton button = new JButton("...");
					button.setFont(new Font("Dialog", Font.BOLD, 30));
					button.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent arg0) {
							String[] options = new String[]{"reset Game", "save", "load", "host", "join"};
							if (network) {
								options = new String[]{"quit network and reset", "save"};
							}
							int response = JOptionPane.showOptionDialog(null, "Options:", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
							switch (response) {
								case 0 :
									if (network) {
										// end the network mode
										control.stopNetwork(field);
										network = false;
									}
									// reset the game
									control.setupGame(field);
									break;								
								case 1:
									//save board
									control.save();
									break;
								case 2:
									//load board
									control.load(field);
									break;
								case 3 :
									// host a game
									control.hostGame(field);
									network = true;

									break;
								case 4 :
									// join a hosted game
									control.joinGame(field);
									network = true;
									break;
							}
						}
					});
					frame.getContentPane().add(button);
				}
				// create chess board
				else {
					field[i][j] = new JLabel();
					field[i][j].setFont(new Font("Arial Unicode MS", Font.PLAIN, 40));
					field[i][j].setSize(5, 5);
					field[i][j].setOpaque(true);
					// add Click listener to label
					field[i][j].addMouseListener(new MyMouseAdapter(j - 1, i) {
						@Override
						public void mouseClicked(MouseEvent e) {
							// check if left mouse button was clicked
							if (e.getButton() == 1) {
								if (moving) {
									// check if player clicked another of his
									// pieces
									if (control.getBoard().getPiece(getX(), getY()).getColor() == control.getPlayer()) {
										x1 = getX();
										y1 = getY();
										control.showMoves(x1, y1, field);
									} else {
										moving = false;
										x2 = getX();
										y2 = getY();
										// store if piece was actually moved
										boolean moved = control.move(x1, y1, x2, y2, field);
										if (network && moved) {
											new Thread(new Runnable() {
												@Override
												public void run() {
													control.networking(x1, y1, x2, y2, field);
												}
											}).start();
										}
									}
									// check if clicked field is not empty
								} else if (control.getBoard().getPiece(getX(), getY()).getType() != PieceType.DEFAULT) {
									x1 = getX();
									y1 = getY();
									// check if right player is moving
									if (control.getPlayer() == control.getBoard().getPiece(x1, y1).getColor()) {
										moving = true;
										// change color of clicked field
										// according to its default color
										if (field[y1][x1 + 1].getBackground() == Color.white) {
											field[y1][x1 + 1].setBackground(Color.yellow);
										}
										if (field[y1][x1 + 1].getBackground() == Color.gray) {
											field[y1][x1 + 1].setBackground(Color.orange);
										}
										// use control to show allowed moves
										control.showMoves(x1, y1, field);
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
								}
							}
							// if right mouse button was clicked quit moving
							if (e.getButton() == 3) {
								moving = false;
								control.updateBoard(field);
							}
						}
					});
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
					frame.getContentPane().add(field[i][j]);
					// set y-coordinates 1-8
					if (j == 0 && i != 8) {
						field[i][j].setBackground(Color.white);
						field[i][j].setText(String.valueOf(8 - i));
						field[i][j].setHorizontalAlignment(0);
					}
					// set x-coordinates A-H
					if (i == 8 && j != 0) {
						field[i][j].setBackground(Color.white);
						field[i][j].setText(String.valueOf((char) (64 + j)));
						field[i][j].setHorizontalAlignment(0);
					}

				}
			}
		}
		control.setupGame(field);
	}

}