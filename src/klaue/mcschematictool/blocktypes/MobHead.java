package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A mob head
 * @author klaue
 *
 */
public class MobHead extends DirectionalBlock {
	/**
	 * The type of mob head
	 * @author klaue
	 */
	public enum HeadType {
		/** skeleton */ SKELETON("Skeleton skull"), /** wither */ WITHERSKELETON("Wither skeleton skull"), /** zombie */ ZOMBIE("Zombie head"),
		/** human */ HUMAN("Human head"), /** creeper */ CREEPER("Creeper head");
		
		private String name = null;
		HeadType(String name) { this.name = name; }
		@Override
		public String toString() { return this.name; }
	};
	
	private static HashMap<HeadType, HashMap<Direction, BufferedImage> > mobHeadImageCache = new HashMap<HeadType, HashMap<Direction, BufferedImage> >();
	private static double mobHeadZoomCache = -1;
	
	private boolean isWallMounted = false;
	private String name = "";
	private HeadType headType = HeadType.HUMAN;
	
	/**
	 * Constructor for use in ShematicReader, parameters are values from the tile entity, see http://www.minecraftwiki.net/wiki/Chunk_format#Tile_Entity_Format
	 * @param skullType skulltype, 0-4 with 3 = human
	 * @param rotation rotation, 0-15, only used for freestanding heads. (8=north)
	 * @param name the name of the player this head is from. null is treated as empty
	 * @param data the blocks' normal data value, 1-5, determining if the head is free standing and if not, to which wall it's attached
	 */
	public MobHead(byte skullType, byte rotation, String name, byte data) {
		super((short)144, data);
		setHeadTypeByTileEntityValue(skullType);
		
		// prematurely set !wall mounted so setDirection does not fail for directions like NNW. setData, which sets
		// the correct value, will overwrite direction for wall mounted heads anyway
		this.isWallMounted = false; 
		setDirectionByTileEntityValue(rotation);
		
		setData(data); // sets if freestanding or not and directions of wall mounted heads
		setName(name);
	}
	
	/**
	 * Makes a new mob head instance
	 * @param headType the type of head or null for human
	 * @param direction the direction the head is facing
	 * @param name The previous owner's name, null or empty string for none
	 * @param isWallMounted true if this head is on a wall (only valid directions in this case: N, E, S, W)
	 */
	public MobHead(HeadType headType, Direction direction, String name, boolean isWallMounted) {
		super((short)144);
		this.isWallMounted = isWallMounted;
		this.setName(name);
		this.setHeadType(headType);
		this.setDirection(direction);
	}
	
	/**
	 * Sets the head type by the value in the tile entities
	 * @param headType head type, 0-4
	 */
	public void setHeadTypeByTileEntityValue(byte headType) {
		switch(headType) {
			case 0:	this.headType = HeadType.SKELETON;			break;
			case 1:	this.headType = HeadType.WITHERSKELETON;	break;
			case 2:	this.headType = HeadType.ZOMBIE;			break;
			case 3:	this.headType = HeadType.HUMAN;				break;
			case 4:	this.headType = HeadType.CREEPER;			break;
			default: throw new IllegalArgumentException("Illegal head type: " + headType);
		}
	}
	
	/**
	 * Gets the head type in the right format for tile entities
	 * @return head type as tile entity number
	 */
	public byte getHeadTypeForTileEntities() {
		switch(this.headType) {
			case SKELETON:			return 0;
			case WITHERSKELETON:	return 1;
			case ZOMBIE:			return 2;
			case HUMAN:				return 3;
			case CREEPER:
			default:				return 4; // to make eclipse happy
		}
	}
	
	/**
	 * Sets the head direction by the value in the tile entities
	 * @param direction direction, 0-15. If this head is wall mounted, only directions N, E, S, W are legal
	 */
	public void setDirectionByTileEntityValue(byte direction) {
		Direction dir;
		switch(direction) {
			case 0:  dir = Direction.S;		break;
			case 1:  dir = Direction.SSW;	break;
			case 2:  dir = Direction.SW;	break;
			case 3:  dir = Direction.WSW;	break;
			case 4:  dir = Direction.W;		break;
			case 5:  dir = Direction.WNW;	break;
			case 6:  dir = Direction.NW;	break;
			case 7:  dir = Direction.NNW;	break;
			case 8:  dir = Direction.N;		break;
			case 9:  dir = Direction.NNE;	break;
			case 10: dir = Direction.NE;	break;
			case 11: dir = Direction.ENE;	break;
			case 12: dir = Direction.E;		break;
			case 13: dir = Direction.ESE;	break;
			case 14: dir = Direction.SE;	break;
			case 15: dir = Direction.SSE;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + direction);
		}
		
