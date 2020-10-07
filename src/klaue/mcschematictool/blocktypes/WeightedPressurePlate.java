package klaue.mcschematictool.blocktypes;


/**
 * A weighted pressure plate can be pressed and be of different materials
 * @author klaue
 *
 */
public class WeightedPressurePlate extends Block {
	/**
	 * The type of the weighted pressure plate
	 * @author klaue
	 */
	public enum WeightedPlateType {/** Light Pressure Plate */ LIGHT, /** Heavy Pressure Plate */ HEAVY}
	
	private WeightedPlateType weightedPlateType;
	private boolean isPressed = false;
	
	/**
	 * initializes the weighted pressure plate
	 * @param id the id (147 (Light) or 148 (Heavy))
	 * @param data 1 if pressed, 0 if not pressed 
	 */
	public WeightedPressurePlate(short id, byte data) {
		super(id, data);
		if (id != 147 && id != 148) {
			throw new IllegalArgumentException("illegal id for weighted pressure plates: " + id);
		}
		this.weightedPlateType = (id == 147) ? WeightedPlateType.LIGHT : WeightedPlateType.HEAVY;
		this.type = Type.WEIGHTEDPRESSUREPLATE;
		this.setData(data);
	}
	
	/**
	 * initializes the weighted pressure plate
	 * @param weightedPlateType the type of plate
	 * @param data 1 if pressed, 0 if not pressed 
	 */
	public WeightedPressurePlate(WeightedPlateType weightedPlateType, byte data) {
		super((short) ((weightedPlateType == WeightedPlateType.LIGHT) ? 147 : 148), data);
		this.weightedPlateType = weightedPlateType;
		this.type = Type.WEIGHTEDPRESSUREPLATE;
		this.setData(data);
	}
	
	/**
	 * initializes the weighted pressure plate
	 * @param weightedPlateType the type of plate
	 * @param isPressed true if this weighted pressure plate is pressed (should not be set manually)
	 */
	public WeightedPressurePlate(boolean isPressed, WeightedPlateType weightedPlateType) {
		super((short) ((weightedPlateType == WeightedPlateType.LIGHT) ? 147 : 148), (byte) (isPressed ? 1 : 0));
		this.isPressed = isPressed;
		this.weightedPlateType = weightedPlateType;
		this.type = Type.WEIGHTEDPRESSUREPLATE;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", " + (this.isPressed ? "pressed" : "not pressed");
	}
	
	@Override
	public void setData(byte data) {
		if (data != 0 && data != 1) throw new IllegalArgumentException("illegal data for weighted pressure plates: " + data);
		this.data = data;
		this.isPressed = (data == 1);
	}
	
	@Override
	protected void setId(short id) {
		if (id != 147 && id != 148) {
			throw new IllegalArgumentException("illegal id for weighted pressure plates: " + id);
		}
		this.id = id;
		this.weightedPlateType = (id == 147) ? WeightedPlateType.LIGHT : WeightedPlateType.HEAVY;
	}

	/**
	 * Returns true if this weighted pressure plate is pressed. 
	 * @return true if pressed
	 */
	public boolean isPressed() {
		return this.isPressed;
	}

	/**
	 * Set if this plate is pressed (should not be set manually)
	 * @param isPressed true if this plate should be pressed
	 */
	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
		this.data = (byte) (isPressed ? 1 : 0);
	}

	/**
	 * @return the type of plate
	 */
	public WeightedPlateType getPlateType() {
		return this.weightedPlateType;
	}

	/**
	 * Sets the type of plate (warning: changes id)
	 * @param plateType the plateType to set
	 */
	public void setPlateType(WeightedPlateType plateType) {
		this.weightedPlateType = plateType;
		this.id = (byte) ((plateType == WeightedPlateType.LIGHT) ? 147 : 148);
	}
}
