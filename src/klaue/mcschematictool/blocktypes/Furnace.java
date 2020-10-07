package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A furnace can have different directions and be on or off
 * @author klaue
 *
 */
public class Furnace extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> onImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> offImageCache = new HashMap<Direction, BufferedImage>();
	private static double furnaceZoomCache = -1;
	
	private boolean isOn;
	
	/**
	 * initializes the furnace
	 * @param id the id. valid ids are 61 (off) and 62 (on)
	 * @param data the direction as a minecraft data value 
	 */
	public Furnace(short id, byte data) {
		super(id, data);
		if (id != 61 && id != 62) throw new IllegalArgumentException("illegal id for furnaces: " + id);
		this.type = Type.FURNACE;
		setData(data);
		this.isOn = (id == 62);
	}
	
	/**
	 * initializes the furnace
	 * @param isOn true if the furnace is burning (no sense in setting that manually since contents can't be saved)
	 * @param direction the direction as a minecraft data value 
	 */
	public Furnace(boolean isOn, byte direction) {
		super((short) (isOn ? 62 : 61), direction);
		this.type = Type.FURNACE;
		setData(direction);
		this.isOn = isOn;
	}
	
	/**
	 * initializes the furnace
	 * @param isOn true if the furnace is burning (no sense in setting that manually since contents can't be saved)
	 * @param direction the direction, valid directions are N, E, S, and W
	 */
	public Furnace(boolean isOn, Direction direction) {
		super((short) (isOn ? 62 : 61));
		this.type = Type.FURNACE;
		setDirection(direction);
		this.isOn = isOn;
	}
	
	/**
	 * Returns true if the furnace is burning
	 * @return true if burning
	 */
	public boolean isOn() {
		return this.isOn;
	}

	/**
	 * Sets the furnace's burning state
	 * @param isOn true if the furnace is burning (no sense in setting that manually since contents can't be saved)
	 */
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		this.setId((byte) (isOn ? 62 : 61));
	}

	@Override
	public String toString() {
		return super.toString() + " (" + (this.isOn ? "on" : "off") + "), direction: " + this.direction;
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
	protected void setId(short id) {
		if (id == 61) {
			this.isOn = false;
		} else if (id == 62) {
			this.isOn = true;
		} else {
			throw new IllegalArgumentException("Invalid furnace ID: " + id);
		}
		super.setId(id);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (furnaceZoomCache != zoom) {
			// reset cache
			onImageCache.clear();
			offImageCache.clear();
			furnaceZoomCache = zoom;
		} else {
			if (this.isOn) {
				img = onImageCache.get(this.direction);
			} else {
				img = offImageCache.get(this.direction);
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isOn) {
			onImageCache.put(this.direction, img);
		} else {
			offImageCache.put(this.direction, img);
		}
		
		return img;
	}
}
