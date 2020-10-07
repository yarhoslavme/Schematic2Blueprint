package klaue.mcschematictool.blocktypes;


/**
 * A crops block (wheat, carrots, potatoes) that can be in different states of growth
 * @author klaue
 *
 */
public class Crop extends Block {
	/**
	 * initializes the crops block
	 * @param id legal ids for crops are 59 (Wheat), 141 (carrots) and 142 (potatoes)
	 * @param growth 0-7
	 */
	public Crop(short id, byte growth) {
		super(id, growth);
		this.type = Type.CROPS;
		if (id != 59 && id != 141 && id != 142) {
			throw new IllegalArgumentException("not a valid crop id: " + id); 
		}
		if (growth < 0 || growth > 7) {
			throw new IllegalArgumentException("growth " + growth + "outside boundaries"); 
		}
	}
	
	/**
	 * Get the growth value of the crops. Growth 0 are newly planted ones, growth 7 are ones that can be harvested
	 * @return the growth
	 */
	public byte getGrowth() {
		return this.data;
	}

	/**
	 * Set the growth value of the crops. Growth 0 are newly planted ones, growth 7 are ones that can be harvested
	 * @param growth the growth to set
	 */
	public void setGrowth(byte growth) {
		if (growth < 0 || growth > 7) {
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
