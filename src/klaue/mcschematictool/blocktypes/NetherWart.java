package klaue.mcschematictool.blocktypes;


/**
 * A nether wart block that can be in different states of growth
 * @author klaue
 *
 */
public class NetherWart extends DataImageCacheBlock {
	/**
	 * initializes the nether wart block
	 * @param growth 0-3
	 */
	public NetherWart(byte growth) {
		super((short)115, growth);
		this.type = Type.NETHERWART;
		if (growth < 0 || growth > 3) {
			throw new IllegalArgumentException("growth " + growth + "outside boundaries"); 
		}
	}
	
	/**
	 * Get the growth value of the nether wart. Growth 0 are newly planted ones, growth 3 are ones that can be harvested
	 * @return the growth
	 */
	public byte getGrowth() {
		return this.data;
	}

	/**
	 * Set the growth value of the nether wart. Growth 0 are newly planted ones, growth 3 are ones that can be harvested
	 * @param growth the growth to set, 0-3
	 */
	public void setGrowth(byte growth) {
		if (growth < 0 || growth > 3) {
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
