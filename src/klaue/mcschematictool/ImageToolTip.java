package klaue.mcschematictool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JToolTip;

/**
 * An implementation of JToolTip that supports images. return an instance of this in the components createToolTip method
 * @author klaue
 */
public class ImageToolTip extends JToolTip{
	private BufferedImage image = null;
	
	/**
	 * Initializes the tooltip
	 * @param img the image that should be the tooltip. Null will cause this tooltip to act like a normal one
	 */
	public ImageToolTip(BufferedImage img) {
		super();
		setImage(img);
		setBackground(new Color(0x00FFFFFF, true));
	}
	
	@Override
	public void paint(Graphics g) {
		if (this.image == null) {
			super.paint(g);
		} else {
			g.drawImage(this.image, 0, 0, null);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (this.image == null) {
			super.paintComponent(g);
		} else {
			g.drawImage(this.image, 0, 0, null);
		}
	}
	
	/**
	 * @param img the image that should be the tooltip. Null will cause this tooltip to act like a normal one
	 */
	public void setImage(BufferedImage img) {
		this.image = img;
		if (img != null) {
			Dimension size = new Dimension(img.getWidth(), img.getHeight());
			this.setMinimumSize(size);
			this.setPreferredSize(size);
			this.setMaximumSize(size);
			this.setSize(size);
		}
	}
	 /**
	  * returns the current image of this tooltip (or null for no image)
	  * @return the current image of this tooltip (or null for no image) (redundant, isn't it?)
	  */
	public BufferedImage getImage() {
		return this.image;
	}
}
