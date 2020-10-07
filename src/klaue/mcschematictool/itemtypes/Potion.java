package klaue.mcschematictool.itemtypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;

/**
 * A potion item that can have different effects and be splash or normal
 * @author klaue
 *
 */
public class Potion extends Item {
	private static HashMap<Integer, BufferedImage> potionImageCache = new HashMap<Integer, BufferedImage>();
	private static double potionZoomCache = -1;
	
	/**
	 * All names of the possible potion effects
	 */
	public static HashMap<Byte, String> potionEffects = new HashMap<Byte, String>();
	
	/**
	 * All the possible names of the potions. Potion with ID 0 (mundane potion) can also be "Water Bottle" 
	 */
	public static HashMap<Byte, String> potionNames = new HashMap<Byte, String>();
	
	static {
		// no effect for 0
 		potionEffects.put((byte)1,  "Regeneration");
 		potionEffects.put((byte)2,  "Speed");
 		potionEffects.put((byte)3,  "Fire Resistance");
 		potionEffects.put((byte)4,  "Poison");
 		potionEffects.put((byte)5,  "Instant Health");
 		potionEffects.put((byte)6,  "Night Vision");
 		// no effect for 7
 		potionEffects.put((byte)8,  "Weakness");
 		potionEffects.put((byte)9,  "Strength");
 		potionEffects.put((byte)10, "Slowness");
		potionEffects.put((byte)11, "Jump Boost");
 		potionEffects.put((byte)12, "Instant Damage");
		potionEffects.put((byte)13, "Water Breathing");
 		potionEffects.put((byte)14, "Invisibility");

		potionNames.put((byte)0,  "Mundane Potion");
		potionNames.put((byte)1,  "Potion of Regeneration");
		potionNames.put((byte)2,  "Potion of Swiftness");
		potionNames.put((byte)3,  "Potion of Fire Resistance");
		potionNames.put((byte)4,  "Potion of Poison");
		potionNames.put((byte)5,  "Potion of Healing");
 		potionNames.put((byte)6,  "Potion of Night Vision");
 		potionNames.put((byte)7,  "Clear Potion");
		
		
		potionNames.put((byte)8,  "Potion of Weakness");
		potionNames.put((byte)9,  "Potion of Strength");
		potionNames.put((byte)10, "Potion of Slowness");
		potionNames.put((byte)11, "Potion of Leaping");
		potionNames.put((byte)12, "Potion of Harming");
		potionNames.put((byte)13, "Potion of Water Breathing");
		potionNames.put((byte)14, "Potion of Invisibility");
		potionNames.put((byte)15, "Thin Potion");
		potionNames.put((byte)16, "Awkward Potion");
		potionNames.put((byte)17, "Potion of Regeneration II");
		potionNames.put((byte)18, "Potion of Swiftness II");
		potionNames.put((byte)19, "Potion of Fire Resistance II");
		potionNames.put((byte)20, "Potion of Poison II");
		potionNames.put((byte)21, "Potion of Healing II");
		potionNames.put((byte)22, "Potion of Night Vision II");
		potionNames.put((byte)23, "Bungling Potion");
		potionNames.put((byte)24, "Potion of Weakness II");
		potionNames.put((byte)25, "Potion of Strength II");
		potionNames.put((byte)26, "Potion of Slowness II");
		potionNames.put((byte)27, "Potion of Leaping II");
		potionNames.put((byte)28, "Potion of Harming II");
		potionNames.put((byte)29, "Potion of Water Breathing II");
		potionNames.put((byte)30, "Potion of Invisibility II");
		potionNames.put((byte)31, "Debonair Potion");
		potionNames.put((byte)32, "Thick Potion");
		potionNames.put((byte)33, "Potion of Regeneration III");
		potionNames.put((byte)34, "Potion of Swiftness III");
		potionNames.put((byte)35, "Potion of Fire Resistance III");
		potionNames.put((byte)36, "Potion of Poison III");
		potionNames.put((byte)37, "Potion of Healing III");
		potionNames.put((byte)38, "Potion of Night Vision III");
		potionNames.put((byte)39, "Charming Potion");
		potionNames.put((byte)40, "Potion of Weakness III");
		potionNames.put((byte)41, "Potion of Strength III");
		potionNames.put((byte)42, "Potion of Slowness III");
		potionNames.put((byte)43, "Potion of Leaping III");
		potionNames.put((byte)44, "Potion of Harming III");
		potionNames.put((byte)45, "Potion of Water Breathing III");
		potionNames.put((byte)46, "Potion of Invisibility III");
		potionNames.put((byte)47, "Sparkling Potion");
		potionNames.put((byte)48, "Potent Potion");
		potionNames.put((byte)49, "Potion of Regeneration IV");
		potionNames.put((byte)50, "Potion of Swiftness IV");
		potionNames.put((byte)51, "Potion of Fire Resistance IV");
		potionNames.put((byte)52, "Potion of Poison IV");
		potionNames.put((byte)53, "Potion of Healing IV");
		potionNames.put((byte)54, "Potion of Night Vision IV");
		potionNames.put((byte)55, "Rank Potion");
		potionNames.put((byte)56, "Potion of Weakness IV");
		potionNames.put((byte)57, "Potion of Strength IV");
		potionNames.put((byte)58, "Potion of Slowness IV");
		potionNames.put((byte)59, "Potion of Leaping IV");
		potionNames.put((byte)60, "Potion of Harming IV");
		potionNames.put((byte)61, "Potion of Water Breathing IV");
		potionNames.put((byte)62, "Potion of Invisibility IV");
		potionNames.put((byte)63, "Stinky Potion");
	}
	
