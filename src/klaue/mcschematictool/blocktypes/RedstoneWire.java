package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.blocktypes.DirectionalBlock.Direction;

/**
 * A redstone wire
 * @author klaue
 */
public class RedstoneWire extends Block {
	// special hashmap because the orientation of rs-wires can only be calculated in Slice
	// first booelan is on (true), second is line (true) or "cross" (false). when not called by Slice, line defaults to false and direction to NONE
	private static HashMap<Boolean, HashMap<Boolean, HashMap<Direction, BufferedImage> > > directionalWireImageCache = 
		new HashMap<Boolean, HashMap<Boolean, HashMap<Direction, BufferedImage> > >();
	private static double wireZoomCache = -1;
	
	private boolean isLine = false;
	private Direction typeDir = Direction.NONE; // the type of the wire, see javadoc of setWireType 
	
	/**
	 * Initializes the redstone wire
	 * @param strength 0-15 where 0 is non-powered
	 */
	public RedstoneWire(byte strength) {
		super((short)55, strength);
		this.type = Type.REDSTONE_WIRE;
		if (strength < 0 || strength > 0xF) {
			throw new IllegalArgumentException("strength " + strength + "outside boundaries"); 
		}
	}

	/**
	 * Get the strength of the wire. Strength 0 is a non-powered one, strength 15 (0xF) is a wire right next to a power source
	 * @return the strength
	 */
	public byte getStrength() {
		return this.data;
	}

	/**
	 * Set the strength of the wire. Strength 0 is a non-powered one, strength 15 (0xF) is a wire right next to a power source
	 * @param strength the strength to set
	 */
	public void setStrength(byte strength) {
		if (strength < 0 || strength > 0xF) {
			throw new IllegalArgumentException("strength " + strength + "outside boundaries"); 
		}
		this.data = strength;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", strength: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setStrength(data);
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
			HashMap<Boolean, HashMap<Direction,BufferedImage> > tempOnMap = directionalWireImageCache.get(this.data != 0);
			if (tempOnMap != null) {
				HashMap<Direction, BufferedImage> tempLineMap = tempOnMap.get(this.isLine);
				if (tempLineMap != null) {
					img = tempLineMap.get(this.typeDir);
				}
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
			if (this.data != 0) {
				img = ImageProvider.getAdditionalImage("redstone_wire_line_on");
			} else {
				img = ImageProvider.getAdditionalImage("redstone_wire_line_off");
			}
		}
		
		// turn if line, cut off stuff if not line
		if (this.isLine) {
			switch (this.typeDir) {
				case N:
				case S: img = ImageProvider.rotateImage(90, img); break;
				case W:
				case E: break; // do nothing
				default:
					// should never happen
					throw new AssertionError(this.typeDir);
			}
		} else {
			if (this.typeDir != Direction.NONE) {
				// cutt off 5 pixels on the appropriate side
				img = ImageProvider.copyImage(img);
				Graphics2D g = img.createGraphics();
				g.setBackground(new Color(0x00FFFFFF, true)); // transparent - actually a transparent white
				
				if (this.typeDir == Direction.N || this.typeDir == Direction.NW || this.typeDir == Direction.NE) {
					g.clearRect(7, 0, 3, 5);
				}
				if (this.typeDir == Direction.E || this.typeDir == Direction.NE || this.typeDir == Direction.SE) {
					g.clearRect(11, 6, 5, 3);
				}
				if (this.typeDir == Direction.S || this.typeDir == Direction.SE || this.typeDir == Direction.SW) {
					g.clearRect(6, 11, 4, 5);
				}
				if (this.typeDir == Direction.W || this.typeDir == Direction.SW || this.typeDir == Direction.NW) {
					g.clearRect(0, 6, 5, 3);
				}
			}
		}
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (!directionalWireImageCache.containsKey(this.data != 0)) {
			directionalWireImageCache.put((this.data != 0), new HashMap<Boolean, HashMap<Direction,BufferedImage> >());
		}
		if (!directionalWireImageCache.get(this.data != 0).containsKey(this.isLine)) {
			directionalWireImageCache.get(this.data != 0).put(this.isLine, new HashMap<Direction,BufferedImage>());
		}
		directionalWireImageCache.get(this.data != 0).get(this.isLine).put(this.typeDir, img);
		
		return img;
	}

	/**
	 * Sets the type of the wire. Since the direction of a redstone wire is not saved into the block data,
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
				throw new IllegalArgumentException("illegal direction for redstone wires that are lines " + direction);
			}
		} else if (direction == Direction.NNW || direction == Direction.NNE || direction == Direction.ENE || direction == Direction.ESE
				|| direction == Direction.SSE || direction == Direction.SSW || direction == Direction.WSW || direction == Direction.WNW) {
			throw new IllegalArgumentException("illegal direction for redstone wires " + direction);
		}
		
		this.typeDir = direction;
	}
	
	/**
	 * Sets the type of the wire. Since the direction of a redstone wire is not saved into the block data,
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
