package klaue.mcschematictool.itemtypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import klaue.mcschematictool.ImageProvider;

/**
 * A colored helmet made of leather
 * @author klaue
 *
 */
public class LeatherHelmet extends ColoredItem {
	/**
	 * Generates a new leather, helmet color white
	 * @param data Item data
	 */
	public LeatherHelmet(short data) {
		this(data, (byte)0);
	}
	
	/**
	 * Generates a new leather helmet, color white
	 * @param data Item data
	 * @param stacksize the number of items in this stack
	 */
	public LeatherHelmet(short data, byte stacksize) {
		this(data, stacksize, new Color(139, 69, 19)); // brown
	}
	
	/**
	 * Generates a new leather helmet of type id
	 * @param data Item data
	 * @param stacksize the number of items in this stack
	 * @param color the color or white if null
	 */
	public LeatherHelmet(short data, byte stacksize, Color color) {
		super((short)298, data, stacksize);
		setColor(color);
		this.type = Type.LEATHERHELMET;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		// since we plan to use the parents getImage and use its imagecache too (no need to waste space storing colored, but incomplete leather armor parts)
		// we have to check if the image is in the cache before we call the parents getImage (or else it would be there anyway but no one could tell if it was
		// the completed or just the colored image)
		// if the image is in the cache, we can just happily return the cached image
		if (coloredZoomCache != zoom) {
			// reset cache
			coloredItemCache.clear();
			coloredZoomCache = zoom;
		} else {
			if (coloredZoomCache == zoom && coloredItemCache.containsKey(this.id) && coloredItemCache.get(this.id).containsKey(this.data)
					&& coloredItemCache.get(this.id).get(this.data).containsKey(this.color)) {
				return coloredItemCache.get(this.id).get(this.data).get(this.color);
			}
		}
		
		// image was not in cache - get parents colored image and add overlay (we make no new image - new image in cache is updated)
		BufferedImage coloredImage = super.getImage(zoom);
		BufferedImage overlay = ImageProvider.getItemImage("helmetCloth_overlay");
		
		Graphics2D base = coloredImage.createGraphics();
		base.drawRenderedImage(overlay, null);
		
		return coloredImage;
	}
}
