package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.NameProvider;


/**
 * A redstone torch can have different directions and be off or on
 * @author klaue
 *
 */
public class RedstoneTorch extends Torch {
	private static HashMap<Boolean, HashMap<Direction, BufferedImage> > rtorchImageCache = new HashMap<Boolean, HashMap<Direction, BufferedImage> >();
	private static double rtorchZoomCache = -1;
	
	private boolean isOn = true;

	/**
	 * initializes the redstone torch
	 * @param id the block id (valid ids are 75 (on) and 76 (off))
	 * @param data the direction as a minecraft data value 
	 */
	public RedstoneTorch(short id, byte data) {
		super(data);
		if (id != 75 && id != 76) throw new IllegalArgumentException("not a valid redstone torch id: " + id); 
		this.setId(id);
		this.isOn = (id == 76);
		this.type = Type.REDSTONE_TORCH;
	}
	
	/**
	 * initializes the redstone torch
	 * @param isOn true if this redstone torch is burning
	 * @param direction the direction as a minecraft data value 
	 */
	public RedstoneTorch(boolean isOn, byte direction) {
		super(direction);
		this.setId((byte) (isOn ? 76 : 75));
		this.isOn = isOn;
		this.type = Type.REDSTONE_TORCH;
	}
	
	/**
	 * initializes the redstone torch
	 * @param isOn true if this redstone torch is burning
	 * @param direction the direction (where the torch is pointing), valid directions are N, E, S, W and None
	 */
	public RedstoneTorch(boolean isOn, Direction direction) {
		super(direction);
		this.setId((byte) (isOn ? 76 : 75));
		this.isOn = isOn;
		this.type = Type.REDSTONE_TORCH;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", " + (this.isOn ? "burning" : "not burning");
	}
	
	@Override
	public String getToolTipText() {
		if (this.direction == Direction.NONE) {
			return NameProvider.getNameOfBlockOrItem(this.id) + ", " + (this.isOn ? "burning" : "not burning");
		}
		return this.toString();
	}

	/**
	 * Returns true if this torch is on (burning). 
	 * @return true if on
	 */
	public boolean isOn() {
		return this.isOn;
	}

	/**
	 * Set to true if this torch is on (burning). Warning: Changes block ID
	 * @param isOn true if on
	 */
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		setId((byte) (isOn ? 76 : 75));
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (rtorchZoomCache != zoom) {
			// reset cache
			rtorchImageCache.clear();
			rtorchZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> tempMap = rtorchImageCache.get(this.isOn);
			if (tempMap != null) {
				img = tempMap.get(this.direction);
			}
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
		if (!rtorchImageCache.containsKey(this.isOn)) {
			rtorchImageCache.put(this.isOn, new HashMap<Direction, BufferedImage>());
		}
		rtorchImageCache.get(this.isOn).put(this.direction, img);
		
		return img;
	}
}
