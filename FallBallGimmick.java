import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.net.UnknownHostException;

class FallBallGimmick extends JFrame {
	static FallBallGimmick frame;
	static CountdownThread countdownthread;
	static PlayerlogThread playerlogthread;
	static PingThread pingthread;
	static Font fontfamily_sec;
	static Font fontfamily_msec;
	static Font fontfamily_serverIP;
	static String serverIP;

	public static void main(String[] args) throws Exception {
		int pt_x = 10;
		int pt_y = 10;
		int size_x = 100;
		int size_y = 100;
		BufferedImage image = null;
		try{
			File file = new File("window_pt.ini");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			String[] value;
			while((str = br.readLine()) != null) {
				value = str.split(" ", 2);
				pt_x = Integer.parseInt(value[0]);
				pt_y = Integer.parseInt(value[1]);
			}
			br.close();

			file = new File("path.ini");
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null) {
				path_str = str;
			}
			br.close();

			image = ImageIO.read(new File("background.png"));
			size_x = image.getWidth();
			size_y = image.getHeight();

			Font fontfamily = Font.createFont(Font.TRUETYPE_FONT,new File("TitanOne-Regular.ttf"));
			fontfamily_sec = fontfamily.deriveFont(50f);
			fontfamily_msec = fontfamily.deriveFont(40f);
			fontfamily_serverIP = fontfamily.deriveFont(18f);
			serverIP = "127.0.0.1";
		} catch(FileNotFoundException e) { System.exit(1);
		} catch(FontFormatException e){ System.exit(1);
		} catch(IOException e) { System.exit(1); }
		
		frame = new FallBallGimmick(size_x, size_y, image);
		frame.setUndecorated(true);
		frame.setBounds(pt_x, pt_y, size_x, size_y);
		frame.setTitle("FallBallGimmick");
		frame.setBackground(new Color(0x0, true));
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
	}

	static JPanel p;
	static String path_str;
	static JLabel countdown_sec;
	static JLabel countdown_dot;
	static JLabel countdown_msec;
	static JLabel fliper1;
	static JLabel fliper2;
	static JLabel serverIP_label;

	static boolean countdown_flg;
	static Point mouseDownCompCoords;
	private JPopupMenu popup;

	static Date startDate;
	static SimpleDateFormat sdf_utc;

	FallBallGimmick(int size_x, int size_y, BufferedImage image) {
		p = new JPanel(null) {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, this);
			}
		};
		p.setSize(size_x, size_y);

		countdown_sec = new JLabel("3");
		countdown_sec.setSize(image.getWidth()-138, image.getHeight()-38);
		countdown_sec.setHorizontalAlignment(JLabel.RIGHT);
		countdown_sec.setVerticalAlignment(JLabel.BOTTOM);
		countdown_sec.setForeground(Color.WHITE);
		countdown_sec.setFont(fontfamily_sec);
		p.add(countdown_sec);

		countdown_dot = new JLabel(".");
		countdown_dot.setSize(image.getWidth()-123, image.getHeight()-40);
		countdown_dot.setHorizontalAlignment(JLabel.RIGHT);
		countdown_dot.setVerticalAlignment(JLabel.BOTTOM);
		countdown_dot.setForeground(Color.WHITE);
		countdown_dot.setFont(fontfamily_msec);
		p.add(countdown_dot);

		countdown_msec = new JLabel("0");
		countdown_msec.setSize(image.getWidth()-95, image.getHeight()-40);
		countdown_msec.setHorizontalAlignment(JLabel.RIGHT);
		countdown_msec.setVerticalAlignment(JLabel.BOTTOM);
		countdown_msec.setForeground(Color.WHITE);
		countdown_msec.setFont(fontfamily_msec);
		p.add(countdown_msec);

		fliper1 = new JLabel("青→？");
		fliper1.setFont(new Font("Meiryo UI", Font.BOLD, 20));
		fliper1.setSize(image.getWidth()-16, image.getHeight()-62);
		fliper1.setForeground(Color.WHITE);
		fliper1.setHorizontalAlignment(JLabel.RIGHT);
		fliper1.setVerticalAlignment(JLabel.BOTTOM);
		p.add(fliper1);
		fliper2 = new JLabel("黄→？");
		fliper2.setFont(new Font("Meiryo UI", Font.BOLD, 20));
		fliper2.setSize(image.getWidth()-16, image.getHeight()-40);
		fliper2.setForeground(Color.WHITE);
		fliper2.setHorizontalAlignment(JLabel.RIGHT);
		fliper2.setVerticalAlignment(JLabel.BOTTOM);
		p.add(fliper2);
		
		serverIP_label = new JLabel("        " + serverIP + "  0ms");
		serverIP_label.setFont(fontfamily_serverIP);
		serverIP_label.setSize(image.getWidth(), image.getHeight()-21);
		serverIP_label.setForeground(Color.WHITE);
		serverIP_label.setHorizontalAlignment(JLabel.LEFT);
		serverIP_label.setVerticalAlignment(JLabel.BOTTOM);
		p.add(serverIP_label);

		popup = new JPopupMenu();
		JMenuItem popup_start = new JMenuItem("Start");
		popup_start.setFont(new Font("Meiryo UI", Font.BOLD, 16));
		popup.add(popup_start);
		popup_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (countdown_flg == false){
					startDate = getCurDateUTC();
					countdown_flg = true;
				}
			}
		});
		JMenuItem popup_reset = new JMenuItem("Reset");
		popup_reset.setFont(new Font("Meiryo UI", Font.BOLD, 16));
		popup.add(popup_reset);
		popup_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (countdown_flg == true) countdown_flg = false;
			}
		});
		JMenuItem popup_shutdown = new JMenuItem("Close");
		popup_shutdown.setFont(new Font("Meiryo UI", Font.BOLD, 16));
		popup.add(popup_shutdown);
		popup_shutdown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		p.addMouseListener(new MouseListener(){
			public void mouseReleased(MouseEvent e) {
				boolean press = SwingUtilities.isRightMouseButton(e);
				if (press == true) showPopup(e);
				mouseDownCompCoords = null;
			}
			public void mousePressed(MouseEvent e) {
				boolean press = SwingUtilities.isLeftMouseButton(e);
				if (press == true) mouseDownCompCoords = e.getPoint();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		p.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e) {
				boolean press = SwingUtilities.isLeftMouseButton(e);
				if (press == true){
					Point currCoords = e.getLocationOnScreen();
					frame.setLocation(currCoords.x-mouseDownCompCoords.x, currCoords.y-mouseDownCompCoords.y);
				}
			}
		});

		Container contentPane = getContentPane();
		contentPane.add(p, BorderLayout.CENTER);

		sdf_utc = new SimpleDateFormat("HH:mm:ss.SSS");
		sdf_utc.setTimeZone(TimeZone.getTimeZone("UTC"));

		countdownthread = new CountdownThread();
		countdownthread.start();
		playerlogthread = new PlayerlogThread();
		playerlogthread.start();
		pingthread = new PingThread();
		pingthread.start();
	}

	private void showPopup(MouseEvent e){
		if (e.isPopupTrigger()) popup.show(e.getComponent(), e.getX(), e.getY());
	}
	private void save() {
		try{
		  File file = new File("window_pt.ini");
		  PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		  Point pt = frame.getLocationOnScreen();
		  pw.print(pt.x + " " + pt.y);
		  pw.close();
		}catch(IOException e) {}
		System.exit(0);
	}

	static Date getCurDateUTC(){
		Date curDate = new Date();
		String curDate_str = sdf_utc.format(curDate);
		try {
			curDate = sdf_utc.parse(curDate_str);
		} catch (Exception e){}
		return curDate;
	}
}

