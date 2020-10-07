package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A tripwire hook can have different directions and be off or on
 * @author klaue
 *
 */
public class TripwireHook extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> wireHookImageCache = new HashMap<Direction, BufferedImage>();
	private static double leverZoomCache = -1;
	
	private boolean isConnected;
	private boolean isActivated;
	
	/**
	 * initializes the tripwire hook
	 * @param data the block data containing direction and connection/activation state
	 */
	public TripwireHook(byte data) {
		super((short)131, data);
		this.type = Type.TRIPWIREHOOK;
		this.setData(data);
	}
	
	/**
	 * initializes the tripwire hook
	 * @param isConnected true if this hook has a wire connected to it (middle position)
	 * @param isActivated true if this hook is activated (down position)
	 * @param direction the direction, valid directions are N, E, S, W
	 */
	public TripwireHook(boolean isConnected, boolean isActivated, Direction direction) {
		super((short)131);
		this.isConnected = isConnected;
		this.isActivated = isActivated;
		this.type = Type.TRIPWIREHOOK;
		this.data = (byte)(isConnected ? 4 : 0);
		this.data += (byte)(isActivated ? 8 : 0);
		this.setDirection(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + (this.isConnected ? "connected" : "not connected") + ", " + (this.isActivated ? "activated" : "not activated") + "), direction: " + this.direction;
	}

	/**
	 * Returns true if this hook is connected to a wire
	 * @return true if connected
	 */
	public boolean isConnected() {
		return this.isConnected;
	}

	/**
	 * Set to true to set this lever to connected state
	 * @param isConnected true if connected to wire
	 */
	public void setConnected(boolean isConnected) {
		if (this.isConnected != isConnected) {
			if (this.isConnected) {
				this.data = (byte)(this.data & 0xB); // 1011
			} else {
				this.data = (byte)(this.data | 4); // 0100
			}
			this.isConnected = isConnected;
		}
	}

	/**
	 * Returns true if this hook is triggered
	 * @return true if activated
	 */
	public boolean isActivated() {
		return this.isConnected;
	}

	/**
	 * Set to true to set this lever to activated state
	 * @param isActivated true if triggered
	 */
	public void setActivated(boolean isActivated) {
		if (this.isActivated != isActivated) {
			if (this.isActivated) {
				this.data = (byte)(this.data & 7); // 0111
			} else {
				this.data = (byte)(this.data | 8); // 1000
			}
			this.isActivated = isActivated;
		}
	}

	@Override
	public void setData(byte data) {
		if (data < 1 || data > 15) throw new IllegalArgumentException("data out of range: " + data);
		
		byte activated = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isActivated = (activated != 0);
		
		byte connected = (byte) (data & 4); // 0100(bin) if true, 0000 if false
		this.isConnected = (connected != 0);
		
		byte dirData = (byte) (data & 3);
		
		switch (dirData) {
			case 0:  this.direction = Direction.S;	break;
			case 1:  this.direction = Direction.W;	break;
			case 2:  this.direction = Direction.N;	break;
			case 3:  this.direction = Direction.E;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		switch (direction) {
			case N:	dirData = 2; break;
			case E:	dirData = 3; break;
			case S:	dirData = 0; break;
			case W:	dirData = 1; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
		this.data = (byte) (dirData + (this.data & 0xC));
	}

	@Override
	public void turn(boolean CW) {
		byte dirData = 0;
		if (CW) {
			switch (this.direction) {
				case N:	this.direction = Direction.E;	dirData = 3;	break;
				case E:	this.direction = Direction.S;	dirData = 0;	break;
				case S:	this.direction = Direction.W;	dirData = 1;	break;
				case W:	this.direction = Direction.N;	dirData = 2;	break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	dirData = 1;	break;
				case E:	this.direction = Direction.N;	dirData = 2;	break;
				case S:	this.direction = Direction.E;	dirData = 3;	break;
				case W:	this.direction = Direction.S;	dirData = 0;	break;
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
		
		if (leverZoomCache != zoom) {
			// reset cache
			wireHookImageCache.clear();
			leverZoomCache = zoom;
		} else {
			img = wireHookImageCache.get(this.direction);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;

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
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		wireHookImageCache.put(this.direction, img);
		
		return img;
	}
}
