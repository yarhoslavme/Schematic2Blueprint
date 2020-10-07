package klaue.mcschematictool.blocktypes;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JToolTip;

import klaue.mcschematictool.BlockComponent;
import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.NameProvider;

// TODO: make blocks for entities Painting, Minecart, Boat?

/**
 * Represents a single block
 *
 * @author klaue
 */
public class Block {
    /**
     * Block types. Normal is a non-special, non-directional block without a state, like cobblestone.
     *
     * @author klaue
     */
    public enum Type {
        NORMAL,
        //Liquid
        LAVA, WATER,
        //Blocks
        DIRT, WOODENPLANK, WOOD, HUGEMUSHROOM, STONE, STONEBRICK,
        HARDENEDCLAY, SANDSTONE, NETHERWART, HAYBLOCK, QUARTZBLOCK, WOOL,

        //Odd shaped blocks
        STAIR, STONESLAB, WOODENSLAB,

        // Interactive Objects
        CHEST, DOOR, WEIGHTEDPRESSUREPLATE, PRESSUREPLATE, SIGN,
        PISTON, PISTONEXTENSION, DISPENSER, DROPPER, HOPPER,
        NOTE, LADDER, LEVER, BUTTON, FURNACE, BED, TRAP_DOOR, CAULDRON,
        TRIPWIRE, TRIPWIREHOOK, BEACON, BREWINGSTAND, ENDERCHEST, COMMANDBLOCK,
        JUKEBOX, ANVIL,

        //Fences and walls
        FENCE, GLASS, FENCEGATE, COBBLESTONEWALL,

        //Plants, seeds, food
        CROPS, COCOAPOD, FARMLAND, FIRE, LEAF, FLOWER, SAPLING, CACTUS,
        PUMPKIN, JACKOLANTERN, CAKE, TALL_GRASS, STEM, VINES,

        //Rails
        RAIL, POWERED_RAIL, DETECTOR_RAIL, ACTIVATOR_RAIL,
        //Redstone
        REDSTONE_TORCH, REDSTONELAMP, REDSTONE_REPEATER, REDSTONE_WIRE,

        //Light and decorations
        TORCH, FLOWERPOT, MOBHEAD,

        //Portals
        ENDPORTALFRAME,

        //Coverings
        SNOWCOVER, CARPET


    }

    ;

    protected short id = 0; // air
    protected byte data = 0;
    protected Type type = Type.NORMAL;

    // caching of block images
    protected static HashMap<String, BufferedImage> imageCache = new HashMap<>();
    protected static double zoomCache = -1;

    /**
     * Generates a new air block
     * If you want a block other than air, use Block.getInstance() to ensure the right subtype
     */
    public Block() {
        this((byte) 0, (byte) 0);
    }

    /**
     * Generates a new block of type id with no additional data
     *
     * @param id
     */
    protected Block(short id) {
        this(id, (byte) 0);
    }

