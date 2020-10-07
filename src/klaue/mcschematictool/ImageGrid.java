package klaue.mcschematictool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.RepaintManager;
import javax.swing.ToolTipManager;

import klaue.mcschematictool.blocktypes.Block;

/**
 * A simple class to render (draw) a Slice of blocks, using the block's ImageComponent. It will draw a grid between the blocks
 * of a thickness of 1px (and 2 px all 5 blocks from the top left)
 * @author klaue
 */
public class ImageGrid extends JComponent implements Printable, MouseListener {
	private Slice slice = null;
	private Color gridColor = null;
	private Color markColor = null;
	private double zoom = 1;
	private long lastPrintTime = new Date().getTime();
	private BufferedImage printCache = null;
	private Point markedBlock = null;
	
	private static Dimension imgDim = new Dimension(16, 16);
	
	/**
	 * An ImageGrid with the given images, black grid lines and a red marker.
	 * @param slice the backend slice to draw
	 * @param zoom the current zoom level
	 */
	public ImageGrid(Slice slice, double zoom) {
		this(slice, zoom, Color.black, Color.RED);
	}
	
	/**
	 * An ImageGrid with the given images and the given grid color.
	 * @param slice the backend slice to draw
	 * @param zoom the current zoom level
	 * @param gridColor the color of the grid
	 * @param markColor the color of the marked block
	 */
	public ImageGrid(Slice slice, double zoom, Color gridColor, Color markColor) {
		this.slice = slice;
		this.gridColor = gridColor;
		this.markColor = markColor;
		this.zoom = zoom;
		
		calcNewSize();
		ToolTipManager.sharedInstance().registerComponent(this);
		this.addMouseListener(this);
	}

	/**
	 * @return the images
	 */
	public Slice getSlice() {
		return this.slice;
	}

	/**
	 * @param slice the backend slice to display
	 */
	public void setSlice(Slice slice) {
		this.slice = slice;
		calcNewSize();
	}
	
	private void calcNewSize() {
		if (this.slice != null) {
			int sliceWidth = this.slice.getWidth();
			int sliceHeight = this.slice.getHeight();
			
			int width = sliceWidth * ((int)(imgDim.width * this.zoom));
			int height = sliceHeight * ((int)(imgDim.height * this.zoom));
			
			// add lines
			width += sliceWidth - 2;
			height += sliceHeight - 2;
			
			// add thicker lines every 5th block
			for (int i = 0; i < sliceWidth; ++i) {
				if ((i + 1) % 5 == 0) {
					++width;
				}
			}
			for (int i = 0; i < sliceHeight; ++i) {
				if ((i + 1) % 5 == 0) {
					++height;
				}
			}
			Dimension dim = new Dimension(width, height);
			this.setMinimumSize(dim);
			this.setPreferredSize(dim);
			this.setMaximumSize(dim);
			this.setSize(dim);
			
		}
	}

	/**
	 * @return the grid color
	 */
	public Color getGridColor() {
		return this.gridColor;
	}

	/**
	 * @param gridColor the grid color to set
	 */
	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	/**
	 * @return the marker color
	 */
	public Color getMarkColor() {
		return this.markColor;
	}

	/**
	 * @param markColor the marker color to set
	 */
	public void setMarkColor(Color markColor) {
		this.markColor = markColor;
	}
	
