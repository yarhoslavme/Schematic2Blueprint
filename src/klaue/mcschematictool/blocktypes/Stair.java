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
public class Stair extends DirectionalBlock {
	/**
	 * The stair type
	 * @author klaue
	 */
	public enum StairType {
		OAKWOOD,
		COBBLESTONE,
		BRICK,
		STONEBRICK,
		NETHERBRICK,
		SANDSTONE,
		SPRUCEWOOD,
		BIRCHWOOD,
		JUNGLEWOOD,
		QUARTZ,
		ACACIAWOOD,
		DARKOAKWOOD,
		REDSANDSTONE,
		PURPUR
	}

	private static HashMap<StairType, HashMap<Direction, BufferedImage> > normalStairImageCache = new HashMap<StairType, HashMap<Direction, BufferedImage> >();
	private static HashMap<StairType, HashMap<Direction, BufferedImage> > invertedStairImageCache = new HashMap<StairType, HashMap<Direction, BufferedImage> >();
	private static double stairZoomCache = -1;
	
	private StairType stairType;
	private boolean isUpsideDown = false;
	
	/**
	 * initializes the stair
	 * @param id the id of the stair (valid ids are 53 for wood and 67 for cobble)
	 * @param data the direction as a minecraft data value 
	 */
	public Stair(short id, byte data) {
		super(id, data);
		this.stairType = getStairTypeById(id);
		this.type = Type.STAIR;
		setData(data);
	}
	
	/**
	 * initializes the stair
	 * @param stairType the type of stair
	 * @param direction the direction as a minecraft data value 
	 */
	public Stair(StairType stairType, byte direction) {
		super(getStairIdByType(stairType), direction);
		this.type = Type.STAIR;
		setData(direction);
		this.stairType = stairType;
	}
	
	/**
	 * initializes the stair
	 * @param stairType the type of stair
	 * @param direction the direction, valid directions are N, E, S, and W
	 * @param isUpsideDown true if the stair is upside down
	 */
	public Stair(StairType stairType, Direction direction, boolean isUpsideDown) {
		super(getStairIdByType(stairType));
		this.type = Type.STAIR;
		setDirection(direction);
		setUpsideDown(isUpsideDown);
		this.stairType = stairType;
	}
	
	/**
	 * Function to get the proper stair ID from the stair type
	 * @param stairType
	 * @return the id
	 */
	public static short getStairIdByType(StairType stairType) {
		switch (stairType) {
			case COBBLESTONE:	return 67;
			case BRICK:			return 108;
			case STONEBRICK:	return 109;
			case NETHERBRICK:	return 114;
			case SANDSTONE:		return 128;
			case SPRUCEWOOD:	return 134;
			case BIRCHWOOD:		return 135;
			case JUNGLEWOOD:	return 136;
			case QUARTZ:		return 156;
			case ACACIAWOOD:	return 163;
			case DARKOAKWOOD:	return 164;
			case REDSANDSTONE:	return 180;
			case PURPUR:		return 203;
			case OAKWOOD:
			default:			return 53;
		}
	}
	
	/**
	 * Function to get the proper stair type from the stair id
	 * @param id
	 * @return the type
	 */
	public static StairType getStairTypeById(short id) {
		switch (id) {
			case 53:	return StairType.OAKWOOD;
			case 67:	return StairType.COBBLESTONE;
			case 108:	return StairType.BRICK;
			case 109:	return StairType.STONEBRICK;
			case 114:	return StairType.NETHERBRICK;
			case 128:	return StairType.SANDSTONE;
			case 134:	return StairType.SPRUCEWOOD;
			case 135:	return StairType.BIRCHWOOD;
			case 136:	return StairType.JUNGLEWOOD;
			case 156:	return StairType.QUARTZ;
			case 163:	return StairType.ACACIAWOOD;
			case 164:	return StairType.DARKOAKWOOD;
			case 180:	return StairType.REDSANDSTONE;
			case 203:	return StairType.PURPUR;
			
			default:	throw new IllegalArgumentException("illegal id for stairs: " + id);
		}
	}
	
	/**
	 * @return the stairType
	 */
	public StairType getStairType() {
		return this.stairType;
	}

