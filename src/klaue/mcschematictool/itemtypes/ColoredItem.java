package klaue.mcschematictool.itemtypes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A colored item is the same as an item, just that a color can be set which is used to colorize the normal item image
 * @author klaue
 *
 */
public class ColoredItem extends Item {
	protected Color color = Color.WHITE;
	protected static HashMap<Short, HashMap<Short, HashMap<Color, BufferedImage> > > coloredItemCache = new HashMap<Short, HashMap<Short, HashMap<Color, BufferedImage> > >();
	protected static double coloredZoomCache = -1;
	
	/**
	 * Generates a new Colored Item of type id, color white
	 * @param id Item ID
	 * @param data Item data
	 */
	public ColoredItem(short id, short data) {
		this(id, data, (byte)0);
	}
	
	/**
	 * Generates a new Colored Item of type id, color white
	 * @param id Item ID
	 * @param data Item data
	 * @param stacksize the number of items in this stack
	 */
	public ColoredItem(short id, short data, byte stacksize) {
		this(id, data, stacksize, Color.white);
	}
	
	/**
	 * Generates a new Colored Item of type id
	 * @param id Item ID
	 * @param data Item data
	 * @param stacksize the number of items in this stack
	 * @param color the color or white if null
	 */
	public ColoredItem(short id, short data, byte stacksize, Color color) {
		super(id, data, stacksize);
		setColor(color);
		this.type = Type.COLORED;
	}
	
	
	/**
	 * Set the color in MC's Item Structure format
	 * @param color format Red<<16 + Green<<8 + Blue - same as Javas RGB int
	 */
	public void setColor(int color) {
		if (color > 0xFFFFFF || color < 0) return;
		this.color = new Color(color, false);
	}
	
	/**
	 * Set the color
	 * @param color
	 */
	public void setColor(Color color) {
		if (color == null) return;
		this.color = color;
	}
	
	/**
	 * Set color
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Gets the color in MC's Item Structure format
	 * @return color in format Red<<16 + Green<<8 + Blue - same as Javas RGB int
	 */
	public int getColorAsInt() {
		return this.color.getRGB();
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		// don't bother even looking in the cache if uncolored (white), just take image from parent (and parentcache)
		if (this.color.equals(Color.WHITE)) {
			return super.getImage(zoom);
		}
		
		if (coloredZoomCache != zoom) {
			// reset cache
			coloredItemCache.clear();
			coloredZoomCache = zoom;
		} else {
			if (coloredItemCache.containsKey(this.id) && coloredItemCache.get(this.id).containsKey(this.data)
					&& coloredItemCache.get(this.id).get(this.data).containsKey(this.color)) {
				return coloredItemCache.get(this.id).get(this.data).get(this.color);
			}
		}
		
		// image not in cache, make new
		BufferedImage uncoloredImg = super.getImage(zoom);
		if (uncoloredImg == null) return null;
		
		BufferedImage coloredImg = null;
		try {
			coloredImg = ImageProvider.multiplyImage(uncoloredImg, this.getColor());
		} catch (InterruptedException e) {
			e.printStackTrace();
			return uncoloredImg; // nothing to save in cache
		}
		
		// save image to cache
		HashMap<Short,HashMap<Color,BufferedImage> > dmgMap = coloredItemCache.get(this.id);
		if (dmgMap == null) {
			dmgMap = new HashMap<Short,HashMap<Color,BufferedImage> >();
			HashMap<Color, BufferedImage> colorMap = new HashMap<Color, BufferedImage>();
			colorMap.put(this.color, coloredImg);
			dmgMap.put(this.data, colorMap);
			coloredItemCache.put(this.id, dmgMap);
		} else {
			HashMap<Color, BufferedImage> colorMap = dmgMap.get(this.data);
			if (colorMap == null) {
				colorMap = new HashMap<Color, BufferedImage>();
				colorMap.put(this.color, coloredImg);
				dmgMap.put(this.data, colorMap);
			} else {
				colorMap.put(this.color, coloredImg);
			}
		}
		
		return coloredImg;
	}
}
