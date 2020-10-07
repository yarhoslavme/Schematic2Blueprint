package klaue.mcschematictool.blocktypes;


// TODO: make this a directional block for the pillar quartz values
/**
 * A block of quartz that can be normal, cracked or mossy
 * @author klaue
 *
 */
public class QuartzBlock extends DataImageCacheBlock {
	/**
	 * The Type a block of quartz can have
	 * @author klaue
	 */
	public enum QuartzType {/** Normal */ NORMAL, /** chiseled block of quartz */ CHISELED, /** pillar quartz (vertical) */ PILLAR_V,
		/** pillar quartz (north/south) */ PILLAR_N, /** pillar quartz (east/west) */ PILLAR_E};
	
	private QuartzType quartzType = QuartzType.NORMAL;
	
	/**
	 * initializes the block of quartz
	 * @param data the minecraft data value (representing quartz block type)
	 */
	public QuartzBlock(byte data) {
		super((short)155, data);
		this.type = Type.QUARTZBLOCK;
		setData(data);
	}
	
	/**
	 * initializes a normal block of quartz
	 */
	public QuartzBlock() {
		this(QuartzType.NORMAL);
	}
	
	/**
	 * initializes a block of quartz
	 * @param quartzType the type of this block of quartz
	 */
	public QuartzBlock(QuartzType quartzType) {
		super((short)155);
		this.type = Type.QUARTZBLOCK;
		setQuartzType(quartzType);
	}
	
	/**
	 * Get the type of the block of quartz.
	 * @return the quartz type
	 */
	public QuartzType getQuartzType() {
		return this.quartzType;
	}

	/**
	 * Set the type of the block of quartz.
	 * @param quartzType the type to set
	 */
	public void setQuartzType(QuartzType quartzType) {
		this.quartzType = quartzType;
		switch(quartzType) {
			case NORMAL:	this.data = 0;  break;
			case CHISELED:	this.data = 1;  break;
			case PILLAR_V:	this.data = 2;  break;
			case PILLAR_N:	this.data = 3;  break;
			case PILLAR_E:	this.data = 4;  break;
		}
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.quartzType = QuartzType.NORMAL;		break;
			case 1:  this.quartzType = QuartzType.CHISELED;	break;
			case 2:  this.quartzType = QuartzType.PILLAR_V;	break;
			case 3:  this.quartzType = QuartzType.PILLAR_N;	break;
			case 4:  this.quartzType = QuartzType.PILLAR_E;	break;
			default: throw new IllegalArgumentException("illegal quartz type value: " + this.data);
		}
		this.data = data;
	}
}
