package com.lukauranic.roboticarm;

import com.lukauranic.roboticarm.graphics.Renderer;
import com.lukauranic.roboticarm.graphics.Sprite;

public class Ball {

	public double x, y, z,  xDir = 0.5, yDir = -0.2, zDir = 0.3, renderX, renderY;
	
	public Ball(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.renderX = x;
		this.renderY = y;
	}
	
	
	public void update() {
		x += xDir;
		y += yDir;
		z += zDir;
		
		if(x < 0 || x + Sprite.ball.width >= RoboticArm.WIDTH) xDir *= -1;
		if(y < 0 || y + Sprite.ball.height >= RoboticArm.HEIGHT) yDir *= -1;
		if(z < 0 || z + Sprite.ball.width >= RoboticArm.WIDTH) zDir *= -1;
	}
	
	public void render() {
		Renderer.renderSprite(Sprite.ball, (int) x, (int) y); 
	}
}
