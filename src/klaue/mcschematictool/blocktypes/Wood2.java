package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.blocktypes.TreeType;

/**
 * A Wood2 block that can be from different types of Tree
 * @author klaue
 *
 *
 *  Tree Types 0-3 are going to be ID 17 (minecraft:log), Wood.java
 *  	OAK, SPRUCE,  BIRCH, JUNGLE
 * 	Types 4-7 are ID 162 (minecraft:log2) -- Wood2.java
 * 		ACACIA, DARK_OAK
 *
 */
public class Wood2 extends DirectionalBlock {
	private static HashMap<TreeType, HashMap<Direction, BufferedImage> > woodImageCache = new HashMap<TreeType, HashMap<Direction, BufferedImage> >();
	private static double woodZoomCache = -1;
	
	private TreeType treeType = TreeType.OAK;
	
	/**
	 * Initializes the wood block
	 * @param data the block data
	 */
	public Wood2(byte data) {
		super((short)162, data);
		this.type = Type.WOOD;
		setData(data);
	}
	
	/**
	 * Initializes the wood block
	 * @param type The Tree Type to use
	 */
	public Wood2(TreeType type) {
		super((short)162);
		this.type = Type.WOOD;
		setTreeType(type);
	}

	/**
	 * Set the wood type
	 * @param type
	 */
	public void setTreeType(TreeType type) {
		byte typeData = 0;
		switch (type) {
			case ACACIA:	typeData = 0; break; // these are 
			case DARK_OAK:	typeData = 1; break; // in Wood2.java 
			case OAK:		typeData = 2; break; // these are in wood.java
			case SPRUCE:	typeData = 3; break; // will move them into Wood.java eventually
			case BIRCH:		typeData = 4; break;
			case JUNGLE:	typeData = 5; break;
			
		}
		this.treeType = type;
		this.data = (byte)((this.data & 0xC) + typeData);
	}

	/**
	 * Get the wood type
	 * @return the type of tree
	 */
	public TreeType getTreeType() {
		return this.treeType;
	}

	//	@Override
	//	public String toString() {
	//		return "Wood, type: " + this.type;
	//	}
	
	@Override
	public void setData(byte data) {
		byte typeData = (byte)(data & 0x3);
		switch (typeData) {
			case 0: this.treeType = TreeType.ACACIA;	break;
			case 1: this.treeType = TreeType.DARK_OAK;	break;

			case 2: this.treeType = TreeType.OAK;		break;
			case 3: this.treeType = TreeType.SPRUCE;	break;
			case 4: this.treeType = TreeType.BIRCH;		break;
			case 5: this.treeType = TreeType.JUNGLE;	break;
			default: throw new IllegalArgumentException("illegal tree type: " + typeData);
		}
		
		byte directionData = (byte)(data >> 2);
		switch (directionData) {
			case 1:		this.direction = Direction.E;		break;
			case 2:		this.direction = Direction.N;		break;
			case 3:		this.direction = Direction.NONE;	break;
			case 0:
			default:	this.direction = Direction.DOWN;	break; // normal log
		}
		// direction 3 is in the wiki as "Directionless; all faces have the appropriate bark texture"
		
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (woodZoomCache != zoom) {
			// reset cache
			woodImageCache.clear();
			woodZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> dirMap = woodImageCache.get(this.treeType);
			if (dirMap != null) {
				img = dirMap.get(this.treeType);
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		if (this.direction != Direction.NONE && this.direction != Direction.DOWN) {
			img = addArrowToImage(this.direction, img);
		}
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (woodImageCache.containsKey(this.treeType)) {
			woodImageCache.get(this.treeType).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> dirMap = new HashMap<Direction, BufferedImage>();
			dirMap.put(this.direction, img);
			woodImageCache.put(this.treeType, dirMap);
		}
		
		return img;
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		switch (this.direction) {
			case N:
			case S:	this.direction = Direction.E;	dirData = 4;	break;
			case E:
			case W:	this.direction = Direction.N;	dirData = 8;	break;
			case DOWN:
			case NONE: break; // no turning
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
		
		if (this.direction != Direction.NONE && this.direction != Direction.DOWN) {
			this.data = (byte)(dirData + (this.data & 3));
		}
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case N:
			case S:		dirData =   8; this.direction = Direction.N;	break;
			case E:
			case W:		dirData =   4; this.direction = Direction.E;	break;
			case DOWN:
			case UP:	dirData =   0; this.direction = Direction.DOWN;	break;
			case NONE:	dirData = 0xC; this.direction = Direction.NONE;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 3));
	}
}
