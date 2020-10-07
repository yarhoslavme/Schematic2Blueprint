package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * An ender chest can have different directions and contain items (not supported)
 * @author klaue
 *
 */
public class EnderChest extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> enderChestImageCache = new HashMap<Direction, BufferedImage>();
	private static double enderChestZoomCache = -1;
	
	/**
	 * initializes the ender chest
	 * @param data the direction as a minecraft data value 
	 */
	public EnderChest(byte data) {
		super((short)130, data);
		this.type = Type.ENDERCHEST;
		setData(data);
	}
	
	/**
	 * initializes the ender chest
	 * @param direction the direction, valid directions are N, E, S, and W
	 */
	public EnderChest(Direction direction) {
		super((short)130);
		this.type = Type.ENDERCHEST;
		setDirection(direction);
	}

	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 2:  this.direction = Direction.N;	break;
			case 3:  this.direction = Direction.S;	break;
			case 4:  this.direction = Direction.W;	break;
			case 5:  this.direction = Direction.E;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		switch (direction) {
			case N:	this.data = 2; break;
			case E:	this.data = 5; break;
			case S:	this.data = 3; break;
			case W:	this.data = 4; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
	}

	@Override
	public void turn(boolean CW) {
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 5;	break;
				case E:	this.direction = Direction.S;	this.data = 3;	break;
				case S:	this.direction = Direction.W;	this.data = 4;	break;
				case W:	this.direction = Direction.N;	this.data = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 4;	break;
				case E:	this.direction = Direction.N;	this.data = 2;	break;
				case S:	this.direction = Direction.E;	this.data = 5;	break;
				case W:	this.direction = Direction.S;	this.data = 3;	break;
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
		
		if (enderChestZoomCache != zoom) {
			// reset cache
			enderChestImageCache.clear();
			enderChestZoomCache = zoom;
		} else {
			img = enderChestImageCache.get(this.direction);
			
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
		enderChestImageCache.put(this.direction, img);
		
		return img;
	}
}
