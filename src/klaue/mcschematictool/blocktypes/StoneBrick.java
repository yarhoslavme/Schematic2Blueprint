package klaue.mcschematictool.blocktypes;



/**
 * A stone brick that can be normal, cracked or mossy
 * @author klaue
 *
 */
public class StoneBrick extends DataImageCacheBlock {
	/**
	 * The Type a stone brick can have
	 * @author klaue
	 */
	public enum BrickType {/** Normal */ NORMAL, /** Mossy stone brick */ MOSSY, /** Cracked stone brick */ CRACKED, /** Circle stone brick */ CIRCLE};
	
	private BrickType brickType = BrickType.NORMAL;
	
	/**
	 * initializes the stone brick block
	 * @param data the minecraft data value (representing brick type)
	 */
	public StoneBrick(byte data) {
		super((short)98, data);
		this.type = Type.STONEBRICK;
		setData(data);
	}
	
	/**
	 * initializes a normal stone brick
	 */
	public StoneBrick() {
		this(BrickType.NORMAL);
	}
	
	/**
	 * initializes a stone brick
	 * @param brickType the type of this stone brick
	 */
	public StoneBrick(BrickType brickType) {
		super((short)98);
		this.type = Type.STONEBRICK;
		setBrickType(brickType);
	}
	
	/**
	 * Get the type of the stone brick.
	 * @return the brick type
	 */
	public BrickType getBrickType() {
		return this.brickType;
	}

	/**
	 * Set the type of the stone brick.
	 * @param brickType the type to set
	 */
	public void setBrickType(BrickType brickType) {
		this.brickType = brickType;
		switch(brickType) {
			case NORMAL:	this.data = 0;  break;
			case MOSSY:		this.data = 1;  break;
			case CRACKED:	this.data = 2;  break;
			case CIRCLE:	this.data = 3;  break;
		}
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.brickType = BrickType.NORMAL;		break;
			case 1:  this.brickType = BrickType.MOSSY;		break;
			case 2:  this.brickType = BrickType.CRACKED;	break;
			case 3:  this.brickType = BrickType.CIRCLE;		break;
			default: throw new IllegalArgumentException("illegal brick type value: " + this.data);
		}
		this.data = data;
	}
}
