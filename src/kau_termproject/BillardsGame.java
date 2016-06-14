package kau_termproject;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import javax.swing.*;

public class BillardsGame {
	//메인 클래스에서는 frame을 생성하고 show() 메소드를 호출하는 역할만 함
	public static void main(String[] args) {
		JFrame frame = new BounceThreadFrame();
		frame.show();
	}
}

class BounceThreadFrame extends JFrame {
	public BounceThreadFrame() {
		JPanel canvas = new JPanel(); // 당구대를 넣기 위한 JPanel
		setSize(800, 500);
		setTitle("당구게임");
		addWindowListener(new WindowAdapter() { // 창을 닫으면 프로그램 종료
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Container contentPane = getContentPane();
		canvas.setBackground(Color.green);
		contentPane.add(canvas);
		setVisible(true);
		JPanel p = new JPanel(); // 버튼을 넣기 위한 JPanel
		addButton(p, "시작",

				new ActionListener() { // 시작 버튼이 눌렸을 때 수행
			public void actionPerformed(ActionEvent evt) {
				Ball b1 = new Ball(canvas, "Yellow");
				ball[3] = b1;
				Ball b2 = new Ball(canvas, "White");
				ball[2] = b2;
				Ball b3 = new Ball(canvas, "Red1");
				ball[1] = b3;
				Ball b4 = new Ball(canvas, "Red2");
				ball[0] = b4;
				// 4개의 볼 인스턴스 생성
				control c = new control(ball, canvas);
				// control 인스턴스 생성
				addMouseMotionListener(c);
				addMouseListener(c);
				c.Initial(); // 볼 초기 위치를 정함
				for (int i = 0; i < 4; i++) {
					ball[i].start(); // 각 볼의 스레드를 시작함
				}
				try {
					s = new ServerSocket(3333); // 서버를 생성
					incoming = s.accept(); // 접속될 때까지 대기
					is = new BufferedReader(new InputStreamReader(incoming
							.getInputStream()));
					os = new PrintWriter(incoming.getOutputStream());
				} catch (IOException e) {
				}
				c.start(); // 컨트롤 스레드를 시작함
			}
		});
		contentPane.add(p, "South");

		JPanel q = new JPanel(); // 스코어 보드로 사용될 Jpanel
		q.add(new JLabel("Player1 : "));
		q.add(score_label[0]);
		q.add(label);
		q.add(score_label[1]);
		score_label[0].setForeground(Color.red);
		contentPane.add(q, "North");
	}

	public void addButton(Container c, String title, ActionListener a) {
		// 버튼을 만들 때 사용되는 메소드
		JButton b = new JButton(title);
		c.add(b);
		b.addActionListener(a);
	}

	Ball[] ball = new Ball[4]; // 4개의 공 인스턴스를 저장할 배열
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

	// 네트워크를 위해 필요한 변수 선언
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

		public void Initial() { //공 위치 및 색상 초기화
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
				// 마우스가 드래그될 때마다 선 위치를 바꿈
				// turnCheck가 true 일 때는 칠 수 없음(공이 움직이는 중 일때)
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
				// 마우스를 떼면 볼 객체의 방향과 파워를 나타내는 인스턴스 변수를 바꿔줌 
				// turnCheck가 true 일 때는 칠 수 없음(공이 움직이는 중 일때)
				remove();
				point1 = e.getPoint();
				dx = (ball[3].x + 20) - point1.x;
				dy = (ball[3].y + 20) - point1.y;
				// 방향벡터를 구함
				ball[3].dx = unitdx(dx, dy);
				ball[3].dy = unitdy(dx, dy);
				// 방향벡터를 정규화하여 넣어줌
				ball[3].power = setPower(dx, dy);
				// 길이를 계산하여 파워를 넣어줌
				turnCheck = true;
				// 공을 치고 나서 바로 또 칠 수 없도록 turnCheck를 true로 함
			}
		}

		public void draw() {
			// 선을 그려주는 메소드
			Graphics g = box.getGraphics();
			int x = (int) ball[3].x + 20;
			int y = (int) ball[3].y + 20;
			g.setColor(Color.BLACK);
			g.setXORMode(box.getBackground());
			if (point != null) {
				g.drawLine(x, y, point.x, point.y);
				// 마우스부터 공까지 선이 검정색으로 연결됨
				g.setColor(Color.BLUE);
				g.drawLine(x, y, x + 20 * (x - point.x), y + 20 * (y - point.y));
				// 마우스부터 공까지 선의 연장선이 파란색으로 그려짐
			}
		}

		public void remove() {
			// 선이 움직일 때마다 이전에 그려진 선을 지워주는 메소드
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
			// 공의 충돌을 계속해서 확인하고 관전 프로그램으로 정보를 계속해서 보내주는 메소드
			try {
				while (!Thread.currentThread().isInterrupted()) {										
					for (int i = 0; i < 3; i++) {
						for (int j = 1; j < 4; j++)
							if (isCollided(ball[i], ball[j]) == true)
								// 충돌이 감지되면 공 궤적을 새로 넣어줌 
								newdir(ball[i], ball[j]);
					}
					if (turnCheck && ball[0].power == 0 && ball[1].power == 0
							&& ball[2].power == 0 && ball[3].power == 0) {
						//turnCheck이 true이고 모든 공이 멈췄을 때
						Ball tempBall;
						score_label[turn].setForeground(Color.red);
						//턴이 바뀐 것을 시각적으로 보임
						if (scoreCheck[0] && scoreCheck[1] && !scoreCheck[2]) {
							// 빨간 공 두개를 맞추고 상대 수구를 맞추지 않았을 경우
							// 점수를 내리고 턴이 넘어가지 않도록함
							score[turn] = score[turn] - 10;
							tempBall = ball[3];
							ball[3] = ball[2];
							ball[2] = tempBall;
							turn = Math.abs(turn - 1);
						} else if (scoreCheck[2]
								|| (!scoreCheck[0] && !scoreCheck[1])) {
							// 상대방 수구를 맞추거나 빨간공을 하나도 못맞췄을 경우
							// 점수가 올라감
							score[turn] = score[turn] + 10;
						}
						// 아래 과정들은 스코어보드에 점수를 바꿔주고 턴을 바꿔주는 역할을 함
						score_label[0].setText("" + score[0]);
						score_label[1].setText("" + score[1]);
						score_label[turn].setForeground(Color.black);
						tempBall = ball[3];
						ball[3] = ball[2];
						ball[2] = tempBall;
						turn = Math.abs(turn - 1);
						score_label[turn].setForeground(Color.red);
						for (int i = 0; i < 3; i++)
							// scoreCheck를 모두 초기화 해줌
							scoreCheck[i] = false;
						turnCheck = false;
						// turnCheck를 false로 하여 공을 칠 수 있게함
					}
					sleep(5);

					//공의 이름과 공의 위치, 점수, 턴에 관한 정보를 송신함 
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
		// 두 공의 충돌을 감지하는 메소드
		double distanceX = b1.x - b2.x;
		double distanceY = b1.y - b2.y;
		double distance = Math.sqrt((distanceX * distanceX)
				+ (distanceY * distanceY));

		if (distance < 40) {
			// 공의 중심 거리가 공의 지름인 40보다 작아지면 충돌 된 것으로 간주
			double distanceXPrime = (b1.x + b1.dx) - (b2.x + b2.dx);
			double distanceYPrime = (b1.y + b1.dy) - (b2.y + b2.dy);
			double distancePrime = Math.sqrt(distanceXPrime * distanceXPrime
					+ distanceYPrime * distanceYPrime);

			if (distance > distancePrime) {
				//반지름의 합이 중점간의 거리보다 크거나 같더라도 처음 생성된 상황은 제외
				/*아래 여러 if ~ else문 들은 사실은 한번 충돌했는데 sleep이 빨리 돌아서 
				여러번 충돌되는 현상을 막기위한 if문이다. 충돌됬을 때 Check를 false로 만들고 
				공의 거리가 다시 40보다 커지면 true가 됨으로써 여러번 충돌을 방지한다.
또한 어떤 공과 충돌이 일어났는지 확인하여 점수와 턴을 계산 할 수 있게 해준다.
				(부딪히면 해당 scoreCheck를 true로 해주면서)*/
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
		//충돌 되지 않았을 경우 false 리턴
	}

	public void newdir(Ball b1, Ball b2) {
		//충돌을 감지하였을 때 각 볼 객체에 새로운 방향과 파워를 주는 메소드
		double dx1 = 0, dy1 = 0, tempdx = 0, tempdy = 0;
		double angle1 = 0, angle2 = 0, angle = 0;
		double hpi = Math.PI / 2;
		if (b1.power > b2.power) {
			// 근사화로써 b1 공의 파워가 더 크면 b2는 멈춰 있다고 가정한다.
			dx1 = b2.x - b1.x;
			dy1 = b2.y - b1.y;
			// 두 공의 중심점을 이은 방향벡터
			tempdx = unitdx(dx1, dy1);
			tempdy = unitdy(dx1, dy1);
			// 방향벡터를 정규화 해준다.
			angle1 = Math.atan(b1.dy / b1.dx);
			// 두 공의 중심점을 이은 방향벡터의 각
			angle2 = Math.atan(tempdy / tempdx);
			// 파워가 더 강한 공이 진행하던 방향벡터

			/*아래 여러 if ~ else문은 두 공의 중심점을 이은 방향벡터와 파워가 더 강한
			 공의 방향벡터가 1~4사분면에 존재할 때 계산하는 방식이 다르기 때문에 발생한다.
			 둘 다 1사분면에 존재할 경우부터 여러 경우가 존재하는데 총 12 경우가 발생하였다.*/
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

			/* 아래 두 if문은 두 방향벡터의 angle이 음수이냐 양수이냐에 따라 법선벡터를 다르게
			설정해야 하기 때문에 발생한다.(법선벡터는 방향이 완전 반대인 2개가 나오는데 그 2개 중 
			알맞은 법선벡터를 선택하기 위해)*/
			if (angle < 0) {
				// 법선 벡터의 방향을 넣어준다
				b1.dy = -unitdx(dx1, dy1);
				b1.dx = unitdy(dx1, dy1);
			} else if (angle > 0) {
				b1.dy = unitdx(dx1, dy1);
				b1.dx = -unitdy(dx1, dy1);
			}

			b2.dx = unitdx(dx1, dy1);
			b2.dy = unitdy(dx1, dy1);
			// 두 공의 중심점을 이은 벡터로 방향을 넣어준다
			b2.power = b1.power
					* (((Math.PI / 2) - Math.abs(angle)) / (Math.PI / 2));
			b1.power = b1.power * (Math.abs(angle) / (Math.PI / 2));
			/* 파워는 두 공이 나눠서 갖는데 두 공의 각에 따라 나누어 갖는다. 각이 작을수록
			 수구가 파워를 많이 잃고 적구가 파워를 많이 얻는다.*/


		}

		/*아래 else문은 위 if문과 반대로 b2 공의 파워가 더 크면 b1는 멈춰 있다고 가정한다.
		 b1과 b2의 위치만 바뀌었을뿐 위 if문과 모두 똑같은 과정을 반복하기 때문에 주석은 생략한다.*/
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
		//방향벡터를 정규화하는 메소드
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		dx = dx / length;
		return dx;
	}

	public double unitdy(double dx, double dy) {
		//방향벡터를 정규화하는 메소드
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		dy = dy / length;
		return dy;
	}

	public double setPower(double dx, double dy) {
		//길이를 계산해 파워를 정하는 메소드
		double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		return length / 20;
	}

	class Ball extends Thread {
		// 각각의 공을 나타내는 볼 클래스
		String ball_name;
		private JPanel box;
		private double x;
		private double y;
		private double dx = 0;
		private double dy = 0;
		private double power = 0;
		Color color;
		//공의 이름, 위치, 방향, 파워, 색을 나타내는 인스턴스 변수들 선언

		int count;

		public Ball(JPanel b, String string) {
			box = b;
			ball_name = string;
		}

		public void draw() {
			// 공을 그리는 메소드
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
		}

		public void move() {
			// 각각의 공들의 움직임을 정해주는 메소드
			if (!box.isVisible())
				return;
			Graphics g = box.getGraphics();
			g.setColor(color);
			g.setXORMode(box.getBackground());
			g.fillOval((int) x, (int) y, XSIZE, YSIZE);
			// 그려져 있던 공을 지운다
			if (power > 0.03)
				power *= 0.995;
			// sleep이 한번 돌 때마다 파워를 점점 감소시킨다.
			else {
				//파워가 0.03 이하이면 공을 멈춘다.
				power = 0;
				dx = 0;
				dy = 0;
			}
			x += power * dx;
			y += power * dy;
			//공의 다음 위치는 파워와 방향벡터가 곱해져서 계산된다.
			Dimension d = box.getSize();
			//당구대의 사이즈를 얻는다.

			//아래 if문은 공이 당구대에 부딪혔을 때 정반사 시켜주는 역할을 한다
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
			//새로운 위치에 공을 그려준다
			g.dispose();
		}

		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					if (turnCheck)
						/*turnCheck이 true이면 공이 모두 멈춰있는 상태이므로
						move()와 draw()를 수행할 필요 없음*/
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
