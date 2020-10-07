package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JToolTip;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.ImageToolTip;

/**
 * A sign is a "block" with text
 * @author klaue
 *
 */
public class Sign extends DirectionalBlock {
	private static HashMap<Direction, BufferedImage> wallsignImageCache = new HashMap<Direction, BufferedImage>();
	private static HashMap<Direction, BufferedImage> signImageCache = new HashMap<Direction, BufferedImage>();
	private static double signZoomCache = -1;
	
	/**
	 * An array of exactly 4 Strings corresponding to the 4 lines of text of the sign
	 */
	private String[] text = null;
	
	private boolean isWallSign = false;
	
	/**
	 * Initializes the sign. The new sign will contain exactly 4 lines of text, some or all of which may be empty
	 * @param text The signs text or null for an emty sign
	 * @param id the id (valid values are 63 and 68)
	 * @param data the direction as a minecraft data value
	 */
	public Sign(String[] text, short id, byte data) {
		super (id, data);
		if (id != 63 && id != 68) throw new IllegalArgumentException("Invalid sign id: " + id);
		this.type = Type.SIGN;
		
		this.isWallSign = (id == 68);
		
		if (text == null || text.length == 0) {
			// empty sign
			this.text = new String[4];
			Arrays.fill(this.text, "");
		} else if (text.length > 4) {
			throw new IllegalArgumentException("Signs can only have up to 4 lines of text");
		} else if (text.length == 4) {
			this.text = text;
		} else {
			this.text = new String[4];
			Arrays.fill(this.text, "");
			for (int i = 0; i < text.length; ++i) {
				this.text[i] = text[i];
			}
		}
		
		setDirectionByCurData();
	}
	
	/**
	 * Initializes the sign. The new sign will contain exactly 4 lines of text, some or all of which may be empty
	 * @param text The signs text or null for an emty sign
	 * @param isWallSign true if this sign is a wall sign (ID 68), false if it's a freestanding one (ID 63)
	 * @param data the direction as a minecraft data value
	 */
	public Sign(String[] text, boolean isWallSign, byte data) {
		super ((byte) (isWallSign ? 68 : 63), data);
		this.type = Type.SIGN;
		
		this.isWallSign = isWallSign;
		
		if (text == null || text.length == 0) {
			// empty sign
			this.text = new String[4];
			Arrays.fill(this.text, "");
		} else if (text.length > 4) {
			throw new IllegalArgumentException("Signs can only have up to 4 lines of text");
		} else if (text.length == 4) {
			this.text = text;
		} else {
			this.text = new String[4];
			Arrays.fill(this.text, "");
			for (int i = 0; i < text.length; ++i) {
				this.text[i] = text[i];
			}
		}
		
		setDirectionByCurData();
	}
	
	/**
	 * Initializes the sign. The new sign will contain exactly 4 lines of text, some or all of which may be empty
	 * @param text The signs text or null for an emty sign
	 * @param isWallSign true if this sign is a wall sign (ID 68), false if it's a freestanding one (ID 63)
	 * @param direction the direction
	 */
	public Sign(String[] text, boolean isWallSign, Direction direction) {
		this(text, isWallSign, (byte)2); // note: value 2 will not throw an IllegalArgumentException at setDirectionByCurData
		setDirection(direction);
	}

	/**
	 * Gets the signs text
	 * @return the text of the sign, in an array of 4 lines
	 */
	public String[] getText() {
		return this.text;
	}

	/**
	 * Set the text of the sign
	 * @param text the text of the sign, in an array of 4 lines
	 */
	public void setText(String[] text) {
		if (text == null || text.length != 4) {
			throw new IllegalArgumentException("Text array has to be non-null and exactly 4 items long");
		}
		this.text = text;
	}

	/**
	 * Checks if this sign is a wall sign or a free standing one
	 * @return true if this sign is a wall sign
	 */
	public boolean isWallSign() {
		return this.isWallSign;
	}

	/**
	 * Sets if this sign is a wall sign or a free standing one. Warning: Will change block ID
	 * @param isWallSign true if this sign is a wall sign
	 */
	public void setWallSign(boolean isWallSign) {
		setId((byte) (isWallSign ? 68 : 63));
		this.isWallSign = isWallSign;
	}
	
