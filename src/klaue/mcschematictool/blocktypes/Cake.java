package klaue.mcschematictool.blocktypes;


/**
 * A yummy cake
 * @author klaue
 */
public class Cake extends Block {
	/**
	 * Initializes the cake
	 * @param amountEaten 0-5, where 0 is a full cake
	 */
	public Cake(byte amountEaten) {
		super((short)92, amountEaten);
		this.type = Type.CAKE;
		if (amountEaten < 0 || amountEaten > 5) {
			throw new IllegalArgumentException("amountEaten " + amountEaten + "outside boundaries"); 
		}
	}

	/**
	 * Get the amount of eaten cake. 0 is a full cake, 5 is a nearly eaten one
	 * @return the amount that was eaten
	 */
	public byte getAmountEaten() {
		return this.data;
	}

	/**
	 * Set the amount that was eaten off this cake. 0 is a full cake, 5 is a nearly eaten one
	 * @param amountEaten the strength to set
	 */
	public void setAmountEaten(byte amountEaten) {
		if (amountEaten < 0 || amountEaten > 5) {
			throw new IllegalArgumentException("amountEaten " + amountEaten + "outside boundaries"); 
		}
		this.data = amountEaten;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", amountEaten: " + this.data;
	}
	
	@Override
	public void setData(byte data) {
		setAmountEaten(data);
	}
	
	/**
	 * @return true if this cake is a lie
	 */
	public boolean isALie() {
		return true;
	}
}
