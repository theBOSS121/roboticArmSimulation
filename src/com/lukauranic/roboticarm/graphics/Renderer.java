package com.lukauranic.roboticarm.graphics;

import com.lukauranic.roboticarm.RoboticArm;

public class Renderer {
	public static int width = RoboticArm.WIDTH, height = RoboticArm.HEIGHT;
	public static int[] pixels = new int[width * height];
	
	public static void renderBackground() {
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				pixels[x + y * width] = 0xfff4f4f4;
			}
			
		}
	}
	
	public static void renderSprite(Sprite s, int xp, int yp) {
		if(xp < -s.width || yp < -s.height || xp >= width || yp >= height) return;
		
		for(int y = 0; y < s.height; y++) {
			int yy = y + yp;
			if(yy >= height || yy < 0) continue;
			for(int x = 0; x < s.width; x++) {
				int xx = x + xp;
				if(xx < 0 || x >= width) continue;
				int col = combineColors(s.pixels[x + y * s.width], xx, yy);
				pixels[xx + yy * width] = col;
			}
		}
	}

	private static int combineColors(int col, int x, int y) {
		int pCol = pixels[x + y * width];
		int a = (col >> 24) & 0xff;
		if(a == 0xff) return col;
		if(a <= 0) return pCol;
		
		int pr = (pCol >> 16) & 0xff;
		int pg = (pCol >> 8) & 0xff;
		int pb = (pCol) & 0xff;
		int r = (col >> 16) & 0xff;
		int g = (col >> 8) & 0xff;
		int b = (col) & 0xff;
		
		int nr = (int) (pr - ((pr - r) * (a / 255f)));
		int ng = (int) (pg - ((pg - g) * (a / 255f)));
		int nb = (int) (pb - ((pb - b) * (a / 255f)));
		
		return (nr << 16) | (ng << 8) | nb;
	}

	public static void renderLine(double x1, double y1, double x2, double y2) {
//		y = kx + n
		double xDiff = Math.abs(x2 - x1);
		double yDiff = Math.abs(y2 - y1);
		
		if(xDiff >= yDiff) {
			if(x1 > x2) {
				double x3 = x1;
				x1 = x2;
				x2 = x3;
				double y3 = y1;
				y1 = y2;
				y2 = y3;
			}
			double k = (y2 - y1) / (x2 - x1);
			double n = y1 - k * x1;
			
			for(int x = (int) x1; x <= (int) x2; x++) {
				int index = (int) (x + (int) (x * k + n) * width);			
				if(index < 0 || index >= pixels.length) continue;
				pixels[index] = 0xff000000;
			}
		}else {
			if(y1 > y2) {
				double x3 = x1;
				x1 = x2;
				x2 = x3;
				double y3 = y1;
				y1 = y2;
				y2 = y3;
			}
			if(x1 == x2) {
				for(int y = (int) y1; y <= (int) y2; y++) {
					int index = (int) (x1 + y * width);			
					if(index < 0 || index > pixels.length) continue;
					pixels[index] = 0xff000000;
				}
			}else {				
				double k = (y2 - y1) / (x2 - x1);
				double n = y1 - k * x1;
			
				for(int y = (int) y1; y <= (int) y2; y++) {
					int index = (int) ((int) ((y - n) / k) + y * width);			
					if(index < 0 || index >= pixels.length) continue;
					pixels[index] = 0xff000000;
				}
				
			}
		}
//		Start and end pixels
//		pixels[(int) (x1 + y1 * width)] = 0xff000000;			
//		pixels[(int) (x2 + y2 * width)] = 0xff000000;
	}
}
