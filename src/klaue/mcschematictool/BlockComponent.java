package klaue.mcschematictool;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JToolTip;

import klaue.mcschematictool.blocktypes.Block;

/**
 * A small class to represent a block as a component inside swing
 * 
 * @author klaue
 */
public class BlockComponent extends JComponent {
	private Block block = null;
	private double zoom = -1;
	
	/**
	 * Initializes the block component
	 * 
	 * @param block the block this component should display
	 * @param zoom the currend zoom value
	 */
	public BlockComponent(Block block, double zoom) {
		super();
		setBlock(block);
		setZoom(zoom);
		setToolTipText(block.getToolTipText());
	}
	
	/**
	 * Sets the block to use
	 * @param block the block this component should display
	 */
	public void setBlock(Block block) {
		if (block == null) throw new IllegalArgumentException("block is null");
		this.block = block;
	}
	
	/**
	 * @return the current image
	 */
	public Block getBlock() {
		return this.block;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (this.block.getId() == 0) return; // air
		
		BufferedImage img = this.block.getImage(this.zoom);
		g.drawImage(img, 0, 0, null); // see javadoc for more info on the parameters
	}
	
	@Override
	public JToolTip createToolTip() {
		JToolTip cutomToolTip = this.block.getCustomToolTip();
		if (cutomToolTip == null) {
			return super.createToolTip();
		}
		return cutomToolTip;
	}

	/**
	 * @return the current zoom
	 */
	public double getZoom() {
		return this.zoom;
	}

	/**
	 * @param zoom the zoom to set
	 */
	public void setZoom(double zoom) {
		if (zoom == this.zoom) return;
		this.zoom = zoom;
		
		Dimension newDim = new Dimension((int)(16 * zoom), (int)(16 * zoom));
		this.setMinimumSize(newDim);
		this.setPreferredSize(newDim);
		this.setMaximumSize(newDim);
		this.setSize(newDim);
		
		this.repaint();
	}
}
