package klaue.mcschematictool.blocktypes;



/**
 * Stones are no longer so simple.
 * @author klaue, modified rmcgarry
 *
 */
public class Stone extends DataImageCacheBlock {
	/**
	 * The Type a stone can have
	 * @author klaue, modified rmcgarry
	 */
	public enum StoneType {
		/** Normal */ NORMAL, 
		GRANITE, 
		GRANITE_POLISHED, 
		ANDESITE,
		ANDESITE_POLISHED,
		DIORITE,
		DIORITE_POLISHED
		
	};
	
	private StoneType stoneType = StoneType.NORMAL;
	
	/**
	 * initializes the stone  block
	 * @param data the minecraft data value (representing stone type)
	 */
	public Stone(byte data) {
		super((short)1, data);
		this.type = Type.STONE;
		setData(data);
	}
	
	/**
	 * initializes a normal stone brick
	 */
	public Stone() {
		this(StoneType.NORMAL);
	}
	
	/**
	 * initializes a stone brick
	 * @param brickType the type of this stone brick
	 */
	public Stone(StoneType stoneType) {
		super((short)1);
		this.type = Type.STONE;
		setStoneType(stoneType);
	}
	
	/**
	 * Get the type of the stone brick.
	 * @return the brick type
	 */
	public StoneType getStoneType() {
		return this.stoneType;
	}

	/**
	 * Set the type of the stone brick.
	 * @param brickType the type to set
	 */
	public void setStoneType(StoneType stoneType) {
		this.stoneType = stoneType;
		switch(stoneType) {
			case NORMAL:			this.data = 0;  break;
			case GRANITE:			this.data = 1;  break;
			case GRANITE_POLISHED:	this.data = 2;  break;
			case DIORITE:			this.data = 3;  break;
			case DIORITE_POLISHED:	this.data = 4;  break;
			case ANDESITE:			this.data = 5;  break;
			case ANDESITE_POLISHED:	this.data = 6;  break;
			
		}
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.stoneType = StoneType.NORMAL;				break;
			case 1:  this.stoneType = StoneType.GRANITE;			break;
			case 2:  this.stoneType = StoneType.GRANITE_POLISHED;	break;
			case 3:  this.stoneType = StoneType.DIORITE;			break;
			case 4:  this.stoneType = StoneType.DIORITE_POLISHED;	break;
			case 5:  this.stoneType = StoneType.ANDESITE;			break;
			case 6:  this.stoneType = StoneType.ANDESITE_POLISHED;	break;
			
			default: throw new IllegalArgumentException("illegal stone type value: " + this.data);
		}
		this.data = data;
	}
}
