package klaue.mcschematictool.blocktypes;



/**
 * A redstone lamp can be on or off
 * @author klaue
 *
 */
public class RedstoneLamp extends Block {
	private boolean isOn;
	
	/**
	 * initializes the redstone lamp
	 * @param id the id. valid ids are 123 (off) and 124 (on)
	 */
	public RedstoneLamp(short id) {
		super(id);
		if (id != 123 && id != 124) throw new IllegalArgumentException("illegal id for furnaces: " + id);
		this.type = Type.REDSTONELAMP;
		this.isOn = (id == 124);
	}
	
	/**
	 * initializes the redstone lamp
	 * @param isOn true if the lamp is burning
	 */
	public RedstoneLamp(boolean isOn) {
		super((short) (isOn ? 124 : 123));
		this.type = Type.REDSTONELAMP;
		this.isOn = isOn;
	}
	
	/**
	 * Returns true if the lamp is burning
	 * @return true if burning
	 */
	public boolean isOn() {
		return this.isOn;
	}

	/**
	 * Sets the lamps burning state
	 * @param isOn true if the lamp should be burning
	 */
	public void setOn(boolean isOn) {
		this.isOn = isOn;
		this.setId((byte) (isOn ? 124 : 123));
	}
	
	@Override
	protected void setId(short id) {
		if (id == 123) {
			this.isOn = false;
		} else if (id == 124) {
			this.isOn = true;
		} else {
			throw new IllegalArgumentException("Invalid redstone lamp ID: " + id);
		}
		super.setId(id);
	}
}
