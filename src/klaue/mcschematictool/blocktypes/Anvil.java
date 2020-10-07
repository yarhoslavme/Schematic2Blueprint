package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A anvil can have different directions and be damaged
 * @author klaue
 *
 */
public class Anvil extends DirectionalBlock {
	/**
	 * The anvil's condition
	 * @author klaue
	 */
	public enum AnvilCondition {
		/** OK condition */ MINT, /** slightly damaged */ SLIGHTLYDAMAGED("slightly damaged"), /** very damaged */ VERYDAMAGED("very damaged");
		
		private String name = null;
		AnvilCondition(){}
		AnvilCondition(String name) { this.name = name; }
		@Override
		public String toString() { return (this.name == null) ? super.toString().toLowerCase() : this.name;}
	}
	
	private static HashMap<Direction, BufferedImage> anvilImageCache = new HashMap<Direction, BufferedImage>();
	private static double anvilZoomCache = -1;
	
	private AnvilCondition condition = AnvilCondition.MINT;
	
	/**
	 * initializes the anvil
	 * @param data the block data containing direction and condition
	 */
	public Anvil(byte data) {
		super((short)145, data);
		this.type = Type.ANVIL;
		this.setData(data);
	}
	
	/**
	 * initializes the anvil
	 * @param condition the anvil's condition
	 * @param direction the direction this anvil is facing. Valid directions are N/S or E/W
	 */
	public Anvil(AnvilCondition condition, Direction direction) {
		super((short)145);
		this.type = Type.REDSTONE_REPEATER;
		setDirection(direction);
		setCondition(condition);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction: " + this.direction + ((this.condition ==  AnvilCondition.MINT) ? "" : ", condition: " + this.condition);
	}

	@Override
	public void setData(byte data) {
		// TODO when to set both damage bits? never? what about bit at 0x2?
		if (data < 0 || data > 13) throw new IllegalArgumentException("data out of range: " + data); // 1101
		
		byte dirData = (byte) (data & 1); // 0001
		
		switch (dirData) {
			case 0:  this.direction = Direction.N;	break;
			case 1:  this.direction = Direction.E;	break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		boolean veryDamaged =  (byte) (data & 8) != 0;
		boolean slightlyDamaged =  (byte) (data & 4) != 0;
		
		if (veryDamaged) {
			this.condition = AnvilCondition.VERYDAMAGED;
		} else if (slightlyDamaged) {
			this.condition = AnvilCondition.SLIGHTLYDAMAGED;
		} else {
			this.condition = AnvilCondition.MINT;
		}
		
		this.data = data;
	}
	
	/**
	 * @return the anvils condition
	 */
	public AnvilCondition getCondition() {
		return this.condition;
	}
	
	/**
	 * sets the condition of this anvil
	 * @param condition
	 */
	public void setCondition(AnvilCondition condition) {
		this.data = (byte)(this.data & 1); // delete condition bits
		
		switch(condition) {
			case VERYDAMAGED:		this.data += 8;	break; // 100x
			case SLIGHTLYDAMAGED:	this.data += 4;	break; // 010x
			case MINT:							break; // no damage bits
		}
		this.condition = condition;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		
		switch (direction) {
			case N:
			case S:	dirData = 0; this.direction = Direction.N;	break;
			case E:
			case W:	dirData = 1; this.direction = Direction.E;	break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.data = (byte) (dirData + (this.data & 14)); // 14 = 1110
	}

	@Override
	public void turn(boolean CW) {
		switch (this.direction) {
			case N:
			case S:	setDirection(Direction.E);	break;
			case E:
			case W:	setDirection(Direction.N);	break;
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (anvilZoomCache != zoom) {
			// reset cache
			anvilImageCache.clear();
			anvilZoomCache = zoom;
		} else {
			img = anvilImageCache.get(this.direction);
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
		anvilImageCache.put(this.direction, img);
		
		return img;
	}
}