	private int color = 0xFF3050A8; // FF = alpha
	private byte potionEffect = 0;
	private byte potionName = 0;
	private boolean isTiered = false;
	private boolean hasExtDuration = false;
	private boolean isSplash = false;
	private int duration = -1;
	
	/**
	 * initializes the potion item
	 * @param data the minecraft data value (representing type of potion)
	 */
	public Potion(short data) {
		this(data, (byte)0);
	}
	
	/**
	 * initializes the potion item
	 * @param data the minecraft data value (representing type of potion)
	 * @param stacksize the size of this stack
	 */
	public Potion(short data, byte stacksize) {
		super((short)373, data, stacksize);
		this.type = Type.POTION;
		setData(data);
		setStacksize(stacksize);
	}
	
	@Override
	public void setData(short data) {
		byte potionEffect =		(byte)(data&15);			// 1111
		byte tier =				(byte)((data&32)>>5);		// 100000
		byte potionName =		(byte)(data&63);			// 111111
		byte extendedDuration =	(byte)((data&64)>>6);		// 1000000
		byte splashPotion =		(byte)((data&16384)>>14);	// 100000000000000
		
		this.isTiered = (tier == 1);
		this.isSplash = (splashPotion == 1);
		this.hasExtDuration = (extendedDuration == 1);
		
		this.potionName = potionName;
		this.potionEffect = potionEffect;
		switch(potionEffect) {
			// colors from http://rapidtables.com/web/color/RGB_Color.htm
			case 1: //Regeneration
				// this.color = 0xFFDB63EF;
				this.color = 0xFFFFC0CB; // pink
				this.duration = 45;
				break;
			case 2: // Speed
				//this.color = 0xFF6A96A9;
				this.color = 0xFF87CEEB; // sky blue
				this.duration = 180;
				this.duration = 180;
				break;
			case 3: // Fire Resistance
				//this.color = 0xFFC28432; 
				this.color = 0xFFFFA500; // orange
				this.duration = 180;
				break;
			case 4: // Poison
				//this.color = 0xFF437E2A; 
				this.color = 0xFF008000; // green
				this.duration = 45;
				break;
			case 5: // Instant Health
				this.color = 0xFFD31F1E; //no color
				this.duration = 0;
				break;
			case 6: // Night Vision
				/*this.color = 0xFF1F1F9E; */
				this.color = 0xFF000080; // navy blue
				this.duration = 180;
				break;
			case 8: // Weakness
				//this.color = 0xFF3E423E;
				this.color = 0xFF808080; // gray
				this.duration = 90;
				break;
			case 9: // Strength
				// this.color = 0xFF7E1F1E;
				this.color = 0xFF8B0000; // dark red
				this.duration = 180;
				break;
			case 10: // Slowness
				//this.color = 0xFF4D5C6E;
				this.color = 0xFF778899; //blue-gray (slate-blue for this)
				this.duration = 90;
				break;
			case 11: // Jump Boost
				//this.color = 0xFF4D5C6E; 
				this.color = 0xFF00FF00; // Bright Green (lime)
				this.duration = 180;
				break;
			case 12: // Instant Damage
				this.color = 0xFF390908; // no color
				this.duration = 0;
				break;
			case 13: // Water Breathing
				// this.color = 0xFF390908;
				this.color = 0xFF0000FF; //blue
				this.duration = 180;
				break;
			case 14: // Invisibility
				// this.color = 0xFF7D8190; 
				this.color = 0xFFD3D3D3; // light gray
				this.duration = 180;
				break;
			default: /* default values */ break;
		}
		
		this.data = data;
	}

