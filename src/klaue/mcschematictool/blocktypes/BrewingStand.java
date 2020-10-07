package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JToolTip;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.ImageToolTip;
import klaue.mcschematictool.blocktypes.DirectionalBlock.Direction;
import klaue.mcschematictool.itemtypes.Item;


/**
 * Vines can have different directions (walls they're attached to). A Vine block can be attached to multiple sides at once
 * @author klaue
 *
 */
public class BrewingStand extends MultiDirectionalBlock {
	private static HashMap<Byte, BufferedImage> brewingStandImageCache = new HashMap<Byte, BufferedImage>();
	private static double brewingStandZoomCache = -1;
	
	/**
	 * An array of exactly 3 items corresponding to the 3 slots of a brewing stand.
	 * Empty slots are Items with data = 0
	 */
	private Item[] content = null;
	
	private int brewingTime = 0;
	
	/**
	 * initializes the brewing stand (empty)
	 */
	public BrewingStand() {
		this((byte)0, null, 0);
	}
	
	/**
	 * initializes the brewing stand
	 * @param direction the direction(s) as a minecraft data value 
	 * @param content the content of this brewing stand: up to 3 items
	 * @param brewingTime brewing time in ticks
	 */
	public BrewingStand(byte direction, Item[] content, int brewingTime) {
		super((short)117, direction);
		this.type = Type.BREWINGSTAND;

		if (content == null || content.length == 0) {
			// empty brewing stand
			this.content = new Item[4];
			Arrays.fill(this.content, new Item());
			this.setData((byte)0);
			this.brewingTime = 0;
			return;
		} else if (content.length > 4) {
			throw new IllegalArgumentException("Brewing stands can only have up to 3 items and 1 result (4 items in total)");
		} else if (content.length == 4) {
			this.content = content;
			setData(direction);
			this.brewingTime = brewingTime;
		} else {
			this.content = new Item[4];
			Arrays.fill(this.content, new Item());
			for (int i = 0; i < content.length; ++i) {
				this.content[i] = content[i];
			}
			// do not test direction because fuck you
			setData(direction);
			this.brewingTime = brewingTime;
		}
	}
	
	/**
	 * initializes the vine
	 * @param directions the directions, valid directions are either any of E, SW, NW, or NONE 
	 * @param content the content of this brewing stand: up to 3 items
	 * @param brewingTime brewing time in ticks
	 */
	public BrewingStand(HashSet<Direction> directions, Item[] content, int brewingTime) {
		this((byte)0, content, brewingTime);
		setDirections(directions);
	}
	
	@Override
	public String toString() {
		return super.toString() + ", direction(s): " + this.directions.toString().replaceAll("[\\[\\]]", "") +
			", contents: " + Arrays.deepToString(this.content) + ", brewing time: " + this.brewingTime;
	}
	
	@Override
	public String getToolTipText() {
		StringBuffer sb = new StringBuffer("<html><body>Brewing stand, brewing time: ").append(this.brewingTime).append(", contents:<br>");
		for (int i = 0; i < this.content.length; ++i) {
			Item item = this.content[i];
			sb.append(item);
			if (i == this.content.length - 1) {
				sb.append(item).append("<br>");
			} else {
				sb.append(item).append(", ");
			}
		}
		sb.append("Directions: ").append(this.directions.toString().replaceAll("[\\[\\]]", ""));
		sb.append("</body></html>");
		return sb.toString();
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 7) throw new IllegalArgumentException("illegal directional state: " + data);
		
		this.data = data;
		this.directions = new HashSet<Direction>();
		
		if (data == 0) {
			this.directions.add(Direction.NONE);
			return;
		}

		if ((data & 1) != 0) this.directions.add(Direction.E);
		if ((data & 2) != 0) this.directions.add(Direction.NW);
		if ((data & 4) != 0) this.directions.add(Direction.SW);
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
					case E:		data += 1;	break; // 001
					case NW:	data += 2;	break; // 010
					case SW:	data += 4;	break; // 100
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
					case E:		newDirections.add(Direction.SW);	break;
					case SW:	newDirections.add(Direction.NW);	break;
					case NW:	newDirections.add(Direction.E);		break;
					default:
						// should never happen
						throw new AssertionError(this.directions);
				}
			}
		} else {
			for (Direction dir : this.directions) {
				switch (dir) {
					case E:		newDirections.add(Direction.NW);	break;
					case SW:	newDirections.add(Direction.E);		break;
					case NW:	newDirections.add(Direction.SW);	break;
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
		
		if (brewingStandZoomCache != zoom) {
			// reset cache
			brewingStandImageCache.clear();
			brewingStandZoomCache = zoom;
		} else {
			img = brewingStandImageCache.get(this.data);
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
		brewingStandImageCache.put(this.data, img);
		
		return img;
	}

	/**
	 * @return the content
	 */
	public Item[] getContent() {
		return this.content;
	}

	/**
	 * sets the content. make sure to set the new directions too
	 * @param content the content to set (has to be exactly 4 long, pad with items of id 0)
	 */
	public void setContent(Item[] content) {
		if (content.length != 4) {
			throw new IllegalArgumentException("Brewing stands need exactly 3 items");
		}
		this.content = content;
	}

	/**
	 * @return the brewing time in ticks
	 */
	public int getBrewingTime() {
		return this.brewingTime;
	}

	/**
	 * @param brewingTime the brewing time (in ticks) to set (ignored if no items)
	 */
	public void setBrewingTime(int brewingTime) {
		for (Item i : this.content) {
			if (i.getId() != 0) {
				this.brewingTime = brewingTime;
				break;
			}
		}
	}
	
	@Override
	public JToolTip getCustomToolTip() {
		BufferedImage background = ImageProvider.getBrewingStandPlaneCopy();
		Graphics2D g = background.createGraphics();
		
		// add item images
		Point[] imagePoints = new Point[4];
		imagePoints[0] = new Point(6, 38);	// left
		imagePoints[1] = new Point(29, 45);	// bottom
		imagePoints[2] = new Point(52, 38);	// right
		imagePoints[3] = new Point(29, 9);	// top
		
		for (int i = 0; i < this.content.length; ++i) {
			Item item = this.content[i];
			Point p = imagePoints[i];

			g.drawImage(item.getImage(1), p.x, p.y, 16, 16, null, null);
			
			// add contents
			byte amount = item.getStacksize();
			if (amount > 1) {
				if (amount > 99) amount = 99; // in mc, max is 64
				String amountstr = Byte.toString(amount);
				BufferedImage[] text = ImageProvider.stringToImage(amountstr, 0xFFFFFFFF);
				BufferedImage[] shadow = ImageProvider.stringToImage(amountstr, 0xFF3F3F3F);
				
				if (text.length == 2) {
					g.drawImage(shadow[0], p.x + 6, p.y + 10, 8, 8, null, null); // overlaps 1px over the item icon frame
					g.drawImage(text[0], p.x + 5, p.y + 9, 8, 8, null, null);
				}
				g.drawImage(shadow[shadow.length - 1], p.x + 12, p.y + 10, 8, 8, null, null);
				g.drawImage(text[text.length - 1], p.x + 11, p.y + 9, 8, 8, null, null);
			}
		}
		
		BufferedImage result = ImageProvider.zoom(2, background);
		
		ImageToolTip tooltip = new ImageToolTip(result);
		
		return tooltip;
	}
}
