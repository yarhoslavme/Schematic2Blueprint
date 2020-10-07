package klaue.mcschematictool.blocktypes;

/**
 * A lava block that can be of different heights
 * @author klaue
 *
 */
public class Lava extends Block {
	private boolean isStationary = false;
	private boolean isFalling = false;
	private byte level = 0;
	
	/**
	 * initializes the lava block
	 * @param id the block id. valid ids are 10 (running) and 11 (stationary)
	 * @param data
	 */
	public Lava(short id, byte data) {
		super(id, data);
		if (id != 10 && id != 11) throw new IllegalArgumentException("invalid id for lava: " + id); 
		this.isStationary = (id == 11);
		this.type = Type.LAVA;
		setData(data);
	}
	
	/**
	 * initializes the lava block
	 * @param isStationary true for stationary lava
	 * @param data
	 */
	public Lava(boolean isStationary, byte data) {
		super((short) (isStationary ? 11 : 10), data);
		this.isStationary = isStationary;
		this.type = Type.LAVA;
		setData(data);
	}
	
	/**
	 * initializes the lava block
	 * @param isStationary true for stationary lava
	 * @param isFalling true for falling lava (only spreads downwards)
	 * @param level The level. Level 0 is a full block, 6 a nearly empty one. Valid values are 0, 2, 4 and 6
	 */
	public Lava(boolean isStationary, boolean isFalling, byte level) {
		super((short) (isStationary ? 11 : 10));
		
		if (level != 0 && level != 2 && level != 4 && level != 6) {
			throw new IllegalArgumentException("level " + level + "outside lava boundaries"); 
		}
		
		this.isStationary = isStationary;
		this.isFalling = isFalling;
		this.level = level;
		this.type = Type.LAVA;
		
		this.data = (byte) (level + (isFalling ? 8 : 0));
	}
	
	/**
	 * Get the level value of the lava. Level 0 is a full block, 6 a nearly empty one. Valid values are 0, 2, 4 and 6
	 * @return the level
	 */
	public byte getLevel() {
		return this.level;
	}

	/**
	 * Set the level value of the lava. Level 0 is a full block, 6 a nearly empty one. Valid values are 0, 2, 4 and 6
	 * @param level the level to set
	 */
	public void setLevel(byte level) {
		if (level != 0 && level != 2 && level != 4 && level != 6) {
			throw new IllegalArgumentException("level " + level + "outside lava boundaries"); 
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
		if (data > 14 || data < 0) {
			throw new IllegalArgumentException("data value " + data + "outside lava boundaries"); 
		}
		
		byte falling = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isFalling = (falling != 0);
		this.level = (byte) (data & 7);

		if (this.level != 0 && this.level != 2 && this.level != 4 && this.level != 6) {
			throw new IllegalArgumentException("level " + this.level + "outside lava boundaries"); 
		}
		
		this.data = data;
	}

	/**
	 * Checks if this lava is stationary
	 * @return true if lava is stationary
	 */
	public boolean isStationary() {
		return this.isStationary;
	}

	/**
	 * Sets if this lava is stationary. Warning: Changes block ID
	 * @param isStationary
	 */
	public void setStationary(boolean isStationary) {
		this.isStationary = isStationary;
		setId((byte) (isStationary ? 11 : 10));
	}

	/**
	 * Returns true if this lava is "falling" and only spreads downward. 
	 * @return true if falling
	 */
	public boolean isFalling() {
		return this.isFalling;
	}

	/**
	 * Set to true if this lava is "falling" and only spreads downward. 
	 * @param isFalling true if falling
	 */
	public void setFalling(boolean isFalling) {
		this.isFalling = isFalling;
		this.data = (byte) (this.level + (isFalling ? 8 : 0));
	}
}
