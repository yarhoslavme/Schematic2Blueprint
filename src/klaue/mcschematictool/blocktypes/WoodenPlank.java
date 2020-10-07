package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A Wooden plank Block
 * @author klaue
 *
 */
public class WoodenPlank extends Block {
	private static HashMap<TreeType, BufferedImage> plankImageCache = new HashMap<TreeType, BufferedImage>();
	private static double plankZoomCache = -1;
	
	private TreeType treeType = TreeType.OAK;
	
	/**
	 * initializes the wooden plank
	 * @param data 0-5 for different types
	 */
	public WoodenPlank(byte data) {
		super((short)5, data);
		this.type = Type.WOODENPLANK;
		this.setData(data);
	}
	
	/**
	 * initializes the wooden plank
	 * @param treeType the type of wooden plank
	 */
	public WoodenPlank(TreeType treeType) {
		super((short)5);
		setTreeType(treeType);
		this.type = Type.WOODENPLANK;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0:	this.treeType = TreeType.OAK;		break;
			case 1:	this.treeType = TreeType.SPRUCE;	break;
			case 2:	this.treeType = TreeType.BIRCH;		break;
			case 3:	this.treeType = TreeType.JUNGLE;	break;
			case 4: this.treeType = TreeType.ACACIA;	break;
			case 5: this.treeType = TreeType.DARK_OAK;	break;
			
			default: throw new IllegalArgumentException("illegal data value for wooden planks: " + data);
		}
		this.data = data;
	}

	/**
	 * @return the type of plank
	 */
	public TreeType getTreeType() {
		return this.treeType;
	}

	/**
	 * Sets the type of the plank
	 * @param treeType the plank type to set
	 */
	public void setTreeType(TreeType treeType) {
		this.treeType = treeType;
		switch (treeType) {
			case OAK:		this.data = (byte)0;	break;
			case SPRUCE:	this.data = (byte)1;	break;
			case BIRCH:		this.data = (byte)2;	break;
			case JUNGLE:	this.data = (byte)3;	break;
			case ACACIA:	this.data = (byte)4;	break;
			case DARK_OAK:	this.data = (byte)5;	break;

		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (plankZoomCache != zoom) {
			// reset cache
			plankImageCache.clear();
			plankZoomCache = zoom;
		} else {
			img = plankImageCache.get(this.treeType);
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
		plankImageCache.put(this.treeType, img);

		return img;
	}
}
