package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A sapling block that can be in different states of growth
 * @author klaue
 *
 */
public class TallGrass extends Block {
	/**
	 * The type of tall grass
	 * @author klaue
	 */
	public enum GrassType {
		/** Dead shrub */ DEAD_SHRUB, /** Normal tall grass */ TALL_GRASS, /** Fern */ FERN
	};
	
	private static HashMap<GrassType, BufferedImage> tgrassImageCache = new HashMap<GrassType, BufferedImage>();
	private static double tgrassZoomCache = -1;
	
	private GrassType grassType = GrassType.TALL_GRASS;
	
	/**
	 * initializes the tall grass block
	 * @param data the type of tall grass
	 */
	public TallGrass(byte data) {
		super((short)31, data);
		this.type = Type.TALL_GRASS;
		setData(data);
	}
	
	/**
	 * Initializes the tall grass block
	 * @param type The tall grass type to use
	 */
	public TallGrass(GrassType type) {
		super((short)31);
		this.type = Type.TALL_GRASS;
		setGrassType(type);
	}

	/**
	 * Set the grass type
	 * @param type
	 */
	public void setGrassType(GrassType type) {
		switch (type) {
			case DEAD_SHRUB:	this.data = 0; break;
			case TALL_GRASS:	this.data = 1; break;
			case FERN:			this.data = 2; break;
		}
		this.grassType = type;
	}

	/**
	 * Get the grass type
	 * @return the type of grass
	 */
	public GrassType getGrassType() {
		return this.grassType;
	}
	
//	@Override
//	public String toString() {
//		return "Tall Grass, type " + this.grassType;
//	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0: this.grassType = GrassType.DEAD_SHRUB;	break;
			case 1: this.grassType = GrassType.TALL_GRASS;	break;
			case 2: this.grassType = GrassType.FERN;		break;
			default: throw new IllegalArgumentException("illegal grass type: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (tgrassZoomCache != zoom) {
			// reset cache
			tgrassImageCache.clear();
			tgrassZoomCache = zoom;
		} else {
			img = tgrassImageCache.get(this.grassType);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		tgrassImageCache.put(this.grassType, img);
		
		return img;
	}
}
