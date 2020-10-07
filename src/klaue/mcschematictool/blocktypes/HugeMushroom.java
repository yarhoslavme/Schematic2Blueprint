package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.blocktypes.DirectionalBlock.Direction;


/**
 * Vines can have different directions (walls they're attached to). A Vine block can be attached to multiple sides at once
 * @author klaue
 *
 */
public class HugeMushroom extends MultiDirectionalBlock {
	/**
	 * The type of the huge mushroom
	 * @author klaue
	 */
	public enum HugeMushroomType {
		/** Huge red mushroom */ RED, /** Huge brown mushroom */ BROWN
	}
	
	/**
	 * Type of the mushroom-block 
	 * (internally used for caching of the image representation)
	 * @author klaue
	 */
	public enum HugeMushroomBlockType {
		/** a hood piece of the mushroom type */ HOOD("hood piece"), /** a stem piece */ STEM("stem piece"), /** an internal piece */ FLESH("flesh piece");
		
		private String name = null;
		HugeMushroomBlockType(String name) { this.name = name; }
		@Override
		public String toString() { return this.name; }
	};
	
	// Byte = direction bitmap, see getImage()
	private static HashMap<HugeMushroomType, HashMap<HugeMushroomBlockType, HashMap<Byte, BufferedImage> > > hugeMushroomImageCache =
		new HashMap<HugeMushroomType, HashMap<HugeMushroomBlockType, HashMap<Byte, BufferedImage> > >();
	private static double hugeMushroomZoomCache = -1;

	private HugeMushroomBlockType blockType = HugeMushroomBlockType.FLESH;
	private HugeMushroomType mushroomType = HugeMushroomType.BROWN;
	
	/**
	 * initializes the huge mushroom
	 * @param id the id of the huge mushroom
	 * @param data the data containing information if it's a stem piece or a hood one (and which part of the hood) 
	 */
	public HugeMushroom(short id, byte data) {
		super(id, data);
		if (id != 99 && id != 100) throw new IllegalArgumentException("Id not valid for huge mushrooms");
		this.type = Type.HUGEMUSHROOM;
		this.mushroomType = (id == 99) ? HugeMushroomType.BROWN : HugeMushroomType.RED;
		setData(data);
	}
	
	/**
	 * initializes the huge mushroom
	 * @param type the type of the huge mushroom
	 * @param data the data containing information if it's a stem piece or a hood one (and which part of the hood) 
	 */
	public HugeMushroom(HugeMushroomType type, byte data) {
		super((short)((type == HugeMushroomType.BROWN) ? 99 : 100), data);
		this.mushroomType = type;
		this.type = Type.HUGEMUSHROOM;
		setData(data);
	}
	
	/**
	 * initializes the huge mushroom
	 * @param type the type of the huge mushroom
	 * @param blockType the type of mushroom block
	 * @param directions the directions in which the hood texture is used (leave it null if this is a stem or flesh block)
	 */
	public HugeMushroom(HugeMushroomType type, HugeMushroomBlockType blockType, HashSet<Direction> directions) {
		super((short)((type == HugeMushroomType.BROWN) ? 99 : 100));
		this.type = Type.HUGEMUSHROOM;
		this.blockType = blockType;
		this.mushroomType = type;
		if (this.blockType == HugeMushroomBlockType.STEM) {
			this.data = 10;
		} else if (this.blockType == HugeMushroomBlockType.FLESH) {
			this.data = 0;
		}
		
		if (this.blockType == HugeMushroomBlockType.STEM || this.blockType == HugeMushroomBlockType.FLESH) {
			this.directions = new HashSet<Direction>();
			this.directions.add(Direction.NONE);
		} else {
			setDirections(directions);
		}
	}
	
	@Override
	public String toString() {
		String retVal = super.toString() + ", " + this.blockType;
		if (this.blockType == HugeMushroomBlockType.HOOD) {
			 retVal += ", directions of texture: " + this.directions.toString().replaceAll("[\\[\\]]", "");
		}
		return retVal;
	}
	
	@Override
	public String getToolTipText() {
		/*if (this.blockType == HugeMushroomBlockType.STEM || this.blockType == HugeMushroomBlockType.FLESH) {
			return this.toString();
		}
		return super.toString() + ", hood piece";*/
		return this.toString();
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 10) throw new IllegalArgumentException("block data out of data range: " + data);
		
		this.data = data;
		this.directions = new HashSet<Direction>();
		if (data == 10) {
			this.directions.add(Direction.NONE);
			this.blockType = HugeMushroomBlockType.STEM;
			return;
		}
		
		if (data == 0) {
			this.directions.add(Direction.NONE);
			this.blockType = HugeMushroomBlockType.FLESH;
			return;
		}
		
