package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A slab can be of different materials and be a single or a double slab
 * @author klaue
 *
 */
public class StoneSlab extends Block {
	/**
	 * The type of the slab
	 * @author klaue
	 */
	public enum SlabType {/** Stone Slab */ STONE, /** Sandstone Slab */ SANDSTONE,
						/** Wooden stone Slab */ WOOD, /** Cobblestone Slab */ COBBLESTONE,
						/** Brick Slab */ BRICK, /** Stone Brick Slab */ STONEBRICK,
						/** nether brick */ NETHERBRICK, /** quartz */ QUARTZ,
						/** smooth stone */ SMOOTHSTONE, /** smooth sandstone */ SMOOTHSANDSTONE,
						/** tile quartz */ TILEQUARTZ, PURPUR}
	

	private static HashMap<SlabType, BufferedImage> lowerSlabImageCache = new HashMap<SlabType, BufferedImage>();
	private static HashMap<SlabType, BufferedImage> upperSlabImageCache = new HashMap<SlabType, BufferedImage>();
	private static HashMap<SlabType, BufferedImage> doubleslabImageCache = new HashMap<SlabType, BufferedImage>();
	private static double slabZoomCache = -1;
	
	private SlabType slabType;
	private boolean isDoubleSlab;
	private boolean isUpperHalf;
	
	/**
	 * initializes the slab
	 * @param id the id (43 (Doubleslab) or 44 (single Slab))
	 * @param data 0-5 for different materials
	 */
	public StoneSlab(short id, byte data) {
		super(id, data);
		if (id != 43 && id != 44) {
			throw new IllegalArgumentException("illegal id for stone slabs: " + id);
		}
		this.isDoubleSlab = (id == 43);
		this.type = Type.STONESLAB;
		this.setData(data);
	}
	
	/**
	 * initializes the slab
	 * @param isDoubleSlab true for double slabs
	 * @param data 0-5 for different materials
	 */
	public StoneSlab(boolean isDoubleSlab, byte data) {
		super((short) (isDoubleSlab ? 43 : 44), data);
		this.isDoubleSlab = isDoubleSlab;
		this.type = Type.STONESLAB;
		this.setData(data);
	}
	
	/**
	 * initializes the slab
	 * @param isDoubleSlab true if this slab is a double slab
	 * @param isUpperHalf true if this slab is an inverted slab (occupying upper half of voxel)
	 * @param slabType the type of slab
	 */
	public StoneSlab(boolean isDoubleSlab, boolean isUpperHalf, SlabType slabType) {
		super((short) (isDoubleSlab ? 43 : 44));
		
		if (!isDoubleSlab) {
			if (slabType == SlabType.SMOOTHSTONE || slabType == SlabType.SMOOTHSANDSTONE || slabType == SlabType.TILEQUARTZ) {
				throw new IllegalArgumentException("Slabs of this type can only be a doubleslab: " + slabType);
			}
		}
		
		setSlabType(slabType);
		setUpperHalf(isUpperHalf);
		this.isDoubleSlab = isDoubleSlab;
		this.type = Type.STONESLAB;
	}
	
	@Override
	public void setData(byte data) {
		if (data > 15) throw new IllegalArgumentException("illegal data value for stone slabs: " + data);
		
		byte typedata = (byte) (data & 0x7);
		if (!this.isDoubleSlab) this.isUpperHalf = (data & 0x8) != 0;
		switch (typedata) {
			case 0:	this.slabType = SlabType.STONE;				break;
			case 1:	this.slabType = SlabType.SANDSTONE;			break;
			case 2:	this.slabType = SlabType.WOOD;				break;
			case 3:	this.slabType = SlabType.COBBLESTONE;		break;
			case 4:	this.slabType = SlabType.BRICK;				break;
			case 5:	this.slabType = SlabType.STONEBRICK;		break;
			case 6:	this.slabType = SlabType.NETHERBRICK;		break;
			case 7:	this.slabType = SlabType.QUARTZ;			break;
			default: throw new IllegalArgumentException("illegal data value for stone slabs: " + data);
		}
		if (this.isDoubleSlab) {
			switch (data) {
				case 8:		this.slabType = SlabType.SMOOTHSTONE;		break;
				case 9:		this.slabType = SlabType.SMOOTHSANDSTONE;	break;
				case 15:	this.slabType = SlabType.TILEQUARTZ;		break;
			}
		}
		this.data = data;
	}
	
	@Override
	protected void setId(short id) {
		if (id != 43 && id != 44) {
			throw new IllegalArgumentException("illegal id for stone slabs: " + id);
		}
		this.id = id;
		this.isDoubleSlab = (id == 43);
		
		switch(this.slabType) {
			case SMOOTHSTONE:	this.slabType = SlabType.STONE;			break;
			case SMOOTHSANDSTONE:	this.slabType = SlabType.SANDSTONE;	break;
			case TILEQUARTZ:	this.slabType = SlabType.QUARTZ;		break;
			default: 													break;
		}
	}

	/**
	 * Returns true if this slab is a double slab
	 * @return true if it is a double slab
	 */
	public boolean isDoubleSlab() {
		return this.isDoubleSlab;
	}

	/**
	 * Set if this slab is a double slab (warning, changes id)
	 * @param isDoubleSlab true if this slab should be a double slab
	 */
	public void setDoubleSlab(boolean isDoubleSlab) {
		setId((byte) (isDoubleSlab ? 43 : 44));
	}

