package klaue.mcschematictool.blocktypes;


/**
 * A farmland block with wetness
 * @author klaue
 *
 */
public class Farmland extends Block {
	/**
	 * initializes the farmland block
	 * @param wetness 0-8
	 */
	public Farmland(byte wetness) {
		super((short)60, wetness);
		this.type = Type.FARMLAND;
		if (wetness < 0 || wetness > 8) {
			throw new IllegalArgumentException("wetness " + wetness + "outside boundaries"); 
		}
	}
	
	/**
	 * Get the wetness value of the farmland. 0 is dry, 8 is the wettest. This is ingame set by the distance to the next water block
	 * @return the wetness
	 */
	public byte getWetness() {
		return this.data;
	}

	/**
	 * Get the wetness value of the farmland. 0 is dry, 8 is the wettest. This is ingame set by the distance to the next water block,
	 * so it may not be a good idea to set this.
	 * @param wetness the wetness to set
	 */
	public void setWetness(byte wetness) {
		if (wetness < 0 || wetness > 8) {
			throw new IllegalArgumentException("wetness " + wetness + "outside boundaries"); 
		}
		this.data = wetness;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", wetness: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setWetness(data);
	}
}
