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
 * A hopper is a block that contains items
 * @author klaue
 *
 */
public class Hopper extends Dispenser {
	private static HashMap<Direction, BufferedImage> hopperImageCache = new HashMap<Direction, BufferedImage>();
	private static double hopperZoomCache = -1;
	
	/**
	 * Initializes the hopper. The new hopper will contain exactly 9 items, some or all of which may be zero (empty)
	 * @param content The hopper content or null for an emty hopper
	 * @param direction The hopper's data (the direction it's facing)
	 */
	public Hopper(Item[] content, byte direction) {
		super(content, direction);
		this.setId((short)154);
		this.type = Type.HOPPER;
		
		// dispenser has a bigger item size, check if still ok for hopper
		Item[] chestcontent = content;
		if (chestcontent == null || chestcontent.length == 0) {
			// empty chest
			chestcontent = new Item[5];
			Arrays.fill(chestcontent, new Item());
		}
		
		if (chestcontent.length > 5) {
			throw new IllegalArgumentException("Hoppers can only have up to 5 items");
		}
		
		if (chestcontent.length == 5) {
			this.content = chestcontent;
		} else {
			this.content = new Item[5];
			Arrays.fill(this.content, new Item());
			for (int i = 0; i < chestcontent.length; ++i) {
				this.content[i] = chestcontent[i];
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (hopperZoomCache != zoom) {
			// reset cache
			hopperImageCache.clear();
			hopperZoomCache = zoom;
		} else {
			img = hopperImageCache.get(this.direction);
			
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
		hopperImageCache.put(this.direction, img);
		
		return img;
	}
	
	@Override
	public JToolTip getCustomToolTip() {
		BufferedImage background = ImageProvider.getHopperPlaneCopy();
		Graphics2D g = background.createGraphics();
		
		// add item images
		int x = 0;
		for (Item item : this.content) {
			int xPixels = 7 + (x * 18);
			int yPixels = 7;

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
			
			++x;
		}
		
		BufferedImage result = ImageProvider.zoom(2, background);
		
		ImageToolTip tooltip = new ImageToolTip(result);
		
		return tooltip;
	}
}
