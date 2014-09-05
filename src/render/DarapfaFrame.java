package render;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.Timer;

import map.CircleObstruction;
import map.SquareObstruction;
import render.map.Camera;
import render.map.MapCanvas;
import render.map.MapStub;

public class DarapfaFrame extends JFrame implements KeyListener, ActionListener {
	public static int RENDER_DELAY = 20; // ± 1000/60 -> 60 frames a second

	private Camera cam;
	private MapStub map;
	private HashSet<Integer> keysDown;

	public DarapfaFrame()	{
		super("David and Roland's Amazing Path Finding Algorithm");
		setSize(600, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		map = MapStub.generateRandomMap(40, 40, 100, 30);
		cam = new Camera(0, 0, 0, 30, 20);
		keysDown = new HashSet<Integer>();

		map.addObstruction(new SquareObstruction(20, 20, 50, 50, (float)Math.toRadians(50)));
		map.addObstruction(new SquareObstruction(100, 200, 50, 50, (float)Math.toRadians(20)));
		map.addObstruction(new CircleObstruction(200, 200, 50));

		getContentPane().add(new MapCanvas(map, cam));

		setVisible(true);

		addKeyListener(this);
		// Create the timer that does our rendering and game logic
		new Timer(RENDER_DELAY, this).start();
	}
	
	public void render()	{
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		keysDown.add(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(e.getKeyCode());
	}
	
	@Override
	// This is given to the timer and run every frame
	public void actionPerformed(ActionEvent evt) {
		if(keysDown.contains(KeyEvent.VK_UP))
			cam.move(0, -1);
		if(keysDown.contains(KeyEvent.VK_DOWN))
			cam.move(0, 1);
		if(keysDown.contains(KeyEvent.VK_LEFT))
			cam.move(-1, 0);
		if(keysDown.contains(KeyEvent.VK_RIGHT))
			cam.move(1, 0);
		
		render();
	}
}