class PingThread extends Thread{
	public void run(){
		while (true){
			ReachabilityTest();
			try{
		 		Thread.sleep(10*1000);
			} catch (InterruptedException e) {}
		}
	}

	static void ReachabilityTest() {
		long sum = 0;
		long num = 0;

		try {
			String tmp_serverIP = FallBallGimmick.frame.serverIP;
			InetAddress address = InetAddress.getByName(tmp_serverIP);

			for (int i = 0; i < 4; i++) {
				long start = System.currentTimeMillis();
				boolean isReachable = address.isReachable(2000);
				long time = System.currentTimeMillis() - start;

				if (isReachable) {
					sum += time;
					num++;
					try { Thread.sleep(1000);
					} catch (InterruptedException e) {}
				} else {
					try { Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}

			if (num == 0){
				FallBallGimmick.frame.serverIP_label.setText("        Unreachable");
			} else {
				sum = sum / num;
				FallBallGimmick.frame.serverIP_label.setText("        " + tmp_serverIP + "  " + String.valueOf(sum) + "ms");
			}
        } catch (Exception e) {}
	}
}

class CountdownThread extends Thread{
	public void run(){
		while (true){
			long countdown = 3000;
			displayCountdown(countdown);
			while (FallBallGimmick.frame.countdown_flg == true){
				long tmp_countdown = calCountdown(countdown);
				displayCountdown(tmp_countdown);

				if (tmp_countdown <= 0) {
					while (tmp_countdown <= 0) tmp_countdown = 12333 + tmp_countdown;
					countdown = tmp_countdown;
					FallBallGimmick.frame.startDate = FallBallGimmick.frame.getCurDateUTC();
				}

				if (FallBallGimmick.frame.countdown_flg == false) break;
				try{
			 		Thread.sleep(50);
				} catch (InterruptedException e) {}
			}
			try{
			 	Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}
	
	private long calCountdown(long countdown){
		long startDateMill = FallBallGimmick.frame.startDate.getTime();
		long curDateMill = FallBallGimmick.frame.getCurDateUTC().getTime();
		long diff = curDateMill - startDateMill;
		if (startDateMill > curDateMill) diff = diff + 24*60*60*1000;
		return countdown - diff;
	}

	private void displayCountdown(long count){
		String count_str = String.valueOf(count);
		
		if (count < 100){
			FallBallGimmick.frame.countdown_sec.setText("0");
			FallBallGimmick.frame.countdown_msec.setText("0");
		} else if (count < 1000){
			FallBallGimmick.frame.countdown_sec.setText("0");
			FallBallGimmick.frame.countdown_msec.setText(count_str.substring(count_str.length()-3, count_str.length()-2));
		} else {
			FallBallGimmick.frame.countdown_sec.setText(count_str.substring(0, count_str.length()-3));
			FallBallGimmick.frame.countdown_msec.setText(count_str.substring(count_str.length()-3, count_str.length()-2));
		}
	}
}

class PlayerlogThread extends Thread{
	private Path log_path;
	private int line_cnt;
	private long file_size;
	private int match_status;
	private SimpleDateFormat sdf_utc;

	public void run() {
		log_path = Paths.get(FallBallGimmick.frame.path_str);
		line_cnt = 0;
		file_size = 0;
		match_status = 0;
		sdf_utc = new SimpleDateFormat("HH:mm:ss.SSS");
		sdf_utc.setTimeZone(TimeZone.getTimeZone("UTC"));

		while (true) {
			long cur_file_size = new File(FallBallGimmick.frame.path_str).length();
			if (file_size > cur_file_size) { line_cnt = 0; match_status = 0; }
			file_size = cur_file_size;

			int tmp_line_cnt = 0;
			try (BufferedReader br = Files.newBufferedReader(log_path, Charset.forName("UTF-8"))) {
				String text;
				while((text = br.readLine()) != null) {
					if (tmp_line_cnt >= line_cnt) {
						getStartTime(text);
						getFlipperStatus(text);
					}
					tmp_line_cnt++;
				}
				line_cnt = tmp_line_cnt;
			} catch (Exception e) {}
			try{
			 	Thread.sleep(3*1000);
			} catch (InterruptedException e) {}
		}
	}

	private void getStartTime(String text) {
		switch(match_status) {
			case 0: // start a ball ball match
				if (text.indexOf("[StateConnectToGame] We're connected to the server!") != -1){
					String[] sp1 = text.split("Host = ", 2);
					String[] sp2 = sp1[1].split(":", 2);
					FallBallGimmick.frame.serverIP = sp2[0];
					// FallBallGimmick.frame.pingthread.ReachabilityTest();
				} else if (text.indexOf("[StateGameLoading] Loading game level scene FallGuy_FallBall_5") != -1){
					match_status = 1;
				}
				break;

			case 1: // start-end a fall ball match
				if (text.indexOf("[GameSession] Changing state from Countdown to Playing") != -1){
					String[] sp = text.split(": ", 2);
					try {
						FallBallGimmick.frame.startDate = sdf_utc.parse(sp[0]);
						FallBallGimmick.frame.countdown_flg = true;
					} catch (Exception e){}
				} else if ((text.indexOf("[ClientGameManager] Server notifying that the round is over.") != -1) ||
							(text.indexOf("[StateMainMenu] Creating or joining lobby") != -1) ||
							(text.indexOf("[StateMatchmaking] Begin matchmaking") != -1)) {
					match_status = 0;
					FallBallGimmick.frame.countdown_flg = false;
				}
				break;
		}
	}
	private void getFlipperStatus(String text) {
		if ((text.indexOf("SeededRandomisable 12: Flipper has initial flip direction:")) != -1) {
		   if (text.substring(text.length() - 6).equals("North ")) {
			 FallBallGimmick.frame.fliper1.setText("青→青");
		   } else {
			 FallBallGimmick.frame.fliper1.setText("青→黄");
		   }
		} else if ((text.indexOf("SeededRandomisable 13: Flipper has initial flip direction:")) != -1) {
		   if (text.substring(text.length() - 6).equals("North ")) {
			 FallBallGimmick.frame.fliper2.setText("黄→青");
		   } else {
			 FallBallGimmick.frame.fliper2.setText("黄→黄");
		   }
		}
	}
}