package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A door can be open or closed, have different directions, can be a top or bottom half and can be wood or iron
 * @author klaue
 *
 */
public class Door extends DirectionalBlock {
	private static HashMap<DoorType, HashMap<Direction, BufferedImage> > topImageCache = new HashMap<DoorType, HashMap<Direction, BufferedImage> >();
	private static HashMap<DoorType, HashMap<Direction, BufferedImage> > bottomImageCache = new HashMap<DoorType, HashMap<Direction, BufferedImage> >();
	private static double doorZoomCache = -1;
	
	/**
	 * The type of the door
	 * @author klaue
	 */
	public enum DoorType {
		/** A wooden door */	WOOD,
		/** Additional wood door types **/ SPRUCE, BIRCH, JUNGLE, 	ACACIA, DARK_OAK,
		/** An iron door */		IRON
	}
	
	private DoorType doorType;
	private boolean isOpen;
	private boolean isBottomHalf;
	
	/**
	 * initializes the door
	 * @param id the blocks id. valid values are 64 (wooden door) or 71 (iron door)
	 * 	OR, 
	 * 	SPRUCE = 193
	 *  BIRCH = 194
	 *  JUNGLE = 195
	 *  ACACIA = 196
	 *  DARK_OAK = 197
	 *  
	 * @param data the block data containing direction, open state and if it's the bottom half
	 */
	public Door(short id, byte data) {
		super(id, data);
		this.doorType = getDoorTypeById(id);	
		this.type = Type.DOOR;
		this.setData(data);
	}
	
	/**
	 * initializes the door
	 * @param doorType the type of the door this is
	 * @param data the block data containing direction, open state and if it's the bottom half
	 */
	public Door(DoorType doorType, byte data) {
		super((short)getDoorIdByType(doorType), data);
		this.type = Type.DOOR;
		this.doorType = doorType;
		this.setData(data);
	}
	
	/**
	 * initializes the door
	 * @param doorType the type of the door this is
	 * @param isBottomHalf true if this is the bottom half of the door
	 * @param isOpen true if this door has swung counterclockwise around its hinge. (this is actually closed for the "wrong"
	 * 				half of a double door)
	 * @param direction the direction (Location of the hinge in the block), valid directions are NE, SE, SW, NW
	 */
	public Door(DoorType doorType, boolean isBottomHalf, boolean isOpen, Direction direction) {
		super((short)getDoorIdByType(doorType));
		this.isOpen = isOpen;
		this.isBottomHalf = isBottomHalf;
		this.doorType = doorType;
		this.type = Type.DOOR;
		
		this.data = (byte)(isBottomHalf ? 0 : 8);
		this.data += (byte)(isOpen ? 4 : 0);
		this.setDirection(direction); // sets data too
	}
	
	/**
	 * Function to get the proper stair ID from the stair type
	 * @param stairType
	 * @return the id
	 */
	public static short getDoorIdByType(DoorType doorType) {
		switch (doorType) {
			case WOOD: 		return 64;
			case IRON:		return 71;
			case SPRUCE:	return 193;
			case BIRCH:		return 194;
			case JUNGLE: 	return 195;
			case ACACIA: 	return 196;
			case DARK_OAK:	return 197;
			default:
				return 64;
		}
	}
	
	/**
	 * Function to get the proper stair type from the stair id
	 * @param id
	 * @return the type
	 */
	public static DoorType getDoorTypeById(short id) {
		switch (id) {
		case 64: 	return DoorType.WOOD;
		case 71: 	return DoorType.IRON;
		case 193: 	return DoorType.SPRUCE;
		case 194: 	return DoorType.BIRCH;
		case 195: 	return DoorType.JUNGLE;
		case 196: 	return DoorType.ACACIA;
		case 197:	return DoorType.DARK_OAK;
			default:	return DoorType.WOOD;
		}
	}
	
	@Override
	public String toString() {
		String open = this.isOpen ? "open" : "closed";
		return super.toString() + ", " + open + ", direction: " + this.direction;
	}

	/**
	 * Returns true if this door is open (swung counterclockwise around its hinge - closed for the "wrong" site of a double door)
	 * @return true if open
	 */
	public boolean isOpen() {
		return this.isOpen;
	}

	/**
	 * Set to true if this door is open (swung counterclockwise around its hinge - closed for the "wrong" site of a double door)
	 * @param isOpen true if open
	 */
	public void setOpen(boolean isOpen) {
		if (this.isOpen != isOpen) {
			if (this.isOpen) {
				// remove open bit
				this.data = (byte)(this.data & 11); // 1011
			} else {
				//set open bit
				this.data = (byte)(this.data | 4);
			}
			this.isOpen = isOpen;
		}
	}

	@Override
	public void setData(byte data) {
		if (data < 0 || data > 15) throw new IllegalArgumentException("data out of range: " + data);
		
		byte topHalf = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isBottomHalf = (topHalf == 0);
		
		byte open = (byte) (data & 4); // 0100(bin) if true, 0000 if false
		this.isOpen = (open != 0);
		
		byte dirData = (byte) (data & 3);
		
		switch (dirData) {
			case 0:  this.direction = Direction.NW;	break;
			case 1:  this.direction = Direction.NE;	break;
			case 2:  this.direction = Direction.SE;	break;
			case 3:  this.direction = Direction.SW;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case NE:	dirData = 1;	break;
			case SE:	dirData = 2;	break;
			case SW:	dirData = 3;	break;
			case NW:	dirData = 0;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 12));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		
		if (CW) {
			switch (this.direction) {
				case NE:	this.direction = Direction.SE;	dirData = 2;	break;
				case SE:	this.direction = Direction.SW;	dirData = 3;	break;
				case SW:	this.direction = Direction.NW;	dirData = 0;	break;
				case NW:	this.direction = Direction.NE;	dirData = 1;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case NE:	this.direction = Direction.NW;	dirData = 0;	break;
				case SE:	this.direction = Direction.NE;	dirData = 1;	break;
				case SW:	this.direction = Direction.SE;	dirData = 2;	break;
				case NW:	this.direction = Direction.SW;	dirData = 3;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data = (byte) (dirData + (this.data & 12));
	}

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

	/**
	 * @return the type of the door
	 */
	public DoorType getDoorType() {
		return this.doorType;
	}

	/**
	 * @param doorType the type of the door
	 */
	public void setDoorType(DoorType doorType) {
		this.doorType = doorType;
		//this.id = (byte) ((doorType == DoorType.WOOD) ? 64 : 71);
		this.id = getDoorIdByType(doorType);
		}
	
	@Override
	protected void setId(short id) {
		this.doorType = getDoorTypeById(id);
		super.setId(id);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (doorZoomCache != zoom) {
			// reset cache
			bottomImageCache.clear();
			topImageCache.clear();
			doorZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap;
			if (this.isBottomHalf) {
				directionalMap = bottomImageCache.get(this.doorType);
			} else {
				directionalMap = topImageCache.get(this.doorType);
			}
			if (directionalMap != null) {
				img = directionalMap.get(this.direction);
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isBottomHalf) {
			if (bottomImageCache.containsKey(this.doorType)) {
				bottomImageCache.get(this.doorType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				bottomImageCache.put(this.doorType, tempMap);
			}
		} else {
			if (topImageCache.containsKey(this.doorType)) {
				topImageCache.get(this.doorType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				topImageCache.put(this.doorType, tempMap);
			}
		}
		return img;
	}
}
