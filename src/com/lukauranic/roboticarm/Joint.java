package com.lukauranic.roboticarm;

import java.util.Random;

import com.lukauranic.roboticarm.graphics.Renderer;

public class Joint {

	public double x, y, x2, y2;
	public double rot, localRot, fixedRot = 0.0;
	public int lenght;
	public boolean fixedJoint = false;
	
	public Joint previous = null;
	
	private Random rand = new Random();
	double offset, time;
		
	public Joint(double x, double y, double localRot, int lenght) {
		this.x = x;
		this.y = y;
		this.localRot = localRot;
		this.lenght = lenght;
		this.x2 = lenght * Math.cos(rot) + x;
		this.y2 = lenght * Math.sin(rot) + y;
	}
	
	public Joint(Joint previous, double localRot, int lenght) {
		this.previous = previous;
		this.x = previous.x2;
		this.y = previous.y2;
		this.localRot = localRot;
		this.rot = this.localRot + previous.rot;
		this.lenght = lenght;
		this.x2 = lenght * Math.cos(this.rot) + this.x;
		this.y2 = lenght * Math.sin(this.rot) + this.y;

	}
	
	public void setPoint1(double px, double py) {
		x = px;
		y = py;
		calculatePoint2();
	}
	
	public void calculatePoint2() {
		x2 = lenght * Math.cos(rot) + x;
		y2 = lenght * Math.sin(rot) + y;		
	}
	
	public void calculatePoint1() {
		x = x2 - lenght * Math.cos(rot);
		y = y2 - lenght * Math.sin(rot);		
	}
	
	public void follow(double targetX, double targetY) {
		if(fixedJoint) {
			rot = fixedRot;
		}else {
			rot = Math.atan2(targetY - y, targetX - x);			
		}
		if(previous != null) localRot = rot - previous.rot;
		else localRot = rot;
		x2 = targetX;
		y2 = targetY;
		calculatePoint1();
	}
	
	public void update() {
		if(previous != null) {
			x = previous.x2;
			y = previous.y2;
			rot = localRot + previous.rot;
		}else {
			rot = localRot;
		}
		calculatePoint2();
	}
	
	public void render() {
//		Rendering front view
		int xx = (int) (Math.cos(Game.armRotation) * Math.abs(x - RoboticArm.WIDTH / 2) + RoboticArm.WIDTH / 2);
		int xx2 = (int) (Math.cos(Game.armRotation) * Math.abs(x2 - RoboticArm.WIDTH / 2) + RoboticArm.WIDTH / 2);
		int yy = (int) y;
		int yy2 = (int) y2;
		Renderer.renderLine(xx, yy, xx2, yy2);
//		Rendering multiple lines to get an illusion of 3d closer object is bigger
		int z = (int) (RoboticArm.WIDTH - (Math.sin(Game.armRotation) * Math.abs(x - RoboticArm.WIDTH / 2) + RoboticArm.WIDTH / 2));
		int z2 = (int) (RoboticArm.WIDTH - (Math.sin(Game.armRotation) * Math.abs(x2 - RoboticArm.WIDTH / 2) + RoboticArm.WIDTH / 2));
		if(z < 300 && z2 < 300) {
			Renderer.renderLine(xx - 1, yy, xx2 - 1, yy2);	
			if(z < 200 && z2 < 200) {
				Renderer.renderLine(xx + 1, yy, xx2 + 1, yy2);	
				if(z < 100 && z2 < 100) {
					Renderer.renderLine(xx - 2, yy, xx2 - 2, yy2);	
					Renderer.renderLine(xx + 2, yy, xx2 + 2, yy2);	
				}else if(z < 100) {
					Renderer.renderLine(xx - 2, yy, xx2, yy2);
					Renderer.renderLine(xx + 2, yy, xx2, yy2);		
				}else if(z2 < 100) {
					Renderer.renderLine(xx, yy, xx2 - 2, yy2);
					Renderer.renderLine(xx, yy, xx2 + 2, yy2);			
				}
			}else if(z < 200) {
				Renderer.renderLine(xx + 1, yy, xx2, yy2);		
			}else if(z2 < 200) {
				Renderer.renderLine(xx, yy, xx2 + 1, yy2);			
			}
		}else if(z < 300) {
			Renderer.renderLine(xx - 1, yy, xx2, yy2);		
		}else if(z2 < 300) {
			Renderer.renderLine(xx, yy, xx2 - 1, yy2);			
		}
//		Rendering side view
		double scalingFactor = 0.25;
		xx = (int) (-Math.sin(Game.armRotation) * (x - RoboticArm.WIDTH / 2) * scalingFactor + RoboticArm.WIDTH / 8 * 5);
		xx2 = (int) (-Math.sin(Game.armRotation) * (x2 - RoboticArm.WIDTH / 2) * scalingFactor + RoboticArm.WIDTH / 8 * 5);
		yy = (int) ((y - RoboticArm.HEIGHT / 2) * scalingFactor + RoboticArm.HEIGHT) - 35;
		yy2 = (int) ((y2 - RoboticArm.HEIGHT / 2) * scalingFactor + RoboticArm.HEIGHT) - 35;
		Renderer.renderLine(xx, yy, xx2, yy2);
		xx = (int) (Math.sin(Game.armRotation) * (x - RoboticArm.WIDTH / 2) * scalingFactor + RoboticArm.WIDTH / 8 * 3);
		xx2 = (int) (Math.sin(Game.armRotation) * (x2 - RoboticArm.WIDTH / 2) * scalingFactor + RoboticArm.WIDTH / 8 * 3);
		Renderer.renderLine(xx, yy, xx2, yy2);
	}	
}
