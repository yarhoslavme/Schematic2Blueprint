package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JToolTip;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.ImageToolTip;
import klaue.mcschematictool.itemtypes.Item;

/**
 * A dispenser is a block that contains items
 * @author klaue
 *
 */
public class Dispenser extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> dispenserImageCache = new HashMap<Direction, BufferedImage>();
	private static double dispenserZoomCache = -1;
	
	/**
	 * An array of exactly 9 items corresponding to the 9 slots of a dispenser.
	 * Empty slots are Items with data = 0
	 */
	public Item[] content = null;
	
	/**
	 * Initializes the dispenser. The new dispenser will contain exactly 9 items, some or all of which may be zero (empty)
	 * @param content The dispensers content or null for an emty dispenser
	 * @param direction The dispenser's data (the direction it's facing)
	 */
	public Dispenser(Item[] content, byte direction) {
		super((short)23, direction);
		this.type = Type.DISPENSER;
		Item[] chestcontent = content;
		if (chestcontent == null || chestcontent.length == 0) {
			// empty chest
			chestcontent = new Item[9];
			Arrays.fill(chestcontent, new Item());
		}
		
		if (chestcontent.length > 9) {
			throw new IllegalArgumentException(super.toString() + " can only have up to 9 items");
		}
		
		if (chestcontent.length == 9) {
			this.content = chestcontent;
		} else {
			this.content = new Item[9];
			Arrays.fill(this.content, new Item());
			for (int i = 0; i < chestcontent.length; ++i) {
				this.content[i] = chestcontent[i];
			}
		}
		
		this.setData(direction);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", contents: " + Arrays.deepToString(this.content) + ", direction " + this.direction;
	}
	
	@Override
	public void turn(boolean CW) {
		switch(this.direction) {
			case N:	this.data = (byte) ((CW) ? 5 : 4);
				this.direction = (CW) ? Direction.E : Direction.W;
				break;
			case E:	this.data = (byte) ((CW) ? 3 : 2);
				this.direction = (CW) ? Direction.S : Direction.N;
				break;
			case S:	this.data = (byte) ((CW) ? 4 : 5);
				this.direction = (CW) ? Direction.W : Direction.E;
				break;
			case W:	this.data = (byte) ((CW) ? 2 : 3);
				this.direction = (CW) ? Direction.N : Direction.S;
				break;
			case DOWN:
			case UP:	break;
			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
	}

	@Override
	public void setDirection(Direction direction) {
		switch (direction) {
			case N:		this.data = 2; break;
			case E:		this.data = 5; break;
			case S:		this.data = 3; break;
			case W:		this.data = 4; break;
			case DOWN:	this.data = 0; break;
			case UP:	this.data = 1; break;
			default: throw new IllegalArgumentException("Dispensers do not support the direction " + direction);
		}
		this.direction = direction;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0: this.direction = Direction.DOWN;	break;
			case 1: this.direction = Direction.UP;		break;
			case 2: this.direction = Direction.N;		break;
			case 3: this.direction = Direction.S;		break;
			case 4: this.direction = Direction.W;		break;
			case 5: this.direction = Direction.E;		break;
			default: throw new IllegalArgumentException("illegal directional state: " + this.data); 
		}
		this.data = data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(this.content);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Dispenser other = (Dispenser) obj;
		if (!Arrays.equals(this.content, other.content)) return false;
		return true;
	}
	
	@Override
	public String getToolTipText() {
		StringBuffer sb = new StringBuffer("<html><body>" + super.toString() + ", direction " + this.direction + ", contents:<br>");
		int i = 0;
		int j = 0;
		for (Item item : this.content) {
			if (i == 2 && j < 2) {
				sb.append(item).append("<br>");
				i = 0;
				++j;
				continue;
			} else if (i == 2 && j == 2) {
				sb.append(item).append("</body></html>");
				break;
			} else {
				sb.append(item).append(", ");
			}
			++i;
		}
		
		return sb.toString();
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (dispenserZoomCache != zoom) {
			// reset cache
			dispenserImageCache.clear();
			dispenserZoomCache = zoom;
		} else {
			img = dispenserImageCache.get(this.direction);
			
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
		dispenserImageCache.put(this.direction, img);
		
		return img;
	}
	
	@Override
	public JToolTip getCustomToolTip() {
		BufferedImage background = ImageProvider.getDispenserPlaneCopy();
		Graphics2D g = background.createGraphics();
		
		// add item images
		int x = 0;
		int y = 0;
		for (Item item : this.content) {
			int xPixels = 7 + (x * 18);
			int yPixels = 7 + (y * 18);

			g.drawImage(item.getImage(1), xPixels, yPixels, 16, 16, null, null);
			
			// add writing
			byte amount = item.getStacksize();
			if (amount > 1) {
				if (amount > 99) amount = 99; // in mc, max is 64
				String amountstr = Byte.toString(amount);
				BufferedImage[] text = ImageProvider.stringToImage(amountstr, 0xFFFFFFFF);
				BufferedImage[] shadow = ImageProvider.stringToImage(amountstr, 0xFF3F3F3F);
				
				if (text.length == 2) {
					g.drawImage(shadow[0], xPixels + 6, yPixels + 10, 8, 8, null, null); // overlaps 1px over the item icon frame
					g.drawImage(text[0], xPixels + 5, yPixels + 9, 8, 8, null, null);
				}
				g.drawImage(shadow[shadow.length - 1], xPixels + 12, yPixels + 10, 8, 8, null, null);
				g.drawImage(text[text.length - 1], xPixels + 11, yPixels + 9, 8, 8, null, null);
			}
			
			if (x == 2) {
				x = 0;
				++y;
			} else {
				++x;
			}
		}
		
		BufferedImage result = ImageProvider.zoom(2, background);
		
		ImageToolTip tooltip = new ImageToolTip(result);
		
		return tooltip;
	}
}
