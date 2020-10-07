package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;



/**
 * A button is placed on a specific side of the block, and be off or on
 * As of 1.8, buttons can be placed on top or bottoms of blocks.
 * @author klaue
 *
 */
public class Button extends DirectionalBlock {
	/**
	 * The type of button
	 * @author klaue
	 */
	public enum ButtonType {/** stone button */ STONE, /** wooden button */ WOOD}
	
	
	private static HashMap<Short, HashMap<Direction, BufferedImage> > buttonImageCache = new HashMap<Short, HashMap<Direction, BufferedImage> >();
	private static double buttonZoomCache = -1;

	private ButtonType buttonType;
	private boolean isPressed;
	
	/**
	 * initializes the button
	 * @param id the id (77 (stone) or 143 (wood))
	 * @param data the block data containing direction and if the button is pressed
	 */
	public Button(short id, byte data) {
		super(id, data);
		if (id != 77 && id != 143) {
			throw new IllegalArgumentException("illegal id for buttons: " + id);
		}
		this.type = Type.BUTTON;
		this.buttonType = (id == 77) ? ButtonType.STONE : ButtonType.WOOD;
		this.setData(data);
	}
	
	/**
	 * initializes the button
	 * @param buttonType the type of button
	 * @param isPressed true if this button is pressed (should not be set manually because the button will never depress)
	 * @param direction the side of the block the button is placed, valid directions are N, E, S, W, UP, DOWN
	 */
	public Button(ButtonType buttonType, boolean isPressed, Direction direction) {
		super((short)((buttonType == ButtonType.STONE) ? 77 : 143));
		this.isPressed = isPressed;
		this.type = Type.BUTTON;
		this.data = (byte) (isPressed ? 8 : 0);
		this.setDirection(direction); // sets the rest of the data too
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + (this.isPressed ? "on" : "off") + "), facing: " + this.direction;
	}

	/**
	 * Returns true if this button was pressed 
	 * @return true if pressed
	 */
	public boolean isPressed() {
		return this.isPressed;
	}

	/**
	 * Set to true to set this button pressed (should not be set manually because the button will never depress)
	 * @param isPressed true if pressed
	 */
	public void setPressed(boolean isPressed) {
		if (this.isPressed != isPressed) {
			if (this.isPressed) {
				// remove pressed bit
				this.data = (byte)(this.data & 7);
			} else {
				//set pressed bit
				this.data = (byte)(this.data | 8);
			}
			this.isPressed = isPressed;
		}
	}

	@Override
	public void setData(byte data) {
		if (data < 1 || data > 12) throw new IllegalArgumentException("data out of range: " + data);
		
		byte pressed = (byte) (data & 8); // 1000(bin) if true, 0000 if false
		this.isPressed = (pressed != 0);
		
		byte dirData = (byte) (data & 7);
		
		switch (dirData) {
			case 1:  this.direction = Direction.E;	break;
			case 2:  this.direction = Direction.W;	break;
			case 3:  this.direction = Direction.S;	break;
			case 4:  this.direction = Direction.N;	break;
			case 5: this.direction = Direction.UP; break;
			case 0: this.direction = Direction.DOWN; break;
			default: throw new IllegalArgumentException("illegal directional state: " + data);
		}
		
		this.data = data;
	}

	@Override
	public void setDirection(Direction direction) {
		byte dirData = 0;
		switch (direction) {
		case UP: dirData = 5; break;
		case DOWN: dirData = 0; break;
			case N:	dirData = 4; break;
			case E:	dirData = 1; break;
			case S:	dirData = 3; break;
			case W:	dirData = 2; break;
			default: throw new IllegalArgumentException("illegal direction: " + direction);
		}
		this.direction = direction;
		this.data = (byte) (dirData + (this.data & 8));
	}

	@Override
	public void turn(boolean CW) {
		if (CW) {  //rotate the direction clockwise
			switch (this.direction) {
				case N:	this.direction = Direction.E;	this.data = 1;	break;
				case E:	this.direction = Direction.S;	this.data = 3;	break;
				case S:	this.direction = Direction.W;	this.data = 2;	break;
				case W:	this.direction = Direction.N;	this.data = 4;	break;
				
				case UP: this.direction = Direction.UP; this.data = 5; 	break;
				case DOWN: this.direction = Direction.DOWN; this.data = 0; break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		} else {
			switch (this.direction) {
				case N:	this.direction = Direction.W;	this.data = 2;	break;
				case E:	this.direction = Direction.N;	this.data = 4;	break;
				case S:	this.direction = Direction.E;	this.data = 1;	break;
				case W:	this.direction = Direction.S;	this.data = 3;	break;
				case UP: this.direction = Direction.UP; this.data = 5; 	break;
				case DOWN: this.direction = Direction.DOWN; this.data = 0; break;
				default:
					// should never happen
					throw new AssertionError(this.direction);
			}
		}
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (buttonZoomCache != zoom) {
			// reset cache
			buttonImageCache.clear();
			buttonZoomCache = zoom;
		} else {
			if (buttonImageCache.containsKey(this.id)
				&& buttonImageCache.get(this.id).containsKey(this.direction)) {
					return buttonImageCache.get(this.id).get(this.direction);
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id);
		
		if (img == null) return null;
		
		// flip directions because they say where the button is facing, but to show the wall it's attached to makes more sense
		// RM - Not necessary anymore - direction is already the side of the block
/*		Direction direction;
		switch(this.direction) {
			case N: direction = Direction.S; break;
			case E: direction = Direction.W; break;
			case S: direction = Direction.N; break;
			case W: direction = Direction.E; break;
			case UP: this.direction = Direction.UP; this.data = 5; 	break;
			case DOWN: this.direction = Direction.DOWN; this.data = 0; break;

			default:
				// should never happen
				throw new AssertionError(this.direction);
		}
	*/	
		img = addArrowToImage(this.direction, img);
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		if (buttonImageCache.containsKey(this.id)) {
			buttonImageCache.get(this.id).put(this.direction, img);
		} else {
			HashMap<Direction, BufferedImage> dirMap = new HashMap<Direction, BufferedImage>();
			dirMap.put(this.direction, img);
			buttonImageCache.put(this.id, dirMap);
		}
		
		return img;
	}

	/**
	 * @return the type of button
	 */
	public ButtonType getButtonType() {
		return this.buttonType;
	}

	/**
	 * Sets the type of button (warning: changes id)
	 * @param buttonType the buttonType to set
	 */
	public void setButtonType(ButtonType buttonType) {
		this.buttonType = buttonType;
		this.id = (byte) ((buttonType == ButtonType.STONE) ? 77 : 143);
	}
	
	@Override
	protected void setId(short id) {
		if (id != 77 && id != 143) {
			throw new IllegalArgumentException("illegal id for buttons: " + id);
		}
		this.id = id;
		this.buttonType = (id == 77) ? ButtonType.STONE : ButtonType.WOOD;
	}
}
