package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JToolTip;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.ImageToolTip;
import klaue.mcschematictool.itemtypes.Item;

/**
 * A chest is a block that contains items
 * @author klaue
 *
 */
public class Chest extends Block {
	/**
	 * An array of exactly 27 items corresponding to the 27 slots of a chest.
	 * Empty slots are Items with data = 0
	 */
	public Item[] content = null;
	private boolean isTrapped = false;

	/**
	 * Initializes an empty, non-trapped chest.
	 */
	public Chest() {
		this((short)54, null);
	}
	
	/**
	 * Initializes an empty chest.
	 * @param id 54 = chest, 146 = trapped chest, other values illegal
	 */
	public Chest(short id) {
		this(id, null);
	}
	
	/**
	 * Initializes an empty chest.
	 * @param isTrapped true for a trapped chest 
	 */
	public Chest(boolean isTrapped) {
		this(isTrapped, null);
	}
	
	/**
	 * Initializes the (untrapped) chest. The new chest will contain exactly 27 items, some or all of which may be zero (empty)
	 * @param content The chests content or null for an empty chest
	 */
	public Chest(Item[] content) {
		this(false, content);
	}
	
	/**
	 * Initializes the chest. The new chest will contain exactly 27 items, some or all of which may be zero (empty)
	 * @param isTrapped true for a trapped chest 
	 * @param content The chests content or null for an empty chest
	 */
	public Chest(boolean isTrapped, Item[] content) {
		this(isTrapped ? (short)146 : (short)54, content);
	}
	
	/**
	 * Initializes the chest. The new chest will contain exactly 27 items, some or all of which may be zero (empty)
	 * @param id 54 = chest, 146 = trapped chest, other values illegal
	 * @param content The chests content or null for an emty chest
	 */
	public Chest(short id, Item[] content) {
		super(id);
		switch(id) {
			case 54:	this.isTrapped = false;	break;
			case 146:	this.isTrapped = true;		break;
			default:	throw new IllegalArgumentException("Invalid ID " + id + " for a chest");
		}
		
		this.type = Type.CHEST;
		if (content == null || content.length == 0) {
			// empty chest
			this.content = new Item[27];
			Arrays.fill(this.content, new Item());
			return;
		}
		
		if (content.length > 27) {
			throw new IllegalArgumentException("Chests can only have up to 27 items");
		}
		
		if (content.length == 27) {
			this.content = content;
		} else {
			this.content = new Item[27];
			Arrays.fill(this.content, new Item());
			for (int i = 0; i < content.length; ++i) {
				this.content[i] = content[i];
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ", contents: " + Arrays.deepToString(this.content);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = this.id;
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
		Chest other = (Chest) obj;
		if (!Arrays.equals(this.content, other.content)) return false;
		if (other.isTrapped != this.isTrapped) return false;
		return true;
	}
	
	@Override
	public String getToolTipText() {
		StringBuffer sb = new StringBuffer("<html><body>" + super.toString() + ", contents:<br>");
		int i = 0;
		int j = 0;
		for (Item item : this.content) {
			if (i == 8 && j < 2) {
				sb.append(item).append("<br>");
				i = 0;
				++j;
				continue;
			} else if (i == 8 && j == 2) {
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
	public JToolTip getCustomToolTip() {
		BufferedImage background = ImageProvider.getChestPlaneCopy();
		Graphics2D g = background.createGraphics();
		
		// add item images
		int x = 0;
		int y = 0;
		for (Item item : this.content) {
			int xPixels = 7 + (x * 18);
			int yPixels = 7 + (y * 18);

			g.drawImage(item.getImage(1), xPixels, yPixels, 16, 16, null, null);
			
			// add contents
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
			
			if (x == 8) {
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

	/**
	 * @return the isTrapped
	 */
	public boolean isTrapped() {
		return this.isTrapped;
	}

	/**
	 * @param isTrapped the isTrapped to set
	 */
	public void setTrapped(boolean isTrapped) {
		this.isTrapped = isTrapped;
		if (this.isTrapped != isTrapped) {
			this.isTrapped = isTrapped;
			this.setId(isTrapped ? (short)146 : (short)54);
		}
	}
}
