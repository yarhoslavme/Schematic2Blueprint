package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A powered rail can have different directions and can be a flat rail or an ascending rail
 * @author klaue
 */
public class PoweredRail extends Rail {
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > onPoweredRailImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > offPoweredRailImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static double poweredRailZoomCache = -1;
	
	// private RailType railType; from subclass
	protected boolean isOn;
	
	/**
	 * initializes the powered rail
	 * @param data
	 */
	public PoweredRail(byte data) {
		super((short)27, data);
		this.type = Type.POWERED_RAIL;
		setData(data);
	}
	
	/**
	 * ctor for subclasses
	 * @param id
	 * @param data
	 */
	protected PoweredRail(short id, byte data) {
		super(id, data);
		this.type = Type.POWERED_RAIL;
		setData(data);
	}
	protected PoweredRail() {
		super();
		this.type = Type.POWERED_RAIL;
	}
	
	/**
	 * initializes the powered rail
	 * @param type The type of rail
	 * @param isOn true if this rail is on (boosting)
	 * @param direction the direction (N, W for flat, N, E, S, W for ascending rails)
	 */
	public PoweredRail(RailType type, boolean isOn, Direction direction) {
		super();
		if (type == RailType.CORNER) {
			throw new IllegalArgumentException("powered rails can only be flat or ascending, not corners");
		}
		this.setId((short)27);
		this.isOn = isOn;
		this.type = Type.POWERED_RAIL;
		this.railType = type;
		setDirection(direction);
	}

	@Override
	public String toString() {
		return super.toString() + ", " + (this.isOn ? "powered" : "not powered");
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 13) {
			throw new IllegalArgumentException("data value out of range: " + data);
		}
		
		byte on = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isOn = (on != 0);
		
		byte dirData = (byte) (data & 7);
		super.setData(dirData); // for directions
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		super.setDirection(direction);
		// add on bit
		if (this.isOn) this.data += 8;
	}

	@Override
	public void turn(boolean CW) {
		super.turn(CW);
		// add on bit
		if (this.isOn) this.data += 8;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (poweredRailZoomCache != zoom) {
			// reset cache
			onPoweredRailImageCache.clear();
			offPoweredRailImageCache.clear();
			poweredRailZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap;
			if (this.isOn) {
				directionalMap = onPoweredRailImageCache.get(this.railType);
			} else {
				directionalMap = offPoweredRailImageCache.get(this.railType);
			}
				
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
		
		// turn
		double angle = 0;

		switch(this.direction) {
			case N:
			case S: angle = 0;  break;
			case E:
			case W: angle = 90; break;
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
		
		img = ImageProvider.rotateImage(angle, img);
		
		// add arrows to ascending rails
		if (this.railType == RailType.ASCENDING) {
			img = addArrowToImage(this.direction, img);
		}
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isOn) {
			if (onPoweredRailImageCache.containsKey(this.railType)) {
				onPoweredRailImageCache.get(this.railType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				onPoweredRailImageCache.put(this.railType, tempMap);
			}
		} else {
			if (offPoweredRailImageCache.containsKey(this.railType)) {
				offPoweredRailImageCache.get(this.railType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				offPoweredRailImageCache.put(this.railType, tempMap);
			}
		}
		return img;
	}

	/**
	 * @return the isOn
	 */
	public boolean isOn() {
		return this.isOn;
	}

	/**
	 * @param isOn the isOn to set
	 */
	public void setOn(boolean isOn) {
		if (this.isOn == isOn) return;
		if (isOn) {
			this.data += 8;
		} else {
			this.data =(byte)(this.data & 7);
		}
		this.isOn = isOn;
	}
}