	/**
	 * Returns true if this slab is an inverted slap (upper half of voxel instead of lower half)
	 * @return true if it is the upper half
	 */
	public boolean isUpperHalf() {
		return this.isUpperHalf;
	}

	/**
	 * Set if this slab is an inverted slap (upper half of voxel instead of lower half)
	 * @param isUpperHalf true if this slab should be an inverted slap
	 */
	public void setUpperHalf(boolean isUpperHalf) {
		if (this.isUpperHalf != isUpperHalf && !this.isDoubleSlab) {
			if (this.isUpperHalf) {
				this.data = (byte)(this.data | 8);
			} else {
				this.data = (byte)(this.data & 7); // 0111
			}
			this.isUpperHalf = isUpperHalf;
		}
	}
	
	/**
	 * @return the type of slab
	 */
	public SlabType getSlabType() {
		return this.slabType;
	}

	/**
	 * Sets the type of the slab
	 * @param slabType the slabType to set
	 */
	public void setSlabType(SlabType slabType) {
		if (!this.isDoubleSlab) {
			if (slabType == SlabType.SMOOTHSTONE || slabType == SlabType.SMOOTHSANDSTONE || slabType == SlabType.TILEQUARTZ) {
				 throw new IllegalArgumentException("slab type illegal for single slabs: " + slabType);
			}
		}
		
		byte upperhalfdata = (byte)(this.data & 0x8);
		this.slabType = slabType;
		switch (slabType) {
			case STONE:				this.data = (byte)0;	break;
			case SANDSTONE:			this.data = (byte)1;	break;
			case WOOD:				this.data = (byte)2;	break;
			case COBBLESTONE:		this.data = (byte)3;	break;
			case BRICK:				this.data = (byte)4;	break;
			case STONEBRICK:		this.data = (byte)5;	break;
			case NETHERBRICK:		this.data = (byte)6;	break;
			case QUARTZ:			this.data = (byte)7;	break;
			case SMOOTHSTONE:		this.data = (byte)8;	break;
			case SMOOTHSANDSTONE:	this.data = (byte)9;	break;
			case TILEQUARTZ:		this.data = (byte)15;	break;
		}
		
		// add half bit if not doubleslab
		if (!this.isDoubleSlab) this.data = (byte)(this.data + upperhalfdata);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (slabZoomCache != zoom) {
			// reset cache
			lowerSlabImageCache.clear();
			upperSlabImageCache.clear();
			doubleslabImageCache.clear();
			slabZoomCache = zoom;
		} else {
			if (this.isDoubleSlab) {
				img = doubleslabImageCache.get(this.slabType);
			} else {
				if (this.isUpperHalf) {
					img = upperSlabImageCache.get(this.slabType);
				} else {
					img = lowerSlabImageCache.get(this.slabType);
				}
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		switch(this.slabType) {
			case COBBLESTONE:		img = ImageProvider.getImageByBlockOrItemID((short)4);				break;
			case SANDSTONE:			img = ImageProvider.getImageByBlockOrItemID((short)24);				break;
			case STONE:				img = ImageProvider.getImage("stoneslab_side");						break; // stone doubleslab
			case WOOD:				img = ImageProvider.getImageByBlockOrItemID((short)5);				break;
			case BRICK:				img = ImageProvider.getImageByBlockOrItemID((short)45);				break;
			case STONEBRICK:		img = ImageProvider.getImageByBlockOrItemID((short)98);				break;
			case NETHERBRICK:		img = ImageProvider.getImageByBlockOrItemID((short)112);			break;
			case QUARTZ:			img = ImageProvider.getImageByBlockOrItemID((short)155,	(byte)0);	break;
			case SMOOTHSTONE:		img = ImageProvider.getImageByBlockOrItemID((short)1);				break;
			case SMOOTHSANDSTONE:	img = ImageProvider.getImageByBlockOrItemID((short)24, (byte)2);	break;
			case TILEQUARTZ:		img = ImageProvider.getImageByBlockOrItemID((short)155, (byte)0);	break;
		}
		
		if (img == null) return null;
		
		if (this.isDoubleSlab) {
			// all double slabs beside stone look just like the regular blocks
			// to make it more clear that they're doubleslabs, we add a black line in the middle
			if (this.slabType != SlabType.STONE) {
				img = ImageProvider.copyImage(img);
				Graphics2D g = img.createGraphics();
				g.setColor(Color.BLACK);
				g.drawLine(0, 7, 16, 7);
				g.drawLine(0, 8, 16, 8);
			}
		} else {
			// half the block to show it's only a slab
			img = ImageProvider.copyImage(img);
			Graphics2D g = img.createGraphics();
			g.setBackground(new Color(0x00FFFFFF, true)); // 0x00FFFFFF = 100% transparent "white"
			if (this.isUpperHalf) {
				g.clearRect(0, 8, 16, 8);
			} else {
				g.clearRect(0, 0, 16, 8);
			}
		}
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isDoubleSlab) {
			doubleslabImageCache.put(this.slabType, img);
		} else {
			if (this.isUpperHalf) {
				upperSlabImageCache.put(this.slabType, img);
			} else {
				lowerSlabImageCache.put(this.slabType, img);
			}
		}

		return img;
	}
}
