package klaue.mcschematictool.blocktypes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import klaue.mcschematictool.ImageProvider;


/**
 * A flower pot contains various flowers
 * @author klaue
 *
 */
public class FlowerPot extends Block {
	/**
	 * The type of plant for the flowerpot
	 * @author klaue
	 */
	public enum PottedPlantType {
		/** None */ NONE, /** Rose */ ROSE, /** Dandelion */ DANDELION, /** Oak sapling */ OAKSAPLING("oak sapling"),
		/** Spruce sapling */ SPRUCESAPLING("spruce sapling"), /** Birch sapling */ BIRCHSAPLING("birch sapling"),
		/** Jungle tree sapling */ JUNGLETREESAPLING("jungle tree sapling"), /** Red mushroom */ REDMUSHROOM("red mushroom"),
		/** Brown mushroom */ BROWNMUSHROOM("brown mushroom"), /** Cactus */ CACTUS, /** Dead bush */ DEADBUSH("dead bush"),
		/** Fern */ FERN;
		
		private String name = null;
		PottedPlantType(){}
		PottedPlantType(String name) { this.name = name; }
		@Override
		public String toString() { return (this.name == null) ? super.toString().toLowerCase() : this.name; }
	};
	
	private static HashMap<PottedPlantType, BufferedImage> flowerPotImageCache = new HashMap<PottedPlantType, BufferedImage>();
	private static double flowerPotZoomCache = -1;
	
	private PottedPlantType pottedPlant = PottedPlantType.NONE;
	
	/**
	 * initializes the flower pot
	 * @param data the minecraft data value (representing color)
	 */
	public FlowerPot(byte data) {
		super((short)140, data);
		this.type = Type.FLOWERPOT;
		setData(data);
	}
	
	/**
	 * initializes a flower pot without plant
	 */
	public FlowerPot() {
		this(PottedPlantType.NONE);
	}
	
	/**
	 * initializes the flower pot
	 * @param pottedPlant the plant potted in this pot
	 */
	public FlowerPot(PottedPlantType pottedPlant) {
		super((short)140);
		this.type = Type.FLOWERPOT;
		setPottedPlant(pottedPlant);
	}
	
	/**
	 * Get the plant potted in this pot
	 * @return the plant
	 */
	public PottedPlantType getPottedPlant() {
		return this.pottedPlant;
	}

	/**
	 * Set the plant potted in the pot
	 * @param pottedPlant the plant to pot
	 */
	public void setPottedPlant(PottedPlantType pottedPlant) {
		this.pottedPlant = pottedPlant;
		switch(pottedPlant) {
			case NONE:				this.data = 0;	break;
			case ROSE:				this.data = 1;	break;
			case DANDELION:			this.data = 2;	break;
			case OAKSAPLING:		this.data = 3;	break;
			case SPRUCESAPLING:		this.data = 4;	break;
			case BIRCHSAPLING:		this.data = 5;	break;
			case JUNGLETREESAPLING:	this.data = 6;	break;
			case REDMUSHROOM:		this.data = 7;	break;
			case BROWNMUSHROOM:		this.data = 8;	break;
			case CACTUS:			this.data = 9;	break;
			case DEADBUSH:			this.data = 10;	break;
			case FERN:				this.data = 11;	break;
		}
	}
	
	@Override
	public String toString() {
		if (this.pottedPlant == PottedPlantType.NONE) {
			return super.toString();
		}
		return super.toString() + " with " + this.pottedPlant;
	}
	
	@Override
	public void setData(byte data) {
		switch(data) {
			case 0:  this.pottedPlant = PottedPlantType.NONE;				break;
			case 1:  this.pottedPlant = PottedPlantType.ROSE;				break;
			case 2:  this.pottedPlant = PottedPlantType.DANDELION;			break;
			case 3:  this.pottedPlant = PottedPlantType.OAKSAPLING;		break;
			case 4:  this.pottedPlant = PottedPlantType.SPRUCESAPLING;		break;
			case 5:  this.pottedPlant = PottedPlantType.BIRCHSAPLING;		break;
			case 6:  this.pottedPlant = PottedPlantType.JUNGLETREESAPLING;	break;
			case 7:  this.pottedPlant = PottedPlantType.REDMUSHROOM;		break;
			case 8:  this.pottedPlant = PottedPlantType.BROWNMUSHROOM;		break;
			case 9:  this.pottedPlant = PottedPlantType.CACTUS;			break;
			case 10: this.pottedPlant = PottedPlantType.DEADBUSH;			break;
			case 11: this.pottedPlant = PottedPlantType.FERN;				break;
			default: throw new IllegalArgumentException("illegal potted plant value: " + this.data);
		}
		this.data = data;
	}
	
	@Override
	public synchronized BufferedImage getImage(double zoom) {
		if (!ImageProvider.isActivated()) return null;
		if (zoom <= 0) return null;
		
		BufferedImage img = null;
		
		if (flowerPotZoomCache != zoom) {
			// reset cache
			flowerPotImageCache.clear();
			flowerPotZoomCache = zoom;
		} else {
			img = flowerPotImageCache.get(this.pottedPlant);
			if (img != null) {
				return img;
			}
		}
		
		// image not in cache, make new
		// get image from imageprovider
		if (this.pottedPlant == PottedPlantType.NONE) {
			img = ImageProvider.getItemImage("flowerPot");
		} else {
			// get plant image, then copy to new image with pot "border"
			BufferedImage plantImg = null;
			switch(this.pottedPlant) {
				case ROSE:				plantImg = ImageProvider.getImage("rose");				break;
				case DANDELION:			plantImg = ImageProvider.getImage("flower");			break;
				case OAKSAPLING:		plantImg = ImageProvider.getImage("sapling");			break;
				case SPRUCESAPLING:		plantImg = ImageProvider.getImage("sapling_spruce");	break;
				case BIRCHSAPLING:		plantImg = ImageProvider.getImage("sapling_birch");		break;
				case JUNGLETREESAPLING:	plantImg = ImageProvider.getImage("sapling_jungle");	break;
				case REDMUSHROOM:		plantImg = ImageProvider.getImage("mushroom_red");		break;
				case BROWNMUSHROOM:		plantImg = ImageProvider.getImage("mushroom_brown");	break;
				case CACTUS:			plantImg = ImageProvider.getImage("cactus_side");		break;
				case DEADBUSH:			plantImg = ImageProvider.getImage("deadbush");			break;
				case FERN:
				default:				plantImg = ImageProvider.getAdditionalImage("fern");	break;
			}
			
			BufferedImage potImg = ImageProvider.getAdditionalImage("flower_pot");
			
			img = new BufferedImage(plantImg.getWidth(), plantImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = img.createGraphics();
			g.drawRenderedImage(plantImg, null);
			g.drawRenderedImage(potImg, null);
		}
		
		if (img == null) return null;
		
		// zoom
		if (zoom != 1) {
			img = ImageProvider.zoom(zoom, img);
		}

		// save image to cache
		flowerPotImageCache.put(this.pottedPlant, img);
		
		return img;
	}
}
