package klaue.mcschematictool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * A stack of ImageGrids
 * @author klaue
 */
public class ImageGridStack implements Printable {
	private ImageGrid[] grids;
	private Color gridColor = null;
	private Color markColor = null;
	private double zoom = 1;
	
	/**
	 * initialize the image grid stack.
	 * @param grids the ImageGrids. It is assumed that all grids in this array are of the same zoom value and grid line color
	 */
	public ImageGridStack(ImageGrid[] grids) {
		this.grids = grids;
		if (grids.length > 0) {
			this.zoom = grids[0].getZoom();
			this.gridColor = grids[0].getGridColor();
			this.markColor = grids[0].getMarkColor();
		}
	}
	
	/**
	 * Gets the grid at the level
	 * @param level the level
	 * @return the grid
	 */
	public ImageGrid getGridAtLevel(int level) {
		return this.grids[level];
	}
	
	/**
	 * Sets the grid at the given level
	 * @param level
	 * @param grid the grid. Has to have the same zoom value as the other grids in this stack
	 */
	public void setGridAtLevel(int level, ImageGrid grid) {
		if (grid == null) {
			throw new IllegalArgumentException("null grids not allowed");
		}
		if (this.grids.length == 0) {
			// first imagegrid
			this.zoom = grid.getZoom();
			this.gridColor = grid.getGridColor();
			this.markColor = grid.getMarkColor();
		} else if (grid.getZoom() != this.zoom || grid.getGridColor() != this.gridColor || grid.getMarkColor() != this.markColor) {
			throw new IllegalArgumentException("new grid not of same gridcolor, markcolor or zoom as the rest of the grids");
		}
		
		this.grids[level] = grid; // may throw arrayoutofboundsexception
	}
	
	/**
	 * @return the size of this stack
	 */
	public int getStackSize() {
		return this.grids.length;
	}

	/**
	 * @return the current zoom value
	 */
	public double getZoom() {
		return this.zoom;
	}

	/**
	 * @param zoom the zoom value to set (distributed to all contained imagegrids)
	 */
	public void setZoom(double zoom) {
		if (zoom == this.zoom) return;
		this.zoom = zoom;
		for (ImageGrid grid : this.grids) {
			grid.setZoom(zoom);
		}
	}

	/**
	 * @return the gridColor
	 */
	public Color getGridColor() {
		return this.gridColor;
	}

	/**
	 * @param gridColor the gridColor to set
	 */
	public void setGridColor(Color gridColor) {
		if (this.gridColor == gridColor) return;
		this.gridColor = gridColor;
		for (ImageGrid grid : this.grids) {
			grid.setGridColor(gridColor);
		}
	}

	/**
	 * @return the markColor
	 */
	public Color getMarkColor() {
		return this.markColor;
	}

	/**
	 * @param markColor the gridColor to set
	 */
	public void setMarkColor(Color markColor) {
		if (this.markColor == markColor) return;
		this.markColor = markColor;
		for (ImageGrid grid : this.grids) {
			grid.setMarkColor(markColor);
		}
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex >= this.grids.length) {
			return NO_SUCH_PAGE;
		}
		this.grids[pageIndex].print(g, pageFormat, 0);
		
		return PAGE_EXISTS;
	}
}
