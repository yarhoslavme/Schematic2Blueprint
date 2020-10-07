package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A Stained Glass Pane
 * @author klaue
 *
 */
public class StainedGlassPane extends Block {
	/**
	 * Type of Stained Glass
	 * @author klaue
	 */
	public enum PaneColor {		
		WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK,
		GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK
	}

	private static HashMap<PaneColor, BufferedImage> blockImageCache = new HashMap<PaneColor, BufferedImage>();
	private static double blockZoomCache = -1;
	
	private PaneColor paneColor;
	
	/**
	 * initializes the Stained Glass Window Pane
	 * @param id the id of the stair (valid ids are 53 for wood and 67 for cobble)
	 * @param data the direction as a minecraft data value 
	 */
	public StainedGlassPane(byte data) {
		super((short)160, data);
		this.paneColor = getColorByValue(data);
		this.type = Type.GLASS;
		setData(data);
	}
	
	/**
	 * initializes the stair
	 * @param fenceType the type of stair
	 * @param direction the direction as a minecraft data value 
	 */
	public StainedGlassPane(PaneColor paneColor) {
		super((short)160);
		this.type = Type.GLASS;
		this.paneColor = paneColor;
	}
	

	/**
	 * Function to get the proper stair type from the stair id
	 * @param id
	 * @return the type
	 */
	public static PaneColor getColorByValue(byte data) {		
		switch (data) {
			case 0:	return PaneColor.WHITE;
			case 1:	return PaneColor.ORANGE;			
			case 2:	return PaneColor.MAGENTA;
			case 3:	return PaneColor.LIGHT_BLUE;
			case 4:	return PaneColor.YELLOW;
			case 5:	return PaneColor.LIME;
			case 6:	return PaneColor.PINK;
			case 7:	return PaneColor.GRAY;
			case 8:	return PaneColor.LIGHT_GRAY;
			case 9:	return PaneColor.CYAN;
			case 10:	return PaneColor.PURPLE;
			case 11:	return PaneColor.BLUE;
			case 12:	return PaneColor.BROWN;
			case 13:	return PaneColor.GREEN;
			case 14:	return PaneColor.RED;
			case 15:	return PaneColor.BLACK;
			
			default:	throw new IllegalArgumentException("illegal Color value for Stained Glass Pane: " + data);
		}
	}
	
	/**
	 * @return the Color
	 */
	public PaneColor getpaneColor() {
		return this.paneColor;
	}

	/**
	 * @param fenceType the fenceType to set
	 */
	public void setPaneColor(PaneColor paneColor) {
		this.paneColor=paneColor;
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public void setData(byte data) {

		this.data = data;
	}

	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (blockZoomCache != zoom) {
			// reset cache
			blockImageCache.clear();
			blockZoomCache = zoom;
		} else {
			img = blockImageCache.get(this.paneColor);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);
		
		if (img == null) return null;
						
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		blockImageCache.put(this.paneColor, img);
		
		
		return img;
	}
}
