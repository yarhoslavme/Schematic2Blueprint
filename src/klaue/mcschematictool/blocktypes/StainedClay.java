package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Stained Clay block 
 * @author klaue
 *	4/6/2015 rmcgarry
 */
public class StainedClay extends Block {
	/**
	 * The Color a Stained Clay block can have
	 * @author klaue
	 */
	public enum BlockColor {
		WHITE, ORANGE, MAGENTA, LIGHTBLUE, YELLOW,
		LIME, PINK, GRAY, LIGHTGRAY, CYAN,
		PURPLE, BLUE, BROWN, DARKGREEN, RED, BLACK
	};
	
	private static HashMap<BlockColor, BufferedImage> blockImageCache = new HashMap<BlockColor, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private BlockColor color = BlockColor.WHITE;
	
	/**
	 * initializes the wool block
	 * @param data the minecraft data value (representing color)
	 */
	public StainedClay(byte data) {
		super((short)159, data);
		this.type = Type.HARDENEDCLAY;
		setData(data);
	}
	
	/**
	 * initializes a white wool block
	 */
	public StainedClay() {
		this(BlockColor.WHITE);
	}
	
	/**
	 * initializes a colored clay block
	 * @param color the color this clay should be dyed in
	 */
	public StainedClay(BlockColor color) {
		super((short)159);
		this.type = Type.HARDENEDCLAY;
		setColor(color);
	}
	
	/**
	 * Get the color of the wool.
	 * @return the color
	 */
	public BlockColor getColor() {
		return this.color;
	}

	/**
	 * Set the color of the block.
	 * @param color the color to set
	 */
	public void setColor(BlockColor color) {
		this.color = color;
		switch(color) {
			case WHITE:			this.data = 0;  break;
			case ORANGE:		this.data = 1;  break;
			case MAGENTA:		this.data = 2;  break;
			case LIGHTBLUE:		this.data = 3;  break;
			case YELLOW:		this.data = 4;  break;
			case LIME:			this.data = 5;  break;
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
			case 0:  this.color = BlockColor.WHITE;		break;
			case 1:  this.color = BlockColor.ORANGE;	break;
			case 2:  this.color = BlockColor.MAGENTA;	break;
			case 3:  this.color = BlockColor.LIGHTBLUE;	break;
			case 4:  this.color = BlockColor.YELLOW;	break;
			case 5:  this.color = BlockColor.LIME;		break;
			case 6:  this.color = BlockColor.PINK;		break;
			case 7:  this.color = BlockColor.GRAY;		break;
			case 8:  this.color = BlockColor.LIGHTGRAY;	break;
			case 9:  this.color = BlockColor.CYAN;		break;
			case 10: this.color = BlockColor.PURPLE;	break;
			case 11: this.color = BlockColor.BLUE;		break;
			case 12: this.color = BlockColor.BROWN;		break;
			case 13: this.color = BlockColor.DARKGREEN;	break;
			case 14: this.color = BlockColor.RED;		break;
			case 15: this.color = BlockColor.BLACK;		break;
			default: throw new IllegalArgumentException("illegal color value: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (blockZoomCache != zoom) {
			// reset cache
			blockImageCache.clear();
			blockZoomCache = zoom;
		} else {
			img = blockImageCache.get(this.color);
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
		blockImageCache.put(this.color, img);
		
		return img;
	}
}
