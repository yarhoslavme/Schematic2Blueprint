package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Block block that can be in different states of growth
 * @author klaue
 *
 */
public class RedFlower extends Block {
	/**
	 * Red Flowers - Formerly Rose, now contains all flower types except for Dandelion.
	 * @author klaue
	 */
	public enum BlockType {
		POPPY, BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP, PINK_TULIP, OXEYE_DAISY

	};
	
	private static HashMap<BlockType, BufferedImage> blockImageCache = new HashMap<BlockType, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private BlockType flowerType = BlockType.POPPY;
	
	/**
	 * initializes the block block
	 * @param data the minecraft data value (representing flower type)
	 */
	public RedFlower(byte data) {
		super((short)38, data);
		this.type = Type.FLOWER;
		setData(data);
	}
	
	/**
	 * initializes a white block block
	 */
	public RedFlower() {
		this(BlockType.POPPY);
	}
	
	/**
	 * initializes a colored block block
	 * @param color the color this block should be dyed in
	 */
	public RedFlower(BlockType flowerType) {
		super((short)38);
		this.type = Type.FLOWER;
		setFlowerType(flowerType);
	}
	
	/**
	 * Get the color of the block.
	 * @return the color
	 */
	public BlockType getFlowerType() {
		return this.flowerType;
	}

	/**
	 * Set the color of the wool.
	 * @param color the color to set
	 */
	public void setFlowerType(BlockType flowerType) {
		this.flowerType = flowerType;
		switch(flowerType) {
			case POPPY:				this.data = 0;  break;
			case BLUE_ORCHID:		this.data = 1;  break;
			case ALLIUM:			this.data = 2;  break;
			case AZURE_BLUET:		this.data = 3;  break;
			case RED_TULIP:			this.data = 4;  break;
			case ORANGE_TULIP:		this.data = 5;  break;
			case WHITE_TULIP:		this.data = 6;  break;
			case PINK_TULIP:		this.data = 7;  break;
			case OXEYE_DAISY:		this.data = 8;  break;
		}
	}
	
//	@Override
//	public String toString() {
//		return "Block, color: " + this.flowerType;
//	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.flowerType = BlockType.POPPY;			break;
			case 1:  this.flowerType = BlockType.BLUE_ORCHID;	break;
			case 2:  this.flowerType = BlockType.ALLIUM;		break;
			case 3:  this.flowerType = BlockType.AZURE_BLUET;	break;
			case 4:  this.flowerType = BlockType.RED_TULIP;		break;
			case 5:  this.flowerType = BlockType.ORANGE_TULIP;	break;
			case 6:  this.flowerType = BlockType.WHITE_TULIP;	break;
			case 7:  this.flowerType = BlockType.PINK_TULIP;	break;
			case 8:  this.flowerType = BlockType.OXEYE_DAISY;	break;
			
			default: throw new IllegalArgumentException("Illegal flowerType: " + this.data);
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
			img = blockImageCache.get(this.flowerType);
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
		blockImageCache.put(this.flowerType, img);
		
		return img;
	}
}
