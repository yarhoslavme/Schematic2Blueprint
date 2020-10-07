package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A redstone repeater can have different directions and delays and can be on or off
 * @author klaue
 *
 */
public class RedstoneRepeater extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> onImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> offImageCache = new HashMap<Direction, BufferedImage>();
	private static double repeaterZoomCache = -1;
	
	private byte delay;
	private boolean isOn;
	
	/**
	 * initializes the redstone repeater
	 * @param id the id of this repeater. can be 93 (off) or 94 (on)
	 * @param data the block data containing direction and delay
	 */
	public RedstoneRepeater(short id, byte data) {
		super(id, data);
		if (id != 93 && id != 94) throw new IllegalArgumentException("id not valid for redstone repeaters: " + id);
		this.type = Type.REDSTONE_REPEATER;
		this.isOn = (id == 94);
		this.setData(data);
	}
	
	/**
	 * initializes the redstone repeater
	 * @param isOn true if this repeater is on
	 * @param delay the delay of this repeater (0-3)
	 * @param direction the direction this repeater is facing. Valid directions are N, E, S, W
	 */
	public RedstoneRepeater(boolean isOn, byte delay, Direction direction) {
		super((short) (isOn ? 94 : 93));
		this.type = Type.REDSTONE_REPEATER;
		this.data = (byte) (delay << 2);
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + (this.isOn ? "on" : "off") + "), delay: " + this.delay + ", direction: " + this.direction;
	}

	/**
	 * Returns true if this block is on
	 * @return true if on
	 */
	public boolean isOn() {
		return this.isOn;
	}

	/**
	 * Set to true if this repeater should be on (Warning, changes ID)
	 * @param isOn true if on
	 */
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		this.id = (byte) (this.isOn ? 94 : 93);
	}

	@Override
	public void setData(byte data) {
		if (data < 0 || data > 15) throw new IllegalArgumentException("data out of range: " + data);
		
		this.delay = (byte) (data >> 2);
		
		byte dirData = (byte) (data & 3);
		
		switch (dirData) {
			case 0:  this.direction = Direction.N;	break;
			case 1:  this.direction = Direction.E;	break;
			case 2:  this.direction = Direction.S;	break;
			case 3:  this.direction = Direction.W;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case N:	dirData = 0;	break;
			case E:	dirData = 1;	break;
			case S: dirData = 2;	break;
			case W:	dirData = 3;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 12));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	dirData = 1;	break;
				case E:	this.direction = Direction.S;	dirData = 2;	break;
				case S:	this.direction = Direction.W;	dirData = 3;	break;
				case W:	this.direction = Direction.N;	dirData = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	dirData = 3;	break;
				case E:	this.direction = Direction.N;	dirData = 0;	break;
				case S:	this.direction = Direction.E;	dirData = 1;	break;
				case W:	this.direction = Direction.S;	dirData = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data = (byte) (dirData + (this.data & 12));
	}

	/**
	 * Returns the delay of this repeater. Can be anywhere from 0 (1 tick) to 3 (4 ticks)
	 * @return the delay
	 */
	public byte getDelay() {
		return this.delay;
	}

	/**
	 * Sets the delay of this repeater. Can be anywhere from 0 (1 tick) to 3 (4 ticks)
	 * @param delay the delay to set
	 */
	public void setDelay(byte delay) {
		if (delay < 0 || delay > 3) throw new IllegalArgumentException("delay value out of bounds: " + delay);
		this.delay = delay;
		this.data = (byte) ((delay << 2) + (this.data & 3));
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (repeaterZoomCache != zoom) {
			// reset cache
			onImageCache.clear();
			offImageCache.clear();
			repeaterZoomCache = zoom;
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
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		// rotaterationalismael
		double angle = 0;
		switch(this.direction) {
			case N: angle = 0;   break;
			case E: angle = 90;  break;
			case S: angle = 180; break;
			case W: angle = 270; break;
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
		if (this.isOn) {
			onImageCache.put(this.direction, img);
		} else {
			offImageCache.put(this.direction, img);
		}
		
		return img;
	}
}
