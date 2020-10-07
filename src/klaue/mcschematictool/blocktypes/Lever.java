package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A lever can be on the ground or on a wall, have different directions and be off or on
 * @author klaue
 *
 *		OFF											ON
 * 		000 - (0) Bottom/Ceiling, East when off		1000 - (8)
 * 		001 - (1) East								1001 - (9)
 * 		010 - (2) West								1010 - (10)
 * 		011 - (3) South								1011 - (11)
 * 		100 - (4) North								1100 - (12)
 * 		101 - (5) Top/Ground, south when off		1101 - (13)
 * 		110 - (6) Top/Ground, East when off			1110 - (14)
 * 		111 - (7) Bottom/Ceiling, south when off	1111 - (15)
 * 
 * 	4th bit determines whether it is currently switched on
 *
 */

public class Lever extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> onGroundImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> onCeilingImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> onWallImageCache = new HashMap<Direction, BufferedImage>();
	private static double leverZoomCache = -1;
	
	private boolean isThrown;
	private boolean isOnGround;
	private boolean isOnCeiling;
	
	/**
	 * initializes the lever
	 * @param data the block data containing direction and if the lever is thrown
	 */
	public Lever(byte data) {
		super((short)69, data);
		this.type = Type.LEVER;
		this.setData(data);
	}
	
	/**
	 * initializes the lever
	 * @param isThrown true if this lever is in the on-state (providing energy)
	 * @param isOnGround true if this lever is on the ground
	 * @param isOnCeiling true if this lever is on the ceiling
	 * @param direction the direction, valid directions are N, E, S, W
	 */
	public Lever(boolean isThrown, boolean isOnGround, boolean isOnCeiling, Direction direction) {
		super((short)69);
		if (isOnGround && isOnCeiling) throw new IllegalArgumentException("Lever can not be on the ground and on the ceiling at the same time");
		this.isThrown = isThrown;
		this.isOnGround = isOnGround;
		this.isOnCeiling = isOnCeiling;
		this.type = Type.LEVER;
		this.data = (byte) (isThrown ? 8 : 0);
		this.setDirection(direction); // sets the rest of the data too
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + (this.isThrown ? "on" : "off") + "), on " + (this.isOnGround ? "ground" : (this.isOnCeiling ? "ceiling" : "wall")) + ", direction: " + this.direction;
	}

	/**
	 * Returns true if this lever is thrown
	 * @return true if on
	 */
	public boolean isThrown() {
		return this.isThrown;
	}

	/**
	 * Set to true to set this lever to the on-state (providing energy)
	 * @param isThrown true if thrown
	 */
	public void setThrown(boolean isThrown) {
		if (this.isThrown != isThrown) {
			if (this.isThrown) {
				// remove thrown bit
				this.data = (byte)(this.data & 7);
			} else {
				//set thrown bit
				this.data = (byte)(this.data | 8);
			}
			this.isThrown = isThrown;
		}
	}

	@Override
	public void setData(byte data) {
		if (data < 0 || data > 15) throw new IllegalArgumentException("data out of range: " + data);
		
		byte thrown = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isThrown = (thrown != 0);
		
		byte dirData = (byte) (data & 7);
		
		if (dirData <= 4 && dirData != 0) {
			this.isOnGround = false;
			this.isOnCeiling = false;
			switch (dirData) {
				case 1:  this.direction = Direction.E;	break;
				case 2:  this.direction = Direction.W;	break;
				case 3:  this.direction = Direction.S;	break;
				case 4:  this.direction = Direction.N;	break;
				default: throw new IllegalArgumentException("illegal directional state: " + data);
			}
		} else if (dirData == 5 || dirData == 6) {
			this.isOnGround = true;
			this.isOnCeiling = false;
			switch (dirData) {
				case 5:  this.direction = Direction.S;	break;
				case 6:  this.direction = Direction.E;	break;
				default: throw new IllegalArgumentException("illegal directional state: " + data);
			}
		} else {
			this.isOnGround = false;
			this.isOnCeiling = true;
			switch (dirData) {
				case 7:  this.direction = Direction.S;	break;
				case 0:  this.direction = Direction.E;	break;
				default: throw new IllegalArgumentException("illegal directional state: " + data);
			}
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		if (this.isOnGround) {
			switch (direction) {
				case E:
				case W:	dirData = 6;	this.direction = Direction.E;	break;
				case N:
				case S:	dirData = 5;	this.direction = Direction.S;	break;
				default: throw new IllegalArgumentException("illegal direction: " + direction);
			}
		} else if (this.isOnCeiling) {
			switch (direction) {
				case E:
				case W:	dirData = 0;	this.direction = Direction.E;	break;
				case N:
				case S:	dirData = 7;	this.direction = Direction.S;	break;
				default: throw new IllegalArgumentException("illegal direction: " + direction);
			}
		} else {
			switch (direction) {
				case N:	dirData = 4; break;
				case E:	dirData = 1; break;
				case S:	dirData = 3; break;
				case W:	dirData = 2; break;
				default: throw new IllegalArgumentException("illegal direction: " + direction);
			}
		}
		this.direction = direction;
		this.data = (byte) (dirData + (this.data & 8));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		if (this.isOnGround) {
			// CW or CCW doesn't matter
			switch (this.direction) {
				case S:	this.direction = Direction.E;	dirData = 6;	break;
				case E:	this.direction = Direction.S;	dirData = 5;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else if (this.isOnCeiling) {
			// CW or CCW doesn't matter
			switch (this.direction) {
				case S:	this.direction = Direction.E;	dirData = 0;	break;
				case E:	this.direction = Direction.S;	dirData = 7;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			if (CW) {
				switch (this.direction) {
					case N:	this.direction = Direction.E;	dirData = 1;	break;
					case E:	this.direction = Direction.S;	dirData = 3;	break;
					case S:	this.direction = Direction.W;	dirData = 2;	break;
					case W:	this.direction = Direction.N;	dirData = 4;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			} else {
				switch (this.direction) {
					case N:	this.direction = Direction.W;	dirData = 2;	break;
					case E:	this.direction = Direction.N;	dirData = 4;	break;
					case S:	this.direction = Direction.E;	dirData = 1;	break;
					case W:	this.direction = Direction.S;	dirData = 3;	break;
					default:
						// should never happen
						throw new AssertionError(this.direction);
				}
			}
		}
		this.data = (byte) (dirData + (this.data & 8));
	}

	/**
	 * @return the isOnGround
	 */
	public boolean isOnGround() {
		return this.isOnGround;
	}

	/**
	 * @param isOnGround the isOnGround to set
	 */
	public void setOnGround(boolean isOnGround) {
		this.isOnGround = isOnGround;
		if (isOnGround) this.isOnCeiling = false;
		
		//reset directions (also updates data)
		setDirection(this.direction);
	}

	/**
	 * @return the isOnCeiling
	 */
	public boolean isOnCeiling() {
		return this.isOnCeiling;
	}

	/**
	 * @param isOnCeiling the isOnCeiling to set
	 */
	public void setOnCeiling(boolean isOnCeiling) {
		this.isOnCeiling = isOnCeiling;
		if (isOnCeiling) this.isOnGround = false;
		
		//reset directions (also updates data)
		setDirection(this.direction);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (leverZoomCache != zoom) {
			// reset cache
			onGroundImageCache.clear();
			onCeilingImageCache.clear();
			onWallImageCache.clear();
			leverZoomCache = zoom;
		} else {
			if (this.isOnGround) {
				img = onGroundImageCache.get(this.direction);
			} else if (this.isOnCeiling) {
				img = onCeilingImageCache.get(this.direction);
			} else {
				img = onWallImageCache.get(this.direction);
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		if (this.isOnGround || this.isOnCeiling) {
			// TODO: graphical way to know if on ceiling or ground
			// N<->S is in terrain.png
			if (this.direction == Direction.E || this.direction == Direction.W) { // W<->E
				img = ImageProvider.rotateImage(90, img);
			}
			
		} else {
			// flip directions because they say where the lever is facing, but to show the wall it's attached to makes more sense
			Direction direction;
			switch(this.direction) {
				case N: direction = Direction.S; break;
				case E: direction = Direction.W; break;
				case S: direction = Direction.N; break;
				case W: direction = Direction.E; break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
			
			img = addArrowToImage(direction, img);
		}
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isOnGround) {
			onGroundImageCache.put(this.direction, img);
		} else if (this.isOnCeiling) {
			onCeilingImageCache.put(this.direction, img);
		} else {
			onWallImageCache.put(this.direction, img);
		}
		
		return img;
	}
}