	/**
	 * @return the name of the potion
	 */
	public String getPotionName() {
		if (this.data == 0) return "Water Bottle";
		return potionNames.get(this.potionName);
	}

	/**
	 * @return the effect of the potion
	 */
	public String getPotionEffect() {
		return potionEffects.get(this.potionEffect);
	}

	/**
	 * @param potionName The name of the potion. Must be exact and not include "Splash", or else a water bottle/mundane potion is generated.
	 * See ItemPotion.potionNames
	 */
	public void setPotionName(String potionName) {
		this.potionName = 0;
		for (Byte key : potionNames.keySet()) {
			if (potionNames.get(key).equalsIgnoreCase(potionName)) {
				this.potionName = key;
				break;
			}
		}
		
		// setData ensures that color, duration etc is also updated
		this.setData((short)((this.data&65472) + this.potionName)); // 65472 = 1111111111000000
	}

	/**
	 * @return true if the potion has extended duration 
	 */
	public boolean hasExtDuration() {
		return this.hasExtDuration;
	}

	/**
	 * @param hasExtDuration true if this potion has ext. duration
	 */
	public void setExtDuration(boolean hasExtDuration) {
		if (this.hasExtDuration == hasExtDuration) return;
		this.hasExtDuration = hasExtDuration;
		this.data = (short)(this.data|64);
	}

	/**
	 * @return true if this is a thrown potion
	 */
	public boolean isSplash() {
		return this.isSplash;
	}

	/**
	 * @param isSplash true to change this potion to a thrown one
	 */
	public void setSplash(boolean isSplash) {
		if (this.isSplash == isSplash) return;
		this.isSplash = isSplash;
		this.data = (short)(this.data|16384);
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return new Color(this.color, true);
	}

	/**
	 * @param base true to get the base duration value in seconds, false to get the real one (with tier and ext. duration)
	 * @return the duration
	 */
	public int getDuration(boolean base) {
		if (base) return this.duration;
		
		int calcDuration = this.duration;
		
		if (this.hasExtDuration) calcDuration *= (8/3);
		if (this.isTiered) calcDuration *= 0.5;
		return calcDuration;
	}
	
	@Override
	public String toString() {
		String name = (this.stacksize > 1) ? this.stacksize + "x " : "";
		if (this.data == 0) return name + "Water bottle";
		
		if (this.isSplash) name += "Splash ";
		name += getPotionName();
		if (this.isTiered) name += " (Tier 2)";
		if (this.hasExtDuration) name += " (ext. Duration)";
		
		// TODO Auto-generated method stub
		return name;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (potionZoomCache != zoom) {
			// reset cache
			potionImageCache.clear();
			potionZoomCache = zoom;
		} else {
			img = potionImageCache.get(this.color);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		if (this.isSplash) {
			img = ImageProvider.getItemImage("potion_splash");
		} else {
			img = ImageProvider.getItemImage("potion");
		}
		
		if (img == null) return null;
		
		// add color (7x6px)
		int[] colorArray = new int[42];
		for (int i = 0; i < 42; ++i) colorArray[i] = this.color;
		
		BufferedImage background = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		background.setRGB(5, 8, 7, 6, colorArray, 0, 7);
		
		BufferedImage newImg = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = newImg.createGraphics();
		g.drawRenderedImage(background, null);
		g.drawRenderedImage(img, null);
		img = newImg;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		potionImageCache.put(this.color, img);
		
		return img;
	}

	/**
	 * @return true if this potion is tier 2
	 */
	public boolean isTiered() {
		return this.isTiered;
	}
}
