package klaue.mcschematictool.blocktypes;

/**
 * A cauldron
 * @author klaue
 */
public class Cauldron extends Block {
	/**
	 * Initializes the cauldron block
	 * @param remainingFillings how many glass bottles can still be filled with this Cauldron. 0-3
	 */
	public Cauldron(byte remainingFillings) {
		super((short)118, remainingFillings);
		this.type = Type.CAULDRON;
		if (remainingFillings < 0 || remainingFillings > 3) {
			throw new IllegalArgumentException("remaining fillings number " + remainingFillings + " outside boundaries"); 
		}
	}

	/**
	 * Get the number of times you can fill a glass bottle with this cauldron
	 * @return the remaining fillings
	 */
	public byte getRemainingFillings() {
		return this.data;
	}

	/**
	 * Set the number of times you can fill a glass bottle with this cauldron. 0-3
	 * @param remainingFillings the remaining fillings to set
	 */
	public void setRemainingFillings(byte remainingFillings) {
		if (remainingFillings < 0 || remainingFillings > 3) {
			throw new IllegalArgumentException("remaining fillings number " + remainingFillings + "outside boundaries"); 
		}
		this.data = remainingFillings;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", remaining fillings: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setRemainingFillings(data);
	}
}
