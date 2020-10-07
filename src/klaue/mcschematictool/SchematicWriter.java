package klaue.mcschematictool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import klaue.mcschematictool.blocktypes.Beacon;
import klaue.mcschematictool.blocktypes.Block;
import klaue.mcschematictool.blocktypes.BrewingStand;
import klaue.mcschematictool.blocktypes.Chest;
import klaue.mcschematictool.blocktypes.CommandBlock;
import klaue.mcschematictool.blocktypes.Dispenser;
import klaue.mcschematictool.blocktypes.Dropper;
import klaue.mcschematictool.blocktypes.Hopper;
import klaue.mcschematictool.blocktypes.MobHead;
import klaue.mcschematictool.blocktypes.Note;
import klaue.mcschematictool.blocktypes.Sign;
import klaue.mcschematictool.exceptions.ClassicNotSupportedException;
import klaue.mcschematictool.exceptions.ParseException;
import klaue.mcschematictool.itemtypes.ColoredItem;
import klaue.mcschematictool.itemtypes.Item;

import org.jnbt.ByteArrayTag;
import org.jnbt.ByteTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTOutputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;


/**
 * Handles all stuff for writing shematic files
 * @author klaue
 *
 */
public class SchematicWriter {
	/**
	 * Writes the given slicestack to a new file
	 * @param f the File
	 * @param stack a SliceStack-object to build the shematics out of
	 * @throws IOException
	 * @throws ClassicNotSupportedException
	 * @throws ParseException 
	 */
	public static void writeSchematicsFile(SliceStack stack, File f) throws IOException, ClassicNotSupportedException, ParseException {
		try {
			int numOfBlocks = stack.getHeight() * stack.getWidth() * stack.getLength();
			
			Map<String, Tag> masterMap = new HashMap<String, Tag>();
			masterMap.put("Entities", new ListTag("Entities", CompoundTag.class, new ArrayList<Tag>())); // entities are not supported
			masterMap.put("Materials", new StringTag("Materials", "Alpha"));
			masterMap.put("Height", new ShortTag("Height", (short)stack.getHeight()));
			// length in MC means the depth (height of stack) but in MCSchematicTool, it means the width of the slice
			masterMap.put("Length", new ShortTag("Length", (short)stack.getWidth()));
			masterMap.put("Width", new ShortTag("Width", (short)stack.getLength()));
			
			byte[] blocks = new byte[numOfBlocks];
			byte[] data = new byte[numOfBlocks];
			ArrayList<Tag> tileEntities = new ArrayList<Tag>();
			
			
			// Blocks in MC are saved as a byte array which is ordered first by the height (lowest first),
			// then by the length (nord-south) and finally by the width (west-east)
			// substituting letters for blocks, that means the array [a, b, c, d, e, f, g, h] would result in two slices like the following ones (assuming
			// a height, width and length of 2):
			// (top)
			// ef
			// gh
			//
			// ab
			// cd
			// (bottom)

			int blocknumber = 0;
			
			
			for (int slz = 0; slz < stack.getHeight(); ++slz) {
				Slice slice = stack.getSlice(slz);
				for (int y = 0; y < slice.getHeight(); ++y) {
					for (int x = 0; x < slice.getWidth(); ++x) {
						Block block = slice.getBlockAt(x, y);
						blocks[blocknumber] = (byte)block.getId();
						data[blocknumber] = block.getData();
						
						// tile entity
						if (block.isChest() || block.isDispenser() || block.isNote() || block.isSign() || block.isBrewingStand() || block.isCommandBlock() || block.isBeacon()) {
							int mcZ = y;
							int mcX = x;
							int mcY = slz;
							// from mc wiki:
							// To access a specific block from either the block or data array from XYZ coordinates, use the following formula:
							// Index = x + (y * Height + z) * Width 
							assert((mcX + (mcY * stack.getHeight() + mcZ) * stack.getWidth()) == blocknumber);
							
							Map<String, Tag> tileEntityDataMap = new HashMap<String, Tag>();
							tileEntityDataMap.put("x", new IntTag("x", mcX));
							tileEntityDataMap.put("y", new IntTag("y", mcY));
							tileEntityDataMap.put("z", new IntTag("z", mcZ));
							
							if (block.isChest() || block.isDispenser() || block.isHopper() || block.isDropper()) {
								Item[] items = null;
								String idStr = null;
								switch(block.getType()) {
									case DISPENSER:	idStr = "Trap";		items = ((Dispenser)block).content;	break;
									case DROPPER:	idStr = "Dropper";	items = ((Dropper)block).content;	break;
									case HOPPER:	idStr = "Hopper";	items = ((Hopper)block).content;	break;
									case CHEST:
									default:		idStr = "Chest";	items = ((Chest)block).content;		break;
								}
								tileEntityDataMap.put("id", new StringTag("id",	idStr));
								
								ArrayList<Tag> itemList = new ArrayList<Tag>();
								
								for (int i = 0; i < items.length; ++i) {
									if (items[i].getId() == 0) continue; // empty
									
									Map<String, Tag> itemComp = getItemMapForCompound(items[i], i);
									itemList.add(new CompoundTag("Item", itemComp));
								} 

								tileEntityDataMap.put("Items", new ListTag("Items", CompoundTag.class, itemList));
								
							} else if(block.isNote()) {
								tileEntityDataMap.put("id", new StringTag("id", "Music"));
								tileEntityDataMap.put("note", new ByteTag("note", ((Note)block).getPitch()));
							} else if(block.isSign()) {
								tileEntityDataMap.put("id", new StringTag("id", "Sign"));
								String[] text = ((Sign)block).getText();
								
								tileEntityDataMap.put("Text1", new StringTag("Text1", text[0]));
								tileEntityDataMap.put("Text2", new StringTag("Text2", text[1]));
								tileEntityDataMap.put("Text3", new StringTag("Text3", text[2]));
								tileEntityDataMap.put("Text4", new StringTag("Text4", text[3]));
							} else if (block.isBrewingStand()) {
								tileEntityDataMap.put("id", new StringTag("id", "Cauldron"));
								Item[] items = ((BrewingStand)block).getContent();
								
								ArrayList<Tag> itemList = new ArrayList<Tag>();
								
								for (int i = 0; i < items.length; ++i) {
									if (items[i].getId() == 0) continue; // empty
									
									Map<String, Tag> itemComp = getItemMapForCompound(items[i], i);
									itemList.add(new CompoundTag("Item", itemComp));
								} 

								tileEntityDataMap.put("Items", new ListTag("Items", CompoundTag.class, itemList));
								tileEntityDataMap.put("BrewTime", new IntTag("BrewTime", ((BrewingStand)block).getBrewingTime()));
								
							} else if(block.isCommandBlock()) {
								CommandBlock commandBlock = (CommandBlock)block;
								tileEntityDataMap.put("id", new StringTag("id", "Control"));
								tileEntityDataMap.put("Command", new StringTag("Command", commandBlock.getCommand()));
								tileEntityDataMap.put("SuccessCount", new IntTag("SuccessCount", commandBlock.getSignalStrength()));
							} else if (block.isBeacon()) {
								Beacon beacon = (Beacon)block;
								tileEntityDataMap.put("id", new StringTag("id", "Beacon"));
								tileEntityDataMap.put("Levels", new IntTag("Levels", beacon.getLevel()));
								tileEntityDataMap.put("Primary", new IntTag("Primary", beacon.getPrimaryEffect()));
								tileEntityDataMap.put("Secondary", new IntTag("Secondary", beacon.getSecondaryEffect()));
							} else if (block.isMobHead()) {
								MobHead mobHead = (MobHead)block;
								tileEntityDataMap.put("id", new StringTag("id", "Skull"));
								tileEntityDataMap.put("SkullType", new ByteTag("SkullType", mobHead.getHeadTypeForTileEntities()));
								tileEntityDataMap.put("ExtraType", new StringTag("ExtraType", mobHead.getName()));
								tileEntityDataMap.put("Rot", new ByteTag("Rot", mobHead.getDirectionForTileEntities()));
							}
							tileEntities.add(new CompoundTag("", tileEntityDataMap));
						}
						
						++blocknumber;
					}
				}
			}

			masterMap.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, tileEntities));
			masterMap.put("Blocks", new ByteArrayTag("Blocks", blocks));
			masterMap.put("Data", new ByteArrayTag("Data", data));
			