	/**
	 * @param stairType the stairType to set
	 */
	public void setStairType(StairType stairType) {
		this.stairType = stairType;
		this.setId(getStairIdByType(stairType));
	}

	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction + ((this.isUpsideDown) ? ", upside down" : "");
	}
	
	@Override
	public void setData(byte data) {
		byte directionalData = (byte)(data & 0x3);
		switch (directionalData) {
			case 0:  this.direction = Direction.E;	break;
			case 1:  this.direction = Direction.W;	break;
			case 2:  this.direction = Direction.S;	break;
			case 3:  this.direction = Direction.N;	break;
			default: throw new IllegalArgumentException("illegal directional state for stairs: " + data);
		}
		
		if ((data & 0x4) > 0) {
			this.isUpsideDown = true;
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte newData = 0;
		switch (direction) {
			case N:	newData = 3; break;
			case E:	newData = 0; break;
			case S:	newData = 2; break;
			case W:	newData = 1; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
		this.data = (this.isUpsideDown) ? (byte)(newData | 0x4) : newData;
	}
	
	/**
	 * Returns true if this stairs are upside down stairs
	 * @return true if stairs are upside down
	 */
	public boolean isUpsideDown() {
		return this.isUpsideDown;
	}
	
	/**
	 * Sets if this stairs should be upside down
	 * @param isUpsideDown true to set upside down
	 */
	public void setUpsideDown(boolean isUpsideDown) {
		if (this.isUpsideDown != isUpsideDown) {
			if (this.isUpsideDown) {
				this.data = (byte)(this.data | 4);
			} else {
				this.data = (byte)(this.data & 3); // 011
			}
			this.isUpsideDown = isUpsideDown;
		}
	}

	@Override
	public void turn(boolean CW) {
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 0;	break;
				case E:	this.direction = Direction.S;	this.data = 2;	break;
				case S:	this.direction = Direction.W;	this.data = 1;	break;
				case W:	this.direction = Direction.N;	this.data = 3;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 1;	break;
				case E:	this.direction = Direction.N;	this.data = 3;	break;
				case S:	this.direction = Direction.E;	this.data = 0;	break;
				case W:	this.direction = Direction.S;	this.data = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
	}
	
	@Override
	protected void setId(short id) {
		this.stairType = getStairTypeById(id);
		super.setId(id);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (stairZoomCache != zoom) {
			// reset cache
			normalStairImageCache.clear();
			invertedStairImageCache.clear();
			stairZoomCache = zoom;
		} else {
			if (this.isUpsideDown) {
				HashMap<Direction, BufferedImage> directionalMap = invertedStairImageCache.get(this.stairType);
				if (directionalMap != null) {
					img = directionalMap.get(this.direction);
				}
			} else {
				HashMap<Direction, BufferedImage> directionalMap = normalStairImageCache.get(this.stairType);
				if (directionalMap != null) {
					img = directionalMap.get(this.direction);
				}
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		switch(this.stairType) {
			case COBBLESTONE:	img = ImageProvider.getImageByBlockOrItemID((short)4);				break;
			case OAKWOOD:		img = ImageProvider.getImageByBlockOrItemID((short)5);				break;
			case BRICK:			img = ImageProvider.getImageByBlockOrItemID((short)45);				break;
			case STONEBRICK:	img = ImageProvider.getImageByBlockOrItemID((short)98);				break;
			case NETHERBRICK:	img = ImageProvider.getImageByBlockOrItemID((short)112);			break;
			case SANDSTONE:		img = ImageProvider.getImageByBlockOrItemID((short)24);				break;
			case SPRUCEWOOD:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)1);	break;
			case BIRCHWOOD:		img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)2);	break;
			case JUNGLEWOOD:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)3);	break;
			case QUARTZ:		img = ImageProvider.getImageByBlockOrItemID((short)155,	(byte)0);	break;
			case ACACIAWOOD:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)4);	break;
			case DARKOAKWOOD:	img = ImageProvider.getImageByBlockOrItemID((short)5,	(byte)5);	break;
			case REDSANDSTONE:	img = ImageProvider.getImageByBlockOrItemID((short)179);	break;
			case PURPUR: 		img = ImageProvider.getImageByBlockOrItemID((short)201);
		}
		
		if (img == null) return null;
		
		// since the stairs are just like the normal blocks but with some stuff missing, cut off the edges of the block
		img = ImageProvider.copyImage(img);
		Graphics2D g = img.createGraphics();
		g.setBackground(new Color(0x00FFFFFF, true)); // 0x00FFFFFF = 100% transparent "white"
		
		// as two of the 4 possible layouts are blocked by upside-downiness, use the same image for n/e and s/w
		if (this.isUpsideDown) {
			switch (this.direction) {
				case N:
				case E:	g.clearRect(0, 8, 8, 8);	break; // cut off lower left corner
				case S:
				case W:	g.clearRect(8, 8, 8, 8);	break; // cut off lower right corner
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:
				case E:	g.clearRect(0, 0, 8, 8);	break; // cut off upper left corner
				case S:
				case W:	g.clearRect(8, 0, 8, 8);	break; // cut off upper right corner
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		g = null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isUpsideDown) {
			if (invertedStairImageCache.containsKey(this.stairType)) {
				invertedStairImageCache.get(this.stairType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				invertedStairImageCache.put(this.stairType, tempMap);
			}
		} else {
			if (normalStairImageCache.containsKey(this.stairType)) {
				normalStairImageCache.get(this.stairType).put(this.direction, img);
			} else {
				HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
				tempMap.put(this.direction, img);
				normalStairImageCache.put(this.stairType, tempMap);
			}
		}
		
		return img;
	}
}
