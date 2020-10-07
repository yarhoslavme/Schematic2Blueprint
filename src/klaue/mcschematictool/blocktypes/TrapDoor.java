package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A trap door can be open or closed and can have different directions
 * @author klaue
 *
 * @modified rmcgarry
 * 		Wooden Trapdoor - minecraft:trapdoor - 96
 * 		Iron Trapdoor	- minecraft:iron_trapdoor - 167  (not implemented yet)
 * 
 * 		Data Values:  small 2 bits = orientation
 * 			00	- 0	-	Placed on West side of block
 * 			01	- 1 -	South
 * 			10	- 2 - 	East
 * 			11	- 3 - 	North
 *         
 * 		0x4 bit - Open or closed
 * 	NEW - 	0x8 bit	- 1 if Placed on top half of block or 0 if bottom half of block
 */
public class TrapDoor extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> tdoorImageCache = new HashMap<Direction, BufferedImage>();
	private static double tdoorZoomCache = -1;
	
	private boolean isOpen;
	private boolean isTopHalf;		//1 if placed on top half of block, 0 if bottom
	
	/**
	 * initializes the trap door
	 * @param data the block data containing direction and open state
	 *  ADDED position
	 */
	public TrapDoor(byte data) {
		super((short)96, data);
		this.type = Type.TRAP_DOOR;
		this.setData(data);
	}
	
	/**
	 * initializes the trap door
	 * @param isOpen true if this trap door is open
	 * @param direction the direction (wall it's attached to), valid directions are N, E, S, W
	 * @param isTopHalf if the TrapDoor is attached to the top or bottom half of the block (1=top, 0=bottom)
	 */
	public TrapDoor(boolean isOpen, Direction direction, boolean isTopHalf) {
		super((short)96);
		this.isOpen = isOpen;
		this.type = Type.TRAP_DOOR;
		this.isTopHalf = isTopHalf;
		
		this.data = (byte)(isOpen ? 4 : 0);
		this.data = (byte)(isTopHalf ? 8 : 0);
		this.setDirection(direction); // sets data too
	}
	
	@Override
	public String toString() {
		String open = this.isOpen ? "open" : "closed";
		//return super.toString() + ", " + open + ", direction: " + this.direction;
		String topHalf = this.isTopHalf ? "top" : "bottom";
		return super.toString() + ", " + open + ", direction: " + this.direction + ", position: " + topHalf;
		 	
	}

	/**
	 * Returns true if this trap door is open
	 * @return true if open
	 */
	public boolean isOpen() {
		return this.isOpen;
	}

	/**
	 * Sets if this trap door is open
	 * @param isOpen true if open
	 */
	public void setOpen(boolean isOpen) {
		if (this.isOpen != isOpen) {
			if (this.isOpen) {
				// remove open bit
				this.data = (byte)(this.data & 3); // 011
			} else {
				//set open bit
				this.data = (byte)(this.data | 4);
			}
			this.isOpen = isOpen;
		}
	}

	public boolean isTopHalf() {
		return this.isTopHalf;
	}
	
	@Override
	public void setData(byte data) {
		//if (data < 0 || data > 7) throw new IllegalArgumentException("data out of range: " + data);
		if (data < 0 || data > 15) throw new IllegalArgumentException("data out of range: " + data);
		
		byte open = (byte) (data & 4); // 100(bin) if true, 000 if false
		this.isOpen = (open != 0);
		
		byte topHalf = (byte) (data & 8); //1000(bin) if top half, 0000 if false
		this.isTopHalf = (topHalf != 0);
		
		byte dirData = (byte) (data & 3); // 011
		
		switch (dirData) {
			case 0:  this.direction = Direction.S;	break;
			case 1:  this.direction = Direction.N;	break;
			case 2:  this.direction = Direction.E;	break;
			case 3:  this.direction = Direction.W;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case N:	dirData = 1;	break;
			case E:	dirData = 2;	break;
			case S: dirData = 0;	break;
			case W:	dirData = 3;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 4));
		this.direction = direction;
	}

	@Override
	public void turn(boolean CW) {
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 2;	break;
				case E:	this.direction = Direction.S;	this.data = 0;	break;
				case S:	this.direction = Direction.W;	this.data = 3;	break;
				case W:	this.direction = Direction.N;	this.data = 1;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 3;	break;
				case E:	this.direction = Direction.N;	this.data = 1;	break;
				case S:	this.direction = Direction.E;	this.data = 2;	break;
				case W:	this.direction = Direction.S;	this.data = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (tdoorZoomCache != zoom) {
			// reset cache
			tdoorImageCache.clear();
			tdoorZoomCache = zoom;
		} else {
			img = tdoorImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		tdoorImageCache.put(this.direction, img);
		return img;
	}
}
