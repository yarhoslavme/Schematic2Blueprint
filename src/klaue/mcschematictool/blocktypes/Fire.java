package klaue.mcschematictool.blocktypes;

/**
 * A fiery fire block
 * @author klaue
 */
public class Fire extends Block {
	/**
	 * Initializes the fire block
	 * @param strength 0-15
	 */
	public Fire(byte strength) {
		super((short)51, strength);
		this.type = Type.FIRE;
		if (strength < 0 || strength > 0xF) {
			throw new IllegalArgumentException("strength " + strength + "outside boundaries"); 
		}
	}

	/**
	 * Get the strength of the fire. Strength 0 is a newly set fire, strength 15 (0xF) is an "eternal" fire which receives no
	 * block updates ingame
	 * @return the strength
	 */
	public byte getStrength() {
		return this.data;
	}

	/**
	 * Set the strength of the fire. Strength 0 is a newly set fire, strength 15 (0xF) is an "eternal" fire which receives no
	 * block updates ingame
	 * @param strength the strength to set
	 */
	public void setStrength(byte strength) {
		if (strength < 0 || strength > 0xF) {
			throw new IllegalArgumentException("strength " + strength + "outside boundaries"); 
		}
		this.data = strength;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", strength: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setStrength(data);
	}
}
