package klaue.mcschematictool.blocktypes;


/**
 * A cobblestone wall Block
 * @author klaue
 *
 */
public class CobblestoneWall extends DataImageCacheBlock {
	/**
	 * Wall types
	 * @author klaue
	 */
	public enum WallType {
		/** Oak trees */	COBBLE,
		/** Spruce trees */	MOSSYCOBBLE
	};
	
	private WallType wallType = WallType.COBBLE;
	
	/**
	 * initializes the wall
	 * @param data 0/1 for different types
	 */
	public CobblestoneWall(byte data) {
		super((short)139, data);
		this.type = Type.COBBLESTONEWALL;
		this.setData(data);
	}
	
	/**
	 * initializes the wall
	 * @param wallType the type of wall
	 */
	public CobblestoneWall(WallType wallType) {
		super((short)5);
		setWallType(wallType);
		this.type = Type.COBBLESTONEWALL;
	}
	
	@Override
	public void setData(byte data) {
		switch (data) {
			case 0:	this.wallType = WallType.COBBLE;		break;
			case 1:	this.wallType = WallType.MOSSYCOBBLE;	break;
			default: throw new IllegalArgumentException("illegal data value for cobblestone walls: " + data);
		}
		this.data = data;
	}

	/**
	 * @return the type of wall
	 */
	public WallType getWallType() {
		return this.wallType;
	}

	/**
	 * Sets the type of the wall
	 * @param wallType the wall type to set
	 */
	public void setWallType(WallType wallType) {
		this.wallType = wallType;
		switch (wallType) {
			case COBBLE:		this.data = (byte)0;	break;
			case MOSSYCOBBLE:	this.data = (byte)1;	break;
		}
	}
}
