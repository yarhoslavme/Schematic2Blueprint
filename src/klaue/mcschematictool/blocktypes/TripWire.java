package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.blocktypes.DirectionalBlock.Direction;

/**
 * A tripwire
 * @author klaue
 */
public class TripWire extends Block {
	// special hashmap because the orientation of tripwires can only be calculated in Slice
	// first boolean is line (true) or "cross" (false). when not called by Slice, line defaults to true and direction to E
	private static HashMap<Boolean, HashMap<Direction, BufferedImage> > directionalWireImageCache = 
		new HashMap<Boolean, HashMap<Direction, BufferedImage> >();
	private static double wireZoomCache = -1;
	
	private boolean isLine = true;
	private boolean isActivated = false;
	private boolean isTrigger = false;
	private boolean isSuspended = false;
	private Direction typeDir = Direction.E; // the type of the wire, see javadoc of setWireType 
	
	/**
	 * Initializes the tripwire
	 * @param data 0 = not activated, bit 0x4 = whole tripwire activated, bit 0x1 = entity on this tripwire piece 
	 */
	public TripWire(byte data) {
		super((short)132, data);
		this.type = Type.TRIPWIRE;
		this.setData(data);
	}
	
	/**
	 * @return true if this wire is activated
	 */
	public boolean isActivated() {
		return this.isActivated;
	}
	
	/**
	 * Sets this wires activated state. Note that this does not update the whole wire, just this piece.
	 * @param isActivated true to activate this wire piece
	 */
	public void setActivated(boolean isActivated) {
		if (this.isActivated != isActivated) {
			if (this.isActivated) {
				this.data = (byte)(this.data | 4); // 100
			} else {
				this.data = (byte)(this.data & 3); // 011
			}
			this.isActivated = isActivated;
		}
	}
	
	/**
	 * @return true if this wire piece is the trigger for the wire activation, e.g. if there is something on this wire
	 */
	public boolean isTrigger() {
		return this.isTrigger;
	}
	public boolean isSuspended() {
		return this.isSuspended;
	}
	
	/**
	 * Sets this wires trigger state. Trigger means there's something on this wire (this wire was responsible for activating the rest of the wire)
	 * @param isTrigger true to set this wire piece as the trigger
	 */
	public void setTrigger(boolean isTrigger) {
		if (this.isTrigger != isTrigger) {
			if (this.isTrigger) {
				this.data = (byte)(this.data | 1); // 001
			} else {
				this.data = (byte)(this.data & 6); // 110
			}
			this.isTrigger = isTrigger;
		}
	}

