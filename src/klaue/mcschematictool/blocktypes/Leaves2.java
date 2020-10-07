package klaue.mcschematictool.blocktypes;

/**
 * A Leaf block that can be from the extended different types of Tree (Acacia and Dark Oak)
 * @author klaue
 *
 */
public class Leaves2 extends Block {
	private TreeType leafType = TreeType.OAK;
	
	/**
	 * Initializes the leaf block
	 * @param data the block data
	 */
	public Leaves2(byte data) {
		super((short)161, data);
		this.type = Type.LEAF;
		setData(data);
	}
	
	/**
	 * Initializes the leaf block
	 * @param leafType the type of leaf
	 */
	public Leaves2(TreeType leafType) {
		super((short)161);
		this.type = Type.LEAF;
		setLeafType(leafType);
	}
	
	/**
	 * Set the leaf type
	 * @param type
	 */
	public void setLeafType(TreeType type) {
		switch (type) {
			case ACACIA:		this.data = 0;	break;
			case DARK_OAK:	this.data = 1;	break;
	//		case BIRCH:		this.data = 2;	break;
	//		case JUNGLE:	this.data = 2;	break;
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
			case 0: this.leafType = TreeType.ACACIA;	break;
			case 1: this.leafType = TreeType.DARK_OAK;	break;
			default: throw new IllegalArgumentException("illegal tree type: " + data);
		}
		// 0x8 is just an internal checking bit and can be thrown away
		this.data = data;
	}
}
