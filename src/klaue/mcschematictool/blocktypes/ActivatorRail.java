package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A powered rail can have different directions and can be a flat rail or an ascending rail
 * @author klaue
 */
public class ActivatorRail extends PoweredRail {
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > onActivatorRailImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > offActivatorRailImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static double activatorRailZoomCache = -1;
	
	/**
	 * initializes the powered rail
	 * @param data
	 */
	public ActivatorRail(byte data) {
		super((short)157, data);
		this.type = Type.ACTIVATOR_RAIL;
	}
	
	/**
	 * initializes the powered rail
	 * @param type The type of rail
	 * @param isOn true if this rail is on (boosting)
	 * @param direction the direction (N, W for flat, N, E, S, W for ascending rails)
	 */
	public ActivatorRail(RailType type, boolean isOn, Direction direction) {
		if (type == RailType.CORNER) {
			throw new IllegalArgumentException("activator rails can only be flat or ascending, not corners");
		}
		this.setId((short)157);
		this.isOn = isOn;
		this.type = Type.ACTIVATOR_RAIL;
		this.railType = type;
		setDirection(direction);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		// same as the function in poweredrail. just not sure if that static cache would work witrh a subclass
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (activatorRailZoomCache != zoom) {
			// reset cache
			onActivatorRailImageCache.clear();
			offActivatorRailImageCache.clear();
			activatorRailZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap;
			if (this.isOn) {
				directionalMap = onActivatorRailImageCache.get(this.railType);
			} else {
				directionalMap = offActivatorRailImageCache.get(this.railType);
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
			if (onActivatorRailImageCache.containsKey(this.railType)) {
				onActivatorRailImageCache.get(this.railType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				onActivatorRailImageCache.put(this.railType, tempMap);
			}
		} else {
			if (offActivatorRailImageCache.containsKey(this.railType)) {
				offActivatorRailImageCache.get(this.railType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				offActivatorRailImageCache.put(this.railType, tempMap);
			}
		}
		return img;
	}
}