	@Override
	public void paint(Graphics g) {
		int hzBlockNum = this.slice.getWidth();
		int vtBlockNum = this.slice.getHeight();
		
		if (this.slice == null || hzBlockNum == 0 || vtBlockNum == 0) {
			super.paint(g);
		} else {
			int imgWidth = (int)(imgDim.width * this.zoom);
			int imgHeight = (int)(imgDim.height * this.zoom);
			int thisWidth = this.getPreferredSize().width;
			int thisHeight = this.getPreferredSize().height;
			
			// draw grid lines
			g.setColor(this.gridColor);
			// vert
			int xPx = imgWidth;
			for (int x = 0; x < hzBlockNum - 1; ++x) {
				g.drawLine(xPx, 0, xPx, thisHeight - 1);
				
				if ((x + 1) % 5 == 0) {
					// double line
					g.drawLine(xPx + 1, 0, xPx + 1, thisHeight - 1);
					xPx = xPx + imgWidth + 2;
				} else {
					xPx = xPx + imgWidth + 1;
				}
			}
			// horz
			int yPx = imgHeight;
			for (int y = 0; y < vtBlockNum - 1; ++y) {
				g.drawLine(0, yPx, thisWidth - 1, yPx);
				
				if ((y + 1) % 5 == 0) {
					// double line
					g.drawLine(0, yPx + 1, thisWidth - 1, yPx + 1);
					yPx = yPx + imgHeight + 2;
				} else {
					yPx = yPx + imgHeight + 1;
				}
			}
			
			
			// draw block images
			xPx = 0;
			yPx = 0;
			for (int y = 0; y < vtBlockNum; ++y) {
				for (int x = 0; x < hzBlockNum; ++x) {
					Block block = this.slice.getBlockAt(x, y);
					
					if (block.getId() != 0) {
						BufferedImage img = block.getImage(this.zoom);
						g.drawImage(img, xPx, yPx, null); // see javadoc for more info on the parameters
					}
					
					if (this.markedBlock != null && this.markedBlock.x == x && this.markedBlock.y == y) {
						// draw a red border around this block
						g.setColor(this.markColor);
						boolean drawLeft = (x != 0);
						boolean drawRight = (x != hzBlockNum -1);
						boolean drawTop = (y != 0);
						boolean drawBottom = (y != vtBlockNum -1);
						
						int left = xPx - 1;
						int top = yPx - 1;
						int right = xPx + imgWidth;
						int bottom = yPx + imgHeight;
						
						// corner points
						if (drawLeft && drawTop)		g.drawLine(left, top, left, top);
						if (drawLeft && drawBottom)		g.drawLine(left, bottom, left, bottom);
						if (drawRight && drawTop)		g.drawLine(right, top, right, top);
						if (drawRight && drawBottom)	g.drawLine(right, bottom, right, bottom);
						
						// lines
						if (drawLeft)	g.drawLine(left, top+1, left, bottom-1);
						if (drawRight)	g.drawLine(right, top+1, right, bottom-1);
						if (drawTop)	g.drawLine(left+1, top, right-1, top);
						if (drawBottom)	g.drawLine(left+1, bottom, right-1, bottom);
						
						Color c = new Color(this.markColor.getRed(), this.markColor.getGreen(), this.markColor.getBlue(), 50);
						g.setColor(c);
						g.fillRect(xPx, yPx, imgWidth, imgHeight);
					}
					
					if ((x + 1) % 5 == 0) {
						// double line
						xPx = xPx + (imgWidth + 2);
					} else {
						xPx = xPx + (imgWidth + 1);
					}
				}
				xPx = 0;
				if ((y + 1) % 5 == 0) {
					// double line
					yPx = yPx + (imgHeight + 2);
				} else {
					yPx = yPx + (imgHeight + 1);
				}
			}
		}
	}
	
	/**
	 * This method writes the content of the current ImageGrid to a BufferedImage
	 * A check for enough memory should be done before calling this or this should be wrapped
	 * inside a try-catch for java.lang.OutOfMemoryError - it'll need ~4 byte of memory per pixel
	 * @param background the background color to use. Null for transparent
	 * @param gridLines the color to use for the grid lines
	 * @return the generated image
	 */
	public BufferedImage exportImage(Color background, Color gridLines) {
		if (this.slice == null || this.slice.getWidth() == 0 || this.slice.getHeight() == 0) return null;
		
		int width = this.getMinimumSize().width;
		int height = this.getMinimumSize().height;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g = img.createGraphics();
		if (background != null) {
			g.setColor(background);
			g.fillRect(0, 0, width, height);
		} else {
			g.setBackground(new Color(0x00FFFFFF, true)); // transp. white
			g.clearRect(0, 0, width, height);
		}
		
		Color previousGridColor = this.getGridColor();
		if (previousGridColor != gridLines) {
			this.setGridColor(gridLines);
		}
		
		// set marker color to grid color to hide
		Color previousMarkColor = this.getMarkColor();
		this.setMarkColor(gridLines);
		
		this.paint(g);
		
		this.setGridColor(previousGridColor);
		this.setMarkColor(previousMarkColor);
		
		return img;
	}

	/**
	 * @return the current zoom value
	 */
	public double getZoom() {
		return this.zoom;
	}

	/**
	 * @param zoom the zoom value to set (distributed to all contained block components)
	 */
	public void setZoom(double zoom) {
		if (zoom == this.zoom) return;
		this.zoom = zoom;
		calcNewSize();
		
		this.repaint();
	}
	
	@Override
	public JToolTip createToolTip() {
		Point relativeLocation = getRelativeLocation(MouseInfo.getPointerInfo().getLocation());
		if (relativeLocation != null) {
			Block b = getBlockAtPoint(relativeLocation);
			if (b != null) {
				JToolTip cutomToolTip = b.getCustomToolTip();
				if (cutomToolTip != null) {
					return cutomToolTip;
				}
			}
		}
		return super.createToolTip();
	}
	
	@Override
	public String getToolTipText(MouseEvent evt) {
		Point relativeLocation = getRelativeLocation(evt.getLocationOnScreen());
		if (relativeLocation != null) {
			Block b = getBlockAtPoint(relativeLocation);
			if (b != null) return b.getToolTipText();
		}
		return super.getToolTipText(evt);
	}
	
