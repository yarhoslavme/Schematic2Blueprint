package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A wool block that can be in different states of growth
 * @author klaue
 *
 */
public class Wool extends Block {
	/**
	 * The Color a wool block can have
	 * @author klaue
	 */
	public enum WoolColor {
		WHITE, ORANGE, MAGENTA, LIGHTBLUE, YELLOW,
		LIGHTGREEN, PINK, GRAY, LIGHTGRAY, CYAN,
		PURPLE, BLUE, BROWN, DARKGREEN, RED, BLACK
	};
	
	private static HashMap<WoolColor, BufferedImage> woolImageCache = new HashMap<WoolColor, BufferedImage>();
	private static double woolZoomCache = -1;
	
	private WoolColor color = WoolColor.WHITE;
	
	/**
	 * initializes the wool block
	 * @param data the minecraft data value (representing color)
	 */
	public Wool(byte data) {
		super((short)35, data);
		this.type = Type.WOOL;
		setData(data);
	}
	
	/**
	 * initializes a white wool block
	 */
	public Wool() {
		this(WoolColor.WHITE);
	}
	
	/**
	 * initializes a colored wool block
	 * @param color the color this wool should be dyed in
	 */
	public Wool(WoolColor color) {
		super((short)35);
		this.type = Type.WOOL;
		setColor(color);
	}
	
	/**
	 * Get the color of the wool.
	 * @return the color
	 */
	public WoolColor getColor() {
		return this.color;
	}

	/**
	 * Set the color of the wool.
	 * @param color the color to set
	 */
	public void setColor(WoolColor color) {
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
//		return "Wool, color: " + this.color;
//	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.color = WoolColor.WHITE;		break;
			case 1:  this.color = WoolColor.ORANGE;		break;
			case 2:  this.color = WoolColor.MAGENTA;	break;
			case 3:  this.color = WoolColor.LIGHTBLUE;	break;
			case 4:  this.color = WoolColor.YELLOW;		break;
			case 5:  this.color = WoolColor.LIGHTGREEN;	break;
			case 6:  this.color = WoolColor.PINK;		break;
			case 7:  this.color = WoolColor.GRAY;		break;
			case 8:  this.color = WoolColor.LIGHTGRAY;	break;
			case 9:  this.color = WoolColor.CYAN;		break;
			case 10: this.color = WoolColor.PURPLE;		break;
			case 11: this.color = WoolColor.BLUE;		break;
			case 12: this.color = WoolColor.BROWN;		break;
			case 13: this.color = WoolColor.DARKGREEN;	break;
			case 14: this.color = WoolColor.RED;		break;
			case 15: this.color = WoolColor.BLACK;		break;
			default: throw new IllegalArgumentException("illegal color value: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (woolZoomCache != zoom) {
			// reset cache
			woolImageCache.clear();
			woolZoomCache = zoom;
		} else {
			img = woolImageCache.get(this.color);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		woolImageCache.put(this.color, img);
		
		return img;
	}
}