		setDirection(dir); // sets data and checks if valid for wall mounted heads
	}
	
	/**
	 * Gets the direction in the right format for tile entities
	 * @return direction as tile entity number
	 */
	public byte getDirectionForTileEntities() {
		switch(this.direction) {
			case S:		return 0;
			case SSW:	return 1;
			case SW:	return 2;
			case WSW:	return 3;
			case W:		return 4;
			case WNW:	return 5;
			case NW:	return 6;
			case NNW:	return 7;
			case N:		return 8;
			case NNE:	return 9;
			case NE:	return 10;
			case ENE:	return 11;
			case E:		return 12;
			case ESE:	return 13;
			case SE:	return 14;
			case SSE:
			default: 	return 15;
		}
	}
	
	/**
	 * @param headType
	 */
	public void setHeadType(HeadType headType) {
		this.headType = headType;
	}
	
	/**
	 * @return the head type
	 */
	public HeadType getHeadType() {
		return this.headType;
	}

	@Override
	public void setDirection(Direction direction) {
		// check if direction possible
		if (this.isWallMounted) {
			if (direction != Direction.N && direction != Direction.E && direction != Direction.S && direction != Direction.W) {
				throw new IllegalArgumentException("Illegal direction for wall mounted heads: " + direction);
			}
			
			// wall mounted direction is stored in data
			switch(direction) {
				case N:		this.data = 2;	break;
				case S:		this.data = 3;	break;
				case E:		this.data = 4;	break;
				case W:
				default:	this.data = 5;	break; // making eclipse a happy puppy
			}
		} else {
			// up, down and none are the only directions not supported
			if (direction == Direction.UP || direction == Direction.DOWN || direction == Direction.NONE) {
				throw new IllegalArgumentException("Illegal direction for freestanding heads: " + direction);
			}
			this.data = 1; // 1 = "look up in tile entities"
		}
		this.direction = direction;
	}

	/**
	 * Gets the former owner's name
	 * @return the name of the former owner, or an empty string if none
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name former owner of the head
	 * @param name the name or null/empty string for none
	 */
	public void setName(String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name.trim();
		}
	}
	
	@Override
	public void setData(byte data) {
		// set directory and if wall mounted
		if (data == 1) {
			// free standing - all wall mounted directions work for free standing heads
			// and the other directions come from tile entity, not data, so nothing to do
			// than to set the wall mount flag
			this.isWallMounted = false;
		} else {
			// wall mounted
			switch(data) {
				case 2: this.direction = Direction.N;	break;
				case 3: this.direction = Direction.S;	break;
				case 4: this.direction = Direction.E;	break;
				case 5: this.direction = Direction.W;	break;
				default: throw new IllegalArgumentException("Illegal data value for heads: " + data);
			}
			this.isWallMounted = true;
		}
		this.data = data;
	}

	/**
	 * Checks if this mob head is wall mounted or free standing
	 * @return true if this mob head is wall mounted
	 */
	public boolean isWallMounted() {
		return this.isWallMounted;
	}

	/**
	 * Sets if this mob head is wall mounted or free standing. Warning: will throw exception if incompatible direction!
	 * @param isWallMounted true if this mob head is wall mounted
	 */
	public void setWallMounted(boolean isWallMounted) {
		if (this.direction == Direction.N || this.direction == Direction.E || this.direction == Direction.S || this.direction == Direction.W) {
			this.isWallMounted = isWallMounted;
		} else {
			throw new IllegalArgumentException("Head cannot be changed to wall mounted, illegal direction: " + this.direction);
		}
	}
	
	@Override
	public String toString() {
		String text;
		if (this.name.isEmpty()) {
			text = this.headType.toString();
		} else {
			text = this.name + "'s head";
		}
		
		if (this.isWallMounted) {
			text += ", wall mounted";
		}
		return text + ", facing " + this.direction;
	}
	
	@Override
	public void turn(boolean CW) {
		if (this.isWallMounted) {
			// use setDirection to set data value too
			Direction newDir;
			switch(this.direction) {
				case N:	newDir = CW ? Direction.E : Direction.W;	break;
				case E:	newDir = CW ? Direction.S : Direction.N;	break;
				case S:	newDir = CW ? Direction.W : Direction.E;	break;
				case W:	newDir = CW ? Direction.N : Direction.S;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
			this.setDirection(newDir);
		} else {
			// direction is not saved to data value, just set new direction directly
			switch(this.direction) {
				case N:		this.direction = CW ? Direction.E	: Direction.W;		break;
				case NNE:	this.direction = CW ? Direction.ESE	: Direction.WNW;	break;
				case NE:	this.direction = CW ? Direction.SE	: Direction.NW;		break;
				case ENE:	this.direction = CW ? Direction.SSE	: Direction.NNW;	break;
				case E:		this.direction = CW ? Direction.S	: Direction.N;		break;
				case ESE:	this.direction = CW ? Direction.SSW	: Direction.NNE;	break;
				case SE:	this.direction = CW ? Direction.SW	: Direction.NE;		break;
				case SSE: 	this.direction = CW ? Direction.WSW	: Direction.ENE;	break;
				case S:		this.direction = CW ? Direction.W	: Direction.E;		break;
				case SSW:	this.direction = CW ? Direction.WNW	: Direction.ESE;	break;
				case SW:	this.direction = CW ? Direction.NW	: Direction.SE;		break;
				case WSW:	this.direction = CW ? Direction.NNW	: Direction.SSE;	break;
				case W:		this.direction = CW ? Direction.N	: Direction.S;		break;
				case WNW:	this.direction = CW ? Direction.NNE	: Direction.SSW;	break;
				case NW:	this.direction = CW ? Direction.NE	: Direction.SW;		break;
				case NNW:	this.direction = CW ? Direction.ENE	: Direction.WSW;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
			
			// folowing turn values are for a single step instead of 90°. maybe they're needed sometime again
			/*switch(this.direction) {
				case N:		this.direction = CW ? Direction.NNE	: Direction.NNW;	break;
				case NNE:	this.direction = CW ? Direction.NE	: Direction.N;		break;
				case NE:	this.direction = CW ? Direction.ENE	: Direction.NNE;	break;
				case ENE:	this.direction = CW ? Direction.E	: Direction.NE;		break;
				case E:		this.direction = CW ? Direction.ESE	: Direction.ENE;	break;
				case ESE:	this.direction = CW ? Direction.SE	: Direction.E;		break;
				case SE:	this.direction = CW ? Direction.SSE	: Direction.ESE;	break;
				case SSE: 	this.direction = CW ? Direction.S	: Direction.SE;		break;
				case S:		this.direction = CW ? Direction.SSW	: Direction.SSE;	break;
				case SSW:	this.direction = CW ? Direction.SW	: Direction.S;		break;
				case SW:	this.direction = CW ? Direction.WSW	: Direction.SSW;	break;
				case WSW:	this.direction = CW ? Direction.W	: Direction.SW;		break;
				case W:		this.direction = CW ? Direction.WNW	: Direction.WSW;	break;
				case WNW:	this.direction = CW ? Direction.NW	: Direction.W;		break;
				case NW:	this.direction = CW ? Direction.NNW	: Direction.WNW;	break;
				case NNW:	this.direction = CW ? Direction.N	: Direction.NW;		break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}*/
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		Direction dirForCache = this.direction;
		if (this.isWallMounted) {
			// change direction to where it's mounted on instead where it's facing (e.g. opposite direction)
			switch(this.direction) {
				case E:		dirForCache = Direction.W;	break;
				case S:		dirForCache = Direction.N;	break;
				case W:		dirForCache = Direction.E;	break;
				case N:
				default:	dirForCache = Direction.S;	break;
			}
		}
		
		if (mobHeadZoomCache != zoom) {
			// reset cache
			mobHeadImageCache.clear();
			mobHeadZoomCache = zoom;
		} else {
			if (mobHeadImageCache.containsKey(this.headType) && mobHeadImageCache.get(this.headType).containsKey(dirForCache)) {
				img = mobHeadImageCache.get(this.headType).get(dirForCache);
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		switch (this.headType) {
			case SKELETON:			img = ImageProvider.getItemImage("skull_skeleton");	break;
			case WITHERSKELETON:	img = ImageProvider.getItemImage("skull_wither");	break;
			case ZOMBIE:			img = ImageProvider.getItemImage("skull_zombie");	break;
			case CREEPER:			img = ImageProvider.getItemImage("skull_creeper");	break;
			case HUMAN:				img = ImageProvider.getItemImage("skull_char");		break;
		}
		
		if (img == null) return null;
		
		img = addArrowToImage(dirForCache, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (mobHeadImageCache.containsKey(this.headType)) {
			mobHeadImageCache.get(this.headType).put(dirForCache, img);
		} else {
			HashMap<Direction, BufferedImage> dirMap = new HashMap<Direction, BufferedImage>();
			dirMap.put(dirForCache, img);
			mobHeadImageCache.put(this.headType, dirMap);
		}
		
		return img;
	}
}
