package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A piston can be sticky or normal, extended ot not and have different directions
 * @author klaue
 *
 */
public class Piston extends DirectionalBlock {
	private static HashMap<PistonType, HashMap<Direction, BufferedImage> > pistonImageCache = new HashMap<PistonType, HashMap<Direction, BufferedImage> >();
	private static double pistonZoomCache = -1;
	
	/**
	 * The type of the piston
	 * @author klaue
	 */
	public enum PistonType {
		/** A wooden door */	STICKY,
		/** An iron door */		NORMAL
	}
	
	private PistonType pistonType;
	private boolean isExtended;
	
	/**
	 * initializes the piston
	 * @param id the blocks id. valid values are 29 (sticky piston) or 33 (normal piston)
	 * @param data the block data containing direction and if it's extended or not
	 */
	public Piston(short id, byte data) {
		super(id, data);
		if (id != 29 && id != 33) {
			throw new IllegalArgumentException("ID is neither sticky nor normal piston: " + id);
		}
		this.pistonType = (id == 29) ? PistonType.STICKY : PistonType.NORMAL;
		this.type = Type.PISTON;
		this.setData(data);
	}
	
	/**
	 * initializes the piston
	 * @param pistonType the type of the piston
	 * @param data the block data containing direction and if it's extended or not
	 */
	public Piston(PistonType pistonType, byte data) {
		super((short)((pistonType == PistonType.NORMAL) ? 33 : 29), data);
		this.type = Type.PISTON;
		this.pistonType = pistonType;
		this.setData(data);
	}
	
	/**
	 * initializes the piston
	 * @param pistonType the type of the piston
	 * @param isExtended true if this is piston's arm is extended
	 * @param direction the direction this piston faces, valid directions are N, E, S, W
	 */
	public Piston(PistonType pistonType, boolean isExtended, Direction direction) {
		super((short)((pistonType == PistonType.NORMAL) ? 33 : 29));
		this.isExtended = isExtended;
		this.pistonType = pistonType;
		this.type = Type.PISTON;
		
		this.data = (byte)(isExtended ? 0 : 8);
		this.setDirection(direction); // sets data too
	}
	
	@Override
	public String toString() {
		String extended = this.isExtended ? "extended" : "not extended";
		return super.toString() + ", " + extended + ", direction: " + this.direction;
	}

	/**
	 * Returns true if this pistons arm is extended
	 * @return true if extended
	 */
	public boolean isExtended() {
		return this.isExtended;
	}

	/**
	 * Set this pistons arm to extended or not
	 * @param isExtended true if extended
	 */
	public void setExtended(boolean isExtended) {
		if (this.isExtended != isExtended) {
			if (this.isExtended) {
				// remove extended bit
				this.data = (byte)(this.data & 7); // 0111
			} else {
				//set extended bit
				this.data = (byte)(this.data | 8);
			}
			this.isExtended = isExtended;
		}
	}

	@Override
	public void setData(byte data) {
		// bit 0x8 is sticky or not. other bits can be from 0-5
		if (data < 0 || (data&7) > 5) throw new IllegalArgumentException("data out of range: " + data);
		
		byte extended = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isExtended = (extended != 0);
		
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
	 * @return the type of the piston
	 */
	public PistonType getPistonType() {
		return this.pistonType;
	}

	/**
	 * @param pistonType the type of the piston
	 */
	public void setPistonType(PistonType pistonType) {
		this.pistonType = pistonType;
		this.id = (byte) ((pistonType == PistonType.STICKY) ? 29 : 33);
	}
	
	@Override
	protected void setId(short id) {
		if (id == 29) {
			this.pistonType = PistonType.STICKY;
		} else if (id == 33) {
			this.pistonType = PistonType.NORMAL;
		} else {
			throw new IllegalArgumentException("Invalid door ID: " + id);
		}
		super.setId(id);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (pistonZoomCache != zoom) {
			// reset cache
			pistonImageCache.clear();
			pistonZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap = pistonImageCache.get(this.pistonType);
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
		if (pistonImageCache.containsKey(this.pistonType)) {
			pistonImageCache.get(this.pistonType).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
			tempMap.put(this.direction, img);
			pistonImageCache.put(this.pistonType, tempMap);
		}
		return img;
	}
}
