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
public class Vines extends MultiDirectionalBlock {
	private static HashMap<Byte, BufferedImage> vineImageCache = new HashMap<Byte, BufferedImage>();
	private static double vineZoomCache = -1;
	
	/**
	 * initializes the vine
	 * @param direction the direction(s) as a minecraft data value 
	 */
	public Vines(byte direction) {
		super((short)106, direction);
		this.type = Type.VINES;
		setData(direction);
	}
	
	/**
	 * initializes the vine
	 * @param directions the directions, valid directions are either any of N, E, S or W, or NONE (just top). 
	 */
	public Vines(HashSet<Direction> directions) {
		super((short)106);
		this.type = Type.VINES;
		setDirections(directions);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction(s): " + this.directions.toString().replaceAll("[\\[\\]]", "");
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 15) throw new IllegalArgumentException("illegal directional state: " + data);
		
		this.data = data;
		this.directions = new HashSet<Direction>();
		
		if (data == 0) {
			this.directions.add(Direction.NONE);
			return;
		}

		if ((data & 4) != 0) this.directions.add(Direction.N);
		if ((data & 8) != 0) this.directions.add(Direction.E);
		if ((data & 1) != 0) this.directions.add(Direction.S);
		if ((data & 2) != 0) this.directions.add(Direction.W);
	}

	/**
	 * sets the directions of the blocks this vine is attached to
	 * @param directions valid directions are either any of N, E, S or W, or just TOP. Note that the array should not have duplicates in it.
	 */
	@Override
	public void setDirections(HashSet<Direction> directions) {
		byte data = 0;
		if (!(directions.size() == 1 && directions.contains(Direction.NONE))) {
			for (Direction dir : directions) {
				switch(dir) {
					case N:	data += 4;	break; // 0100
					case E:	data += 8;	break; // 1000
					case S:	data += 1;	break; // 0001
					case W:	data += 2;	break; // 0010
					default: throw new IllegalArgumentException("illegal direction " + dir + " in direction array " + directions);
				}
			}
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
					case N:	newDirections.add(Direction.E);	break;
					case E:	newDirections.add(Direction.S);	break;
					case S:	newDirections.add(Direction.W);	break;
					case W:	newDirections.add(Direction.N);	break;
					default:
						// should never happen
						throw new AssertionError(this.directions);
				}
			}
		} else {
			for (Direction dir : this.directions) {
				switch (dir) {
					case N:	newDirections.add(Direction.W);	break;
					case E:	newDirections.add(Direction.N);	break;
					case S:	newDirections.add(Direction.E);	break;
					case W:	newDirections.add(Direction.S); break;
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
		
		if (vineZoomCache != zoom) {
			// reset cache
			vineImageCache.clear();
			vineZoomCache = zoom;
		} else {
			img = vineImageCache.get(this.data);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		img = addArrowsToImage(this.directions, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		vineImageCache.put(this.data, img);
		
		return img;
	}
}
