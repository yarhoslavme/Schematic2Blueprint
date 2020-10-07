package klaue.mcschematictool;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.Arrays;

import klaue.mcschematictool.blocktypes.Block;
import klaue.mcschematictool.blocktypes.RedstoneWire;
import klaue.mcschematictool.blocktypes.TripWire;


/**
 * One horizontal (2D) slice of the schematic
 * @author klaue
 *
 */
public class Slice {
	private Block[][] blocks;
	
	/**
	 * Returns a slice of the given size initialized to air
	 * @param width
	 * @param height
	 */
	public Slice(int width, int height) {
		this.blocks = new Block[width][];
		
		// initialize to air
		for (int i = 0; i < width; ++i) {
			this.blocks[i] = new Block[height];
			Arrays.fill(this.blocks[i], new Block());
		}
	}
	
	/**
	 * Return the block at [x][y]
	 * @param x
	 * @param y
	 * @return the block
	 * @throws IndexOutOfBoundsException
	 */
	public Block getBlockAt(int x, int y) {
		if (x >= this.blocks.length || x < 0 || y >= this.blocks[x].length || y < 0) {
			throw new IndexOutOfBoundsException(x + " (x) or " + y + " (y) not in slice");
		}
		return this.blocks[x][y];
	}
	
	/**
	 * Sets the block at [x][y]
	 * @param block 
	 * @param x
	 * @param y
	 * @throws IndexOutOfBoundsException
	 */
	public void setBlock(Block block, int x, int y) {
		if (x >= this.blocks.length || x < 0 || y >= this.blocks[x].length || y < 0) {
			throw new IndexOutOfBoundsException(x + " (x) or " + y + " (y) not in slice");
		}
		this.blocks[x][y] = block;
	}
	
	/**
	 * Gets the width
	 * @return the width
	 */
	public int getWidth() {
		return this.blocks.length;
	}

	/**
	 * Gets the height
	 * @return the height
	 */
	public int getHeight() {
		return this.blocks[0].length;
	}
	
	/**
	 * Rotates the whole slice clockwise.
	 */
	public void turnCW() {
		this.turn(true);
	}
	
	/**
	 * Rotates the whole slice counterclockwise.
	 */
	public void turnCCW() {
		this.turn(false);
	}
	
	/**
	 * Rotates the whole slice
	 * @param CW True if rotating should be clockwise
	 */
	public void turn(boolean CW) {
		Block[][] oldSlice = this.blocks;
		int oldWidth = oldSlice.length;
		int oldHeight = oldSlice[0].length;
		
		this.blocks = new Block[oldHeight][];
		
		for (int oldY = 0; oldY < oldHeight; ++oldY) {
			int newX = CW ? ((oldHeight - 1) - oldY) : oldY;
			this.blocks[newX] = new Block[oldWidth];
			for (int oldX = 0; oldX < oldWidth; ++oldX) {
				int newY = CW ? oldX : ((oldWidth - 1) - oldX);
				Block block = oldSlice[oldX][oldY];
				block.turn(CW);
				this.blocks[newX][newY] = block;
			}
		}
	}
	
	/**
	 * Cuts off a part of the slice
	 * @param left How many blocks to cut of on the left
	 * @param top How many blocks to cut of on the top
	 * @param right How many blocks to cut of on the right
	 * @param bottom How many blocks to cut of on the bottom
	 * @throws InvalidParameterException
	 */
	public void cutOff(int left, int top, int right, int bottom) {
		if (this.blocks.length == 0 || this.blocks[0].length == 0) return;
		
		// check for left etc alone in case of integer overflow
		if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
				left >= this.blocks.length || right >= this.blocks.length ||
				top >= this.blocks[0].length || bottom >= this.blocks[0].length ||
				(left + right) >= this.blocks.length || (top + bottom) >= this.blocks[0].length) {
			StringBuffer errMsg = new StringBuffer();
			errMsg.append("Numbers either below zero or too large for the slice: ");
			errMsg.append("left: ").append(left).append(", ");
			errMsg.append("top: ").append(top).append(", ");
			errMsg.append("right: ").append(right).append(", ");
			errMsg.append("bottom: ").append(bottom);
			
			throw new InvalidParameterException(errMsg.toString());
		}
		
		int newWidth = this.blocks.length - left - right;
		int newHeight = this.blocks[0].length - top - bottom;

		Block[][] oldSlice = this.blocks;
		this.blocks = new Block[newWidth][];
		
