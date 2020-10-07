package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Stair can have different directions
 * @author klaue
 *
 */
public class Fence extends Block {
	/**
	 * The stair type
	 * @author klaue
	 */
	public enum FenceType {		
		OAK, SPRUCE, BIRCH, JUNGLE, DARK_OAK, ACACIA	
	}

	private static HashMap<FenceType, BufferedImage> fenceImageCache = new HashMap<FenceType, BufferedImage>();
	private static double fenceZoomCache = -1;
	
	private FenceType fenceType;
	
	/**
	 * initializes the stair
	 * @param id the id of the stair (valid ids are 53 for wood and 67 for cobble)
	 * @param data the direction as a minecraft data value 
	 */
	public Fence(short id, byte data) {
		super(id, data);
		this.fenceType = getFenceTypeById(id);
		this.type = Type.FENCE;
		setData(data);
	}
	
	/**
	 * initializes the stair
	 * @param fenceType the type of stair
	 * @param direction the direction as a minecraft data value 
	 */
	public Fence(FenceType fenceType) {
		super(getFenceIdByType(fenceType));
		this.type = Type.FENCE;
		this.fenceType = fenceType;
	}
	
	/**
	 * Function to get the proper stair ID from the stair type
	 * @param fenceType
	 * @return the id
	 */
	public static short getFenceIdByType(FenceType fenceType) {
		switch (fenceType) {
			case SPRUCE:	return 188;
			case BIRCH:		return 189;
			case JUNGLE:	return 190;
			case DARK_OAK:	return 191;
			case ACACIA:	return 192;
			case OAK:
			default:		return 85;
		}
	}
	
	/**
	 * Function to get the proper stair type from the stair id
	 * @param id
	 * @return the type
	 */
	public static FenceType getFenceTypeById(short id) {		
		switch (id) {
			case 85:	return FenceType.OAK;
			case 188:	return FenceType.SPRUCE;
			case 189:	return FenceType.BIRCH;
			case 190:	return FenceType.JUNGLE;
			case 191:	return FenceType.DARK_OAK;
			case 192:	return FenceType.ACACIA;		
			default:	throw new IllegalArgumentException("illegal id for fences: " + id);
		}
	}
	
	/**
	 * @return the fenceType
	 */
	public FenceType getFenceType() {
		return this.fenceType;
	}

	/**
	 * @param fenceType the fenceType to set
	 */
	public void setFenceType(FenceType fenceType) {
		this.fenceType = fenceType;
		this.setId(getFenceIdByType(fenceType));
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public void setData(byte data) {

		this.data = data;
	}

	@Override
	protected void setId(short id) {
		this.fenceType = getFenceTypeById(id);
		super.setId(id);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (fenceZoomCache != zoom) {
			// reset cache
			fenceImageCache.clear();
			fenceZoomCache = zoom;
		} else {
			img = fenceImageCache.get(this.fenceType);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		switch(this.fenceType) {
			case OAK:		img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)0);	break;
			case SPRUCE:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)1);	break;
			case BIRCH:		img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)2);	break;
			case JUNGLE:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)3);	break;
			case ACACIA:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)4);	break;
			case DARK_OAK:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)5);	break;
		}
		
		if (img == null) return null;
		
		// Fences are derived from the wood blocks, but with three rectangles cut out, to look like a fence.
		img = ImageProvider.copyImage(img);
		Graphics2D g = img.createGraphics();
		g.setBackground(new Color(0x00FFFFFF, true)); // 0x00FFFFFF = 100% transparent "white"
		
		g.clearRect(3, 0, 10, 1);
		g.clearRect(3, 4, 10, 4);
		g.clearRect(3, 11, 10, 5);
		g = null;
				
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		fenceImageCache.put(this.fenceType, img);
		
		
		return img;
	}
}
