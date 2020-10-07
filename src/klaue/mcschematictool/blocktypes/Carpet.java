package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Carpet block that can be in different states of growth
 * @author klaue
 *
 */
public class Carpet extends Block {
	/**
	 * The Color a wool block can have
	 * @author klaue
	 */
	public enum CarpetColor {
		WHITE, ORANGE, MAGENTA, LIGHTBLUE, YELLOW,
		LIGHTGREEN, PINK, GRAY, LIGHTGRAY, CYAN,
		PURPLE, BLUE, BROWN, DARKGREEN, RED, BLACK
	};
	
	private static HashMap<CarpetColor, BufferedImage> carpetImageCache = new HashMap<CarpetColor, BufferedImage>();
	private static double carpetZoomCache = -1;
	
	private CarpetColor color = CarpetColor.WHITE;
	
	/**
	 * initializes the carpet block
	 * @param data the minecraft data value (representing color)
	 */
	public Carpet(byte data) {
		super((short)171, data);
		this.type = Type.CARPET;
		setData(data);
	}
	
	/**
	 * initializes a white carpet block
	 */
	public Carpet() {
		this(CarpetColor.WHITE);
	}
	
	/**
	 * initializes a colored carpet block
	 * @param color the color this carpet should be dyed in
	 */
	public Carpet(CarpetColor color) {
		super((short)171);
		this.type = Type.CARPET;
		setColor(color);
	}
	
	/**
	 * Get the color of the carpet.
	 * @return the color
	 */
	public CarpetColor getColor() {
		return this.color;
	}

	/**
	 * Set the color of the wool.
	 * @param color the color to set
	 */
	public void setColor(CarpetColor color) {
		this.color = color;
		switch(color) {
			case WHITE:			this.data = 0;  break;
			case ORANGE:		this.data = 1;  break;
			case MAGENTA:		this.data = 2;  break;
			case LIGHTBLUE:		this.data = 3;  break;
			case YELLOW:		this.data = 4;  break;
			case LIGHTGREEN:	this.data = 5;  break;
			case PINK:			this.data = 6;  break;
			case GRAY:			this.data = 7;  break;
			case LIGHTGRAY:		this.data = 8;  break;
			case CYAN:			this.data = 9;  break;
			case PURPLE:		this.data = 10; break;
			case BLUE:			this.data = 11; break;
			case BROWN:			this.data = 12; break;
			case DARKGREEN:		this.data = 13; break;
			case RED:			this.data = 14; break;
			case BLACK:			this.data = 15; break;
		}
	}
	
//	@Override
//	public String toString() {
//		return "Carpet, color: " + this.color;
//	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.color = CarpetColor.WHITE;		break;
			case 1:  this.color = CarpetColor.ORANGE;		break;
			case 2:  this.color = CarpetColor.MAGENTA;		break;
			case 3:  this.color = CarpetColor.LIGHTBLUE;	break;
			case 4:  this.color = CarpetColor.YELLOW;		break;
			case 5:  this.color = CarpetColor.LIGHTGREEN;	break;
			case 6:  this.color = CarpetColor.PINK;			break;
			case 7:  this.color = CarpetColor.GRAY;			break;
			case 8:  this.color = CarpetColor.LIGHTGRAY;	break;
			case 9:  this.color = CarpetColor.CYAN;			break;
			case 10: this.color = CarpetColor.PURPLE;		break;
			case 11: this.color = CarpetColor.BLUE;			break;
			case 12: this.color = CarpetColor.BROWN;		break;
			case 13: this.color = CarpetColor.DARKGREEN;	break;
			case 14: this.color = CarpetColor.RED;			break;
			case 15: this.color = CarpetColor.BLACK;		break;
			default: throw new IllegalArgumentException("Illegal color value: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (carpetZoomCache != zoom) {
			// reset cache
			carpetImageCache.clear();
			carpetZoomCache = zoom;
		} else {
			img = carpetImageCache.get(this.color);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		
		// Use the same image for Wool, but put a "C" in the middle to ensure we know it's a carpet.
		// (template is from stair.java)
		img = ImageProvider.copyImage(img);
		Graphics2D g = img.createGraphics();
		g.setBackground(new Color(0xFFFFFFFF, true)); // 0xFFFFFFFF = opaque "white"
		g.clearRect(4, 4, 7, 7);
		g.setColor(new Color(0xFF000000, true));
		g.drawLine(5, 5, 5, 9);
		g.drawLine(5, 5, 9, 5);
		g.drawLine(5, 9, 9, 9);
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		carpetImageCache.put(this.color, img);
		
		return img;
	}
}
