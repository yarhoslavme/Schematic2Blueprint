package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A dirt block - 3 different variants, Dirt, Coarse, and Podzol
 * @author klaue
 *  (derived from Wool)
 */
public class Dirt extends Block {
	/**
	 * The Type a Dirt block can have
	 * @author klaue
	 */
	public enum DirtType {	DIRT, COARSE_DIRT, PODZOL	};
	
	private static HashMap<DirtType, BufferedImage> dirtImageCache = new HashMap<DirtType, BufferedImage>();
	private static double dirtZoomCache = -1;
	
	private DirtType dirtType = DirtType.DIRT;
	
	/**
	 * initializes the dirt block
	 * @param data the minecraft data value (representing type/variant)
	 */
	public Dirt(byte data) {
		super((short)3, data);
		this.type = Type.DIRT;
		setData(data);
	}
	
	/**
	 * initializes a plain dirt block
	 */
	public Dirt() {
		this(DirtType.DIRT);
	}
	
	/**
	 * initializes a Dirt block of a specific Type
	 * @param dirtType the variant of dirt
	 */
	public Dirt(DirtType dirtType) {
		super((short)3);
		this.type = Type.DIRT;
		setDirtType(dirtType);
	}
	
	/**
	 * Get the type of the dirt.
	 * @return the type
	 */
	public DirtType getDirtType() {
		return this.dirtType;
	}

	/**
	 * Set the type of Dirt.
	 * @param dirtType this dirt variant
	 */
	public void setDirtType(DirtType dirtType) {
		this.dirtType = dirtType;
		switch(dirtType) {
			case DIRT:			this.data = 0;  break;
			case COARSE_DIRT:	this.data = 1;  break;
			case PODZOL:		this.data = 2;  break;
		}
	}
	
//	@Override
//	public String toString() {
//		return "Dirt, Variant: " + this.dirtType;
//	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.dirtType = DirtType.DIRT;				break;
			case 1:  this.dirtType = DirtType.COARSE_DIRT;		break;
			case 2:  this.dirtType = DirtType.PODZOL;			break;
			default: throw new IllegalArgumentException("illegal Dirt Variant: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (dirtZoomCache != zoom) {
			// reset cache
			dirtImageCache.clear();
			dirtZoomCache = zoom;
		} else {
			img = dirtImageCache.get(this.dirtType);
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
		dirtImageCache.put(this.dirtType, img);
		
		return img;
	}
}
