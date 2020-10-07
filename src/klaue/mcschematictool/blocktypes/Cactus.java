package klaue.mcschematictool.blocktypes;


/**
 * A cactus block that can be in different states of growth
 * @author klaue
 *
 */
public class Cactus extends Block {
	/**
	 * initializes the cactus block
	 * @param growth 0-15
	 */
	public Cactus(byte growth) {
		super((short)81, growth);
		this.type = Type.CACTUS;
		if (growth < 0 || growth > 0xF) {
			throw new IllegalArgumentException("growth " + growth + "outside boundaries"); 
		}
	}
	
	/**
	 * Get the growth value of the cactus. growth 0 is a newly planted one, growth 15 (0xF) is one that will spawn a new block
	 * atop of it
	 * @return the growth
	 */
	public byte getGrowth() {
		return this.data;
	}

	/**
	 * Set the growth value of the cactus. Strength 0 is a newly planted one, strength 15 (0xF) is one that will spawn a new block
	 * atop of it
	 * @param growth the growth to set
	 */
	public void setGrowth(byte growth) {
		if (growth < 0 || growth > 0xF) {
			throw new IllegalArgumentException("growth " + growth + "outside boundaries"); 
		}
		this.data = growth;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", growth: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setGrowth(data);
	}
}
