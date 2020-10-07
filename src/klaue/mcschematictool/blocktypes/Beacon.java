package klaue.mcschematictool.blocktypes;

import klaue.mcschematictool.itemtypes.Potion;



/**
 * A beacon can have levels (pyramid height) and two effects
 * @author klaue
 *
 */
public class Beacon extends Block {
	private int primaryEffect = 0;
	private int secondaryEffect = 0;
	private int level = 0;
	
	/**
	 * initializes the Beacon with no effects
	 */
	public Beacon() {
		this(0, 0, 0);
	}
	
	/**
	 * initializes the Beacon
	 * @param primaryEffect the primary effect
	 * @param secondaryEffect the secondary effect
	 * @param level the level (height of pyramid)
	 */
	public Beacon(int primaryEffect, int secondaryEffect, int level) {
		super((short)138);
		this.type = Type.BEACON;
		
		setPrimaryEffect(primaryEffect);
		setSecondaryEffect(secondaryEffect);
		setLevel(level);
	}
	
	@Override
	public String toString() {
		String primary = this.getPrimaryEffectName();
		String secondary = this.getSecondaryEffectName();
		String text = super.toString();
		if (primary != null) {
			text += ", primary power: " + primary;
			if (secondary != null) {
				text += ", secondary power: " + secondary;
			}
		}
		return text + ", level " + this.getLevel();
	}

	/**
	 * @return the primary effect or 0 if none
	 */
	public int getPrimaryEffect() {
		return this.primaryEffect;
	}
	
	/**
	 * @return the name of the primary effect or null if none
	 */
	public String getPrimaryEffectName() {
		if (this.primaryEffect == 0) return null;
		return Potion.potionEffects.get((byte)this.primaryEffect);
	}

	/**
	 * @param primaryEffect the primary effect to set or 0 for none
	 */
	public void setPrimaryEffect(int primaryEffect) {
		int effect = (primaryEffect < 0) ? 0 : primaryEffect;
		if (effect != 0 && !Potion.potionEffects.containsKey((byte)effect)) {
			// invalid value
			throw new IllegalArgumentException("primary effect " + effect + " is not a real effect"); 
		}
		this.primaryEffect = effect;
	}

	/**
	 * @return the secondary effect or 0 if none
	 */
	public int getSecondaryEffect() {
		return this.secondaryEffect;
	}
	
	/**
	 * @return the name of the secondary effect or null if none
	 */
	public String getSecondaryEffectName() {
		if (this.secondaryEffect == 0) return null;
		return Potion.potionEffects.get((byte)this.secondaryEffect);
	}

	/**
	 * @param secondaryEffect the secondary effect to set or 0 for none
	 */
	public void setSecondaryEffect(int secondaryEffect) {
		int effect = (secondaryEffect < 0) ? 0 : secondaryEffect;
		if (effect != 0 && !Potion.potionEffects.containsKey((byte)effect)) {
			// invalid value
			throw new IllegalArgumentException("secondary effect " + effect + " is not a real effect"); 
		}
		this.secondaryEffect = effect;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = (level < 0) ? 0 : level;
	}
	
}
