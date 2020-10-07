package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Sand block - 2 different variants, Sand and Red_Sand
 * @author klaue
 *  (derived from Wool, made variables made more generic to simplify adding similar blocks.
 *  4/6/2015 - rmcgarry
 */
public class Sand extends Block {
	/**
	 * The Type a Sand block can have
	 * @author klaue
	 */
	public enum BlockType {	SAND, RED_SAND};
	
	private static HashMap<BlockType, BufferedImage> blockImageCache = new HashMap<BlockType, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private BlockType blockType = BlockType.SAND;
	
	/**
	 * initializes the block
	 * @param data the minecraft data value (representing type/variant)
	 */
	public Sand(byte data) {
		super((short)12, data);
		this.type = Type.DIRT;		//yes, it's not dirt, but I have yet to figure out what Block.Type does
		setData(data);
	}
	
	/**
	 * initializes a plain block
	 */
	public Sand() {
		this(BlockType.SAND);
	}
	
	/**
	 * initializes a block of a specific Type
	 * @param dirtType the variant of dirt
	 */
	public Sand(BlockType blockType) {
		super((short)12);
		this.type = Type.DIRT;
		setBlockType(blockType);
	}
	
	/**
	 * Get the type of the dirt.
	 * @return the type
	 */
	public BlockType getBlockType() {
		return this.blockType;
	}

	/**
	 * Set the type of Dirt.
	 * @param dirtType this dirt variant
	 */
	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
		switch(blockType) {
			case SAND:			this.data = 0;  break;
			case RED_SAND:	this.data = 1;  break;
		}
	}
	
//	@Override
//	public String toString() {
//		return "Sand, Variant: " + this.blockType;
//	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.blockType = BlockType.SAND;			break;
			case 1:  this.blockType = BlockType.RED_SAND;		break;
			default: throw new IllegalArgumentException("illegal Sand Variant: " + this.data);
		}
		this.data = data;
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