		for (int xOld = left, xNew = 0; xOld < (oldSlice.length - right); ++xOld, ++xNew) {
			this.blocks[xNew] = new Block[newHeight];
			for (int yOld = top, yNew = 0; yOld < (oldSlice[xOld].length - bottom); ++yOld, ++yNew) {
				this.blocks[xNew][yNew] = oldSlice[xOld][yOld];
			}
		}
	}
	
	/**
	 * Gets the amount of the slice which can be cut off without losing any block but air
	 * @return the number of blocks on the left that can be cut off without damaging anything
	 */
	public int getAirspaceLeft() {
		int airspace = 0;
		for (int x = 0; x < this.getWidth(); ++x) {
			boolean emptyColumn = true;
			for (int y = 0; y < this.getHeight(); ++y) {
				if (this.getBlockAt(x, y).getId() != 0) {
					emptyColumn = false;
					break;
				}
			}
			if (emptyColumn) {
				++airspace;
			} else {
				break;
			}
		}
		return airspace;
	}
	
	/**
	 * Gets the amount of the slice which can be cut off without losing any block but air
	 * @return the number of blocks on the top that can be cut off without damaging anything
	 */
	public int getAirspaceTop() {
		int airspace = 0;
		for (int y = 0; y < this.getHeight(); ++y) {
			boolean emptyColumn = true;
			for (int x = 0; x < this.getWidth(); ++x) {
				if (this.getBlockAt(x, y).getId() != 0) {
					emptyColumn = false;
					break;
				}
			}
			if (emptyColumn) {
				++airspace;
			} else {
				break;
			}
		}
		return airspace;
	}
	
	/**
	 * Gets the amount of the slice which can be cut off without losing any block but air
	 * @return the number of blocks on the right that can be cut off without damaging anything
	 */
	public int getAirspaceRight() {
		int airspace = 0;
		for (int x = this.getWidth() - 1; x >=0; --x) {
			boolean emptyColumn = true;
			for (int y = 0; y < this.getHeight(); ++y) {
				if (this.getBlockAt(x, y).getId() != 0) {
					emptyColumn = false;
					break;
				}
			}
			if (emptyColumn) {
				++airspace;
			} else {
				break;
			}
		}
		return airspace;
	}
	
	/**
	 * Gets the amount of the slice which can be cut off without losing any block but air
	 * @return the number of blocks on the bottom that can be cut off without damaging anything
	 */
	public int getAirspaceBottom() {
		int airspace = 0;
		for (int y = this.getHeight() - 1; y >= 0; --y) {
			boolean emptyColumn = true;
			for (int x = 0; x < this.getWidth(); ++x) {
				if (this.getBlockAt(x, y).getId() != 0) {
					emptyColumn = false;
					break;
				}
			}
			if (emptyColumn) {
				++airspace;
			} else {
				break;
			}
		}
		return airspace;
	}
	
	/**
	 * Checks if this slice is empty
	 * @return true if all blocks of the slice are air blocks
	 */
	public boolean isEmpty() {
		for (int x = 0; x < this.getWidth(); ++x) {
			for (int y = 0; y < this.getHeight(); ++y) {
				if (this.getBlockAt(x, y).getId() != 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int y = 0; y < this.getHeight(); ++y) {
			for (int x = 0; x < this.getWidth(); ++x) {
				sb.append("[" + this.getBlockAt(x, y).toString() + "]");
			}
			if (y != this.getHeight() - 1) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns an ImageGrid with the content of this slice<br>
	 * Note that the images in the grid will be updated if the blocks change
	 * @param zoom the current zoom value (1 is a 16x16 image/block)
	 * @param calculateWires true to calculate the directions of redstone- and tripwires (costly)
	 * @return an imageGrid or null if ImageProvider was not initialized
	 */
	public ImageGrid getImages(double zoom, boolean calculateWires) {
		if (!ImageProvider.isActivated()) return null;
		
		if (calculateWires) {
			for (int i = 0; i < this.blocks.length; ++i) {
				for (int j = 0; j < this.blocks[i].length; ++j) {
					if (this.blocks[i][j].isRedstoneWire()) {
						boolean wireInNorth = false;
						boolean wireInEast = false;
						boolean wireInSouth = false;
						boolean wireInWest = false;
						
						if (j != 0) {
							Block b = this.blocks[i][j-1];
							wireInNorth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
									|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
						}
						if (this.blocks.length - 1 != i) {
							Block b = this.blocks[i+1][j];
							wireInEast = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
									|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
						}
						if (this.blocks[i].length - 1 != j) {
							Block b = this.blocks[i][j+1];
							wireInSouth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
									|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
						}
						if (i != 0) {
							Block b = this.blocks[i-1][j];
							wireInWest = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
									|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
						}
						((RedstoneWire)this.blocks[i][j]).setWireType(wireInNorth, wireInEast, wireInSouth, wireInWest);
					} else if(this.blocks[i][j].isTripwire()) {
						boolean wireInNorth = false;
						boolean wireInEast = false;
						boolean wireInSouth = false;
						boolean wireInWest = false;
						
						if (j != 0) {
							Block b = this.blocks[i][j-1];
							wireInNorth = (b.isTripwire() || b.isTripwireHook());
						}
						if (this.blocks.length - 1 != i) {
							Block b = this.blocks[i+1][j];
							wireInEast = (b.isTripwire() || b.isTripwireHook());
						}
						if (this.blocks[i].length - 1 != j) {
							Block b = this.blocks[i][j+1];
							wireInSouth = (b.isTripwire() || b.isTripwireHook());
						}
						if (i != 0) {
							Block b = this.blocks[i-1][j];
							wireInWest = (b.isTripwire() || b.isTripwireHook());
						}
						((TripWire)this.blocks[i][j]).setWireType(wireInNorth, wireInEast, wireInSouth, wireInWest);
					}
				}
			}
		}
		
		return new ImageGrid(this, zoom);
	}
	
	/**
	 * Exports the current slice as a BufferedImage.
	 * A check for enough memory should be done before calling this or this should be wrapped inside a try-catch for java.lang.OutOfMemoryError
	 * because it'll need ~4 byte of memory per pixel
	 * @param zoom the current zoom value (1 is a 16x16 image/block)
	 * @param calculateRedstoneWires true to calculate the directions of redstone wires (costly)
	 * @param background the background color to use. Null for transparent
	 * @param gridLines the color to use for the grid lines
	 * @return the BufferedImage or null if ImageProvider was not initialized
	 * @see	ImageGrid#exportImage(Color, Color)
	 */
	public BufferedImage exportImage(double zoom, boolean calculateRedstoneWires, Color background, Color gridLines) {
		ImageGrid grid = getImages(zoom, calculateRedstoneWires);
		if (grid == null) return null;
		return grid.exportImage(background, gridLines);
	}
}

