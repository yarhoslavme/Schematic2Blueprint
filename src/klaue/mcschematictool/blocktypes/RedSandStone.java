package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A RED Sandstone Block
 * @author klaue
 *
 */
public class RedSandStone extends Block {
	/**
	 * The type of the sandstone
	 * @author klaue
	 * Created 4/6/2015 - rmcgarry
	 */
	public enum BlockType {
			/** Normal Red Sandstone block */ NORMAL, 
			/** Red Chiseled sandstone block */ CHISELED,
			/** Red Smooth sandstone block */ SMOOTH}
	

	private static HashMap<BlockType, BufferedImage> blockImageCache = new HashMap<BlockType, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private BlockType blockType = BlockType.NORMAL;
	
	/**
	 * initializes the sandstone
	 * @param data 0-2 for different types
	 */
	public RedSandStone(byte data) {
		super((short)179, data);
		this.type = Type.SANDSTONE;
		this.setData(data);
	}
	
	/**
	 * initializes the sandstone
	 * @param BlockType the type of sandstone
	 */
	public RedSandStone(BlockType blockType) {
		super((short)179);
		setBlockType(blockType);
		this.type = Type.SANDSTONE;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0:	this.blockType = BlockType.NORMAL;			break;
			case 1:	this.blockType = BlockType.CHISELED;		break;
			case 2:	this.blockType = BlockType.SMOOTH;			break;
			default: throw new IllegalArgumentException("illegal data value for sandstones: " + data);
		}
		this.data = data;
	}

	/**
	 * @return the type of sandstone
	 */
	public BlockType getBlockType() {
		return this.blockType;
	}

	/**
	 * Sets the type of the sandstone
	 * @param blockType the blockType to set
	 */
	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
		switch (blockType) {
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
		
		if (blockZoomCache != zoom) {
			// reset cache
			blockImageCache.clear();
			blockZoomCache = zoom;
		} else {
			img = blockImageCache.get(this.blockType);
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
		blockImageCache.put(this.blockType, img);

		return img;
	}
}
