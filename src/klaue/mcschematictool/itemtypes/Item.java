package klaue.mcschematictool.itemtypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.NameProvider;

/**
 * This class represents an item or a stack of items. Used for content of chests/dispensers
 * @author klaue
 *
 */
public class Item {
	/**
	 * Item types. Normal is a non-special item without subclass, currently everything but potions
	 * @author klaue
	 */
	public enum Type {
		/** Normal items like wheat */		NORMAL,
		/** Potions, normal and splash */	POTION,
		/** colored items */				COLORED,
		/** Leather helmet */				LEATHERHELMET,
		/** Leather chestplate */			LEATHERCHESTPLATE,
		/** Leather leggins */				LEATHERLEGGINGS,
		/** Leather boots */				LEATHERBOOTS
		};
	
	// caching of item images
	//                       ID             Damage
	protected static HashMap<Short, HashMap<Short, BufferedImage> > imageCache = new HashMap<Short, HashMap<Short, BufferedImage> >();
	protected static double zoomCache = -1;
	
	/** the ID of the item */
	protected short id = 0;
	
	/** additional data of the item */
	protected short data = 0;
	
	protected Type type = Type.NORMAL;
	
	/** the amount of items in this stack */
	protected byte stacksize = 1;
	
	/** the display name of this item, if set **/
	protected String name = "";
	
	/**
	 * Generates a new Item of id 0 (invalid)
	 */
	public Item() {
		this((short)0, (byte)0, (byte)0);
	}
	
	/**
	 * Generates a new Item of type id with no additional data
	 * @param id
	 */
	protected Item(short id) {
		this(id, (byte)0, (byte)0);
	}
	
	/**
	 * Generates a new Item of type id
	 * @param id Item ID
	 * @param data Item data
	 */
	protected Item(short id, short data) {
		this(id, data, (byte)0);
	}
	
	/**
	 * Generates a new Item of type id
	 * @param id Item ID
	 * @param data Item data
	 * @param stacksize the number of items in this stack
	 */
	protected Item(short id, short data, byte stacksize) {
		this.id = id;
		this.data = data;
		this.stacksize = stacksize;
	}
	
	/**
	 * Returns the right type of item for the id (note that this is no Singleton)
	 * @param id the id of the Item
	 * @return an instance of the right item class
	 */
	public static Item getInstance(short id) {
		return getInstance(id, (byte)0, (byte)0);
	}
	
	/**
	 * Returns the right type of item for the id (note that this is no Singleton)
	 * @param id the id of the Item
	 * @param data the data value of the item
	 * @return an instance of the right item class
	 */
	public static Item getInstance(short id, short data) {
		return getInstance(id, data, (byte)0);
	}
	
	/**
	 * Returns the right type of item for the id (note that this is no Singleton)
	 * @param id the id of the Item
	 * @param data the data value of the item
	 * @param stacksize the size of the item's stack when inside a chest
	 * @return an instance of the right item class
	 */
	public static Item getInstance(short id, short data, byte stacksize) {
		switch (id) {
			case 373:	return new Potion(data, stacksize);
			case 298:	return new LeatherHelmet(data, stacksize);
			case 299:	return new LeatherChestplate(data, stacksize);
			case 300:	return new LeatherLeggings(data, stacksize);
			case 301:	return new LeatherBoots(data, stacksize);
			default:	return new Item(id, data, stacksize);
		}
	}
	
	@Override
	public String toString() {
		if (this.id == 0) return "-";
		
		String name = this.name.isEmpty() ? NameProvider.getNameOfBlockOrItem(this.id, (byte)this.data) : this.name;
		
		if (this.stacksize > 1) {
			return this.stacksize + "x " + name;
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.data;
		result = prime * result + this.id;
		result = prime * result + this.stacksize;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Item other = (Item) obj;
		if (this.data != other.data) return false;
		if (this.id != other.id) return false;
		if (this.stacksize != other.stacksize) return false;
		return true;
	}
	
	/**
	 * Returns a BufferedImage representing the item
	 * @param zoom the current zoom value (min 1, since a 16x16 image is small enough)
	 * @return the BufferedImage or null if images are deactivated
	 */
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (zoomCache != zoom) {
			// reset cache
			imageCache.clear();
			zoomCache = zoom;
		} else {
			if (imageCache.containsKey(this.id) && imageCache.get(this.id).containsKey(this.data)) {
				return imageCache.get(this.id).get(this.data);
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		img = ImageProvider.getImageByBlockOrItemID(this.id, (byte)this.data);
	
		if (img == null) return null;
	
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}
		
		// add stacksize
		// --> done in the tooltips because the shadow of the number is actually outside the image

		// save image to cache
		HashMap<Short, BufferedImage> dmgMap = imageCache.get(this.id);
		if (dmgMap == null) {
			dmgMap = new HashMap<Short, BufferedImage>();
			dmgMap.put(this.data, img);
			imageCache.put(this.id, dmgMap);
		} else {
			dmgMap.put(this.data, img);
		}
		
		return img;
	}

	/**
	 * @return the id
	 */
	public short getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	protected void setId(short id) {
		this.id = id;
	}

	/**
	 * @return the data value
	 */
	public short getData() {
		return this.data;
	}

	/**
	 * sets the data value. note that if availlable, you should use the methods of the specific subclass instead
	 * @param data the data to set
	 */
	public void setData(short data) {
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * @return the stacksize
	 */
	public byte getStacksize() {
		return this.stacksize;
	}

	/**
	 * @param stacksize the stacksize to set
	 */
	public void setStacksize(byte stacksize) {
		this.stacksize = stacksize;
	}

	/**
	 * @return the display name of this item (can be empty, see NameProvider.getNameOfBlockOrItem() for default name)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the display-name to set (or ""/null for default name)
	 */
	public void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			this.name = "";
		} else {
			this.name = name;
		}
	}
	
	/**
	 * Checks if this item is a potion
	 * @return true if this item is a potion
	 */
	public boolean isPotion() {
		return this.type == Type.POTION;
	}
	
	/**
	 * Checks if this item is a colored one
	 * @return true if this item is a colored item
	 */
	public boolean isColoredItem() {
		if (isLeatherBoots() || isLeatherChestplate() || isLeatherHelmet() || isLeatherLeggins()) return true;
		return this.type == Type.COLORED;
	}
	
	/**
	 * Checks if this item is a leather helmet
	 * @return true if this item is a leather helmet
	 */
	public boolean isLeatherHelmet() {
		return this.type == Type.LEATHERHELMET;
	}
	
	/**
	 * Checks if this item is a leather chestplate
	 * @return true if this item is a leather chestplate
	 */
	public boolean isLeatherChestplate() {
		return this.type == Type.LEATHERCHESTPLATE;
	}
	
	/**
	 * Checks if this item is leather leggins
	 * @return true if this item is leather leggins
	 */
	public boolean isLeatherLeggins() {
		return this.type == Type.LEATHERLEGGINGS;
	}
	
	/**
	 * Checks if this item is leather boots
	 * @return true if this item is leather boots
	 */
	public boolean isLeatherBoots() {
		return this.type == Type.LEATHERBOOTS;
	}
}
