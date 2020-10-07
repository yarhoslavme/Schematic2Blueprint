package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * An end portal frame block that can be either fixed (ender eye added) or not
 * @author klaue
 *
 */
public class EndPortalFrame extends Block {
	private boolean isFixed = false;
	
	private static HashMap<Boolean, BufferedImage> endPortalFrameImageCache = new HashMap<Boolean, BufferedImage>();
	private static double endPortalFrameZoomCache = -1;
	
	/**
	 * initializes the end portal frame block
	 * @param data 0 = closed, 1 = open
	 */
	public EndPortalFrame(byte data) {
		super((short)120, data);
		this.type = Type.ENDPORTALFRAME;
		setData(data);
	}

	/**
	 * initializes the end portal frame block
	 * @param isFixed true if this end portal frame is fixed (ender eye added)
	 */
	public EndPortalFrame(boolean isFixed) {
		super((short)120);
		this.isFixed = isFixed;
		this.data = (byte)((isFixed) ? 1 : 0);
		
		this.type = Type.ENDPORTALFRAME;
	}
	
	/**
	 * @param isFixed true if this end portal frame should be fixed (ender eye added)
	 */
	public void setFixed(boolean isFixed) {
		if (this.isFixed == isFixed) return;
		this.isFixed = isFixed;
		this.data = (byte)((isFixed) ? this.data|4 : this.data&3);
	}
	
	/**
	 * @return true if this end portal frame is fixed (ender eye added)
	 */
	public boolean isFixed() {
		return this.isFixed;
	}
	
	@Override
	public void setData(byte data) {
		if (data < 0 || data > 7) {
			throw new IllegalArgumentException("data " + data + " outside boundaries"); 
		}
		this.data = data;
		this.isFixed = (data&4) != 0;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (endPortalFrameZoomCache != zoom) {
			// reset cache
			endPortalFrameImageCache.clear();
			endPortalFrameZoomCache = zoom;
		} else {
			img = endPortalFrameImageCache.get(this.isFixed);
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

		endPortalFrameImageCache.put(this.isFixed, img);

		return img;
	}
}
