package klaue.mcschematictool.blocktypes;


/**
 * A pressure plate can be pressed and be of different materials
 * @author klaue
 *
 */
public class PressurePlate extends Block {
	/**
	 * The type of the pressure plate
	 * @author klaue
	 */
	public enum PlateType {/** Stone Pressure Plate */ STONE, /** Wood Pressure Plate */ WOOD}
	
	private PlateType plateType;
	private boolean isPressed = false;
	
	/**
	 * initializes the pressure plate
	 * @param id the id (72 (Wood) or 70 (Stone))
	 * @param data 1 if pressed, 0 if not pressed 
	 */
	public PressurePlate(short id, byte data) {
		super(id, data);
		if (id != 72 && id != 70) {
			throw new IllegalArgumentException("illegal id for pressure plates: " + id);
		}
		this.plateType = (id == 72) ? PlateType.WOOD : PlateType.STONE;
		this.type = Type.PRESSUREPLATE;
		this.setData(data);
	}
	
	/**
	 * initializes the pressure plate
	 * @param plateType the type of plate
	 * @param data 1 if pressed, 0 if not pressed 
	 */
	public PressurePlate(PlateType plateType, byte data) {
		super((short) ((plateType == PlateType.WOOD) ? 72 : 70), data);
		this.plateType = plateType;
		this.type = Type.PRESSUREPLATE;
		this.setData(data);
	}
	
	/**
	 * initializes the pressure plate
	 * @param plateType the type of plate
	 * @param isPressed true if this pressure plate is pressed (should not be set manually)
	 */
	public PressurePlate(boolean isPressed, PlateType plateType) {
		super((short) ((plateType == PlateType.WOOD) ? 72 : 70), (byte) (isPressed ? 1 : 0));
		this.isPressed = isPressed;
		this.plateType = plateType;
		this.type = Type.PRESSUREPLATE;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", " + (this.isPressed ? "pressed" : "not pressed");
	}
	
	@Override
	public void setData(byte data) {
		if (data != 0 && data != 1) throw new IllegalArgumentException("illegal data for pressure plates: " + data);
		this.data = data;
		this.isPressed = (data == 1);
	}
	
	@Override
	protected void setId(short id) {
		if (id != 72 && id != 70) {
			throw new IllegalArgumentException("illegal id for pressure plates: " + id);
		}
		this.id = id;
		this.plateType = (id == 72) ? PlateType.WOOD : PlateType.STONE;
	}

	/**
	 * Returns true if this pressure plate is pressed. 
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
	public PlateType getPlateType() {
		return this.plateType;
	}

	/**
	 * Sets the type of plate (warning: changes id)
	 * @param plateType the plateType to set
	 */
	public void setPlateType(PlateType plateType) {
		this.plateType = plateType;
		this.id = (byte) ((plateType == PlateType.WOOD) ? 72 : 70);
	}
}
