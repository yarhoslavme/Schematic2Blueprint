package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A pumpkin can have different directions
 * @author klaue
 *
 */
public class Pumpkin extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> pumpkinImageCache = new HashMap<Direction, BufferedImage>();
	private static double pumpkinZoomCache = -1;
	
	/**
	 * initializes the pumpkin
	 * @param direction the direction as a minecraft data value 
	 */
	public Pumpkin(byte direction) {
		super((short)86, direction);
		this.type = Type.PUMPKIN;
		setData(direction);
	}
	
	/**
	 * initializes the pumpkin
	 * @param direction the direction, valid directions are N, E, S, and W
	 */
	public Pumpkin(Direction direction) {
		super((short)86);
		this.type = Type.PUMPKIN;
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0:  this.direction = Direction.S;	break;
			case 1:  this.direction = Direction.W;	break;
			case 2:  this.direction = Direction.N;	break;
			case 3:  this.direction = Direction.E;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		switch (direction) {
			case N:	this.data = 2; break;
			case E:	this.data = 3; break;
			case S:	this.data = 0; break;
			case W:	this.data = 1; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
	}

	@Override
	public void turn(boolean CW) {
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 3;	break;
				case E:	this.direction = Direction.S;	this.data = 0;	break;
				case S:	this.direction = Direction.W;	this.data = 1;	break;
				case W:	this.direction = Direction.N;	this.data = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 1;	break;
				case E:	this.direction = Direction.N;	this.data = 2;	break;
				case S:	this.direction = Direction.E;	this.data = 3;	break;
				case W:	this.direction = Direction.S;	this.data = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (pumpkinZoomCache != zoom) {
			// reset cache
			pumpkinImageCache.clear();
			pumpkinZoomCache = zoom;
		} else {
			img = pumpkinImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		pumpkinImageCache.put(this.direction, img);
		
		return img;
	}
}
