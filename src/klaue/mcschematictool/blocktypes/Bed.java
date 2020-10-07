package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A door can be open or closed, have different directions, can be a top or bottom half and can be wood or iron
 * @author klaue
 *
 */
public class Bed extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> footImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> headImageCache = new HashMap<Direction, BufferedImage>();
	private static double bedZoomCache = -1;
	
	private boolean isFoot;
	
	/**
	 * initializes the bed
	 * @param data the block data containing direction and if the block is the foot of the bed
	 */
	public Bed(byte data) {
		super((short) 26, data);
		this.type = Type.BED;
		this.setData(data);
	}
	
	/**
	 * initializes the bed
	 * @param isFoot true if this bed block is the foot of the bed
	 * @param direction the direction this bed is facing. Valid directions are N, E, S, W
	 */
	public Bed(boolean isFoot, Direction direction) {
		super((short)(26));
		this.type = Type.BED;
		this.data = (byte) (isFoot ? 0 : 8);
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction;
	}

	/**
	 * Returns true if this block is the foot part of the bed
	 * @return true if foot
	 */
	public boolean isFoot() {
		return this.isFoot;
	}

	/**
	 * Set to true if this bed should be the foot part
	 * @param isFoot true if foot
	 */
	public void setFoot(boolean isFoot) {
		if (this.isFoot != isFoot) {
			if (this.isFoot) {
				//set head bit
				this.data = (byte)(this.data | 8);
			} else {
				// remove head bit
				this.data = (byte)(this.data & 7); // 0111
			}
			this.isFoot = isFoot;
		}
	}

	@Override
	public void setData(byte data) {
		if (data < 0 || data > 11) throw new IllegalArgumentException("data out of range: " + data);
		
		byte head = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isFoot = (head == 0);
		
		byte dirData = (byte) (data & 3);
		
		switch (dirData) {
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
		byte dirData = 0;
		
		switch (direction) {
			case N:	dirData = 2;	break;
			case E:	dirData = 3;	break;
			case S: dirData = 0;	break;
			case W:	dirData = 1;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 8));
	}

	@Override
	public void turn(boolean CW) {
		byte head = (byte) (this.data & 8); // 1000(bin) if true, 0000 if false
		byte newData = 0;
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	newData = 3;	break;
				case E:	this.direction = Direction.S;	newData = 0;	break;
				case S:	this.direction = Direction.W;	newData = 1;	break;
				case W:	this.direction = Direction.N;	newData = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	newData = 1;	break;
				case E:	this.direction = Direction.N;	newData = 2;	break;
				case S:	this.direction = Direction.E;	newData = 3;	break;
				case W:	this.direction = Direction.S;	newData = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data = (byte)(newData + head);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (bedZoomCache != zoom) {
			// reset cache
			footImageCache.clear();
			headImageCache.clear();
			bedZoomCache = zoom;
		} else {
			if (this.isFoot) {
				img = footImageCache.get(this.direction);
			} else {
				img = headImageCache.get(this.direction);
			}
			
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
		
		// turn
		if (this.direction != Direction.E) { // E is standard direction
			double angle = 0;
			switch (this.direction) {
				case N:	angle = 270;	break;
				case S:	angle = 90;		break;
				case W: angle = 180;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
			
			img = ImageProvider.rotateImage(angle, img);
		}

		// save image to cache
		if (this.isFoot) {
			footImageCache.put(this.direction, img);
		} else {
			headImageCache.put(this.direction, img);
		}
		
		return img;
	}
}
