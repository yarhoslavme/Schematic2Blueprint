package klaue.mcschematictool.blocktypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A Monster Egg stone brick that can be normal, cracked or mossy
 * @author klaue
 *
 */
public class MonsterEggBricks extends DataImageCacheBlock {
	/**
	 * The Type a stone brick can have
	 * @author klaue
	 */
	public enum BrickType {STONE, COBBLESTONE, STONEBRICK, 
						MOSSYSTONEBRICK, CRACKEDSTONEBRICK, CHISELEDSTONEBRICK};
	
	private BrickType brickType = BrickType.STONE;
	/* To Do:  Add Egg Item image on top of the brick.
	//private static HashMap<BrickType, BufferedImage> dataImageCache = new HashMap<BrickType, BufferedImage>();
	//private static double dataZoomCache = -1;
	
	/**
	 * initializes the stone brick block
	 * @param data the minecraft data value (representing brick type)
	 */
	public MonsterEggBricks(byte data) {
		super((short)97, data);
		this.type = Type.STONEBRICK;
		setData(data);
	}
	
	/**
	 * initializes a normal stone brick
	 */
	public MonsterEggBricks() {
		this(BrickType.STONE);
	}
	
	/**
	 * initializes a stone brick
	 * @param brickType the type of this stone brick
	 */
	public MonsterEggBricks(BrickType brickType) {
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
			case STONE:					this.data = 0;  break;
			case COBBLESTONE:			this.data = 1;  break;
			case STONEBRICK:			this.data = 2;  break;
			case MOSSYSTONEBRICK:		this.data = 3;  break;
			case CRACKEDSTONEBRICK:		this.data = 4;  break;
			case CHISELEDSTONEBRICK:	this.data = 5;  break;
			default: throw new IllegalArgumentException("illegal brick type: " + this.brickType);
		}
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.brickType = BrickType.STONE;					break;
			case 1:  this.brickType = BrickType.COBBLESTONE;			break;
			case 2:  this.brickType = BrickType.STONEBRICK;				break;
			case 3:  this.brickType = BrickType.MOSSYSTONEBRICK;		break;
			case 4:  this.brickType = BrickType.CRACKEDSTONEBRICK;		break;
			case 5:  this.brickType = BrickType.CHISELEDSTONEBRICK;		break;
			
			default: throw new IllegalArgumentException("illegal brick type value: " + this.data);
		}
		this.data = data;
	}

}
