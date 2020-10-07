package klaue.mcschematictool.blocktypes;


/**
 * A water block that can be of different heights
 * @author klaue
 *
 */
public class Water extends Block {
	private boolean isStationary = false;
	private boolean isFalling = false;
	private byte level = 0;
	
	/**
	 * initializes the water block
	 * @param id the id of this block. Valid IDs are 8 (running) and 9 (Stationary water)
	 * @param data
	 */
	public Water(short id, byte data) {
		super(id, data);
		if (id != 8 && id != 9) throw new IllegalArgumentException("not a valid water id: " + id); 
		this.isStationary = (id == 9);
		this.type = Type.WATER;
		setData(data);
	}
	
	/**
	 * initializes the water block
	 * @param isStationary true for still water
	 * @param data
	 */
	public Water(boolean isStationary, byte data) {
		super((short) (isStationary ? 9 : 8), data);
		this.isStationary = isStationary;
		this.type = Type.WATER;
		setData(data);
	}
	
	/**
	 * initializes the water block
	 * @param isStationary true for stationary water
	 * @param isFalling true for falling water (only spreads downwards)
	 * @param level The level. Level 0 is a full block. Can go up to (or rather, down to) 7.
	 */
	public Water(boolean isStationary, boolean isFalling, byte level) {
		super((short) (isStationary ? 9 : 8));
		
		if (level < 0 || level > 7) {
			throw new IllegalArgumentException("level " + level + "outside lava boundaries"); 
		}
		
		this.isStationary = isStationary;
		this.isFalling = isFalling;
		this.level = level;
		this.type = Type.WATER;
		
		this.data = (byte) (level + (isFalling ? 8 : 0));
	}
	
	/**
	 * Get the level value of the water. Level 0 is a full block. Can go up to (or rather, down to) 7.
	 * @return the level
	 */
	public byte getLevel() {
		return this.level;
	}

	/**
	 * Set the level value of the water. Level 0 is a full block. Can go up to (or rather, down to) 7.
	 * @param level the level to set
	 */
	public void setLevel(byte level) {
		if (level < 0 || level > 7) {
			throw new IllegalArgumentException("level " + level + "outside water boundaries"); 
		}
		this.level = level;
		this.data = (byte) (level + (this.isFalling ? 8 : 0));
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + 
			(this.isStationary ? "stationary" : "moving") + "), " + 
			(this.isFalling ? "falling" : "not falling") + ", level: " + this.level;
	}
	
	@Override
	public void setData(byte data) {
		if (data > 15 || data < 0) {
			throw new IllegalArgumentException("data value " + data + "outside water boundaries"); 
		}
		
		byte falling = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isFalling = (falling != 0);
		this.level = (byte) (data & 7);

		if (this.level > 7) {
			throw new IllegalArgumentException("level " + this.level + "outside water boundaries"); 
		}
		
		this.data = data;
	}

	/**
	 * Checks if this water is stationary or moving
	 * @return true if water is stationary
	 */
	public boolean isStationary() {
		return this.isStationary;
	}

	/**
	 * Sets if this water is stationary or moving. Warning: Changes block ID
	 * @param isStationary
	 */
	public void setStill(boolean isStationary) {
		this.isStationary = isStationary;
		setId((byte) (isStationary ? 9 : 8));
	}

	/**
	 * Returns true if this water is "falling" and only spreads downward. 
	 * @return true if falling
	 */
	public boolean isFalling() {
		return this.isFalling;
	}

	/**
	 * Set to true if this water is "falling" and only spreads downward. 
	 * @param isFalling true if falling
	 */
	public void setFalling(boolean isFalling) {
		this.isFalling = isFalling;
		this.data = (byte) (this.level + (isFalling ? 8 : 0));
	}
}
