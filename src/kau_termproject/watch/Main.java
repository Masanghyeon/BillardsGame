package kau_termproject.watch;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new BounceThreadFrame();
		frame.show();
	}
}

class BounceThreadFrame extends JFrame {
	public BounceThreadFrame() {
		JPanel canvas = new JPanel();
		Ball[] ball = new Ball[4];
		setSize(800, 500);
		setTitle("관전");
		final boolean stopRequested = false;
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Container contentPane = getContentPane();
		canvas.setBackground(Color.green);
		contentPane.add(canvas);
		setVisible(true);
		JPanel p = new JPanel();
		addButton(p, "관전",

		new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Ball b1 = new Ball(canvas, "Yellow");
													
				ball[3] = b1;
				Ball b2 = new Ball(canvas, "White");
														
				ball[2] = b2;
				Ball b3 = new Ball(canvas, "Red1");
												
				ball[1] = b3;
				Ball b4 = new Ball(canvas, "Red2"); 
													
				ball[0] = b4;
				control c = new control(ball, canvas);
				c.Initial();

				try {
					incoming = new Socket("localhost", 3333);
					is = new BufferedReader(new InputStreamReader(incoming
							.getInputStream()));
					os = new PrintWriter(incoming.getOutputStream());
				} catch (IOException e) {
				}
				c.start();
			}
		});
		contentPane.add(p, "South");

		JPanel q = new JPanel();
		q.add(new JLabel("Player1 : "));
		q.add(score_label[0]);
		q.add(label);
		q.add(score_label[1]);
		score_label[0].setForeground(Color.red);
		contentPane.add(q, "North");
	}

	public void addButton(Container c, String title, ActionListener a) {
		JButton b = new JButton(title);
		c.add(b);
		b.addActionListener(a);
	}

	Ball[] ball = new Ball[4];
	private int score[] = { 100, 100 };
	private JLabel score_label[] = { new JLabel("100"), new JLabel("100") };
	private JLabel label = new JLabel("        Player2 : ");
	private static final int XSIZE = 40;
	private static final int YSIZE = 40;

	Socket incoming;
	BufferedReader is;
	PrintWriter os;

	class control extends Thread {
		private JPanel box;

		public control(Ball[] b, JPanel b1) {
			ball = b;
			box = b1;
		}

		public void Initial() {
			ball[3].x = 150;
			ball[3].y = 200;
			ball[3].color = Color.YELLOW;
			ball[2].x = 700;
			ball[2].y = 200;
			ball[2].color = Color.WHITE;
			ball[1].x = 150;
			ball[1].y = 250;
			ball[1].color = Color.RED;
			ball[0].x = 600;
			ball[0].y = 200;
			ball[0].color = Color.RED;
		}

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					sleep(5);

					String inString = "";
					StringTokenizer st;
					String op = "";

					for (int i = 0; i < 4; i++) {
						double tempx = 0, tempy = 0;
						int turn = 0;
						try {
							inString = is.readLine();
						} catch (IOException e) {
						}
						st = new StringTokenizer(inString);
						op = st.nextToken();
						if (op.equals("Red2")) {
							tempx = Double.parseDouble(st.nextToken());
							tempy = Double.parseDouble(st.nextToken());
							ball[0].move(tempx, tempy);
							ball[0].draw();
							
							score[0] = Integer.parseInt(st.nextToken());
							score[1] = Integer.parseInt(st.nextToken());
							score_label[0].setText("" + score[0]);
							score_label[1].setText("" + score[1]);
							
							turn = Integer.parseInt(st.nextToken());
							if(turn == 0){
								score_label[0].setForeground(Color.red);
								score_label[1].setForeground(Color.black);
							}
							else{
								score_label[0].setForeground(Color.black);
								score_label[1].setForeground(Color.red);
							}

						} else if (op.equals("Red1")) {
							tempx = Double.parseDouble(st.nextToken());
							tempy = Double.parseDouble(st.nextToken());
							ball[1].move(tempx, tempy);
							ball[1].draw();
						} else if (op.equals("White")) {
							tempx = Double.parseDouble(st.nextToken());
							tempy = Double.parseDouble(st.nextToken());
							ball[2].move(tempx, tempy);
							ball[2].draw();
						} else if (op.equals("Yellow")) {
							tempx = Double.parseDouble(st.nextToken());
							tempy = Double.parseDouble(st.nextToken());
							ball[3].move(tempx, tempy);
							ball[3].draw();
						}
					}
				}
			} catch (InterruptedException e) {
				box.setVisible(false);
			}
			box.setVisible(true);
		}
	}

	class Ball {
		String ball_name;
		private JPanel box;
		private double x;
		private double y;
		Color color;

		protected ServerSocket listen;
		BufferedReader is;
		PrintWriter os;
		Socket client;
		int count;

		public Ball(JPanel b, String string) {
			// TODO Auto-generated constructor stub
			box = b;
			ball_name = string;
		}

		public void draw() {
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
		}

		public void move(double xx, double yy) {
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.setXORMode(box.getBackground());
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
			x = xx;
			y = yy;
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
		}
	}
}
