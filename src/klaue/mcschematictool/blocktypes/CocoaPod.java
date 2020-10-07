package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A cocoa pod can have different directions and sizes
 * @author klaue
 *
 */
public class CocoaPod extends DirectionalBlock {
	/**
	 * The pod size
	 * @author klaue
	 */
	public enum PodSize {
		/** small */
		SMALL,
		/** medium */
		MEDIUM,
		/** large */
		LARGE}
	
	
	private static HashMap<PodSize, HashMap<Direction, BufferedImage> > cocoaPodImageCache = new HashMap<PodSize, HashMap<Direction, BufferedImage> >();
	private static double cocoaPodZoomCache = -1;
	
	private PodSize size;
	
	/**
	 * initializes the cocoa pod
	 * @param data the block data containing direction and size
	 */
	public CocoaPod(byte data) {
		super((short)127, data);
		this.type = Type.COCOAPOD;
		this.setData(data);
	}
	
	/**
	 * initializes the cocoa pod
	 * @param size the size of the pod
	 * @param direction the direction this repeater is facing. Valid directions are N, E, S, W
	 */
	public CocoaPod(PodSize size, Direction direction) {
		super((short)127);
		this.type = Type.COCOAPOD;
		setSize(size);
		setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction;
	}

	/**
	 * Returns the size of this cocoa pod
	 * @return the size
	 */
	public PodSize getSize() {
		return this.size;
	}

	/**
	 * Set the size of this cocoa pod
	 * @param size the new size
	 */
	public void setSize(PodSize size) {
		byte sizedata;
		switch(size) {
			case MEDIUM:	sizedata = 0x4;	break;
			case LARGE:		sizedata = 0x8;	break;
			case SMALL:
			default:		sizedata = 0;	break;
		}
		this.data = (byte)((this.data & 0x3) + sizedata);
	}

	@Override
	public void setData(byte data) {
		if (data < 0 || data > 11) throw new IllegalArgumentException("data out of range: " + data);
		
		byte sizeData = (byte) (data & 0xC);
		byte dirData = (byte) (data & 0x3);
		
		switch (dirData) {
			case 0:  this.direction = Direction.N;	break;
			case 1:  this.direction = Direction.E;	break;
			case 2:  this.direction = Direction.S;	break;
			case 3:  this.direction = Direction.W;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		switch (sizeData) {
			case 0:  this.size = PodSize.SMALL;	break;
			case 4:  this.size = PodSize.MEDIUM;	break;
			case 8:  this.size = PodSize.LARGE;	break;
			default: throw new IllegalArgumentException("illegal size data: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case N:	dirData = 0;	break;
			case E:	dirData = 1;	break;
			case S:	dirData = 2;	break;
			case W:	dirData = 3;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 0xC));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	dirData = 1;	break;
				case E:	this.direction = Direction.S;	dirData = 2;	break;
				case S:	this.direction = Direction.W;	dirData = 3;	break;
				case W:	this.direction = Direction.N;	dirData = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	dirData = 3;	break;
				case E:	this.direction = Direction.N;	dirData = 0;	break;
				case S:	this.direction = Direction.E;	dirData = 1;	break;
				case W:	this.direction = Direction.S;	dirData = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data = (byte) (dirData + (this.data & 0xC));
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (cocoaPodZoomCache != zoom) {
			// reset cache
			cocoaPodImageCache.clear();
			cocoaPodZoomCache = zoom;
		} else {
			HashMap<Direction, BufferedImage> dirMap = cocoaPodImageCache.get(this.size);
			if (dirMap != null) {
				img = dirMap.get(this.direction);
			}
			
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
		
		// flip directions because they say where the pod is facing, but to show the tree it's attached to makes more sense
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
		
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (cocoaPodImageCache.containsKey(this.size)) {
			cocoaPodImageCache.get(this.size).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> dirMap = new HashMap<Direction, BufferedImage>();
			dirMap.put(this.direction, img);
			cocoaPodImageCache.put(this.size, dirMap);
		}
		
		return img;
	}
}
