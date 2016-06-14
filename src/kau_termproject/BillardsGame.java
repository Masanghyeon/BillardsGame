package kau_termproject;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import javax.swing.*;

public class BillardsGame {
	//���� Ŭ���������� frame�� �����ϰ� show() �޼ҵ带 ȣ���ϴ� ���Ҹ� ��
	public static void main(String[] args) {
		JFrame frame = new BounceThreadFrame();
		frame.show();
	}
}

class BounceThreadFrame extends JFrame {
	public BounceThreadFrame() {
		JPanel canvas = new JPanel(); // �籸�븦 �ֱ� ���� JPanel
		setSize(800, 500);
		setTitle("�籸����");
		addWindowListener(new WindowAdapter() { // â�� ������ ���α׷� ����
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Container contentPane = getContentPane();
		canvas.setBackground(Color.green);
		contentPane.add(canvas);
		setVisible(true);
		JPanel p = new JPanel(); // ��ư�� �ֱ� ���� JPanel
		addButton(p, "����",

				new ActionListener() { // ���� ��ư�� ������ �� ����
			public void actionPerformed(ActionEvent evt) {
				Ball b1 = new Ball(canvas, "Yellow");
				ball[3] = b1;
				Ball b2 = new Ball(canvas, "White");
				ball[2] = b2;
				Ball b3 = new Ball(canvas, "Red1");
				ball[1] = b3;
				Ball b4 = new Ball(canvas, "Red2");
				ball[0] = b4;
				// 4���� �� �ν��Ͻ� ����
				control c = new control(ball, canvas);
				// control �ν��Ͻ� ����
				addMouseMotionListener(c);
				addMouseListener(c);
				c.Initial(); // �� �ʱ� ��ġ�� ����
				for (int i = 0; i < 4; i++) {
					ball[i].start(); // �� ���� �����带 ������
				}
				try {
					s = new ServerSocket(3333); // ������ ����
					incoming = s.accept(); // ���ӵ� ������ ���
					is = new BufferedReader(new InputStreamReader(incoming
							.getInputStream()));
					os = new PrintWriter(incoming.getOutputStream());
				} catch (IOException e) {
				}
				c.start(); // ��Ʈ�� �����带 ������
			}
		});
		contentPane.add(p, "South");

		JPanel q = new JPanel(); // ���ھ� ����� ���� Jpanel
		q.add(new JLabel("Player1 : "));
		q.add(score_label[0]);
		q.add(label);
		q.add(score_label[1]);
		score_label[0].setForeground(Color.red);
		contentPane.add(q, "North");
	}

	public void addButton(Container c, String title, ActionListener a) {
		// ��ư�� ���� �� ���Ǵ� �޼ҵ�
		JButton b = new JButton(title);
		c.add(b);
		b.addActionListener(a);
	}

	Ball[] ball = new Ball[4]; // 4���� �� �ν��Ͻ��� ������ �迭
	private boolean turnCheck = false; // 
	private boolean scoreCheck[] = { false, false, false };
	private boolean Check[] = { true, true, true, true, true, true };
	Ball[] checkBall = new Ball[7];
	private int score[] = { 100, 100 };
	private int turn = 0;
	private JLabel score_label[] = { new JLabel("100"), new JLabel("100") };
	private JLabel label = new JLabel("        Player2 : ");
	private static final int XSIZE = 40;
	private static final int YSIZE = 40;

	// ��Ʈ��ũ�� ���� �ʿ��� ���� ����
	ServerSocket s;
	Socket incoming;
	BufferedReader is;
	PrintWriter os;

	class control extends Thread implements MouseListener, MouseMotionListener {
		private JPanel box;
		int rx, ry;
		double dx, dy;
		private Point point = null;
		private Point point1 = null;

		public control(Ball[] b, JPanel b1) {
			ball = b;
			box = b1;
		}

		public void Initial() { //�� ��ġ �� ���� �ʱ�ȭ
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

		public void mouseDragged(MouseEvent e) {
			if (!turnCheck) { 
				// ���콺�� �巡�׵� ������ �� ��ġ�� �ٲ�
				// turnCheck�� true �� ���� ĥ �� ����(���� �����̴� �� �϶�)
				remove();
				point = e.getPoint();
				draw();
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			if (!turnCheck) {
				// ���콺�� ���� �� ��ü�� ����� �Ŀ��� ��Ÿ���� �ν��Ͻ� ������ �ٲ��� 
				// turnCheck�� true �� ���� ĥ �� ����(���� �����̴� �� �϶�)
				remove();
				point1 = e.getPoint();
				dx = (ball[3].x + 20) - point1.x;
				dy = (ball[3].y + 20) - point1.y;
				// ���⺤�͸� ����
				ball[3].dx = unitdx(dx, dy);
				ball[3].dy = unitdy(dx, dy);
				// ���⺤�͸� ����ȭ�Ͽ� �־���
				ball[3].power = setPower(dx, dy);
				// ���̸� ����Ͽ� �Ŀ��� �־���
				turnCheck = true;
				// ���� ġ�� ���� �ٷ� �� ĥ �� ������ turnCheck�� true�� ��
			}
		}

		public void draw() {
			// ���� �׷��ִ� �޼ҵ�
			Graphics g = box.getGraphics();
			int x = (int) ball[3].x + 20;
			int y = (int) ball[3].y + 20;
			g.setColor(Color.BLACK);
			g.setXORMode(box.getBackground());
			if (point != null) {
				g.drawLine(x, y, point.x, point.y);
				// ���콺���� ������ ���� ���������� �����
				g.setColor(Color.BLUE);
				g.drawLine(x, y, x + 20 * (x - point.x), y + 20 * (y - point.y));
				// ���콺���� ������ ���� ���弱�� �Ķ������� �׷���
			}
		}

		public void remove() {
			// ���� ������ ������ ������ �׷��� ���� �����ִ� �޼ҵ�
			Graphics g = box.getGraphics();
			if (point != null) {
				int x = (int) ball[3].x + 20;
				int y = (int) ball[3].y + 20;
				g.setColor(Color.green);
				g.drawLine(x, y, point.x, point.y);
				g.setColor(Color.green);
				g.drawLine(x, y, x + 20 * (x - point.x), y + 20 * (y - point.y));
			}
		}

		public void run() {
			// ���� �浹�� ����ؼ� Ȯ���ϰ� ���� ���α׷����� ������ ����ؼ� �����ִ� �޼ҵ�
			try {
				while (!Thread.currentThread().isInterrupted()) {										
					for (int i = 0; i < 3; i++) {
						for (int j = 1; j < 4; j++)
							if (isCollided(ball[i], ball[j]) == true)
								// �浹�� �����Ǹ� �� ������ ���� �־��� 
								newdir(ball[i], ball[j]);
					}
					if (turnCheck && ball[0].power == 0 && ball[1].power == 0
							&& ball[2].power == 0 && ball[3].power == 0) {
						//turnCheck�� true�̰� ��� ���� ������ ��
						Ball tempBall;
						score_label[turn].setForeground(Color.red);
						//���� �ٲ� ���� �ð������� ����
						if (scoreCheck[0] && scoreCheck[1] && !scoreCheck[2]) {
							// ���� �� �ΰ��� ���߰� ��� ������ ������ �ʾ��� ���
							// ������ ������ ���� �Ѿ�� �ʵ�����
							score[turn] = score[turn] - 10;
							tempBall = ball[3];
							ball[3] = ball[2];
							ball[2] = tempBall;
							turn = Math.abs(turn - 1);
						} else if (scoreCheck[2]
								|| (!scoreCheck[0] && !scoreCheck[1])) {
							// ���� ������ ���߰ų� �������� �ϳ��� �������� ���
							// ������ �ö�
							score[turn] = score[turn] + 10;
						}
						// �Ʒ� �������� ���ھ�忡 ������ �ٲ��ְ� ���� �ٲ��ִ� ������ ��
						score_label[0].setText("" + score[0]);
						score_label[1].setText("" + score[1]);
						score_label[turn].setForeground(Color.black);
						tempBall = ball[3];
						ball[3] = ball[2];
						ball[2] = tempBall;
						turn = Math.abs(turn - 1);
						score_label[turn].setForeground(Color.red);
						for (int i = 0; i < 3; i++)
							// scoreCheck�� ��� �ʱ�ȭ ����
							scoreCheck[i] = false;
						turnCheck = false;
						// turnCheck�� false�� �Ͽ� ���� ĥ �� �ְ���
					}
					sleep(5);

					//���� �̸��� ���� ��ġ, ����, �Ͽ� ���� ������ �۽��� 
					os.println(ball[0].ball_name + " " + ball[0].x + " "
							+ ball[0].y + " " + score[0] + " " + score[1] + " "
							+ turn);
					os.flush();
					System.out.println();
					os.println(ball[1].ball_name + " " + ball[1].x + " "
							+ ball[1].y);
					os.flush();
					os.println(ball[2].ball_name + " " + ball[2].x + " "
							+ ball[2].y);
					os.flush();
					os.println(ball[3].ball_name + " " + ball[3].x + " "
							+ ball[3].y);
					os.flush();
				}
			} catch (Exception e) {
				box.setVisible(false);
			}
			box.setVisible(true);
		}
	}

	public boolean isCollided(Ball b1, Ball b2) {
		// �� ���� �浹�� �����ϴ� �޼ҵ�
		double distanceX = b1.x - b2.x;
		double distanceY = b1.y - b2.y;
		double distance = Math.sqrt((distanceX * distanceX)
				+ (distanceY * distanceY));

		if (distance < 40) {
			// ���� �߽� �Ÿ��� ���� ������ 40���� �۾����� �浹 �� ������ ����
			double distanceXPrime = (b1.x + b1.dx) - (b2.x + b2.dx);
			double distanceYPrime = (b1.y + b1.dy) - (b2.y + b2.dy);
			double distancePrime = Math.sqrt(distanceXPrime * distanceXPrime
					+ distanceYPrime * distanceYPrime);

			if (distance > distancePrime) {
				//�������� ���� �������� �Ÿ����� ũ�ų� ������ ó�� ������ ��Ȳ�� ����
				/*�Ʒ� ���� if ~ else�� ���� ����� �ѹ� �浹�ߴµ� sleep�� ���� ���Ƽ� 
				������ �浹�Ǵ� ������ �������� if���̴�. �浹���� �� Check�� false�� ����� 
				���� �Ÿ��� �ٽ� 40���� Ŀ���� true�� �����ν� ������ �浹�� �����Ѵ�.
���� � ���� �浹�� �Ͼ���� Ȯ���Ͽ� ������ ���� ��� �� �� �ְ� ���ش�.
				(�ε����� �ش� scoreCheck�� true�� ���ָ鼭)*/
				if (Check[0] && (ball[3] == b1 && ball[0] == b2)
						|| (ball[0] == b1 && ball[3] == b2)) {
					scoreCheck[0] = true;
					Check[0] = false;
				} else if (Check[1] && (ball[3] == b1 && ball[1] == b2)
						|| (ball[1] == b1 && ball[3] == b2)) {
					scoreCheck[1] = true;
					Check[1] = false;
				} else if (Check[2] && (ball[3] == b1 && ball[2] == b2)
						|| (ball[2] == b1 && ball[3] == b2)) {
					scoreCheck[2] = true;
					Check[2] = false;
				} else if (Check[3] && (ball[0] == b1 && ball[1] == b2)
						|| (ball[1] == b1 && ball[0] == b2)) {
					Check[3] = false;
				} else if (Check[4] && (ball[2] == b1 && ball[0] == b2)
						|| (ball[0] == b1 && ball[2] == b2)) {
					Check[4] = false;
				} else if (Check[5] && (ball[2] == b1 && ball[1] == b2)
						|| (ball[1] == b1 && ball[2] == b2)) {
					Check[5] = false;
				}
				return true;
			}
		} else {
			if (!Check[0] && (ball[3] == b1 && ball[0] == b2)
					|| (ball[0] == b1 && ball[3] == b2))
				Check[0] = true;
			else if (!Check[1] && (ball[3] == b1 && ball[1] == b2)
					|| (ball[1] == b1 && ball[3] == b2))
				Check[1] = true;
			else if (!Check[2] && (ball[3] == b1 && ball[2] == b2)
					|| (ball[2] == b1 && ball[3] == b2))
				Check[2] = true;
			else if (!Check[3] && (ball[0] == b1 && ball[1] == b2)
					|| (ball[1] == b1 && ball[0] == b2))
				Check[3] = true;
			else if (!Check[4] && (ball[2] == b1 && ball[0] == b2)
					|| (ball[0] == b1 && ball[2] == b2))
				Check[4] = true;
			else if (!Check[5] && (ball[2] == b1 && ball[1] == b2)
					|| (ball[1] == b1 && ball[2] == b2))
				Check[5] = true;
		}

		return false;
		//�浹 ���� �ʾ��� ��� false ����
	}

	public void newdir(Ball b1, Ball b2) {
		//�浹�� �����Ͽ��� �� �� �� ��ü�� ���ο� ����� �Ŀ��� �ִ� �޼ҵ�
		double dx1 = 0, dy1 = 0, tempdx = 0, tempdy = 0;
		double angle1 = 0, angle2 = 0, angle = 0;
		double hpi = Math.PI / 2;
		if (b1.power > b2.power) {
			// �ٻ�ȭ�ν� b1 ���� �Ŀ��� �� ũ�� b2�� ���� �ִٰ� �����Ѵ�.
			dx1 = b2.x - b1.x;
			dy1 = b2.y - b1.y;
			// �� ���� �߽����� ���� ���⺤��
			tempdx = unitdx(dx1, dy1);
			tempdy = unitdy(dx1, dy1);
			// ���⺤�͸� ����ȭ ���ش�.
			angle1 = Math.atan(b1.dy / b1.dx);
			// �� ���� �߽����� ���� ���⺤���� ��
			angle2 = Math.atan(tempdy / tempdx);
			// �Ŀ��� �� ���� ���� �����ϴ� ���⺤��

			/*�Ʒ� ���� if ~ else���� �� ���� �߽����� ���� ���⺤�Ϳ� �Ŀ��� �� ����
			 ���� ���⺤�Ͱ� 1~4��и鿡 ������ �� ����ϴ� ����� �ٸ��� ������ �߻��Ѵ�.
			 �� �� 1��и鿡 ������ ������ ���� ��찡 �����ϴµ� �� 12 ��찡 �߻��Ͽ���.*/
			if (b1.dy > 0 && tempdy > 0 && b1.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b1.dy > 0 && tempdy > 0 && b1.dx < 0 && tempdx < 0)
				angle = angle1 - angle2;
			else if (b1.dy < 0 && tempdy < 0 && b1.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b1.dy < 0 && tempdy < 0 && b1.dx < 0 && tempdx < 0)
				angle = angle1 - angle2;
			else if (b1.dy < 0 && tempdy > 0 && b1.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b1.dy > 0 && tempdy < 0 && b1.dx > 0 && tempdx > 0)
				angle = -(angle1 + angle2);
			else if (b1.dy > 0 && tempdy < 0 && b1.dx < 0 && tempdx < 0)
				angle = -(angle2 - angle1);
			else if (b1.dy < 0 && tempdy > 0 && b1.dx < 0 && tempdx < 0)
				angle = -(angle1 + angle2);
			else if (b1.dx > 0 && tempdx < 0 && b1.dy > 0 && tempdy > 0)
				angle = (angle1 - angle2) - 2 * hpi;
			else if (b1.dx > 0 && tempdx < 0 && b1.dy < 0 && tempdy < 0)
				angle = (angle1 - angle2) + 2 * hpi;
			else if (b1.dx < 0 && tempdx > 0 && b1.dy > 0 && tempdy > 0)
				angle = 2 * hpi - (angle2 - angle1);
			else if (b1.dx < 0 && tempdx > 0 && b1.dy < 0 && tempdy < 0)
				angle = -(2 * hpi + (angle2 - angle1));

			/* �Ʒ� �� if���� �� ���⺤���� angle�� �����̳� ����̳Ŀ� ���� �������͸� �ٸ���
			�����ؾ� �ϱ� ������ �߻��Ѵ�.(�������ʹ� ������ ���� �ݴ��� 2���� �����µ� �� 2�� �� 
			�˸��� �������͸� �����ϱ� ����)*/
			if (angle < 0) {
				// ���� ������ ������ �־��ش�
				b1.dy = -unitdx(dx1, dy1);
				b1.dx = unitdy(dx1, dy1);
			} else if (angle > 0) {
				b1.dy = unitdx(dx1, dy1);
				b1.dx = -unitdy(dx1, dy1);
			}

			b2.dx = unitdx(dx1, dy1);
			b2.dy = unitdy(dx1, dy1);
			// �� ���� �߽����� ���� ���ͷ� ������ �־��ش�
			b2.power = b1.power
					* (((Math.PI / 2) - Math.abs(angle)) / (Math.PI / 2));
			b1.power = b1.power * (Math.abs(angle) / (Math.PI / 2));
			/* �Ŀ��� �� ���� ������ ���µ� �� ���� ���� ���� ������ ���´�. ���� ��������
			 ������ �Ŀ��� ���� �Ұ� ������ �Ŀ��� ���� ��´�.*/


		}

		/*�Ʒ� else���� �� if���� �ݴ�� b2 ���� �Ŀ��� �� ũ�� b1�� ���� �ִٰ� �����Ѵ�.
		 b1�� b2�� ��ġ�� �ٲ������ �� if���� ��� �Ȱ��� ������ �ݺ��ϱ� ������ �ּ��� �����Ѵ�.*/
		else {
			dx1 = b1.x - b2.x;
			dy1 = b1.y - b2.y;
			tempdx = unitdx(dx1, dy1);
			tempdy = unitdy(dx1, dy1);
			angle1 = Math.atan(b2.dy / b2.dx);
			angle2 = Math.atan(tempdy / tempdx);
			System.out.println(Math.atan(b2.dx));
			System.out.println(Math.atan(b2.dy));
			System.out.println(Math.atan(tempdx));
			System.out.println(Math.atan(tempdy));
			if (b2.dy > 0 && tempdy > 0 && b2.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b2.dy > 0 && tempdy > 0 && b2.dx < 0 && tempdx < 0)
				angle = angle1 - angle2;
			else if (b2.dy < 0 && tempdy < 0 && b2.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b2.dy < 0 && tempdy < 0 && b2.dx < 0 && tempdx < 0)
				angle = angle1 - angle2;
			else if (b2.dy < 0 && tempdy > 0 && b2.dx > 0 && tempdx > 0)
				angle = angle1 - angle2;
			else if (b2.dy > 0 && tempdy < 0 && b2.dx > 0 && tempdx > 0)
				angle = -(angle1 + angle2);
			else if (b2.dy > 0 && tempdy < 0 && b2.dx < 0 && tempdx < 0)
				angle = -(angle2 - angle1);
			else if (b2.dy < 0 && tempdy > 0 && b2.dx < 0 && tempdx < 0)
				angle = -(angle1 + angle2);
			else if (b2.dx > 0 && tempdx < 0 && b2.dy > 0 && tempdy > 0)
				angle = (angle1 - angle2) - 2 * hpi;
			else if (b2.dx > 0 && tempdx < 0 && b2.dy < 0 && tempdy < 0)
				angle = (angle1 - angle2) + 2 * hpi;
			else if (b2.dx < 0 && tempdx > 0 && b2.dy > 0 && tempdy > 0)
				angle = 2 * hpi - (angle2 - angle1);
			else if (b2.dx < 0 && tempdx > 0 && b2.dy < 0 && tempdy < 0)
				angle = -(2 * hpi + (angle2 - angle1));
			System.out.println(angle);
			if (angle < 0) {
				b2.dy = -unitdx(dx1, dy1);
				b2.dx = unitdy(dx1, dy1);
			} else if (angle > 0) {
				b2.dy = unitdx(dx1, dy1);
				b2.dx = -unitdy(dx1, dy1);
			}
			b1.dx = unitdx(dx1, dy1);
			b1.dy = unitdy(dx1, dy1);
			b1.power = b2.power
					* (((Math.PI / 2) - Math.abs(angle)) / (Math.PI / 2));
			b2.power = b2.power * (Math.abs(angle) / (Math.PI / 2));
		}
	}

	public double unitdx(double dx, double dy) {
		//���⺤�͸� ����ȭ�ϴ� �޼ҵ�
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		dx = dx / length;
		return dx;
	}

	public double unitdy(double dx, double dy) {
		//���⺤�͸� ����ȭ�ϴ� �޼ҵ�
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		dy = dy / length;
		return dy;
	}

	public double setPower(double dx, double dy) {
		//���̸� ����� �Ŀ��� ���ϴ� �޼ҵ�
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		return length / 20;
	}

	class Ball extends Thread {
		// ������ ���� ��Ÿ���� �� Ŭ����
		String ball_name;
		private JPanel box;
		private double x;
		private double y;
		private double dx = 0;
		private double dy = 0;
		private double power = 0;
		Color color;
		//���� �̸�, ��ġ, ����, �Ŀ�, ���� ��Ÿ���� �ν��Ͻ� ������ ����

		int count;

		public Ball(JPanel b, String string) {
			box = b;
			ball_name = string;
		}

		public void draw() {
			// ���� �׸��� �޼ҵ�
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
		}

		public void move() {
			// ������ ������ �������� �����ִ� �޼ҵ�
			if (!box.isVisible())
				return;
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.setXORMode(box.getBackground());
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
			// �׷��� �ִ� ���� �����
			if (power > 0.03)
				power *= 0.995;
			// sleep�� �ѹ� �� ������ �Ŀ��� ���� ���ҽ�Ų��.
			else {
				//�Ŀ��� 0.03 �����̸� ���� �����.
				power = 0;
				dx = 0;
				dy = 0;
			}
			x += power * dx;
			y += power * dy;
			//���� ���� ��ġ�� �Ŀ��� ���⺤�Ͱ� �������� ���ȴ�.
			Dimension d = box.getSize();
			//�籸���� ����� ��´�.

			//�Ʒ� if���� ���� �籸�뿡 �ε����� �� ���ݻ� �����ִ� ������ �Ѵ�
			if (x < 0) {
				x = 0;
				dx = -dx;
			}
			if (x + XSIZE >= d.width) {
				x = d.width - XSIZE;
				dx = -dx;
			}
			if (y < 0) {
				y = 0;
				dy = -dy;
			}
			if (y + YSIZE >= d.height) {
				y = d.height - YSIZE;
				dy = -dy;
			}
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
			//���ο� ��ġ�� ���� �׷��ش�
			g.dispose();
		}

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					if (turnCheck)
						/*turnCheck�� true�̸� ���� ��� �����ִ� �����̹Ƿ�
						move()�� draw()�� ������ �ʿ� ����*/
						move();
					draw();
					sleep(5);
				}
			} catch (InterruptedException e) {
				box.setVisible(false);
			}
			box.setVisible(true);
		}
	}
}
