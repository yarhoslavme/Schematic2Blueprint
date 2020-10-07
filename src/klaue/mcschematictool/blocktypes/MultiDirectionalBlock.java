package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashSet;

import klaue.mcschematictool.blocktypes.DirectionalBlock.Direction;

/**
 * A block which can hold multiple directions
 * @author klaue
 */
public abstract class MultiDirectionalBlock extends Block {
	/**
	 * the directions this block faces
	 */
	protected HashSet<Direction> directions;
	
	/**
	 * @see Block
	 */
	protected MultiDirectionalBlock() {
		super();
	}
	/**
	 * @param id 
	 * @see Block
	 */
	protected MultiDirectionalBlock(short id) {
		super(id);
	}
	/**
	 * @param id 
	 * @param data 
	 * @see Block
	 */
	protected MultiDirectionalBlock(short id, byte data) {
		super(id, data);
	}

	@Override
	public abstract void turn(boolean CW);
	
	/**
	 * returns the directions this block faces or the directions of the blocks this block is attached to, depending on the type of block
	 * @return The direction.
	 */
	public HashSet<Direction> getDirections() {
		return this.directions;
	}
	
	/**
	 * sets the directions this block faces or the directions of the block this block is attached to, depending on the type of block
	 * @param directions
	 */
	public abstract void setDirections(HashSet<Direction> directions);
	
	@Override
	public boolean isMultiDirectional() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.directions == null) ? 0 : this.directions.hashCode());
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
		MultiDirectionalBlock other = (MultiDirectionalBlock) obj;
		if (this.directions == null) {
			if (other.directions != null) return false;
		} else if (!this.directions.equals(other.directions)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds red arrows to the image, pointing in all the given directions. If one direction is not in the 45° range, two arrows
	 * will be used to show the direction, for example, NNW will have an arrow pointing to north and one pointing to NW.
	 * A new image instance will be returned, the original image will not be changed.
	 * @param directions the directions where the arrows should point
	 * @param img the image to have the arrow added
	 * @return the arrow'd image
	 */
	protected static BufferedImage addArrowsToImage(HashSet<Direction> directions, final BufferedImage img) {
		if (directions.size() == 1 && directions.contains(Direction.NONE)) return img;
		
		BufferedImage newImg = img;
		for (Direction dir : directions) {
			newImg = DirectionalBlock.addArrowToImage(dir, newImg);
		}
		
		return newImg;
	}
}