	@Override
	public void setData(byte data) {
		// 1 - activated
		// 2 - suspended
		// 4 - attached
		// 8 - disarmed
		
		if (data != 0 && data != 1 && data != 2 && data != 4 && data != 5) throw new IllegalArgumentException("illegal data for Tripwires: " + data);
		
		this.isActivated = (data & 0x4) > 0;
		this.isSuspended = (data & 0x2) > 0;
		this.isTrigger = (data & 0x1) > 0;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return super.toString() 
				+ " (" + (this.isActivated ? "activated" : "not activated") 
				+ ", " 
				+ (this.isTrigger ? "is trigger" : "not a trigger")
				+ ", "
				+ (this.isSuspended ? "suspended" : "not suspended")
				+ ")";
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (wireZoomCache != zoom) {
			// reset cache
			directionalWireImageCache.clear();
			wireZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> tempLineMap = directionalWireImageCache.get(this.isLine);
			if (tempLineMap != null) {
				img = tempLineMap.get(this.typeDir);
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		if (!this.isLine) {
			img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		} else {
			img = ImageProvider.getAdditionalImage("tripwire_line");
		}
		
		// turn if line, cut off stuff if not line
		if (this.isLine) {
			switch (this.typeDir) {
				case N:
				case S: break; // do nothing
				case W:
				case E: img = ImageProvider.rotateImage(90, img); break;
				default:
					// should never happen
					throw new AssertionError(this.typeDir);
			}
		} else {
			if (this.typeDir != Direction.NONE) {
				// cut off the appropriate sides
				img = ImageProvider.copyImage(img);
				Graphics2D g = img.createGraphics();
				g.setBackground(new Color(0x00FFFFFF, true)); // transparent - actually a transparent white
				
				if (this.typeDir == Direction.N || this.typeDir == Direction.NW || this.typeDir == Direction.NE) {
					g.clearRect(0, 0, 16, 7); // cut off top
				}
				if (this.typeDir == Direction.E || this.typeDir == Direction.NE || this.typeDir == Direction.SE) {
					g.clearRect(9, 0, 7, 16); // cut off right
				}
				if (this.typeDir == Direction.S || this.typeDir == Direction.SE || this.typeDir == Direction.SW) {
					g.clearRect(0, 9, 16, 7); // cut off bottom
				}
				if (this.typeDir == Direction.W || this.typeDir == Direction.SW || this.typeDir == Direction.NW) {
					g.clearRect(0, 0, 7, 16);  // cut off left
				}
			}
		}
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (!directionalWireImageCache.containsKey(this.isLine)) {
			directionalWireImageCache.put(this.isLine, new HashMap<Direction,BufferedImage>());
		}
		directionalWireImageCache.get(this.isLine).put(this.typeDir, img);
		
		return img;
	}

	/**
	 * Sets the type of the wire. Since the direction of a tripwire is not saved into the block data,
	 * this should be called once the wires neighbors are known, see param direction
	 * @param isLine true if this wire is a line, e.g. if the neighbors form a straight line
	 * @param direction the direction. For non-line wires, this is the direction where the wire is in a 3x3 grid of wires. For example, a corner from
	 * 					bottom to the right would be in the top-left corner of the grid and therefore the direction would be north-west.
	 * 					A cross-like intersection would be in the middle, which is direction "none". For lines, this is the direction they are facing,
	 * 					a line from bottom to top would be north or south, a line from left to right would be west or east.<br>
	 * 					Note that this direction is not the direction inside the minecraft world but just used to determine the image. Therefore, north
	 * 					is always at the top of the 3x3 wire grid
	 */
	public void setWireType(boolean isLine, Direction direction) {
		this.isLine = isLine;
		if (isLine) {
			if (direction != Direction.N && direction != Direction.E && direction != Direction.S && direction != Direction.W) {
				throw new IllegalArgumentException("illegal direction for tripwires that are lines " + direction);
			}
		} else if (direction == Direction.NNW || direction == Direction.NNE || direction == Direction.ENE || direction == Direction.ESE
				|| direction == Direction.SSE || direction == Direction.SSW || direction == Direction.WSW || direction == Direction.WNW) {
			throw new IllegalArgumentException("illegal direction for tripwires " + direction);
		}
		
		this.typeDir = direction;
	}
	
	/**
	 * Sets the type of the wire. Since the direction of a tripwire is not saved into the block data,
	 * this should be called once the wires neighbors are known, see boolean params
	 * @param isWireInNorth true if there's another wire (or a power source) in the north of this wire
	 * @param isWireInEast true if there's another wire (or a power source) in the east of this wire
	 * @param isWireInSouth true if there's another wire (or a power source) in the south of this wire
	 * @param isWireInWest true if there's another wire (or a power source) in the west of this wire
	 */
	public void setWireType(boolean isWireInNorth, boolean isWireInEast, boolean isWireInSouth, boolean isWireInWest) {
		// if wires in all or in no directions
		if ((isWireInNorth && isWireInEast && isWireInSouth && isWireInWest)
				|| (!isWireInNorth && !isWireInEast && !isWireInSouth && !isWireInWest)) {
			this.setWireType(false, Direction.NONE);
			return;
		}
		
		if ((isWireInWest || isWireInEast) && (!isWireInNorth && !isWireInSouth))	this.setWireType(true, Direction.E);
		if ((isWireInNorth || isWireInSouth) && (!isWireInWest && !isWireInEast))	this.setWireType(true, Direction.S);
		if (isWireInSouth && isWireInEast && !isWireInWest && !isWireInNorth)		this.setWireType(false, Direction.NW);
		if (isWireInSouth && isWireInWest && !isWireInEast && !isWireInNorth)		this.setWireType(false, Direction.NE);
		if (isWireInNorth && isWireInWest && !isWireInEast && !isWireInSouth)		this.setWireType(false, Direction.SE);
		if (isWireInNorth && isWireInEast && !isWireInWest && !isWireInSouth)		this.setWireType(false, Direction.SW);
		if (isWireInWest && isWireInSouth && isWireInEast && !isWireInNorth)		this.setWireType(false, Direction.N);
		if (isWireInWest && isWireInSouth && isWireInNorth && !isWireInEast)		this.setWireType(false, Direction.E);
		if (isWireInWest && isWireInNorth && isWireInEast && !isWireInSouth)		this.setWireType(false, Direction.S);
		if (isWireInNorth && isWireInSouth && isWireInEast && !isWireInWest)		this.setWireType(false, Direction.W);
	}
}
