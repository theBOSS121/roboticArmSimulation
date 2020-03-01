package com.lukauranic.roboticarm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.lukauranic.roboticarm.graphics.Renderer;
import com.lukauranic.roboticarm.graphics.Sprite;
import com.lukauranic.roboticarm.input.Mouse;

public class Game {

	private Random rand = new Random();
	
	private List<Joint> joints = new ArrayList<Joint>();
	
	public int targetX = 300, targetY = 150, targetZ = 200, 
			baseX = RoboticArm.WIDTH / 2, baseY = RoboticArm.HEIGHT / 2, baseZ = RoboticArm.WIDTH / 2;
	
	private int counter = 0, roboticArmScale = 4;
	public static double armRotation = 0.0, armCurrentLenght = 0.0;
	private boolean outOfReach = false;
	
	Ball ball;
	
	private Scanner scanner;
	
	public Game() {
		init();
	}
	
	public void init() {
		scanner = new Scanner(System.in);
		new Thread() {
			public void run() {
				listenForInput();				
			}
		}.start();
		
		ball = new Ball(RoboticArm.WIDTH / 5 * 4, RoboticArm.HEIGHT / 5 * 2, RoboticArm.WIDTH / 2);
		
		joints.add(new Joint(baseX, baseY, -Math.PI / 8 * 3, 29 * roboticArmScale));
		joints.add(new Joint(joints.get(joints.size() - 1), Math.PI / 8 * 3, (int) (20.5 * roboticArmScale)));
		joints.add(new Joint(joints.get(joints.size() - 1), Math.PI / 4, 7 *roboticArmScale));


		
		joints.get(joints.size() - 1).fixedRot = Math.PI / 2;
		joints.get(joints.size() - 1).fixedJoint = true;
		
		
//		armRotation = Math.atan2(targetZ - baseZ, targetX - baseX);
	}
	
	public void listenForInput() {
		while(true) {
			System.out.println("Coordinates:");
			targetX = Integer.parseInt(scanner.nextLine());
			targetY = Integer.parseInt(scanner.nextLine());
			targetZ = Integer.parseInt(scanner.nextLine());
//			armRotation = Math.atan2(targetZ - baseZ, targetX - baseX) - Math.PI;
		}
	}
//	todo calculate increase follow values based on angle
	public void update() {		
//		Mouse control
//		if(Mouse.mouseX > RoboticArm.WIDTH / 4 * 3 && Mouse.mouseY > RoboticArm.HEIGHT / 4 * 3) {
//			targetZ = 400 - (Mouse.mouseY - RoboticArm.HEIGHT / 4 * 3) * 4;
//		}else {
//			targetX = Mouse.mouseX;
//			targetY = Mouse.mouseY;
//		}
		
		armRotation = Math.atan2(baseZ - targetZ, -(baseX - targetX));
		
		ball.update();
		targetX = (int) ball.x + Sprite.ball.width / 2;
		targetY = (int) ball.y + Sprite.ball.height / 2;
		targetZ = (int) ball.z + Sprite.ball.width / 2;
		
		for(int i = 0; i < joints.size(); i++) {
			joints.get(i).update();
		}
		
		if(Mouse.buttonUp(MouseEvent.BUTTON3)) {
			joints.get(joints.size() - 1).fixedJoint = !joints.get(joints.size() - 1).fixedJoint;
		}
//		Caluculates lenght of the arm (end point of the last joint to base)
		double xx = (joints.get(joints.size() - 1).x2 - baseX) * Math.cos(armRotation);
		double zz = (joints.get(joints.size() - 1).x2 - baseZ) * Math.sin(armRotation);		
		armCurrentLenght = Math.sqrt(xx * xx + zz * zz);
		
//		Inverse kinematics adapted for 2D with added rotation around y-axis at the end 
//		Thats why we use targetXZ for x component of the follow function.
//		Point in 3d space is rotated to 2d plane z = 0 and then the x is calculated this x is called
//		targetXZ. Inverse kinematics works you rotate end point of the last joint to the target point
//		than move it to the end point. After that you rotate joint before the last one to the start
//		point of the last joint and move the end point of the joint before the last one to the start
//		point of the last joint. You do this until you come to the end of the arm. After that you
//		move all of the joints back as much as the first joint is out of its base, because the first
//		joint should always be at the base position. You repeat this process in a loop.
		double xDiff = baseX - targetX;
		double zDiff = baseZ - targetZ;
		double targetXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff) + baseX;
		joints.get(joints.size() - 1).follow(targetXZ, targetY);
		for(int i = joints.size() - 2; i >= 0; i--) {
			joints.get(i).follow(joints.get(i + 1).x, joints.get(i + 1).y);	
		}
		joints.get(0).setPoint1(baseX, baseY);
		for(int i = 1; i < joints.size(); i++) {
			joints.get(i).setPoint1(joints.get(i - 1).x2, joints.get(i - 1).y2);
		}
		
