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
public class WoodenSlab extends Block {
	private static HashMap<TreeType, BufferedImage> lowerSlabImageCache = new HashMap<TreeType, BufferedImage>();
	private static HashMap<TreeType, BufferedImage> upperSlabImageCache = new HashMap<TreeType, BufferedImage>();
	private static HashMap<TreeType, BufferedImage> doubleslabImageCache = new HashMap<TreeType, BufferedImage>();
	private static double slabZoomCache = -1;
	
	private TreeType treeType;
	private boolean isDoubleSlab;
	private boolean isUpperHalf;
	
	/**
	 * initializes the slab
	 * @param id the id (125 (Doubleslab) or 126 (single Slab))
	 * @param data 0-5 for different tree types
	 */
	public WoodenSlab(short id, byte data) {
		super(id, data);
		if (id != 125 && id != 126) {
			throw new IllegalArgumentException("illegal id for wooden slabs: " + id);
		}
		this.isDoubleSlab = (id == 125);
		this.type = Type.WOODENSLAB;
		this.setData(data);
	}
	
	/**
	 * initializes the slab
	 * @param isDoubleSlab true for double slabs
	 * @param data 0-3 for different tree types
	 */
	public WoodenSlab(boolean isDoubleSlab, byte data) {
		super((short) (isDoubleSlab ? 125 : 126), data);
		this.isDoubleSlab = isDoubleSlab;
		this.type = Type.WOODENSLAB;
		this.setData(data);
	}
	
	/**
	 * initializes the slab
	 * @param isDoubleSlab true if this slab is a double slab
	 * @param isUpperHalf true if this slab is an inverted slab (occupying upper half of voxel)
	 * @param treeType the type of slab
	 */
	public WoodenSlab(boolean isDoubleSlab, boolean isUpperHalf, TreeType treeType) {
		super((short) (isDoubleSlab ? 125 : 126));
		setTreeType(treeType);
		setUpperHalf(isUpperHalf);
		this.isDoubleSlab = isDoubleSlab;
		this.type = Type.WOODENSLAB;
	}
	
	@Override
	public void setData(byte data) {
		byte typedata = (byte) (data & 0x7);
		this.isUpperHalf = (data & 0x8) > 0;
		switch (typedata) {
			case 0:	this.treeType = TreeType.OAK;			break;
			case 1:	this.treeType = TreeType.SPRUCE;		break;
			case 2:	this.treeType = TreeType.BIRCH;			break;
			case 3:	this.treeType = TreeType.JUNGLE;		break;
			case 4:	this.treeType = TreeType.ACACIA;		break;
			case 5:	this.treeType = TreeType.DARK_OAK;		break;
			default: throw new IllegalArgumentException("illegal data value for wooden slabs: " + data);
		}
		this.data = data;
	}
	
	@Override
	protected void setId(short id) {
		if (id != 125 && id != 126) {
			throw new IllegalArgumentException("illegal id for wooden slabs: " + id);
		}
		this.id = id;
		this.isDoubleSlab = (id == 125);
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
		this.isDoubleSlab = isDoubleSlab;
		this.id = (byte) (isDoubleSlab ? 43 : 44);
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
		if (this.isUpperHalf != isUpperHalf) {
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
	public TreeType getTreeType() {
		return this.treeType;
	}

	/**
	 * Sets the type of the slab
	 * @param treeType the slab type to set
	 */
	public void setTreeType(TreeType treeType) {
		byte upperhalfdata = (byte)(this.data & 0x8);
		this.treeType = treeType;
		switch (treeType) {
			case OAK:		this.data = (byte)0;	break;
			case SPRUCE:	this.data = (byte)1;	break;
			case BIRCH:		this.data = (byte)2;	break;
			case JUNGLE:	this.data = (byte)3;	break;
			case ACACIA:	this.data = (byte)3;	break;
			case DARK_OAK:	this.data = (byte)3;	break;
		}
		this.data = (byte)(this.data + upperhalfdata);
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
				img = doubleslabImageCache.get(this.treeType);
			} else {
				if (this.isUpperHalf) {
					img = upperSlabImageCache.get(this.treeType);
				} else {
					img = lowerSlabImageCache.get(this.treeType);
				}
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		byte typedata = (byte) (this.data & 0x7);
		img = ImageProvider.getImageByBlockOrItemID((short)5, typedata); // get image of wooden planks, the type data is compatible
		
		if (img == null) return null;
		
		if (this.isDoubleSlab) {
			// all double slabs look just like the regular blocks
			// to make it more clear that they're doubleslabs, we add a black line in the middle
			img = ImageProvider.copyImage(img);
			Graphics2D g = img.createGraphics();
			g.setColor(Color.BLACK);
			g.drawLine(0, 7, 16, 7);
			g.drawLine(0, 8, 16, 8);
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
			doubleslabImageCache.put(this.treeType, img);
		} else {
			if (this.isUpperHalf) {
				upperSlabImageCache.put(this.treeType, img);
			} else {
				lowerSlabImageCache.put(this.treeType, img);
			}
		}

		return img;
	}
}
