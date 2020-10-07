package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A torch can have different directions
 * @author klaue
 *
 */
public class Torch extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> torchImageCache = new HashMap<Direction, BufferedImage>();
	private static double torchZoomCache = -1;
	
	/**
	 * initializes the torch
	 * @param direction the direction as a minecraft data value 
	 */
	public Torch(byte direction) {
		super((short)50, direction);
		this.type = Type.TORCH;
		setData(direction);
	}
	
	/**
	 * initializes the torch
	 * @param direction the direction (where the torch is pointing), valid directions are N, E, S, W and None
	 */
	public Torch(Direction direction) {
		super((short)50);
		this.type = Type.TORCH;
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", pointing to: " + this.direction;
	}
	
	@Override
	public String getToolTipText() {
		if (this.direction == Direction.NONE) {
			return "Torch";
		}
		return this.toString();
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 1:  this.direction = Direction.E;		break;
			case 2:  this.direction = Direction.W;		break;
			case 3:  this.direction = Direction.S;		break;
			case 4:  this.direction = Direction.N;		break;
			case 5:  this.direction = Direction.NONE;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		switch (direction) {
			case N:		this.data = 4; break;
			case E:		this.data = 1; break;
			case S:		this.data = 3; break;
			case W:		this.data = 2; break;
			case NONE:	this.data = 5; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
	}

	@Override
	public void turn(boolean CW) {
		if (this.direction == Direction.NONE) return;
		
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 1;	break;
				case E:	this.direction = Direction.S;	this.data = 3;	break;
				case S:	this.direction = Direction.W;	this.data = 2;	break;
				case W:	this.direction = Direction.N;	this.data = 4;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 2;	break;
				case E:	this.direction = Direction.N;	this.data = 4;	break;
				case S:	this.direction = Direction.E;	this.data = 1;	break;
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
		
		if (torchZoomCache != zoom) {
			// reset cache
			torchImageCache.clear();
			torchZoomCache = zoom;
		} else {
			img = torchImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		// turn image (not really neccessary because of the arrows (see below), but looks nicer)
		// while at it, flip direction for arrows - they say where the torch is facing, but to
		// show the wall it's attached to makes more sense
		double angle = 0;
		Direction direction;
		switch(this.direction) {
			case N:		direction = Direction.S;	angle = 0;		break;
			case E:		direction = Direction.W;	angle = 90;		break;
			case S:		direction = Direction.N;	angle = 180;	break;
			case W:		direction = Direction.E;	angle = 270;	break;
			case NONE:	direction = Direction.NONE;	angle = 0;		break; // nix
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
		
		img = ImageProvider.rotateImage(angle, img);
		
		img = addArrowToImage(direction, img); // will not add an arrow for "none"
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		torchImageCache.put(this.direction, img);
		
		return img;
	}
}
