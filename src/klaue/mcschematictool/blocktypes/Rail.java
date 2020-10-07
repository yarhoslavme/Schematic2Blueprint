package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A rail can have different directions and can be a corner, a flat rail or an ascending rail
 * @author klaue
 *
 */
public class Rail extends DirectionalBlock {
	/**
	 * The type of the rail
	 * @author klaue
	 */
	public enum RailType {
		/** A flat rail */									FLAT,
		/** A corner */										CORNER,
		/** A flat rail that is ascending to one site */	ASCENDING;
		
		@Override
		public String toString() { return super.toString().toLowerCase();}
	};
	
	private static HashMap<RailType, HashMap<Direction, BufferedImage> > railImageCache = new HashMap<RailType, HashMap<Direction, BufferedImage> >();
	private static double railZoomCache = -1;
	
	protected RailType railType;
	
	/**
	 * initializes the rail
	 * @param data
	 */
	public Rail(byte data) {
		super((short)66, data);
		this.type = Type.RAIL;
		setData(data);
	}
	
	/**
	 * ctor for subclasses, just forwards to superclass constructor
	 * @param id
	 * @param data
	 */
	protected Rail(short id, byte data) {
		super(id, data);
	}
	
	/**
	 * ctor for subclasses, just forwards to superclass constructor
	 */
	protected Rail() {
		super();
	}
	
	/**
	 * initializes the rail
	 * @param type The type of rail
	 * @param direction the direction (N, W for flat rails, N, E, S, W for ascending rails, NE, SE, SW, NW for corners)
	 */
	public Rail(RailType type, Direction direction) {
		this.railType = type;
		this.type = Type.RAIL;
		setDirection(direction);
	}
	
	/**
	 * @return true if the rail is a corner
	 */
	public boolean isCorner() {
		return (this.railType == RailType.CORNER);
	}

	/**
	 * @return true if the rail is flat
	 */
	public boolean isFlat() {
		return (this.railType == RailType.FLAT);
	}

	/**
	 * @return true if the rail is ascending
	 */
	public boolean isAscending() {
		return (this.railType == RailType.ASCENDING);
	}

	/**
	 * @return the type of rail
	 */
	public RailType getRailType() {
		return this.railType;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + this.railType + ", direction: " + this.direction;
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 9) {
			throw new IllegalArgumentException("data value out of range: " + data);
		}
		
		if (data <= 1) {
			switch (data) {
				case 0:  this.direction = Direction.N;	break;
				case 1:  this.direction = Direction.W;	break;
			}
			this.railType = RailType.FLAT;
		} else if (data <= 5) {
			switch (data) {
				case 2:  this.direction = Direction.E;	break;
				case 3:  this.direction = Direction.W;	break;
				case 4:  this.direction = Direction.N;	break;
				case 5:  this.direction = Direction.S;	break;
			}
			this.railType = RailType.ASCENDING;
		} else {
			switch (data) {
				case 6:  this.direction = Direction.NW;	break;
				case 7:  this.direction = Direction.NE;	break;
				case 8:  this.direction = Direction.SE;	break;
				case 9:  this.direction = Direction.SW;	break;
			}
			this.railType = RailType.CORNER;
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		if (this.railType == RailType.FLAT) {
			switch (direction) {
				case N:
				case S:	this.data = 0; this.direction = Direction.N;	break;
				case E:
				case W:	this.data = 1; this.direction = Direction.W;	break;
				default: throw new IllegalArgumentException("illegal direction for flat rails: " + direction);
			}
		} else if (this.railType == RailType.CORNER) {
			switch (direction) {
				case NE:  this.data = 7;	break;
				case SE:  this.data = 8;	break;
				case SW:  this.data = 9;	break;
				case NW:  this.data = 6;	break;
				default: throw new IllegalArgumentException("illegal direction for corner rails: " + direction);
			}
			this.direction = direction;
		} else {
			switch (direction) {
				case N:	this.data = 4;	break;
				case E:	this.data = 2;	break;
				case S:	this.data = 5;	break;
				case W:	this.data = 3;	break;
				default: throw new IllegalArgumentException("illegal direction for ascending rails: " + direction);
			}
			this.direction = direction;
		}
	}

	@Override
	public void turn(boolean CW) {
		if (this.railType == RailType.FLAT) {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 1;	break;
				case W:	this.direction = Direction.N;	this.data = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else if (this.railType == RailType.CORNER) {
			if (CW) {
				switch (this.direction) {
					case NE:	this.direction = Direction.SE;	this.data = 8;	break;
					case SE:	this.direction = Direction.SW;	this.data = 9;	break;
					case SW:	this.direction = Direction.NW;	this.data = 6;	break;
					case NW:	this.direction = Direction.NE;	this.data = 7;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			} else {
				switch (this.direction) {
					case NE:	this.direction = Direction.NW;	this.data = 6;	break;
					case SE:	this.direction = Direction.NE;	this.data = 7;	break;
					case SW:	this.direction = Direction.SE;	this.data = 8;	break;
					case NW:	this.direction = Direction.SW;	this.data = 9;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			}
		} else {
			// ascending rails
			if (CW) {
				switch (this.direction) {
					case N:	this.direction = Direction.E;	this.data = 2;	break;
					case E:	this.direction = Direction.S;	this.data = 5;	break;
					case S:	this.direction = Direction.W;	this.data = 3;	break;
					case W:	this.direction = Direction.N;	this.data = 4;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			} else {
				switch (this.direction) {
					case N:	this.direction = Direction.W;	this.data = 3;	break;
					case E:	this.direction = Direction.N;	this.data = 4;	break;
					case S:	this.direction = Direction.E;	this.data = 2;	break;
					case W:	this.direction = Direction.S;	this.data = 5;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (railZoomCache != zoom) {
			// reset cache
			railImageCache.clear();
			railZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> directionalMap = railImageCache.get(this.railType);
				
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
		
		// turn
		double angle = 0;
		if (this.railType == RailType.CORNER) {
			switch(this.direction) {
				case NE: angle = 90;  break;
				case SE: angle = 180; break;
				case SW: angle = 270; break;
				case NW: angle = 0;   break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch(this.direction) {
				case N:
				case S: angle = 0;  break;
				case E:
				case W: angle = 90; break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		
		img = ImageProvider.rotateImage(angle, img);
		
		// add arrows to ascending rails
		if (this.railType == RailType.ASCENDING) {
			img = addArrowToImage(this.direction, img);
		}
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (railImageCache.containsKey(this.railType)) {
			railImageCache.get(this.railType).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> tempMap = new HashMap<Direction, BufferedImage>();
			tempMap.put(this.direction, img);
			railImageCache.put(this.railType, tempMap);
		}
		return img;
	}
}