		counter++;
	}
	
	public void render() {
		Renderer.renderBackground();
		
//		ball.render();

		for(int i = 0; i < joints.size(); i++) {
			joints.get(i).render();
		}
//		top view line render
		int xx = RoboticArm.WIDTH - 50;
		int yy = RoboticArm.HEIGHT - 50;
		int xx2 = (int) (Math.cos(armRotation) * armCurrentLenght / 4) + xx;
		int yy2 = (int) (Math.sin(armRotation) * armCurrentLenght / 4) + yy;
		Renderer.renderLine(xx, yy, xx2, yy2);
		
		for(int i = 0; i < RoboticArm.pixels.length; i++) {
			RoboticArm.pixels[i] = Renderer.pixels[i];
		}
	}

	public void renderText(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("Verdana", 0, 25));
		g.setColor(Color.RED);
//		all lines are drawn to the right of the real x and down of the real y
		int size = 8;
		if(targetZ < 100) size = 8;
		else if(targetZ < 200) size = 6;
		else if(targetZ < 300) size = 4;
		else if(targetZ < 400) size = 2;
		
		g.fillRect((int) (targetX * RoboticArm.scale) - size / 2, (int) (targetY * RoboticArm.scale) - size / 2, size, size);
		g.fillRect((int) ((targetX / 4 + RoboticArm.WIDTH / 4 * 3) * RoboticArm.scale) - size / 2, (int) ((RoboticArm.WIDTH / 4 - targetZ / 4 + RoboticArm.WIDTH / 4 * 3) * RoboticArm.scale) - size / 2, size, size);
		g.fillRect((int) ((targetZ / 4 + RoboticArm.WIDTH / 4 * 2) * RoboticArm.scale) - size / 2, (int) ((targetY / 4 + RoboticArm.WIDTH / 4 * 3 + 15) * RoboticArm.scale) - size / 2, size, size);
		g.fillRect((int) ((RoboticArm.WIDTH / 4 - targetZ / 4 + RoboticArm.WIDTH / 4) * RoboticArm.scale) - size / 2, (int) ((targetY / 4 + RoboticArm.WIDTH / 4 * 3 + 15) * RoboticArm.scale) - size / 2, size, size);

		g.drawString("x: " + targetX + " y: " + targetY + " z: " + targetZ, (int) (5 * RoboticArm.scale), (int) (30 * RoboticArm.scale));

		g.setFont(new Font("Verdana", 0, 15));
		g.drawString("Front view", (int) (5 * RoboticArm.scale), (int) (12 * RoboticArm.scale));
		g.drawString("Top view", (int) (RoboticArm.WIDTH /  4 * 3 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * 3 * RoboticArm.scale) - 10);
		g.drawString("Side View (Right to Left)", (int) (RoboticArm.WIDTH /  2 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * 3 * RoboticArm.scale) - 10);
		g.drawString("Side View (Left to Right)", (int) (RoboticArm.WIDTH /  4 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * 3 * RoboticArm.scale) - 10);
		g.drawRect((int) (RoboticArm.WIDTH /  4 * 3 * RoboticArm.scale), (int) (RoboticArm.WIDTH /  4 * 3 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale));
		g.drawRect((int) (RoboticArm.WIDTH /  4 * RoboticArm.scale), (int) (RoboticArm.WIDTH /  4 * 3 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale));
		g.drawRect((int) (RoboticArm.WIDTH /  2 * RoboticArm.scale), (int) (RoboticArm.WIDTH /  4 * 3 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale), (int) (RoboticArm.HEIGHT / 4 * RoboticArm.scale));
	}
	
}
