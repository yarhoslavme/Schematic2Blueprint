package klaue.mcschematictool.blocktypes;

/**
 * A jukebox can contain records
 * @author klaue
 *
 */
public class Jukebox extends Block {
	/** The type of the contained disc */
	public enum DiscType {
		/** No disc */ NONE, /** Gold disc */ GOLD, /** Green disc */ GREEN, /** Orange disc */ ORANGE, /** Red disc */ RED,
		/** Lime green disc */ LIMEGREEN("lime green"), /** purple disc */ PURPLE, /** violet disc */ VIOLET, /** black disc */ BLACK,
		/** white disc */ WHITE, /** Sea green disc */ SEAGREEN("sea green"), /** broken disc */ BROKEN;
		
		private String name = null;
		DiscType(){}
		DiscType(String name) { this.name = name; }
		@Override
		public String toString() { return (this.name == null) ? super.toString().toLowerCase() : this.name; }
	};
	
	private DiscType discType = DiscType.NONE;
	
	/**
	 * initializes the Jukebox
	 * @param data disc within, 0 is none, 1 is gold music disc, 2 is green music disc etc
	 */
	public Jukebox(byte data) {
		super((short)84, data);
		this.type = Type.JUKEBOX;
		setData(data);
	}
	
	/**
	 * initializes the Jukebox
	 * @param type disc within
	 */
	public Jukebox(DiscType type) {
		super((short)84);
		this.type = Type.JUKEBOX;
		setDiscContained(type);
	}
	
	/**
	 * Get the record within this jukebox. 1 is gold music disc, 2 is green music disc, 0 is none
	 * @return the record
	 */
	public DiscType getDiscContained() {
		return this.discType;
	}

	/**
	 * Set the disc within this jukebox
	 * @param discType the disc type
	 */
	public void setDiscContained(DiscType discType) {
		switch(discType) {
			case NONE:		this.data = 0;	break;
			case GOLD:		this.data = 1;	break;
			case GREEN:		this.data = 2;	break;
			case ORANGE:	this.data = 3;	break;
			case RED:		this.data = 4;	break;
			case LIMEGREEN:	this.data = 5;	break;
			case PURPLE:	this.data = 6;	break;
			case VIOLET:	this.data = 7;	break;
			case BLACK:		this.data = 8;	break;
			case WHITE:		this.data = 9;	break;
			case SEAGREEN:	this.data = 10;	break;
			case BROKEN:	this.data = 11;	break;
		}
		this.discType = discType;
	}
	
	@Override
	public String toString() {
		if (this.data == 0) return "Jukebox";
		return super.toString() + " with " + this.discType + " record";
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:		this.discType = DiscType.NONE;		break;
			case 1:		this.discType = DiscType.GOLD;		break;
			case 2:		this.discType = DiscType.GREEN;		break;
			case 3:		this.discType = DiscType.ORANGE;	break;
			case 4:		this.discType = DiscType.RED;		break;
			case 5:		this.discType = DiscType.LIMEGREEN;	break;
			case 6:		this.discType = DiscType.PURPLE;	break;
			case 7:		this.discType = DiscType.VIOLET;	break;
			case 8:		this.discType = DiscType.BLACK;		break;
			case 9:		this.discType = DiscType.WHITE;		break;
			case 10:	this.discType = DiscType.SEAGREEN;	break;
			case 11:	this.discType = DiscType.BROKEN;	break;
			default:	throw new IllegalArgumentException("No such disc type: " + data);
		}
		this.data = data;
	}
}
