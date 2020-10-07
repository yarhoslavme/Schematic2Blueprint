package klaue.mcschematictool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;

import klaue.mcschematictool.blocktypes.Block;
import klaue.mcschematictool.blocktypes.RedstoneWire;
import klaue.mcschematictool.blocktypes.StoneSlab;
import klaue.mcschematictool.blocktypes.TripWire;
import klaue.mcschematictool.blocktypes.WoodenSlab;

/**
 * A SliceStack represents a Stack of slices, therefore the 3D Schematics
 * @author klaue
 *
 */
public class SliceStack implements Iterable<Slice> {
	private ArrayList<Slice> stack;
	
	/**
	 * Returns a new, empty SliceStack
	 */
	public SliceStack() {
		this.stack = new ArrayList<Slice>();
	}
	
	/**
	 * Returns a new SliceStack of the given size filled with air blocks 
	 * @param height
	 * @param length (width of the slice)
	 * @param width (depth - height of the slize)
	 */
	public SliceStack(int height, int length, int width) {
		this.stack = new ArrayList<Slice>();
		for (int i = 0; i < height; ++i) {
			this.stack.add(new Slice(length, width));
		}
	}
	
	/**
	 * Adds a slice to the top of the stack
	 * @param slice the new slice
	 * @throws IllegalArgumentException if the new slice is of different size than the others
	 */
	public void addSlice(Slice slice) {
		if (slice == null) {
			throw new IllegalArgumentException("A null slice is not allowed");
		}
		if (!this.stack.isEmpty()) {
			if (this.stack.get(0).getWidth() != slice.getWidth() ||
					this.stack.get(0).getHeight() != slice.getHeight()) {
				throw new IllegalArgumentException("The new slice is of different size than the other(s)");
			}
		}
		
		this.stack.add(slice);
	}
	
	/**
	 * Returns the slice at the specified index, where 0 is the lowest one
	 * @param index
	 * @return the slice
	 */
	public Slice getSlice(int index) {
		return this.stack.get(index);
	}
	
	/**
	 * Removes "Whitespace", eg air blocks, from all sides of the stack, so that the resulting stack is the smallest cuboid without
	 * removing any non-air-blocks
	 */
	public void trim() {
		if (this.stack.size() == 0) return;
		
		// remove empty stacks at the top
		while (this.stack.size() > 0 && this.stack.get(0).isEmpty()) {
			this.stack.remove(0);
		}
		
		// remove empty stacks at the bottom
		while (this.stack.size() > 0 && this.stack.get(this.stack.size() - 1).isEmpty()) {
			this.stack.remove(this.stack.size() - 1);
		}
		
		// get the amount to cut off from all sites
		int left = Integer.MAX_VALUE, top = Integer.MAX_VALUE, right = Integer.MAX_VALUE, bottom = Integer.MAX_VALUE;
		for (Slice s : this.stack) {
			int currentLeft = s.getAirspaceLeft();
			int currentTop = s.getAirspaceTop();
			int currentRight = s.getAirspaceRight();
			int currentBottom = s.getAirspaceBottom();
			
			if (currentLeft < left) left = currentLeft;
			if (currentTop < top) top = currentTop;
			if (currentRight < right) right = currentRight;
			if (currentBottom < bottom) bottom = currentBottom;
		}
		
		// cut off the appropriate amount
		for (Slice s : this.stack) {
			s.cutOff(left, top, right, bottom);
		}
	}
	
	/**
	 * Get the stacks height
	 * @return the stacks height
	 */
	public int getHeight() {
		return this.stack.size();
	}

	/**
	 * Get the stacks length (width of slice)
	 * @return the stacks length
	 */
	public int getLength() {
		if (this.stack.isEmpty()) return 0;
		return this.stack.get(0).getWidth();
	}
	
	/**
	 * Get the stacks width (depth - height of slice)
	 * @return the stacks width (depth)
	 */
	public int getWidth() {
		if (this.stack.isEmpty()) return 0;
		return this.stack.get(0).getHeight();
	}
	