	/**
	 * Returns the given location transformed to a location relative to, say inside, this imagegrid 
	 * @param p the on-screen-location
	 * @return the relative point or null if given location is not inside this imagegrid
	 */
	public Point getRelativeLocation(Point p) {
		if (p == null) return null;
		Point igLocation = this.getLocationOnScreen();
		
		if (p.x < igLocation.x || p.x > igLocation.x + this.getPreferredSize().width
				|| p.y < igLocation.y || p.y > igLocation.y + this.getPreferredSize().height) {
			return null;
		}
		return new Point(p.x - igLocation.x, p.y - igLocation.y);
	}
	
	/**
	 * Returns the block at the given point relative to the image grid
	 * @param p the location relative to the image grid
	 * @return the block or null if not over a block (over grid)
	 */
	public Block getBlockAtPoint(Point p) {
		Point index = getIndexOfBlockAtPoint(p);
		if (index == null) return null;
		
		return this.slice.getBlockAt(index.x, index.y);
	}
	
	/**
	 * gets the index of the block inside the slice. Yeah I know, using Point for this makes not much sense, but I'm not gonna make a new Datatype
	 * just because Java lacks a generic return type for two values.
	 * @param p
	 * @return the index or null
	 */
	private Point getIndexOfBlockAtPoint(Point p) {
		if (p == null) return null;
		int imgWidth = (int)(imgDim.width * this.zoom);
		int imgHeight = (int)(imgDim.height * this.zoom);
		
		int x = p.x;
		int y = p.y;
 
		int lineXRepetitionRate = imgWidth + 1;
		int lineYRepetitionRate = imgHeight + 1;
		int doubleLineXRepetitionRate = (imgWidth + 1) * 5 + 1; // +1 = line
		int doubleLineYRepetitionRate = (imgHeight + 1) * 5 + 1;
		
		// is it the second line of the double line? it is if it's the last pixel of a fiver 
		if (x % doubleLineXRepetitionRate == doubleLineXRepetitionRate - 1) return null;

		// line repetiionrate disregards the second line after each fiver block, so that line has to be removed first
		// fiver count from 0, eg xFiver = 3 is 4. fiver
		int xFiver = x / doubleLineXRepetitionRate; // the two lines at the end of each fiver-block count towards the previous fiver
		int yFiver = y / doubleLineYRepetitionRate;
		
		// remove 2nd line
		x -= xFiver;
		y -= yFiver;
		
		// is it a line? it is if it's the (imgWidth+1)th pixel 
		if (x % lineXRepetitionRate == imgWidth) return null;
		
		// block count from 0, eg xBlock = 3 is 4. block
		int xBlock = x/lineXRepetitionRate;
		int yBlock = y/lineYRepetitionRate;
		
		return new Point(xBlock, yBlock);
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		
		// resize img to page size
		// 72 dpi
		double prevZoom = this.getZoom();
		this.setZoom(1.0); // max precision
		double width = pageFormat.getImageableWidth();
		double height = pageFormat.getImageableHeight();
		double hZoom = width / this.getPreferredSize().width;
		double vZoom = height / this.getPreferredSize().height;
		double zoom = (hZoom < vZoom) ? hZoom : vZoom;
		g2d.scale(zoom, zoom);
		
		// I know that this caching, using time, is ugly, but there's no other way and this method
		// may be called hundreds of times for the same page, which takes ages w/o caching
		if (this.printCache != null && (new Date().getTime() - this.lastPrintTime) <= 200) {
			// return cache
			this.setZoom(prevZoom);
			this.lastPrintTime = new Date().getTime();
			g2d.drawImage(this.printCache, 0, 0, null); // see javadoc for more info on the parameters
			return PAGE_EXISTS;
		}
		
		this.printCache = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D gImg = this.printCache.createGraphics();
		
		RepaintManager currentManager = RepaintManager.currentManager(this);
		currentManager.setDoubleBufferingEnabled(false);
		this.paint(gImg);
		currentManager.setDoubleBufferingEnabled(true);
		
		this.setZoom(prevZoom);
		this.lastPrintTime = new Date().getTime();
		g2d.drawImage(this.printCache, 0, 0, null); // see javadoc for more info on the parameters
		return PAGE_EXISTS;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point relativeLocation = getRelativeLocation(MouseInfo.getPointerInfo().getLocation());
		Point index = getIndexOfBlockAtPoint(relativeLocation);
		if (index == null) return;
		
		if (this.markedBlock != null && this.markedBlock.equals(index)) {
			// unset if allready set
			this.markedBlock = null;
		} else {
			this.markedBlock = index;
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

	/**
	 * @return the markedBlock
	 */
	public Point getMarkedBlock() {
		return this.markedBlock;
	}

	/**
	 * @param markedBlock the markedBlock to set
	 */
	public void setMarkedBlock(Point markedBlock) {
		this.markedBlock = markedBlock;
	}
}
