package klaue.mcschematictool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;


/**
 * Handles all stuff for reading schematic files
 * @author klaue
 *
 */
public class SchematicReader {
	private static boolean hasErrorHappened = false; // a bit ugly, but oh well
	
	/**
	 * checks if any single block could not be read, as that does not throw the parse exception (instead the faulty block gets replaced by air)
	 * @return true if error happened
	 */
	public static boolean hasErrorHappened() {
		return hasErrorHappened;
	}
	
	/**
	 * Reads the given schematics file
	 * @param f the File
	 * @return a SliceStack-object of the Schematics
	 * @throws IOException
	 * @throws ClassicNotSupportedException
	 * @throws ParseException 
	 */
	public static SliceStack readSchematicsFile(File f) throws IOException, ClassicNotSupportedException, ParseException {
		FileInputStream fis = new FileInputStream(f);
		
		// special case if f is not a gzip file (maybe unzipped schematic)
		try {
			GZIPInputStream gis = new GZIPInputStream(fis);
			gis.close();
		} catch (IOException e) {
			if (!e.getMessage().toLowerCase().contains("not in gzip format")) {
				// rethrow
				throw e;
			}
			// not gzip - zip it to temp
			String name = f.getName();
			name = name.substring(0, name.lastIndexOf('.'));
			File outFile = File.createTempFile(name, ".schematic");
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFile));
	        FileInputStream in = new FileInputStream(f);
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        // Complete the GZIP file
	        out.finish();
	        out.close();
	        f = outFile;
	        f.deleteOnExit();
	        System.err.println("Input schematic was not in gzip format! Tried to fix it to " + f.getAbsolutePath());
		}
		
		NBTInputStream nis = new NBTInputStream(new FileInputStream(f));
		CompoundTag master = (CompoundTag) nis.readTag();
		//System.out.println(master);
		nis.close();
		Map<String, Tag> masterMap = master.getValue();

		if (masterMap.get("Materials") == null || !((StringTag)masterMap.get("Materials")).getValue().equalsIgnoreCase("alpha")) {
			throw new ClassicNotSupportedException();
		}
			
		try {
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
			
			// length in MC means the depth (height of stack) but in MCSchematicTool, it means the width of the slice
			int length = ((ShortTag) masterMap.get("Width")).getValue(); // width of slice!
			int width = ((ShortTag) masterMap.get("Length")).getValue(); // height of slice!
			int height = ((ShortTag) masterMap.get("Height")).getValue();
			hasErrorHappened = false;
			
			SliceStack schematic = new SliceStack(height, length, width);
			byte[] blocks = ((ByteArrayTag) masterMap.get("Blocks")).getValue();
			byte[] data = ((ByteArrayTag) masterMap.get("Data")).getValue();
			
			// get tile entities
			List<Tag> entities = ((ListTag) masterMap.get("TileEntities")).getValue();
			Map<Integer, Map<String, Tag> > idxEntitiesMap = new TreeMap<Integer, Map<String, Tag> >();
			for (Tag tag : entities) {
				Map<String, Tag> cmpMap = ((CompoundTag)tag).getValue();
				int y = ((IntTag) cmpMap.get("z")).getValue(); // mc's y and z are not the same
				int z = ((IntTag) cmpMap.get("y")).getValue();
				int x = ((IntTag) cmpMap.get("x")).getValue();
				//Index = (z * width * length) + (y * length) + x
				//Index = x+(y+z*width) * length
				int blockIndex = x + (y + z * width) * length;
				idxEntitiesMap.put(blockIndex, cmpMap);
			}
			
			int blocknumber = 0;
			for (int slz = 0; slz < height; ++slz) {
				Slice s = schematic.getSlice(slz);
				for (int y = 0; y < width; ++y) { // height of slice
					for (int x = 0; x < length; ++x) { // width of slice
						Block block = null;
						
						// &0xFF is because the array uses unsigned bytes while java uses signed ones. it converts values like -127 back to values like 129
						short blockid = (short)(blocks[blocknumber] & 0xFF);
						try {
							// check for special type of block
							// special blocks are those that have tile entities
							if (blockid == 54 || blockid == 146 || blockid == 23 || blockid == 154 || blockid == 158) {
								//chest, trapped chest, dispenser, hopper, dropper
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume empty chest/dispenser/hopper/dropper
									switch(blockid) {
										case 146: // trapped chest
										case 54:	block = new Chest(blockid);							break;
										case 23:	block = new Dispenser(null, data[blocknumber]);		break;
										case 154:	block = new Hopper(null, data[blocknumber]);		break;
										case 158:
										default:	block = new Dropper(null, data[blocknumber]);		break;
									}
								} else {
									String tagId = ((StringTag)tileEntity.get("id")).getValue();
									if (((blockid == 54 || blockid == 146) && !tagId.equals("Chest"))
											|| (blockid == 23 && !tagId.equals("Trap"))
											|| (blockid == 154 && !tagId.equals("Hopper"))
											|| (blockid == 158 && !tagId.equals("Dropper"))) {
										throw new ParseException("Contains a container, id " + blockid + " that has a wrong tile entity of type " + tagId);
									}
									
									// get chest items
									Item[] items = null;
									if (blockid == 54 || blockid == 146) { // chest
										items = new Item[27];
									} else if (blockid == 23 || blockid == 158) { // dispenser/dropper
										items = new Item[9];
									} else { // hopper
										items = new Item[5];
									}
									Arrays.fill(items, new Item());
									
									List<Tag> itemList = ((ListTag) tileEntity.get("Items")).getValue();
									for (Tag tag : itemList) {
										addItemToListFromCompound(items, (CompoundTag) tag);
									}
									
									switch(blockid) {
										case 146: // trapped chest
										case 54:	block = new Chest(blockid, items);					break;
										case 23:	block = new Dispenser(items, data[blocknumber]);	break;
										case 154:	block = new Hopper(items, data[blocknumber]);		break;
										case 158:
										default:	block = new Dropper(items, data[blocknumber]);		break;
									}
								}
							} else if (blockid == 25) {
								// note
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume default noteblock
									block = new Note((byte)0);
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Music")) {
										throw new ParseException("Contains a note block that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									byte pitch = ((ByteTag)tileEntity.get("note")).getValue();
									block = new Note(pitch);
								}
							} else if (blockid == 63 || blockid == 68) {
								// sign
								boolean isWallSign = (blockid == 68);
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume empty sign
									block = new Sign(null, isWallSign, data[blocknumber]);
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Sign")) {
										throw new ParseException("Contains a sign that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									String text[] = new String[4];
									text[0] = ((StringTag)tileEntity.get("Text1")).getValue();
									text[1] = ((StringTag)tileEntity.get("Text2")).getValue();
									text[2] = ((StringTag)tileEntity.get("Text3")).getValue();
									text[3] = ((StringTag)tileEntity.get("Text4")).getValue();
									block = new Sign(text, isWallSign, data[blocknumber]);
								}
							} else if (blockid == 117) {
								// brewing stand
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume empty brewing stand
									block = new BrewingStand();
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Cauldron")) {
										throw new ParseException("Contains a brewing stand that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									
									// get chest items
									Item[] items = new Item[4];
									Arrays.fill(items, new Item());
									
									List<Tag> itemList = ((ListTag) tileEntity.get("Items")).getValue();
									for (Tag tag : itemList) {
										addItemToListFromCompound(items, (CompoundTag) tag);
									}
									
									// brewing time is defined as being IntTag, but some schematic files fly around in which it is ShortTag
									Tag brewingTimeTag = tileEntity.get("BrewTime");
									int brewingTime = 0;
									if (brewingTimeTag instanceof ShortTag) {
										brewingTime = ((ShortTag)brewingTimeTag).getValue();
									} else {
										brewingTime = ((IntTag)brewingTimeTag).getValue();
									}
									
									block = new BrewingStand(data[blocknumber], items, brewingTime);
								}
							} else if (blockid == 137) {
								// command block
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume empty command block
									block = new CommandBlock();
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Control")) {
										throw new ParseException("Contains a command block that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									String command = ((StringTag) tileEntity.get("Command")).getValue();
									Integer strength = ((IntTag) tileEntity.get("SuccessCount")).getValue();
									int signalStrength = (strength == null) ? 0 : strength;
									block = new CommandBlock(command, signalStrength);
								}
							} else if (blockid == 138) {
								// beacon
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume empty beacon
									block = new Beacon();
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Beacon")) {
										throw new ParseException("Contains a beacon that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									Integer levels = ((IntTag) tileEntity.get("Levels")).getValue();
									Integer primary = ((IntTag) tileEntity.get("Primary")).getValue();
									Integer secondary = ((IntTag) tileEntity.get("Secondary")).getValue();
									if (levels == null) levels = 0;
									if (primary == null) primary = 0;
									if (secondary == null) secondary = 0;
									block = new Beacon(primary, secondary, levels);
								}
							} else if (blockid == 144) {
								// mob head
								Map<String, Tag> tileEntity = idxEntitiesMap.get(blocknumber);
								if (tileEntity == null) {
									// assume default mob head
									block = Block.getInstance(blockid, data[blocknumber]);
								} else {
									if (!((StringTag)tileEntity.get("id")).getValue().equals("Skull")) {
										throw new ParseException("Contains a mob head that has a tile entity of type " + ((StringTag)tileEntity.get("id")).getValue() + ":\n" + tileEntity.toString());
									}
									Byte skullType = ((ByteTag) tileEntity.get("SkullType")).getValue();
									/* In 1.8+, there are Block State data values that need to be 
									*  read instead.
									* For now, just ignore it if it's null.
									*/
									String name = "Herobrine";
									if (tileEntity.get("ExtraType") != null){
										name = ((StringTag) tileEntity.get("ExtraType")).getValue();
									}
									//String name = ((StringTag) tileEntity.get("ExtraType")).getValue();
									Byte rotation = ((ByteTag) tileEntity.get("Rot")).getValue();
									
									// should never be null, but just in case, set default values
									if (skullType == null) skullType = 3; // human
									if (rotation == null) rotation = 8; // north
									
									block = new MobHead(skullType, rotation, name, data[blocknumber]);
								}
							} else {
								// boring everyday block or block with data value
								block = Block.getInstance(blockid, data[blocknumber]);
							}
						} catch (Exception e) {
							// current block is faulty, replace with air
							System.err.print("Faulty block (" + blockid + ":" + data[blocknumber] + ") at slice " + slz + ", column " + x + ", row " + y + ". ");
							e.printStackTrace();
							hasErrorHappened = true;
							block = new Block(); // air
						}
						
						s.setBlock(block, x, y);
						++blocknumber;
					}
				}
			}
			
			return schematic;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e);
		}
	}
	
	/**
	 * Adds the given item to the given item array
	 * @param items the array of items
	 * @param itemTag the item-Compoundtag
	 */
	private static void addItemToListFromCompound(Item[] items, CompoundTag itemTag) {
		
		Map<String, Tag> itemMap = itemTag.getValue();
		//System.err.print("Trying to convert " + (itemMap.get("id")).getValue() + " to a Short. ");
		/* Items aren't always going to be shorts anymore.  Need to detect type and if necessary,
		 * convert "minecraft:item" tag to a short value, using the mcNameToShortID(String) function.
		 */
	 
		String itemName = (String)(itemMap.get("id")).getValue().toString();
		short itemId = mcNameToShortID(itemName);
		
		byte itemCount = ((ByteTag)itemMap.get("Count")).getValue();
		byte itemSlot = ((ByteTag)itemMap.get("Slot")).getValue();
		short itemDamage = ((ShortTag)itemMap.get("Damage")).getValue();
		
		items[itemSlot] = Item.getInstance(itemId, itemDamage, itemCount);
		
		// add special tag info - null check everywhere because older MC versions (and therefore older schematics) may not have had a tag-tag
		Tag tag = itemMap.get("tag");
		if (tag == null || tag instanceof CompoundTag == false) return;
		Map<String, Tag> tagMap = ((CompoundTag)tag).getValue();
		
		// display properties - every item "should" have at least the display property "name"
		Tag display = tagMap.get("display");
		if (display != null && display instanceof CompoundTag) {
			Map<String, Tag> displayMap = ((CompoundTag)display).getValue();
			if (displayMap != null) {
				// name - special name to be used instead of default one - can be empty
				Tag name = displayMap.get("Name");
				if (name != null && name instanceof StringTag) {
					String nameStr = ((StringTag)name).getValue();
					items[itemSlot].setName(nameStr);
				}
				
				// color - only supported by leather armor
				if (items[itemSlot].isColoredItem()) {
					Tag color = displayMap.get("color");
					if (color != null && color instanceof IntTag) {
						Integer iColor = ((IntTag)color).getValue();
						if (iColor != null) {
							((ColoredItem)items[itemSlot]).setColor(iColor);
						}
					}
				} // items[itemSlot] instanceof LeatherArmor
			} // displayMap != null
		} // display != null
	} // fnc
	
	public static short mcNameToShortID(String mcName) {
		
		/*
		 * This is a simple lookup, so put all data values and elements in a text file (tab delimited, I suppose)
		 * 
		 * 
Idea:
Read file "minecraftNameIDMap.txt", tab delimited".

Map<String, Integer> myMap = new HashMap<String, Integer>();
String s = "SALES:0,SALE_PRODUCTS:1,EXPENSES:2,EXPENSES_ITEMS:3";
String[] pairs = s.split(",");
for (int i=0;i<pairs.length;i++) {
    String pair = pairs[i];
    String[] keyValue = pair.split(":");
    myMap.put(keyValue[0], Integer.valueOf(keyValue[1]));
}
		 * 
		 * 
		 */
		switch(mcName) {
			case "minecraft:arrow":	return 262; 	//minecraft:arrow
		}
		return 0;
	}
}