    /**
     * Generates a new block of type id
     *
     * @param id   Block ID
     * @param data Block Data
     */
    protected Block(short id, byte data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Returns the right type of block for the id (note that this is no Singleton)
     *
     * @param id the id of the block
     * @return an instance of the right subblock
     */
    public static Block getInstance(short id) {
        return getInstance(id, (byte) 0);
    }

    /**
     * Returns the right type of block for the id (note that this is no Singleton)
     *
     * @param id   the id of the block
     * @param data the minecraft block data
     * @return an instance of the right subblock
     */
    public static Block getInstance(short id, byte data) {
        switch (id) {
            case 1:
                return new Stone(data);

            case 3:
                return new Dirt(data);
            //case 4: 	Cobblestone
            case 5:
                return new WoodenPlank(data);
            case 6:
                return new Sapling(data);
            //case 7:	Bedrock
            case 8:
            case 9:
                return new Water(id, data);
            case 10:
            case 11:
                return new Lava(id, data);
            case 12:
                return new Sand(data);
            case 17:
                return new Wood(data);
            case 18:
                return new Leaves(data);
            case 23:
                return new Dispenser(null, (byte) 0);
            case 24:
                return new Sandstone(data);
            case 26:
                return new Bed(data);
            case 27:
                return new PoweredRail(data);
            case 28:
                return new DetectorRail(data);
            case 29:
            case 33:
                return new Piston(id, data);
            case 31:
                return new TallGrass(data);
            case 34:
                return new PistonExtension(data);
            case 35:
                return new Wool(data);
            case 38:
                return new RedFlower(data);        //minecraft_red_flower - formerly rose, now all flowers except dandelion
            case 43:
            case 44:
                return new StoneSlab(id, data);
            case 50:
                return new Torch(data);
            case 51:
                return new Fire(data);

            // STAIRS
            case 53: // oak
            case 67: // cobble
            case 108: // brick
            case 109: // stonebrick
            case 114: // netherbrick
            case 128: // sandstone
            case 134: // spruce
            case 135: // birch
            case 136: // jungle
            case 156:    // quartz
            case 163:    // Acacia Stairs
            case 164:    // Dark oak Stairs
            case 203:
            case 180:
                return new Stair(id, data); // Red Sandstone Stairs

            case 54:    // normal chest
            case 146:
                return new Chest(id); // trapped chest
            case 55:
                return new RedstoneWire(data);
            case 59:
                return new Crop((short) 59, data); // Wheat
            case 60:
                return new Farmland(data);
            case 61:
            case 62:
                return new Furnace(id, data);
            case 63:
            case 68:
                return new Sign(null, id, data);

            case 64:    // oak door
            case 71:    // iron door
            case 193:    // spruce door
            case 194:    // birch door
            case 195:    // jungle door
            case 196:    //acacia door
            case 197:    //dark oak door
                return new Door(id, data);

            case 78:
                return new SnowCover(data);
            case 65:
                return new Ladder(data);
            case 66:
                return new Rail(data);
            case 69:
                return new Lever(data);
            case 70:
            case 72:
                return new PressurePlate(id, data);
            case 75:
            case 76:
                return new RedstoneTorch(id, data);
            case 77:
                return new Button(id, data);
            case 84:
                return new Jukebox(data);
            case 86:
                return new Pumpkin(data);
            case 91:
                return new JackOLantern(data);
            case 92:
                return new Cake(data);
            case 93:
            case 94:
                return new RedstoneRepeater(id, data);
            case 95:
                return new Block(id, data);
            case 96:
                return new TrapDoor(data);
            case 97:
                return new MonsterEggBricks(data);
            case 98:
                return new StoneBrick(data);
            case 99:
            case 100:
                return new HugeMushroom(id, data);
            case 104:
            case 105:
                return new Stem(id, data);
            case 106:
                return new Vines(data);
            case 107:
                return new FenceGate(data);
            case 115:
                return new NetherWart(data);
            case 117:
                return new BrewingStand();
            case 118:
                return new Cauldron(data);
            case 120:
                return new EndPortalFrame(data);
            case 123:
            case 124:
                return new RedstoneLamp(id);
            case 125:
            case 126:
                return new WoodenSlab(id, data);
            case 127:
                return new CocoaPod(data);
            case 130:
                return new EnderChest(data);
            case 131:
                return new TripwireHook(data);
            case 132:
                return new TripWire(data);
            case 137:
                return new CommandBlock();
            case 138:
                return new Beacon();
            case 139:
                return new CobblestoneWall(data);
            case 140:
                return new FlowerPot(data);
            case 141:
                return new Crop((short) 141, data); // Carrot
            case 142:
                return new Crop((short) 142, data); // Potato
            case 143:
                return new Button(id, data);
            case 144:
                return new MobHead((byte) 3, (byte) 8, null, data); // north facing human head - most data from tile entity
            case 145:
                return new Anvil(data);
            case 147:
            case 148:
                return new WeightedPressurePlate(id, data);
            case 154:
                return new Hopper(null, (byte) 0);
            case 155:
                return new QuartzBlock(data);
            case 157:
                return new ActivatorRail(data);
            case 158:
                return new Dropper(null, (byte) 0);
            case 159:
                return new StainedClay(data);
            case 161:
                return new Leaves2(data);
            case 162:
                return new Wood2(data);
            //case 170:	return new HayBlock(data);
            case 171:
                return new Carpet(data);
            case 175:
                return new LargeFlower(data);
            case 179:
                return new RedSandStone(data);

            case 188:    //Spruce Fence
            case 189:    //Birch Fence
            case 190:    //Jungle Fence
            case 191:    //Dark_Oak Fence
            case 192:    //Acacia Fence
                return new Fence(id, data);
            default:
                return new Block(id, data);
        }
    }

    /**
     * Get the ID of the block
     *
     * @return the ID of the block
     */
    public short getId() {
        return this.id;
    }

    /**
     * Set the ID of the block
     *
     * @param id the new ID of the block
     */
    protected void setId(short id) {
        this.id = id;
    }

    /**
     * Get the additional block data. It's generally a better idea to figure out the block type and using the getters
     * of the specific subclass instead of decoding the data value yourself
     *
     * @return the raw data value
     */
    public byte getData() {
        return this.data;
    }

    /**
     * Sets the raw data value. It's generally a better idea to figure out the block type and using the setters
     * of the specific subclass instead of encoding the data value yourself
     *
     * @param data the new raw data value
     */
    public void setData(byte data) {
        this.data = data;
    }

    /**
     * rotates the block clockwise
     */
    public void turnCW() {
        turn(true);
    }

    /**
     * rotates the block counterclockwise
     */
    public void turnCCW() {
        turn(false);
    }

    /**
     * rotates the block
     *
     * @param CW true for clockwise rotation
     */
    public void turn(boolean CW) {
        // do nothing (overwritten by directional subblocks)
    }

    @Override
    public String toString() {
        // subtypes overwrite this, so it's just implemented for the boring old normal blocks
        return NameProvider.getNameOfBlockOrItem(this.id, this.data);
    }

    /**
     * Returns the tooltiptext (usually the same as toString())
     *
     * @return the tooltiptext
     */
    public String getToolTipText() {
        return this.toString();
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
        Block other = (Block) obj;
        if (this.data != other.data) return false;
        if (this.id != other.id) return false;
        return true;
    }

    /**
     * Gets a custom tooltip for this block
     *
     * @return the custom tool tip
     */
    public JToolTip getCustomToolTip() {
        // to be overwritten by subclasses
        return null;
    }

    /**
     * Returns a new BufferedImage representing the block
     * Warning: Block caches images by ID and ID only. If the block changes looks by data value, make a subclass
     *
     * @param zoom the current zoom value (>0)
     * @return the imagecomponent or null if images are deactivated
     */
    public synchronized BufferedImage getImage(double zoom) {
        if (!ImageProvider.isActivated()) return null;
        if (zoom <= 0) return null;

        BufferedImage img = null;

        if (Block.zoomCache != zoom) {
            // reset cache
            Block.imageCache.clear();
            Block.zoomCache = zoom;
        } else {
            img = Block.imageCache.get(this.id + ":" + this.data);
            if (img != null) {
                return img;
            }
        }

        // image not in cache, make new
        // get image from imageprovider (directional blocks are handled in subclasses)
        img = ImageProvider.getImageByBlockOrItemID(this.id, this.data);

        if (img == null) return null;

        // zoom
        if (zoom != 1) {
            img = ImageProvider.zoom(zoom, img);
        }

        // save image to cache
        Block.imageCache.put(this.id + ":" + this.data, img);

        return img;
    }

    /**
     * Returns a new BlockComponent object representing the block<br>
     * Note that the image will be updated when the block changes
     *
     * @param zoom the current zoom value (>0)
     * @return the imagecomponent or null if images are deactivated
     */
    public synchronized BlockComponent getComponent(double zoom) {
        return new BlockComponent(this, zoom);
    }

    /**
     * Used to get the type of the current block
     *
     * @return the type. Type normal is a non-special,
     * non-directional normal block without state like (something other than) dirt.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Checks if this block can have a direction
     *
     * @return true if this block can have a direction
     */
    public boolean isDirectional() {
        return false;
    }

    /**
     * Checks if this block can have multiple directions
     *
     * @return true if this block can have multiple directions
     */
    public boolean isMultiDirectional() {
        return false;
    }

    /**
     * Checks if this block is a chest
     *
     * @return true if this block is a chest
     */
    public boolean isChest() {
        return this.type == Type.CHEST;
    }

    /**
     * Checks if this block is Dirt.
     *
     * @return true if this block is dirt
     */
    public boolean isDirt() {
        return this.type == Type.DIRT;
    }

    /**
     * Checks if this block is a sign
     *
     * @return true if this block is a sign
     */
    public boolean isSign() {
        return this.type == Type.SIGN;
    }

    /**
     * Checks if this block is a dispenser
     *
     * @return true if this block is a dispenser
     */
    public boolean isDispenser() {
        return this.type == Type.DISPENSER;
    }

    /**
     * Checks if this block is a note block
     *
     * @return true if this block is a note block
     */
    public boolean isNote() {
        return this.type == Type.NOTE;
    }

    /**
     * Checks if this block is a wood block
     *
     * @return true if this block is a wood block
     */
    public boolean isWood() {
        return this.type == Type.WOOD;
    }

    /**
     * Checks if this block is a fire block
     *
     * @return true if this block is a fire block
     */
    public boolean isFire() {
        return this.type == Type.FIRE;
    }

    /**
     * Checks if this block is a leaf block
     *
     * @return true if this block is a leaf block
     */
    public boolean isLeaf() {
        return this.type == Type.LEAF;
    }

    /**
     * Checks if this block is a stone block
     *
     * @return true if this block is a stone block
     */
    public boolean isStone() {
        return this.type == Type.STONE;
    }


    /**
     * Checks if this block is a sapling block
     *
     * @return true if this block is a sapling block
     */
    public boolean isSapling() {
        return this.type == Type.SAPLING;
    }

    /**
     * Checks if this block is a cactus block
     *
     * @return true if this block is a cactus block
     */
    public boolean isCactus() {
        return this.type == Type.CACTUS;
    }

    /**
     * Checks if this block is a water block
     *
     * @return true if this block is a water block
     */
    public boolean isWater() {
        return this.type == Type.WATER;
    }

    /**
     * Checks if this block is a lava block
     *
     * @return true if this block is a lava block
     */
    public boolean isLava() {
        return this.type == Type.LAVA;
    }

    /**
     * Checks if this block is a farmland block
     *
     * @return true if this block is a farmland block
     */
    public boolean isFarmland() {
        return this.type == Type.FARMLAND;
    }

    /**
     * Checks if this block is a crops block
     *
     * @return true if this block is a crops block
     */
    public boolean isCrops() {
        return this.type == Type.CROPS;
    }

    /**
     * Checks if this block is a wool block
     *
     * @return true if this block is a wool block
     */
    public boolean isWool() {
        return this.type == Type.WOOL;
    }

    /**
     * Checks if this block is a Carpet block
     *
     * @return true if this block is a carpet block
     */
    public boolean isCarpet() {
        return this.type == Type.CARPET;
    }

    /**
     * Checks if this block is a torch
     *
     * @return true if this block is a torch
     */
    public boolean isTorch() {
        return this.type == Type.TORCH;
    }

    /**
     * Checks if this block is a redstone torch
     *
     * @return true if this block is a redstone torch
     */
    public boolean isRedstoneTorch() {
        return this.type == Type.REDSTONE_TORCH;
    }

    /**
     * Checks if this block is a rail
     *
     * @return true if this block is a rail
     */
    public boolean isRail() {
        return this.type == Type.RAIL;
    }

    /**
     * Checks if this block is a powered rail
     *
     * @return true if this block is a powered rail
     */
    public boolean isPoweredRail() {
        return this.type == Type.POWERED_RAIL;
    }

    /**
     * Checks if this block is a detector rail
     *
     * @return true if this block is a detector rail
     */
    public boolean isDetectorRail() {
        return this.type == Type.DETECTOR_RAIL;
    }

    /**
     * Checks if this block is a ladder block
     *
     * @return true if this block is a ladder block
     */
    public boolean isLadder() {
        return this.type == Type.LADDER;
    }

    /**
     * Checks if this block is a stair block
     *
     * @return true if this block is a stair block
     */
    public boolean isStair() {
        return this.type == Type.STAIR;
    }

    /**
     * Checks if this block is a lever
     *
     * @return true if this block is a lever
     */
    public boolean isLever() {
        return this.type == Type.LEVER;
    }

    /**
     * Checks if this block is a door
     *
     * @return true if this block is a door
     */
    public boolean isDoor() {
        return this.type == Type.DOOR;
    }

    /**
     * Checks if this block is a button
     *
     * @return true if this block is a button
     */
    public boolean isButton() {
        return this.type == Type.BUTTON;
    }

    /**
     * Checks if this block is a furnace block
     *
     * @return true if this block is a furnace block
     */
    public boolean isFurnace() {
        return this.type == Type.FURNACE;
    }

    /**
     * Checks if this block is a pumpkin block
     *
     * @return true if this block is a pumpkin block
     */
    public boolean isPumpkin() {
        return this.type == Type.PUMPKIN;
    }

    /**
     * Checks if this block is a jack'o'lantern block
     *
     * @return true if this block is a jack'o'lantern block
     */
    public boolean isJackOLantern() {
        return this.type == Type.JACKOLANTERN;
    }

    /**
     * Checks if this block is a pressure plate block
     *
     * @return true if this block is a pressure plate block
     */
    public boolean isPressurePlate() {
        return this.type == Type.PRESSUREPLATE;
    }

    /**
     * Checks if this block is a weighted pressure plate block
     *
     * @return true if this block is a weighted pressure plate block
     */
    public boolean isWeightedPressurePlate() {
        return this.type == Type.WEIGHTEDPRESSUREPLATE;
    }

    /**
     * Checks if this block is a stone slab or a stone double slab block
     *
     * @return true if this block is a stone slab or a stone double slab block
     */
    public boolean isStoneSlab() {
        return this.type == Type.STONESLAB;
    }

    /**
     * Checks if this block is a bed block
     *
     * @return true if this block is a bed block
     */
    public boolean isBed() {
        return this.type == Type.BED;
    }

    /**
     * Checks if this block is a redstone repeater
     *
     * @return true if this block is a redstone repeater
     */
    public boolean isRepeater() {
        return this.type == Type.REDSTONE_REPEATER;
    }

    /**
     * Checks if this block is a jukebox
     *
     * @return true if this block is a jukebox
     */
    public boolean isJukeBox() {
        return this.type == Type.JUKEBOX;
    }

    /**
     * Checks if this block is a redstone wire
     *
     * @return true if this block is a redstone wire
     */
    public boolean isRedstoneWire() {
        return this.type == Type.REDSTONE_WIRE;
    }

    /**
     * Checks if this block is tall grass
     *
     * @return true if this block is tall grass
     */
    public boolean isTallGrass() {
        return this.type == Type.TALL_GRASS;
    }

    /**
     * Checks if this block is a trap door
     *
     * @return true if this block is a trap door
     */
    public boolean isTrapDoor() {
        return this.type == Type.TRAP_DOOR;
    }

    /**
     * Checks if this block is a stone brick
     *
     * @return true if this block is a stone brick
     */
    public boolean isStoneBrick() {
        return this.type == Type.STONEBRICK;
    }

    /**
     * Checks if this block is a stem
     *
     * @return true if this block is a stem
     */
    public boolean isStem() {
        return this.type == Type.STEM;
    }

    /**
     * Checks if this block is a vine block
     *
     * @return true if this block is vines
     */
    public boolean isVines() {
        return this.type == Type.VINES;
    }

    /**
     * Checks if this block is a fence gate
     *
     * @return true if this block is a fence gate
     */
    public boolean isFenceGate() {
        return this.type == Type.FENCEGATE;
    }

    /**
     * Checks if this block is a huge shroom
     *
     * @return true if this block is a huge shroom
     */
    public boolean isHugeMushroom() {
        return this.type == Type.HUGEMUSHROOM;
    }

    /**
     * Checks if this block is a piston
     *
     * @return true if this block is a piston
     */
    public boolean isPiston() {
        return this.type == Type.PISTON;
    }

    /**
     * Checks if this block is a piston extension
     *
     * @return true if this block is a piston extension
     */
    public boolean isPistonExtension() {
        return this.type == Type.PISTONEXTENSION;
    }

    /**
     * Checks if this block is a cauldron
     *
     * @return true if this block is a cauldron
     */
    public boolean isCauldron() {
        return this.type == Type.CAULDRON;
    }

    /**
     * Checks if this block is a nether wart
     *
     * @return true if this block is a nether wart
     */
    public boolean isNetherWart() {
        return this.type == Type.NETHERWART;
    }

    /**
     * Checks if this block is an end portal frame
     *
     * @return true if this block is an end portal frame
     */
    public boolean isEndPortalFrame() {
        return this.type == Type.ENDPORTALFRAME;
    }

    /**
     * Checks if this block is a brewing stand
     *
     * @return true if this block is a brewing stand
     */
    public boolean isBrewingStand() {
        return this.type == Type.BREWINGSTAND;
    }

    /**
     * Checks if this block is a redstone lamp
     *
     * @return true if this block is a redstone lamp
     */
    public boolean isRedstoneLamp() {
        return this.type == Type.REDSTONELAMP;
    }

    /**
     * Checks if this block is a sandstone
     *
     * @return true if this block is a sandstone
     */
    public boolean isSandstone() {
        return this.type == Type.SANDSTONE;
    }

    /**
     * Checks if this block is a wooden plank
     *
     * @return true if this block is a wooden plank
     */
    public boolean isWoodenPlank() {
        return this.type == Type.WOODENPLANK;
    }

    /**
     * Checks if this block is a wooden slab or a wooden double slab block
     *
     * @return true if this block is a wooden slab or a wooden double slab block
     */
    public boolean isWoodenSlab() {
        return this.type == Type.WOODENSLAB;
    }

    /**
     * Checks if this block is a cocoa pod
     *
     * @return true if this block is a cocoa pod
     */
    public boolean isCocoaPod() {
        return this.type == Type.COCOAPOD;
    }

    /**
     * Checks if this block is a tripwire
     *
     * @return true if this block is a tripwire
     */
    public boolean isTripwire() {
        return this.type == Type.TRIPWIRE;
    }

    /**
     * Checks if this block is a tripwire hook
     *
     * @return true if this block is a tripwire hook
     */
    public boolean isTripwireHook() {
        return this.type == Type.TRIPWIREHOOK;
    }

    /**
     * Checks if this block is an ender chest
     *
     * @return true if this block is an ender chest
     */
    public boolean isEnderChest() {
        return this.type == Type.ENDERCHEST;
    }

    /**
     * Checks if this block is a command block
     *
     * @return true if this block is a command block
     */
    public boolean isCommandBlock() {
        return this.type == Type.COMMANDBLOCK;
    }

    /**
     * Checks if this block is a beacon
     *
     * @return true if this block is a beacon
     */
    public boolean isBeacon() {
        return this.type == Type.BEACON;
    }

    /**
     * Checks if this block is an anvil
     *
     * @return true if this block is an anvil
     */
    public boolean isAnvil() {
        return this.type == Type.ANVIL;
    }

    /**
     * Checks if this block is a flower pot
     *
     * @return true if this block is a flower pot
     */
    public boolean isFlowerPot() {
        return this.type == Type.FLOWERPOT;
    }

    /**
     * Checks if this block is a cobble wall
     *
     * @return true if this block is a cobble wall
     */
    public boolean isCobblestoneWall() {
        return this.type == Type.COBBLESTONEWALL;
    }

    /**
     * Checks if this block is a mob head
     *
     * @return true if this block is a mob head
     */
    public boolean isMobHead() {
        return this.type == Type.MOBHEAD;
    }

    /**
     * Checks if this block is an activator rail
     *
     * @return true if this block is an activator rail
     */
    public boolean isActivatorRail() {
        return this.type == Type.ACTIVATOR_RAIL;
    }

    /**
     * Checks if this block is a dropper
     *
     * @return true if this block is a dropper
     */
    public boolean isDropper() {
        return this.type == Type.DROPPER;
    }

    /**
     * Checks if this block is a hopper
     *
     * @return true if this block is a hopper
     */
    public boolean isHopper() {
        return this.type == Type.HOPPER;
    }

    /**
     * Checks if this block is a block of quartz
     *
     * @return true if this block is a block of quartz
     */
    public boolean isQuartzBlock() {
        return this.type == Type.QUARTZBLOCK;
    }

    /**
     * Checks if this block is a snow cover
     *
     * @return true if this block is a snow cover
     */
    public boolean isSnowCover() {
        return this.type == Type.SNOWCOVER;
    }
}