	/**
	 * Sets the direction member value according to the data tag. See the minecraft data tag description
	 */
	private void setDirectionByCurData() {
		if (this.isWallSign) {
			switch(this.data) {
				case 2: this.direction = Direction.N;	break;
				case 3: this.direction = Direction.S;	break;
				case 4: this.direction = Direction.W;	break;
				case 5: this.direction = Direction.E;	break;
				default: throw new IllegalArgumentException("illegal directional state: " + this.data);
			}
		} else {
			switch (this.data) {
				case 0:  this.direction = Direction.S;		break;
				case 1:  this.direction = Direction.SSW;	break;
				case 2:  this.direction = Direction.SW;	break;
				case 3:  this.direction = Direction.WSW;	break;
				case 4:  this.direction = Direction.W;		break;
				case 5:  this.direction = Direction.WNW;	break;
				case 6:  this.direction = Direction.NW;	break;
				case 7:  this.direction = Direction.NNW;	break;
				case 8:  this.direction = Direction.N;		break;
				case 9:  this.direction = Direction.NNE;	break;
				case 10: this.direction = Direction.NE;	break;
				case 11: this.direction = Direction.ENE;	break;
				case 12: this.direction = Direction.E;		break;
				case 13: this.direction = Direction.ESE;	break;
				case 14: this.direction = Direction.SE;	break;
				case 15: this.direction = Direction.SSE;	break;
				default: throw new IllegalArgumentException("illegal directional state: " + this.data);
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ", " + ((this.isWallSign) ? "on Wall, " : "freestanding, ") + ", facing " +
				this.direction + ", text: " + Arrays.deepToString(this.text);
	}
	
	@Override
	public void turn(boolean CW) {
		if (this.isWallSign) {
			switch(this.direction) {
				case E:	this.data = (byte) ((CW) ? 3 : 2); break;
				case W:	this.data = (byte) ((CW) ? 2 : 3); break;
				case N:	this.data = (byte) ((CW) ? 5 : 4); break;
				case S:	this.data = (byte) ((CW) ? 4 : 5); break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			if (CW) {
				if (this.data <= 11) {
					this.data = (byte) (this.data + 4);
				} else {
					this.data = (byte) (this.data - 12);
				}
			} else {
				if (this.data >= 4) {
					this.data = (byte) (this.data - 4);
				} else {
					this.data = (byte) (this.data + 12);
				}
			}
		}
		setDirectionByCurData();
	}
	
	@Override
	public void setData(byte data) {
		this.data = data;
		setDirectionByCurData();
	}

	@Override
	public void setDirection(Direction direction) {
		if (this.isWallSign) {
			switch(direction) {
				case N:	this.data = 2;	break;
				case E:	this.data = 5;	break;
				case S:	this.data = 3;	break;
				case W:	this.data = 4;	break;
				default: throw new IllegalArgumentException("illegal direction for wall sign: " + direction);
			}
		} else {
			switch (direction) {
				case N:		this.data = 8;  break;
				case NNE:	this.data = 9;  break;
				case NE:	this.data = 10; break;
				case ENE:	this.data = 11; break;
				case E:		this.data = 12; break;
				case ESE:	this.data = 13; break;
				case SE:	this.data = 14; break;
				case SSE:	this.data = 15; break;
				case S:		this.data = 0;  break;
				case SSW:	this.data = 1;  break;
				case SW:	this.data = 2;  break;
				case WSW:	this.data = 3;  break;
				case W:		this.data = 4;  break;
				case WNW:	this.data = 5;  break;
				case NW:	this.data = 6;  break;
				case NNW:	this.data = 7;  break;
				default:
					// should never happen
					throw new AssertionError(direction);
			}
		}
		this.direction = direction;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (this.isWallSign ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(this.text);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		Sign other = (Sign) obj;
		if (this.isWallSign != other.isWallSign) return false;
		if (!Arrays.equals(this.text, other.text)) return false;
		return true;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (signZoomCache != zoom) {
			// reset cache
			signImageCache.clear();
			wallsignImageCache.clear();
			signZoomCache = zoom;
		} else {
			if (this.isWallSign) {
				img = wallsignImageCache.get(this.direction);
			} else {
				img = signImageCache.get(this.direction);
			}
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		Direction direction = this.direction;
		
		if (this.isWallSign) {
			// flip directions because they say where the button is facing, but to show the wall it's attached to makes more sense
			switch(this.direction) {
				case N: direction = Direction.S; break;
				case E: direction = Direction.W; break;
				case S: direction = Direction.N; break;
				case W: direction = Direction.E; break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
		
		img = addArrowToImage(direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (this.isWallSign) {
			wallsignImageCache.put(this.direction, img);
		} else {
			signImageCache.put(this.direction, img);
		}
		
		return img;
	}
	
	@Override
	public JToolTip getCustomToolTip() {
		BufferedImage background = ImageProvider.getSignPlaneCopy();
		BufferedImage[] line1 = ImageProvider.stringToImage(this.text[0], 0xFF0000);
		BufferedImage[] line2 = ImageProvider.stringToImage(this.text[1], 0xFF0000);
		BufferedImage[] line3 = ImageProvider.stringToImage(this.text[2], 0xFF0000);
		BufferedImage[] line4 = ImageProvider.stringToImage(this.text[3], 0xFF0000);
		
		Graphics2D g = background.createGraphics();
		for (int i = 0; i < line1.length; ++i) {
			//g.drawImage(line1[i], null, 6*i, 3);
			g.drawImage(line1[i], 6*i, 3, 8, 8, null, null);
		}
		for (int i = 0; i < line2.length; ++i) {
			//g.drawImage(line2[i], null, 6*i, 13);
			g.drawImage(line2[i], 6*i, 13, 8, 8, null, null);
		}
		for (int i = 0; i < line3.length; ++i) {
			//g.drawImage(line3[i], null, 6*i, 23);
			g.drawImage(line3[i], 6*i, 23, 8, 8, null, null);
		}
		for (int i = 0; i < line4.length; ++i) {
			//g.drawImage(line4[i], null, 6*i, 33);
			g.drawImage(line4[i], 6*i, 33, 8, 8, null, null);
		}
		
		BufferedImage result = ImageProvider.zoom(2, background);
		
		ImageToolTip tooltip = new ImageToolTip(result);
		
		return tooltip;
	}
}
