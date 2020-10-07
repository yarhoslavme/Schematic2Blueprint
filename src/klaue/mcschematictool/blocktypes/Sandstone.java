package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A Sandstone Block
 * @author klaue
 *
 */
public class Sandstone extends Block {
	/**
	 * The type of the sandstone
	 * @author klaue
	 */
	public enum SandstoneType {/** Normal Sandstone block */ NORMAL, /** Chiseled sandstone block */ CHISELED,
						/** Smooth sandstone block */ SMOOTH}
	

	private static HashMap<SandstoneType, BufferedImage> sandstoneImageCache = new HashMap<SandstoneType, BufferedImage>();
	private static double sandstoneZoomCache = -1;
	
	private SandstoneType sandstoneType = SandstoneType.NORMAL;
	
	/**
	 * initializes the sandstone
	 * @param data 0-2 for different types
	 */
	public Sandstone(byte data) {
		super((short)24, data);
		this.type = Type.SANDSTONE;
		this.setData(data);
	}
	
	/**
	 * initializes the sandstone
	 * @param sandstoneType the type of sandstone
	 */
	public Sandstone(SandstoneType sandstoneType) {
		super((short)24);
		setSandstoneType(sandstoneType);
		this.type = Type.SANDSTONE;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0:	this.sandstoneType = SandstoneType.NORMAL;			break;
			case 1:	this.sandstoneType = SandstoneType.CHISELED;		break;
			case 2:	this.sandstoneType = SandstoneType.SMOOTH;			break;
			default: throw new IllegalArgumentException("illegal data value for sandstones: " + data);
		}
		this.data = data;
	}

	/**
	 * @return the type of sandstone
	 */
	public SandstoneType getSandstoneType() {
		return this.sandstoneType;
	}

	/**
	 * Sets the type of the sandstone
	 * @param sandstoneType the sandstoneType to set
	 */
	public void setSandstoneType(SandstoneType sandstoneType) {
		this.sandstoneType = sandstoneType;
		switch (sandstoneType) {
			case NORMAL:	this.data = (byte)0;	break;
			case CHISELED:	this.data = (byte)1;	break;
			case SMOOTH:	this.data = (byte)2;	break;
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (sandstoneZoomCache != zoom) {
			// reset cache
			sandstoneImageCache.clear();
			sandstoneZoomCache = zoom;
		} else {
			img = sandstoneImageCache.get(this.sandstoneType);
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
		sandstoneImageCache.put(this.sandstoneType, img);

		return img;
	}
}
