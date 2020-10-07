package klaue.mcschematictool.blocktypes;

/**
 * A Leaf block that can be from different types of Tree
 * @author klaue
 *
 */
public class Leaves extends Block {
	private TreeType leafType = TreeType.OAK;
	
	/**
	 * Initializes the leaf block
	 * @param data the block data
	 */
	public Leaves(byte data) {
		super((short)18, data);
		this.type = Type.LEAF;
		setData(data);
	}
	
	/**
	 * Initializes the leaf block
	 * @param leafType the type of leaf
	 */
	public Leaves(TreeType leafType) {
		super((short)18);
		this.type = Type.LEAF;
		setLeafType(leafType);
	}
	
	/**
	 * Set the leaf type
	 * @param type
	 */
	public void setLeafType(TreeType type) {
		switch (type) {
			case OAK:		this.data = 0;	break;
			case SPRUCE:	this.data = 1;	break;
			case BIRCH:		this.data = 2;	break;
			case JUNGLE:	this.data = 2;	break;
		}
		this.leafType = type;
	}

	/**
	 * Get the leaf type
	 * @return the type of leaf
	 */
	public TreeType getLeafType() {
		return this.leafType;
	}
	
//	@Override
//	public String toString() {
//		return "Leaf, type: " + this.leafType;
//	}
	
	@Override
	public void setData(byte data) {
		byte typeData = (byte)(data&3);
		switch (typeData) {
			case 0: this.leafType = TreeType.OAK;	break;
			case 1: this.leafType = TreeType.SPRUCE;	break;
			case 2: this.leafType = TreeType.BIRCH;		break;
			case 3: this.leafType = TreeType.JUNGLE;		break;
			default: throw new IllegalArgumentException("illegal tree type: " + data);
		}
		// 0x8 is just an internal checking bit and can be thrown away
		this.data = data;
	}
}