			CompoundTag master = new CompoundTag("Schematic", masterMap);
			NBTOutputStream nos = new NBTOutputStream(new FileOutputStream(f));
			nos.writeTag(master);
			nos.close();
		} catch (ClassCastException e) {
			throw new ParseException(e);
		}
	}
	
	private static Map<String, Tag> getItemMapForCompound(Item item, int slot) {
		Map<String, Tag> itemComp = new HashMap<String, Tag>();
		itemComp.put("id", new ShortTag("id", item.getId()));
		itemComp.put("Damage", new ShortTag("Damage", item.getData()));
		itemComp.put("Count", new ByteTag("Count", item.getStacksize()));
		itemComp.put("Slot", new ByteTag("Slot", (byte)slot));
		
		// add additional properties (tag)
		Map<String, Tag> tagMap = new HashMap<String, Tag>();
		Map<String, Tag> displayMap = new HashMap<String, Tag>();
		displayMap.put("Name", new StringTag("Name", item.getName()));
		if (item.isColoredItem()) {
			int color = ((ColoredItem)item).getColorAsInt();
			displayMap.put("color", new IntTag("color", color));
		}
		tagMap.put("display", new CompoundTag("display", displayMap));
		itemComp.put("tag", new CompoundTag("tag", tagMap));
		
		return itemComp;
	}
}
