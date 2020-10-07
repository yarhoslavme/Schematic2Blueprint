package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A piston extension can be sticky or normal and have different directions
 * @author klaue
 *
 */
public class PistonExtension extends DirectionalBlock {
	private static HashMap<PistonExtensionType, HashMap<Direction, BufferedImage> > pistonExtensionImageCache = new HashMap<PistonExtensionType, HashMap<Direction, BufferedImage> >();
	private static double pistonExtensionZoomCache = -1;
	
	/**
	 * The type of the piston extension
	 * @author klaue
	 */
	public enum PistonExtensionType {
		/** A wooden door */	STICKY,
		/** An iron door */		NORMAL
	}
	
	private PistonExtensionType pistonExtensionType;
	
	/**
	 * initializes the piston extension
	 * @param data the block data containing direction and if it's sticky or not
	 */
	public PistonExtension(byte data) {
		super((short)34, data);
		this.type = Type.PISTONEXTENSION;
		this.setData(data);
	}
	
	/**
	 * initializes the piston extension
	 * @param pistonExtensionType the type of the piston extension
	 * @param direction the direction this piston extension faces, valid directions are UP, DOWN, N, E, S, W
	 */
	public PistonExtension(PistonExtensionType pistonExtensionType, Direction direction) {
		super((short)34);
		this.type = Type.PISTONEXTENSION;
		this.pistonExtensionType = pistonExtensionType;
		this.setDirection(direction); // sets data too
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction;
	}

	@Override
	public void setData(byte data) {
		// bit 0x8 is sticky or not. other bits can be from 0-5
		if (data < 0 || (data&7) > 5) throw new IllegalArgumentException("data out of range: " + data);
		
		byte sticky = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.pistonExtensionType = (sticky != 0) ? PistonExtensionType.STICKY : PistonExtensionType.NORMAL;
		
		byte dirData = (byte) (data & 7); // 0111
		
		switch (dirData) {
			case 0:  this.direction = Direction.DOWN;	break;
			case 1:  this.direction = Direction.UP;	break;
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
		byte dirData = 0;
		
		switch (direction) {
			case DOWN:	dirData = 0;	break;
			case UP:	dirData = 1;	break;
			case N:		dirData = 2;	break;
			case S:		dirData = 3;	break;
			case W:		dirData = 4;	break;
			case E:		dirData = 5;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 8));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = (byte)(this.data & 7);
		
		if (CW) {
			switch (this.direction) {
				case UP:	break;
				case DOWN:	break;
				case N:		this.direction = Direction.E;	dirData = 5;	break;
				case E:		this.direction = Direction.S;	dirData = 3;	break;
				case S:		this.direction = Direction.W;	dirData = 4;	break;
				case W:		this.direction = Direction.N;	dirData = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case UP:	break;
				case DOWN:	break;
				case N:		this.direction = Direction.W;	dirData = 4;	break;
				case E:		this.direction = Direction.N;	dirData = 2;	break;
				case S:		this.direction = Direction.E;	dirData = 5;	break;
				case W:		this.direction = Direction.S;	dirData = 3;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data = (byte) (dirData + (this.data & 8));
	}

	/**
	 * @return the type of the piston extension
	 */
	public PistonExtensionType getPistonType() {
		return this.pistonExtensionType;
	}

	/**
	 * @param pistonType the type of the piston extension
	 */
	public void setPistonType(PistonExtensionType pistonType) {
		if (this.pistonExtensionType != pistonType) {
			this.pistonExtensionType = pistonType;
			if (pistonType == PistonExtensionType.STICKY) {
				// remove sticky bit
				this.data = (byte)(this.data & 7); // 0111
			} else {
				//set sticky bit
				this.data = (byte)(this.data | 8);
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (pistonExtensionZoomCache != zoom) {
			// reset cache
			pistonExtensionImageCache.clear();
			pistonExtensionZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap = pistonExtensionImageCache.get(this.pistonExtensionType);
			if (directionalMap != null) {
				img = directionalMap.get(this.direction);
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		// turn if not up, down or north
		double angle = 0;
		switch(this.direction) {
			case UP:	angle = 0;		break;
			case DOWN:	angle = 0;		break;
			case N:		angle = 0;		break;
			case E:		angle = 90;		break;
			case S:		angle = 180;	break;
			case W:		angle = 270;	break;
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
		img = ImageProvider.rotateImage(angle, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (pistonExtensionImageCache.containsKey(this.pistonExtensionType)) {
			pistonExtensionImageCache.get(this.pistonExtensionType).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
			tempMap.put(this.direction, img);
			pistonExtensionImageCache.put(this.pistonExtensionType, tempMap);
		}
		return img;
	}
}