	@Override
	public Iterator<Slice> iterator() {
		return this.stack.iterator();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = this.stack.size() - 1; i >= 0; --i) {
			sb.append(this.stack.get(i).toString());
			sb.append("\n-------------\n");
		}
		return sb.toString();
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
		for (Slice s : this.stack) {
			s.turn(CW);
		}
	}
	
	/**
	 * Cuts off a part of the slicestack
	 * @param top How many blocks to cut of on the top
	 * @param bottom How many blocks to cut of on the bottom
	 * @param north How many blocks to cut of in the north
	 * @param east How many blocks to cut of in the east (right)
	 * @param south How many blocks to cut of in the south
	 * @param west How many blocks to cut of in the west (left)
	 * @throws InvalidParameterException
	 */
	public void cutOff(int top, int bottom, int north, int east, int south, int west) {
		if (this.stack.size() == 0 || this.getSlice(0).getWidth() == 0 || this.getSlice(0).getHeight() == 0) return;
		
		// check for top etc alone in case of integer overflow
		if (top < 0 || bottom < 0 || north < 0 || east < 0 || south < 0 || west < 0 ||
				top >= this.stack.size()						|| bottom >= this.stack.size() ||
				west >= this.getSlice(0).getWidth()				|| east >= this.getSlice(0).getWidth() ||
				north >= this.getSlice(0).getHeight()			|| south >= this.getSlice(0).getHeight() ||
				(top + bottom) >= this.stack.size()				||
				(west + east) >= this.getSlice(0).getWidth() 	|| (north + south) >= this.getSlice(0).getHeight()) {
			StringBuffer errMsg = new StringBuffer();
			errMsg.append("Numbers either below zero or too large for the slicestack: ");
			errMsg.append("top: ").append(top).append(", ");
			errMsg.append("bottom: ").append(bottom).append(", ");
			errMsg.append("north: ").append(north).append(", ");
			errMsg.append("east: ").append(east).append(", ");
			errMsg.append("south: ").append(south).append(", ");
			errMsg.append("west: ").append(west);
			
			throw new InvalidParameterException(errMsg.toString());
		}
		
		ArrayList<Slice> newStack = new ArrayList<Slice>();
		// cut off stuff:
		for (int i = bottom; i < (this.stack.size() - top); ++i) {
			Slice s = this.stack.get(i);
			s.cutOff(west, north, east, south);
			newStack.add(this.stack.get(i));
		}
		this.stack = newStack;
		System.gc();
	}
	
	/**
	 * Returns an ImageGridStack with the content of this stack.<br>
	 * Note that the images in the grids will be updated if the blocks change, so you have to call this or Slice.getImages() or Block.getImage()
	 * for every change
	 * @param zoom the current zoom value (min 1, since a 16x16 image/block is small enough)
	 * @param calculateRedstoneWires true to calculate the directions of redstone wires (costly)
	 * @return an ImageGridStack or null if ImageProvider was not initialized
	 */
	public ImageGridStack getImages(double zoom, boolean calculateRedstoneWires) {
		if (!ImageProvider.isActivated()) return null;
		ImageGrid[] imgGrid = new ImageGrid[this.stack.size()];
		for (int idx = 0; idx < this.stack.size(); ++idx) {
			if (!calculateRedstoneWires) {
				imgGrid[idx] = this.stack.get(idx).getImages(zoom, false);
			} else {
				// Note: this is basically a copy of slice.getImages(). I know that this is ugly, but what can I do..
				Slice slice = this.stack.get(idx);
				for (int i = 0; i < slice.getWidth(); ++i) {
					for (int j = 0; j < slice.getHeight(); ++j) {
						if (slice.getBlockAt(i, j).isRedstoneWire()) {
							boolean wireInNorth = false;
							boolean wireInEast = false;
							boolean wireInSouth = false;
							boolean wireInWest = false;
							
							// same level
							if (j != 0) {
								Block b = slice.getBlockAt(i, j - 1);
								wireInNorth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
										|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
							}
							if (slice.getWidth() - 1 != i) {
								Block b = slice.getBlockAt(i + 1, j);
								wireInEast = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
										|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
							}
							if (slice.getHeight() - 1 != j) {
								Block b = slice.getBlockAt(i, j + 1);
								wireInSouth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
										|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
							}
							if (i != 0) {
								Block b = slice.getBlockAt(i - 1, j);
								wireInWest = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
										|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
							}
							
							// upper level (only if there is a upper level and the block above is air)
							if ((idx != this.stack.size() - 1) && !blockBlocksWire(this.stack.get(idx + 1).getBlockAt(i, j))) {
								Slice upperSlice = this.stack.get(idx + 1);
								if (j != 0 && !wireInNorth) {
									Block b = upperSlice.getBlockAt(i, j - 1);
									wireInNorth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (slice.getWidth() - 1 != i && !wireInEast) {
									Block b = upperSlice.getBlockAt(i + 1, j);
									wireInEast = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (slice.getHeight() - 1 != j && !wireInSouth) {
									Block b = upperSlice.getBlockAt(i, j + 1);
									wireInSouth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (i != 0 && !wireInWest) {
									Block b = upperSlice.getBlockAt(i - 1, j);
									wireInWest = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
							}
							
							// lower level (only if there is a lower level and there's no block in the way on the side)
							if ((idx != 0)) {
								Slice lowerSlice = this.stack.get(idx - 1);
								if (j != 0 && !wireInNorth && !blockBlocksWire(slice.getBlockAt(i, j - 1))) {
									Block b = lowerSlice.getBlockAt(i, j - 1);
									wireInNorth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (slice.getWidth() - 1 != i && !wireInEast && !blockBlocksWire(slice.getBlockAt(i + 1, j))) {
									Block b = lowerSlice.getBlockAt(i + 1, j);
									wireInEast = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (slice.getHeight() - 1 != j && !wireInSouth && !blockBlocksWire(slice.getBlockAt(i, j + 1))) {
									Block b = lowerSlice.getBlockAt(i, j + 1);
									wireInSouth = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
								if (i != 0 && !wireInWest && !blockBlocksWire(slice.getBlockAt(i - 1, j))) {
									Block b = lowerSlice.getBlockAt(i - 1, j);
									wireInWest = (b.isRedstoneWire() || b.isRedstoneTorch() || b.isLever() || b.isPressurePlate() || b.isButton()
											|| b.isDetectorRail() || b.isRepeater() || b.isFenceGate());
								}
							}
							
							
							((RedstoneWire)slice.getBlockAt(i, j)).setWireType(wireInNorth, wireInEast, wireInSouth, wireInWest);
						}  else if(slice.getBlockAt(i, j).isTripwire()) {
							// tripwire is only two dimensional e.g. on slice level
							boolean wireInNorth = false;
							boolean wireInEast = false;
							boolean wireInSouth = false;
							boolean wireInWest = false;
							
							if (j != 0) {
								Block b = slice.getBlockAt(i, j-1);
								wireInNorth = (b.isTripwire() || b.isTripwireHook());
							}
							if (slice.getWidth() - 1 != i) {
								Block b = slice.getBlockAt(i+1, j);
								wireInEast = (b.isTripwire() || b.isTripwireHook());
							}
							if (slice.getHeight() - 1 != j) {
								Block b = slice.getBlockAt(i, j+1);
								wireInSouth = (b.isTripwire() || b.isTripwireHook());
							}
							if (i != 0) {
								Block b = slice.getBlockAt(i-1, j);
								wireInWest = (b.isTripwire() || b.isTripwireHook());
							}
							((TripWire)slice.getBlockAt(i, j)).setWireType(wireInNorth, wireInEast, wireInSouth, wireInWest);
						}
					}
				}
				imgGrid[idx] = this.stack.get(idx).getImages(zoom, false); // false for redstone calc because that was allready done
				
			}
		}
		return new ImageGridStack(imgGrid);
	}
	
	/**
	 * Checks if this block would block wire paths
	 * @param b the block
	 * @return true if the block would.. block
	 */
	private boolean blockBlocksWire(Block b) {
		if (b.isBed() || b.isTorch() || b.isStair() || b.isSign() || b.isLadder()
				|| b.getId() == 0  // air
				|| b.getId() == 20 // glass
				|| b.getId() == 52 // monster spawner
				|| b.getId() == 79 // ice
				|| b.getId() == 85 // fence
				|| (b.isStoneSlab() && !((StoneSlab)b).isDoubleSlab())
				|| (b.isWoodenSlab() && !((WoodenSlab)b).isDoubleSlab())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Exports the SliceStack to a text in the format of the "Builders" mod
	 * @param title The title/name of the blueprint 
	 * @return The text in an Arraylist
	 */
	public ArrayList<String> exportToText(String title) {
		ArrayList<String> text = new ArrayList<String>();
		text.add("// Txt export provided by MCSchematicTools and Schematic2Blueprint");
		text.add("##" + title);

		for (int i = 0; i < this.getHeight(); ++i) {
			text.add("##Layer " + (i + 1));
			Slice s = this.getSlice(i);
			for (int y = 0; y < s.getHeight(); ++y) {
				int width = s.getWidth();
				StringBuffer sb = new StringBuffer((width*2) - 1);
				for (int x = 0; x < width; ++x) {
					if (x != 0) sb.append(' ');
					sb.append(s.getBlockAt(x, y).getId());
				}
				text.add(sb.toString());
			}
			text.add("##End Layer");
		}
		
		return text;
	}
	
	/**
	 * Exports the SliceStack to a text file in the format of the "Builders" mod
	 * @param title The title/name of the blueprint 
	 * @param file The file to save the txt to. Note, contents will be overridden
	 * @throws FileNotFoundException 
	 */
	public void exportToTextFile(String title, File file) throws FileNotFoundException {
		ArrayList<String> text = exportToText(title);
		PrintWriter writer = new PrintWriter(file);
		for (String line : text) {
			writer.println(line);
		}
		writer.flush();
		writer.close();
	}
}
