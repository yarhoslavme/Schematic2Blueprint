package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A jack'o'lantern can have different directions
 * @author klaue
 *
 */
public class JackOLantern extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> jackImageCache = new HashMap<Direction, BufferedImage>();
	private static double jackZoomCache = -1;
	
	/**
	 * initializes the jack'o'lantern
	 * @param direction the direction as a minecraft data value 
	 */
	public JackOLantern(byte direction) {
		super((short)91, direction);
		this.type = Type.PUMPKIN;
		setData(direction);
	}
	
	/**
	 * initializes the jack'o'lantern
	 * @param direction the direction, valid directions are N, E, S, and W
	 */
	public JackOLantern(Direction direction) {
		super((short)91);
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
		
		if (jackZoomCache != zoom) {
			// reset cache
			jackImageCache.clear();
			jackZoomCache = zoom;
		} else {
			img = jackImageCache.get(this.direction);
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
		jackImageCache.put(this.direction, img);
		
		return img;
	}
}
