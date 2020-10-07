package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A block that implements image caching for data values
 * @author klaue
 */
public abstract class DataImageCacheBlock extends Block {
	private static HashMap<Short, HashMap<Byte, BufferedImage> > dataImageCache = new HashMap<Short, HashMap<Byte, BufferedImage> >();
	private static double dataZoomCache = -1;
	
	/**
	 * Generates a new air block
	 * If you want a block other than air, use Block.getInstance() to ensure the right subtype
	 */
	public DataImageCacheBlock() {
		super();
	}
	
	/**
	 * Generates a new block of type id with no additional data
	 * @param id
	 */
	protected DataImageCacheBlock(short id) {
		super(id);
	}
	
	/**
	 * Generates a new block of type id
	 * @param id Block ID
	 * @param data Block Data
	 */
	protected DataImageCacheBlock(short id, byte data) {
		super(id, data);
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
		
		if (dataZoomCache != zoom) {
			// reset cache
			dataImageCache.clear();
			dataZoomCache = zoom;
		} else {
			if (dataImageCache.containsKey(this.id)) {
				if (dataImageCache.get(this.id).containsKey(this.data)) {
					return dataImageCache.get(this.id).get(this.data);
				}
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider (directional blocks are handled in subclasses)
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (dataImageCache.containsKey(this.id)) {
			dataImageCache.get(this.id).put(this.data, img);
		} else {
			HashMap<Byte, BufferedImage> dataMap = new HashMap<Byte, BufferedImage>();
			dataMap.put(this.data, img);
			dataImageCache.put(this.id, dataMap);
		}
		
		return img;
	}
}
