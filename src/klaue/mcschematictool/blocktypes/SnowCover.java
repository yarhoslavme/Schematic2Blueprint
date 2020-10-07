package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A snow cover that can be different heights
 * @author klaue
 *
 */
public class SnowCover extends DataImageCacheBlock {
	private static HashMap<Byte, BufferedImage> snowCoverImageCache = new HashMap<Byte, BufferedImage>();
	private static double snowCoverZoomCache = -1;
	
	/**
	 * initializes the snow cover
	 * @param height 0-7
	 */
	public SnowCover(byte height) {
		super((short)78, height);
		this.type = Type.SNOWCOVER;
		if (height < 0 || height > 7) {
			throw new IllegalArgumentException("height " + height + "outside boundaries"); 
		}
	}
	
	/**
	 * Get the height value of the snow cover. Height 0 is newly fallen snow, height 7 is a full block
	 * @return the height
	 */
	public byte getHeight() {
		return this.data;
	}

	/**
	 * Set the height value of the snow cover. Height 0 is newly fallen snow, height 7 is a full block
	 * @param height the height to set
	 */
	public void setHeight(byte height) {
		if (height < 0 || height > 7) {
			throw new IllegalArgumentException("height " + height + "outside boundaries"); 
		}
		this.data = height;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", height: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setHeight(data);
	}
	
	/**
	 * Returns a new BufferedImage representing the block
	 * @param zoom the current zoom value (>0)
	 * @return the imagecomponent or null if images are deactivated
	 */
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		// since the various stages of snowfall would be too small to notice, we combine that to 3 levels: 1 third snow, two thirds snow, full
		/*int cutOffPixels = 0;
		switch(this.data) {
			case 0:		cutOffPixels = 14;	break;
			case 1:		cutOffPixels = 12;	break;
			case 2:		cutOffPixels = 10;	break;
			case 3:		cutOffPixels = 8;	break;
			case 4:		cutOffPixels = 7;	break;
			case 5: 	cutOffPixels = 5;	 break;
			case 6:		cutOffPixels = 3;	break;
			case 7:		cutOffPixels = 1;	break;
			default:	cutOffPixels = 0;	break;
		}*/
		
		if (snowCoverZoomCache != zoom) {
			// reset cache
			snowCoverImageCache.clear();
			snowCoverZoomCache = zoom;
		} if (snowCoverImageCache.containsKey(this.data)) {
			return snowCoverImageCache.get(this.data);
		}
		
		// image not in cache, make new
		// get image from imageprovider (directional blocks are handled in subclasses)
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		if (img == null) return null;
		
		// since the cover is just like the normal snow block but with some stuff missing, cut off part of the block
		if (this.data != 7) {
			img = ImageProvider.copyImage(img);
			Graphics2D g = img.createGraphics();
			g.setBackground(new Color(0x00FFFFFF, true)); // 0x00FFFFFF = 100% transparent "white"
			
			int cutOffPixels = 0;
			switch(this.data) {
				case 0:		cutOffPixels = 14;	break;
				case 1:		cutOffPixels = 12;	break;
				case 2:		cutOffPixels = 10;	break;
				case 3:		cutOffPixels = 8;	break;
				case 4:		cutOffPixels = 6;	break;
				case 5: 	cutOffPixels = 4;	 break;
				case 6:		cutOffPixels = 2;	break;
				case 7:
				default:	cutOffPixels = 0;	break;
			}
			g.clearRect(0, 0, 16, cutOffPixels);
		}
		
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		snowCoverImageCache.put(this.data, img);
		
		return img;
	}
}
