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
public class LargeFlower extends Block {
	/**
	 * Large Flowers - 0-5 are bottom half, 8 - 13 are top half (mask 8 to determine) 
	 * @author klaue
	 */
	public enum BlockType {
		SUNFLOWER, LILAC, DOUBLETALLGRASS, LARGEFERN, ROSEBUSH, PEONY

	};
	
	private static HashMap<BlockType, BufferedImage> blockImageCache = new HashMap<BlockType, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private BlockType flowerType = BlockType.SUNFLOWER;
	private boolean isBottomHalf;
	
	/**
	 * initializes the block block
	 * @param data the minecraft data value (representing flower type)
	 */
	public LargeFlower(byte data) {
		super((short)175, data);
		this.type = Type.FLOWER;
		setData(data);
	}
	
	/**
	 * initializes a white block block
	 */
	public LargeFlower() {
		this(BlockType.SUNFLOWER);
		
	}
	
	/**
	 * initializes a Flower block
	 * @param flowerType the type of Flower
	 */
	public LargeFlower(BlockType flowerType) {
		super((short)175);
		this.type = Type.FLOWER;
		this.isBottomHalf=false;
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
	 *
	 */
	public void setFlowerType(BlockType flowerType) {
		this.flowerType = flowerType;
		switch(flowerType) {
			case SUNFLOWER:			this.data = 0;  break;
			case LILAC:				this.data = 1;  break;
			case DOUBLETALLGRASS:	this.data = 2;  break;
			case LARGEFERN:			this.data = 3;  break;
			case ROSEBUSH:			this.data = 4;  break;
			case PEONY:				this.data = 5;  break;
		}
	}
	
//	@Override
//	public String toString() {
//		return "Block, color: " + this.flowerType;
//	}
	
	/**
	 * @return true if this block is the bottom half of the door
	 */
	public boolean isBottomHalf() {
		return this.isBottomHalf;
	}
	
	/**
	 * @param isBottomHalf true to set this block to be the bottom half of the door
	 */
	public void setBottomHalf(boolean isBottomHalf) {
		this.isBottomHalf = isBottomHalf;
		if (this.isBottomHalf != isBottomHalf) {
			if (this.isBottomHalf) {
				//set top bit
				this.data = (byte)(this.data | 8);
			} else {
				// remove top bit
				this.data = (byte)(this.data & 7); // 0111
			}
			this.isBottomHalf = isBottomHalf;
		}
	}
	
	@Override
	public void setData(byte data) {
		this.isBottomHalf = !((data & 0x8) > 0);
		byte typedata = (byte) (data & 0x7);
		switch(typedata) {
			case 0:  this.flowerType = BlockType.SUNFLOWER;			break;
			case 1:  this.flowerType = BlockType.LILAC;				break;
			case 2:  this.flowerType = BlockType.DOUBLETALLGRASS;	break;
			case 3:  this.flowerType = BlockType.LARGEFERN;			break;
			case 4:  this.flowerType = BlockType.ROSEBUSH;			break;
			case 5:  this.flowerType = BlockType.PEONY;				break;
			
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
