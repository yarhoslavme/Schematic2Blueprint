package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A detector rail can have different directions and can be a flat rail or an ascending rail
 * @author klaue
 */
public class DetectorRail extends Rail {
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > detectorRailImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static double detectorRailZoomCache = -1;
	
	// private RailType railType; from subclass
	
	/**
	 * initializes the detector rail
	 * @param data
	 */
	public DetectorRail(byte data) {
		super((short)28, data);
		this.type = Type.DETECTOR_RAIL;
		setData(data);
	}
	
	/**
	 * initializes the detector rail
	 * @param type The type of rail
	 * @param direction the direction (N, W for flat, N, E, S, W for ascending rails)
	 */
	public DetectorRail(RailType type, Direction direction) {
		super();
		if (type == RailType.CORNER) {
			throw new IllegalArgumentException("detector rails can only be flat or ascending, not corners");
		}
		this.type = Type.DETECTOR_RAIL;
		this.railType = type;
		setDirection(direction);
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 5) {
			throw new IllegalArgumentException("data value out of range: " + data);
		}
		super.setData(data);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (detectorRailZoomCache != zoom) {
			// reset cache
			detectorRailImageCache.clear();
			detectorRailZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap = detectorRailImageCache.get(this.railType);
				
			if (directionalMap != null) {
				img = directionalMap.get(this.direction);
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
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
		if (detectorRailImageCache.containsKey(this.railType)) {
			detectorRailImageCache.get(this.railType).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
			tempMap.put(this.direction, img);
			detectorRailImageCache.put(this.railType, tempMap);
		}
		return img;
	}
}
