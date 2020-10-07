package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import klaue.mcschematictool.ImageProvider;

/**
 * A block which can hold a direction
 * @author klaue
 */
public abstract class DirectionalBlock extends Block {
	/**
	 * The directions a block can face
	 * @author klaue
	 */
	public enum Direction { NONE, UP, DOWN, N("north"), NNE("north north east"), NE("north east"),
		ENE("east north east"), E("east"), ESE("east south east"), SE("south east"),
		SSE("south south east"), S("south"), SSW("south south west"), SW("south west"),
		WSW("west south west"), W("west"), WNW("west north west"), NW("north west"),
		NNW("north north west");
		
		private String name = null;
		Direction(){}
		Direction(String name) { this.name = name; }
		@Override
		public String toString() { return (this.name == null) ? super.toString().toLowerCase() : this.name; }
	};
	
	private static final int red = 0xFFFF0000; // first two FF = alpha
	private static final int tpr = 0x00FFFFFF; // transparent - actually a transparent white
	
	private static final int[] nArrow = {tpr, tpr, red, red, tpr, tpr,
										 tpr, red, red, red, red, tpr,
										 red, red, red, red, red, red};
	
	private static final int[] neArrow = {red, red, red, red,
										  tpr, red, red, red,
										  tpr, tpr, red, red,
										  tpr, tpr, tpr, red};
	
	private static final int[] eArrow = {red, tpr, tpr,
										 red, red, tpr,
										 red, red, red,
										 red, red, red,
										 red, red, tpr,
										 red, tpr, tpr};
	
	private static final int[] seArrow = {tpr, tpr, tpr, red,
										  tpr, tpr, red, red,
										  tpr, red, red, red,
										  red, red, red, red};
	
	private static final int[] sArrow = {red, red, red, red, red, red,
										 tpr, red, red, red, red, tpr,
										 tpr, tpr, red, red, tpr, tpr};
	
	private static final int[] swArrow = {red, tpr, tpr, tpr,
										  red, red, tpr, tpr,
										  red, red, red, tpr,
										  red, red, red, red};
	
	private static final int[] wArrow = {tpr, tpr, red,
										 tpr, red, red,
										 red, red, red,
										 red, red, red,
										 tpr, red, red,
										 tpr, tpr, red};
	
	private static final int[] nwArrow = {red, red, red, red,
										  red, red, red, tpr,
										  red, red, tpr, tpr,
										  red, tpr, tpr, tpr};
		
	/**
	 * the direction this block faces
	 */
	protected Direction direction;
	
	/**
	 * @see Block
	 */
	protected DirectionalBlock() {
		super();
	}
	/**
	 * @param id 
	 * @see Block
	 */
	protected DirectionalBlock(short id) {
		super(id);
	}
	/**
	 * @param id 
	 * @param data 
	 * @see Block
	 */
	protected DirectionalBlock(short id, byte data) {
		super(id, data);
	}

	@Override
	public abstract void turn(boolean CW);
	
	/**
	 * returns the direction this block faces or the direction of the block this block is attached to, depending on the type of block
	 * @return The direction.
	 */
	public Direction getDirection() {
		return this.direction;
	}
	
	/**
	 * sets the direction this block faces or the direction of the block this block is attached to, depending on the type of block
	 * @param direction
	 */
	public abstract void setDirection(Direction direction);
	
	@Override
	public boolean isDirectional() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.direction == null) ? 0 : this.direction.hashCode());
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
		DirectionalBlock other = (DirectionalBlock) obj;
		if (this.direction == null) {
			if (other.direction != null) return false;
		} else if (!this.direction.equals(other.direction)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds a red arrow to the image, pointing in the given direction. If the direction is not in the 45° range, two arrows
	 * will be used to show the direction, for example, NNW will have an arrow pointing to north and one pointing to NW.
	 * A new image instance will be returned, the original image will not be changed.
	 * Note: Currently there will be no arrow for UP and DOWN
	 * @param direction the direction where the arrow should point
	 * @param img the image to have the arrow added
	 * @return the arrow'd image
	 */
	protected static BufferedImage addArrowToImage(Direction direction, final BufferedImage img) {
		if (direction == Direction.NONE || direction == Direction.UP || direction == Direction.DOWN) return img;
		
		BufferedImage overlay = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		
		// NNE, ENE, ESE, SSE, SSW, WSW, WNW and NNW are two arrows at once
		// therefore, no switch
		if (direction == Direction.N || direction == Direction.NNE || direction == Direction.NNW) {
			overlay.setRGB(5, 0, 6, 3, nArrow, 0, 6);
		} else if (direction == Direction.E || direction == Direction.ENE || direction == Direction.ESE) {
			overlay.setRGB(13, 5, 3, 6, eArrow, 0, 3);
		} else if (direction == Direction.S || direction == Direction.SSE || direction == Direction.SSW) {
			overlay.setRGB(5, 13, 6, 3, sArrow, 0, 6);
		} else if (direction == Direction.W || direction == Direction.WSW || direction == Direction.WNW) {
			overlay.setRGB(0, 5, 3, 6, wArrow, 0, 3);
		}
		if (direction == Direction.NE || direction == Direction.NNE || direction == Direction.ENE) {
			overlay.setRGB(12, 0, 4, 4, neArrow, 0, 4);
		} else if (direction == Direction.SE || direction == Direction.ESE || direction == Direction.SSE) {
			overlay.setRGB(12, 12, 4, 4, seArrow, 0, 4);
		} else if (direction == Direction.SW || direction == Direction.SSW || direction == Direction.WSW) {
			overlay.setRGB(0, 12, 4, 4, swArrow, 0, 4);
		} else if (direction == Direction.NW || direction == Direction.WNW || direction == Direction.NNW) {
			overlay.setRGB(0, 0, 4, 4, nwArrow, 0, 4);
		}
		
		if (img.getWidth() != 16) {
			overlay = ImageProvider.zoom(img.getWidth()/16, overlay);
		}
		
		BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = newImg.createGraphics();
		g.drawRenderedImage(img, null);
		g.drawRenderedImage(overlay, null);
		
		return newImg;
	}
}
