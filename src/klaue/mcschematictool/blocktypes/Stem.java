package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A stem block that can be in different states of growth
 * @author klaue
 *
 */
public class Stem extends Block {
	/**
	 * The stem type
	 * @author klaue
	 */
	public enum StemType {
		/** Pumpkin stem */	PUMPKIN,
		/** Melon stem */	MELON
	};
	
	private static HashMap<StemType, BufferedImage> stemImageCache = new HashMap<StemType, BufferedImage>();
	private static double stemZoomCache = -1;
	
	private StemType stemType = StemType.PUMPKIN;
	
	/**
	 * Initializes the stem block
	 * @param id the id of the stem
	 * @param data the current growth (0-7)
	 */
	public Stem(short id, byte data) {
		super(id);
		setGrowth(data); // sets data
		this.type = Type.STEM;
		this.stemType = (id == 104) ? StemType.PUMPKIN : StemType.MELON;
	}
	
	/**
	 * Initializes the stem block
	 * @param type The Stem Type to use
	 * @param growth the current growth (0-7)
	 */
	public Stem(StemType type, byte growth) {
		super((type == StemType.PUMPKIN) ? (byte)104 : (byte)105);
		setGrowth(growth); // sets data
		this.type = Type.STEM;
		this.stemType = type;
	}
	
	/**
	 * Get the growth value of the stem. Growth 0 is a newly planted one, growth 7 is one that will spawn a melon/pumpkin
	 * @return the growth
	 */
	public byte getGrowth() {
		return this.data;
	}

	/**
	 * Set the growth value of the stem. Strength 0 is a newly planted one, strength 7 is one that will spawn a melon/pumpkin
	 * @param growth the strength to set
	 */
	public void setGrowth(byte growth) {
		if (growth < 0 || growth > 7) {
			throw new IllegalArgumentException("growth " + growth + "outside boundaries"); 
		}
		this.data = growth;
	}

	/**
	 * Set the stem type
	 * @param type
	 */
	public void setStemType(StemType type) {
		switch (type) {
			case PUMPKIN:	this.setId((byte)104); break;
			case MELON:		this.setId((byte)105); break;
		}
		this.stemType = type;
	}

	/**
	 * Get the stem type
	 * @return the type of stem
	 */
	public StemType getStemType() {
		return this.stemType;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", growth: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setGrowth(data);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (stemZoomCache != zoom) {
			// reset cache
			stemImageCache.clear();
			stemZoomCache = zoom;
		} else {
			img = stemImageCache.get(this.stemType);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		stemImageCache.put(this.stemType, img);
		
		return img;
	}
}