		this.blockType = HugeMushroomBlockType.HOOD;
		this.directions.add(Direction.UP);
		switch (data) {
			case 1: // corner
				this.directions.add(Direction.N);
				this.directions.add(Direction.W);
				break;
			case 2: // side
				this.directions.add(Direction.N);
				break;
			case 3: // corner
				this.directions.add(Direction.N);
				this.directions.add(Direction.E);
				break;
			case 4: // side
				this.directions.add(Direction.W);
				break;
			case 5: // top piece w/o any sides
				break;
			case 6: // side
				this.directions.add(Direction.E);
				break;
			case 7: // corner
				this.directions.add(Direction.S);
				this.directions.add(Direction.W);
				break;
			case 8: // side
				this.directions.add(Direction.S);
				break;
			case 9: // corner
				this.directions.add(Direction.S);
				this.directions.add(Direction.E);
				break;
		}
	}

	/**
	 * sets the directions of the blocks this vine is attached to
	 * @param directions valid directions are either any of N, E, S or W, or just TOP. Note that the array should not have duplicates in it.
	 */
	@Override
	public void setDirections(HashSet<Direction> directions) {
		byte data = 0;
		
		if (directions.size() == 1 && directions.contains(Direction.NONE)
				&& (this.blockType == HugeMushroomBlockType.STEM || this.blockType == HugeMushroomBlockType.FLESH)) {
			// data/direction allready set, do nothing
			return;
		}
		
		if (directions.size() > 3) {
			throw new IllegalArgumentException("too many directions in direction array " + directions);
		}

		// since all hood blocks have a top direction, add it if not allready there
		if (!directions.contains(Direction.UP)) directions.add(Direction.UP);
		
		boolean n = directions.contains(Direction.N);
		boolean e = directions.contains(Direction.E);
		boolean s = directions.contains(Direction.S);
		boolean w = directions.contains(Direction.W);
		
		// check if the directions array only contains allowed directions
		// this code is a wee bit ugly, but I know of no better way
		int numOfDirs = 1; // "top" always there
		if (n) ++numOfDirs;
		if (e) ++numOfDirs;
		if (s) ++numOfDirs;
		if (w) ++numOfDirs;
		if (numOfDirs != directions.size()) {
			throw new IllegalArgumentException("illegal directions in direction array " + directions);
		}
		
		
		     if ( n && !e && !s &&  w) data = 1;
		else if ( n && !e && !s && !w) data = 2;
		else if ( n &&  e && !s && !w) data = 3;
		else if (!n && !e && !s &&  w) data = 4;
		else if (!n && !e && !s && !w) data = 5;
		else if (!n &&  e && !s && !w) data = 6;
		else if (!n && !e &&  s &&  w) data = 7;
		else if (!n && !e &&  s && !w) data = 8;
		else if (!n &&  e &&  s && !w) data = 9;
		else {
			throw new IllegalArgumentException("illegal directions " + directions);
		}
		
		this.directions = directions;
		this.data = data;
	}

	@Override
	public void turn(boolean CW) {
		HashSet<Direction> newDirections = new HashSet<Direction>();
		if (CW) {
			for (Direction dir : this.directions) {
				switch (dir) {
					case N:		newDirections.add(Direction.E);		break;
					case E:		newDirections.add(Direction.S);		break;
					case S:		newDirections.add(Direction.W);		break;
					case W:		newDirections.add(Direction.N);		break;
					case UP:	newDirections.add(Direction.UP);	break; // no change
					case NONE:	newDirections.add(Direction.NONE);	break; // no change
					default:
						// should never happen
						throw new AssertionError(this.directions);
				}
			}
		} else {
			for (Direction dir : this.directions) {
				switch (dir) {
					case N:		newDirections.add(Direction.W);		break;
					case E:		newDirections.add(Direction.N);		break;
					case S:		newDirections.add(Direction.E);		break;
					case W:		newDirections.add(Direction.S);		break;
					case UP:	newDirections.add(Direction.UP);	break; // no change
					case NONE:	newDirections.add(Direction.NONE);	break; // no change
					default:
						// should never happen
						throw new AssertionError(this.directions);
				}
			}
		}
		this.setDirections(newDirections); // to set the data value
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		byte directionBitmap = 0;
		// it is ensured that each direction is only in the map once
		for (Direction direction : this.directions) {
			switch(direction) {
				case N:		directionBitmap += (byte)1;		break;
				case E:		directionBitmap += (byte)2;		break;
				case S:		directionBitmap += (byte)4;		break;
				case W:		directionBitmap += (byte)8;		break;
				case UP:
				case NONE:									break; // do nothing
				default:
					// should never happen
					throw new AssertionError(this.directions);
			}
		}
		
		if (hugeMushroomZoomCache != zoom) {
			// reset cache
			hugeMushroomImageCache.clear();
			hugeMushroomZoomCache = zoom;
		} else {
			try {
				img = hugeMushroomImageCache.get(this.mushroomType).get(this.blockType).get(directionBitmap);
				if (img != null) {
					return img;
				}
			} catch (NullPointerException e) {
				// to be expected if not in cache, ignore 
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		img = addArrowsToImage(this.directions, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		// if stem or flesh, save to all mushroom types as they always look the same
		HugeMushroomType[] typesToCache;
		if (this.blockType == HugeMushroomBlockType.FLESH || this.blockType == HugeMushroomBlockType.STEM) {
			typesToCache = HugeMushroomType.values();
		} else {
			typesToCache = new HugeMushroomType[1];
			typesToCache[0] = this.mushroomType;
		}
		for (HugeMushroomType type : typesToCache) {
			// map is: map<type, map<blocktype, map<direction bitmap, image> > >
			if (!hugeMushroomImageCache.containsKey(type)) {
				HashMap<HugeMushroomBlockType, HashMap<Byte, BufferedImage> > outerTempMap = new HashMap<HugeMushroomBlockType, HashMap<Byte, BufferedImage> >();
				HashMap<Byte, BufferedImage> innerTempMap = new HashMap<Byte, BufferedImage>();
				innerTempMap.put(directionBitmap, img);
				outerTempMap.put(this.blockType, innerTempMap);
				hugeMushroomImageCache.put(type, outerTempMap);
			} else {
				if (hugeMushroomImageCache.get(type).containsKey(this.blockType)) {
					hugeMushroomImageCache.get(type).get(this.blockType).put(directionBitmap, img);
				} else {
					HashMap<Byte, BufferedImage> innerTempMap = new HashMap<Byte, BufferedImage>();
					innerTempMap.put(directionBitmap, img);
					hugeMushroomImageCache.get(type).put(this.blockType, innerTempMap);
				}
			}
		}
		
		return img;
	}
}
