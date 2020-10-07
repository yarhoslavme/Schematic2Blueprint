package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A ladder can have different directions (walls it's attached to)
 * @author klaue
 *
 */
public class Ladder extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> ladderImageCache = new HashMap<Direction, BufferedImage>();
	private static double ladderZoomCache = -1;
	
	/**
	 * initializes the ladder
	 * @param direction the direction as a minecraft data value 
	 */
	public Ladder(byte direction) {
		super((short)65, direction);
		this.type = Type.LADDER;
		setData(direction);
	}
	
	/**
	 * initializes the ladder
	 * @param direction the direction, valid directions are N, E, S, and W
	 */
	public Ladder(Direction direction) {
		super((short)65);
		this.type = Type.LADDER;
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", facing " + this.direction;
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
		
		if (ladderZoomCache != zoom) {
			// reset cache
			ladderImageCache.clear();
			ladderZoomCache = zoom;
		} else {
			img = ladderImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		// flip directions because they say where the ladder is facing, but to show the wall it's attached to makes more sense
		Direction direction;
		switch(this.direction) {
			case N: direction = Direction.S; break;
			case E: direction = Direction.W; break;
			case S: direction = Direction.N; break;
			case W: direction = Direction.E; break;
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
		
		img = addArrowToImage(direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		ladderImageCache.put(this.direction, img);
		
		return img;
	}
}
