package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A torch can have different directions
 * @author klaue
 *
 */
public class FenceGate extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> fenceGateImageCache = new HashMap<Direction, BufferedImage>();
	private static double fenceGateZoomCache = -1;
	
	private boolean isOpen = false;
	
	/**
	 * initializes the torch
	 * @param data the data containing direction and open state 
	 */
	public FenceGate(byte data) {
		super((short)107, data);
		this.type = Type.FENCEGATE;
		setData(data);
	}
	
	/**
	 * initializes the torch
	 * @param isOpen 
	 * @param direction the direction (where the torch is pointing), valid directions are N, E, S, W and None
	 */
	public FenceGate(boolean isOpen, Direction direction) {
		super((short)50);
		this.type = Type.FENCEGATE;
		setDirection(direction);
		this.isOpen = isOpen;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + (this.isOpen ? "open" : "closed") + "), opening to: " + this.direction;
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 7) throw new IllegalArgumentException("data value out of range: " + data);
		
		this.isOpen = (data & 0x4) != 0;
		byte dirData = (byte)(data & 0x3); // 011
		switch (dirData) {
			case 0:  this.direction = Direction.S;		break;
			case 1:  this.direction = Direction.W;		break;
			case 2:  this.direction = Direction.N;		break;
			case 3:  this.direction = Direction.E;		break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		switch (direction) {
			case N:		this.data = 2; break;
			case E:		this.data = 3; break;
			case S:		this.data = 0; break;
			case W:		this.data = 1; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
		this.data += (byte)(this.isOpen ? 4 : 0);
	}

	@Override
	public void turn(boolean CW) {
		if (this.direction == Direction.NONE) return;
		
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 3;	break;
				case E:	this.direction = Direction.S;	this.data = 0;	break;
				case S:	this.direction = Direction.W;	this.data = 1;	break;
				case W:	this.direction = Direction.N;	this.data = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 1;	break;
				case E:	this.direction = Direction.N;	this.data = 2;	break;
				case S:	this.direction = Direction.E;	this.data = 3;	break;
				case W:	this.direction = Direction.S;	this.data = 0;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		this.data += (byte)(this.isOpen ? 4 : 0);
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (fenceGateZoomCache != zoom) {
			// reset cache
			fenceGateImageCache.clear();
			fenceGateZoomCache = zoom;
		} else {
			img = fenceGateImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		img = addArrowToImage(this.direction, img); // will not add an arrow for "none"
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		fenceGateImageCache.put(this.direction, img);
		
		return img;
	}
}
