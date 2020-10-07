package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.itemtypes.Item;

/**
 * A dropper is a block that contains items
 * @author klaue
 *
 */
public class Dropper extends Dispenser {
	private static HashMap<Direction, BufferedImage> dropperImageCache = new HashMap<Direction, BufferedImage>();
	private static double dropperZoomCache = -1;
	
	/**
	 * Initializes the dropper. The new dropper will contain exactly 9 items, some or all of which may be zero (empty)
	 * @param content The dropper content or null for an emty dropper
	 * @param direction The dropper's data (the direction it's facing)
	 */
	public Dropper(Item[] content, byte direction) {
		super(content, direction);
		this.setId((short)158);
		this.type = Type.DROPPER;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (dropperZoomCache != zoom) {
			// reset cache
			dropperImageCache.clear();
			dropperZoomCache = zoom;
		} else {
			img = dropperImageCache.get(this.direction);
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		dropperImageCache.put(this.direction, img);
		
		return img;
	}
}
