package klaue.mcschematictool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;

import klaue.mcschematictool.blocktypes.Stair;
import klaue.mcschematictool.blocktypes.StoneSlab;
import klaue.mcschematictool.blocktypes.WoodenSlab;
import klaue.mcschematictool.thirdparty.BlendComposite;

/**
 * This class reads the minecraft images and provides them through getters and setters
 * This whole class is static and should only be initialized once
 *
 * @author klaue
 */
public class ImageProvider {
    private static TreeMap<String, BufferedImage> blockImages = new TreeMap<String, BufferedImage>();
    private static TreeMap<String, BufferedImage> additionalImages = new TreeMap<String, BufferedImage>();
    private static TreeMap<String, BufferedImage> itemImages = new TreeMap<String, BufferedImage>();
    private static TreeMap<String, BufferedImage> tooltipImages = new TreeMap<String, BufferedImage>();
    private static BufferedImage[][] letterimages = null;

    private ImageProvider() {
        // prevent instances
    }

    /**
     * Initializes the ImageProvider by reading the block and item images
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    public static synchronized void initialize() throws IOException, URISyntaxException {
        if (!blockImages.isEmpty()) return; // allready initialized

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // load images
        blockImages = getImagesFromJarDir(classLoader, "klaue/mcschematictool/textures/blocks");
        additionalImages = getImagesFromJarDir(classLoader, "klaue/mcschematictool/textures/additional");
        itemImages = getImagesFromJarDir(classLoader, "klaue/mcschematictool/textures/items");
        tooltipImages = getImagesFromJarDir(classLoader, "klaue/mcschematictool/textures/tooltip");

        // load letters from font.png
        BufferedImage font = ImageIO.read(ClassLoader.getSystemResource("klaue/mcschematictool/font.png"));
        int width = font.getWidth() / 16;
        int height = font.getHeight() / 16;
        letterimages = new BufferedImage[width][];
        for (int x = 0; x < width; ++x) {
            letterimages[x] = new BufferedImage[height];
            for (int y = 0; y < height; ++y) {
                letterimages[x][y] = font.getSubimage(x * 8, y * 8, 8, 8);
            }
        }
    }

    /**
     * Gets the image with the specified name. Make sure not to change the returned reference (only copies of it) or the provider will get compromised
     *
     * @param name the filename of the block image without extension
     * @return a 16x16 px image
     */
    public final static synchronized BufferedImage getImage(String name) {
        return blockImages.get(name.toLowerCase());
    }

    /**
     * Gets the image with the specified name from the additional image set.  Make sure not to change the returned reference (only copies of it) or the provider will get compromised
     *
     * @param name the filename of the additional image without extension
     * @return a 16x16 px image
     */
    public final static synchronized BufferedImage getAdditionalImage(String name) {
        return additionalImages.get(name.toLowerCase());
    }

    /**
     * Gets the image with the specified name from the item image set.  Make sure not to change the returned reference (only copies of it) or the provider will get compromised
     *
     * @param name the filename of the item image without extension
     * @return a 16x16 px image
     */
    public final static synchronized BufferedImage getItemImage(String name) {
        return itemImages.get(name.toLowerCase());
    }

    /**
     * returns an array of BufferedImages that consists of 8x8 px representations of the given (extended ASCII) text
     *
     * @param text  the text, has to be ext. ascii (code page 437)
     * @param color which color the text should be
     * @return 8x8px letter-images for the input string. note that minecraft cuts off the last two pixels (which are empty for normal characters)
     */
    public final static synchronized BufferedImage[] stringToImage(String text, int color) {
        if (letterimages == null) return null;

        byte[] ascii;

        try {
            ascii = text.getBytes("IBM437"); // Cp437 works too
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        // see http://en.wikipedia.org/wiki/Code_page_437
        BufferedImage[] imageText = new BufferedImage[ascii.length];
        for (int i = 0; i < ascii.length; ++i) {
            byte character = ascii[i];
            // because the 256 characters are in a 16x16 grid and in the ASCII ordering, we can take the upper 4 bits as the vertical and the lower
            // 4 bits as the horizontal index
            BufferedImage charImg = letterimages[character & 0x0F][character >> 4];

            // java seems to have no quick'n'easy way to replace a color, sooo.. instead of wasting time with a filter, color model stuff etc,
            // let's do a boring old search'n'replace (shouldn't be too costly for an 8x8 image)
            if (color != 0xFFFFFFFF) {
                charImg = copyImage(charImg);
                for (int x = 0; x < charImg.getWidth(); ++x) {
                    for (int y = 0; y < charImg.getWidth(); ++y) {
                        if (charImg.getRGB(x, y) == 0xFFFFFFFF) {
                            charImg.setRGB(x, y, color);
                        }
                    }
                }
            }

            imageText[i] = charImg;
        }

        return imageText;
    }

    /**
     * Gets a copy of the tooltip image of the sign
     *
     * @return the image
     */
    public final static synchronized BufferedImage getSignPlaneCopy() {
        BufferedImage sign = tooltipImages.get("sign");
        if (sign == null) return null;
        return copyImage(sign);
    }

    /**
     * Gets a copy of the tooltip image of the chest
     *
     * @return the image
     */
    public final static synchronized BufferedImage getChestPlaneCopy() {
        BufferedImage chest = tooltipImages.get("chest");
        if (chest == null) return null;
        return copyImage(chest);
    }

    /**
     * Gets a copy of the tooltip image of the dispenser
     *
     * @return the image
     */
    public final static synchronized BufferedImage getDispenserPlaneCopy() {
        BufferedImage dispenser = tooltipImages.get("dispenser");
        if (dispenser == null) return null;
        return copyImage(dispenser);
    }

    /**
     * Gets a copy of the tooltip image of the hopper
     *
     * @return the image
     */
    public final static synchronized BufferedImage getHopperPlaneCopy() {
        BufferedImage dispenser = tooltipImages.get("hopper");
        if (dispenser == null) return null;
        return copyImage(dispenser);
    }

    /**
     * Gets a copy of the tooltip image of the brewing stand
     *
     * @return the image
     */
    public final static synchronized BufferedImage getBrewingStandPlaneCopy() {
        BufferedImage brewingStand = tooltipImages.get("brewingstand");
        if (brewingStand == null) return null;
        return copyImage(brewingStand);
    }

    /**
     * @return true if images are activated
     */
    public static boolean isActivated() {
        return !itemImages.isEmpty();
    }

    /**
     * Makes a copy of the given BufferedImage
     *
     * @param img the image to copy
     * @return the copy
     */
    public static BufferedImage copyImage(BufferedImage img) {
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = copy.createGraphics();
        g.drawRenderedImage(img, null);
        return copy;
    }

    /**
     * Zooms the given image (returns new image instance)
     *
     * @param zoom
     * @param img
     * @return a zoomed instance
     */
    public static BufferedImage zoom(double zoom, final BufferedImage img) {
        int width = (int) (img.getWidth() * zoom);
        int height = (int) (img.getHeight() * zoom);

        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = newImg.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(zoom, zoom);
        g.drawRenderedImage(img, at);
        return newImg;
    }

    /**
     * Rotates the given image clockwise by the given angle.
     * A new image instance will be returned, the original image will not be changed.
     *
     * @param degrees The degrees (CW) to rotate the image
     * @param img     the image to be rotated
     * @return the rotated image (or the given image if angle is 0)
     */
    public static BufferedImage rotateImage(double degrees, final BufferedImage img) {
        if (degrees == 0) return img;

        // note: width and height do not have to be inverted for all cases, but it doesn't matter anyway because the images
        // are perfect squares
        BufferedImage newImg = new BufferedImage(img.getHeight(), img.getWidth(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = newImg.createGraphics();
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(degrees), img.getWidth() / 2, img.getHeight() / 2);
        g.drawRenderedImage(img, at);
        return newImg;
    }

    /**
     * Uses multiply to blend this image with the given color (same effect as a layer filled with a color set to multiply in GIMP)
     * A new image instance will be returned, the original image will not be changed.
     *
     * @param source       the image to be used for the multiply
     * @param overlayColor the color to be used for the multiply
     * @return the resulting image
     * @throws InterruptedException
     */
    public static BufferedImage multiplyImage(final BufferedImage source, Color overlayColor) throws InterruptedException {
        if (source == null) return null;

        int width = source.getWidth();
        int height = source.getHeight();

        // get original pixels
        int[] pixels = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(source, 0, 0, width, height, pixels, 0, width);
        pixelGrabber.grabPixels();

        // generate color array (BlendComposite handles alpha in the overlay not correctly, treats it as black,
        // therefore there has to be no color in the overlay in the transparent areas of the source)
        int[] colorPixels = new int[width * height];
        for (int i = 0; i < pixels.length; ++i) {
            boolean isAlpha = (pixels[i] >> 32) == 0;
            colorPixels[i] = isAlpha ? pixels[i] : overlayColor.getRGB();
        }

        // make new image with the same size and fill overlap with color
        BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // must be INT
        overlay.setRGB(0, 0, width, height, colorPixels, 0, width);

        // multiply
        BufferedImage coloredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // must be INT
        Graphics2D coloredGraphics = coloredImg.createGraphics();

        coloredGraphics.drawRenderedImage(source, null);
        coloredGraphics.setComposite(BlendComposite.Multiply.derive(1f));
        coloredGraphics.drawRenderedImage(overlay, null);
        return coloredImg;
    }

    /**
     * Returns an image representing the given ID. Note that only images on zoom level 1 are returned, and that additional overlays, like the direction,
     * are not added. The image returned is exactly as the one in minecrafts terrain.png or item.png with the exception of stairs and slabs/double slabs
     * (there is no stair image in the files, it's just a normal block with one edge cut out, and all slabs beside stone slabs also just use the normal
     * block image cut in half).
     * So if you, for example, need an image of a rail turned the right way to accomondate it's direction, or a torch that has an arrow added for the
     * direction, get an instance of the block by Block.getInstance() and use it's getImage method.
     * Same as a call to getImageByBlockOrItemID(<id>, -1);
     *
     * @param id
     * @return an image representing the block. DO NOT CHANGE THIS IMAGE as all block instances will use it
     */
    public final static synchronized BufferedImage getImageByBlockOrItemID(short id) {
        return getImageByBlockOrItemID(id, (byte) -1);
    }

    /**
     * Returns an image representing the given ID. Note that only images on zoom level 1 are returned, and that additional overlays, like the direction,
     * are not added. The image returned is exactly as the one in minecrafts texture directory with the exception of stairs and slabs/double slabs
     * (there is no stair image in the files, it's just a normal block with one edge cut out, and all slabs beside stone slabs also just use the normal
     * block image cut in half).
     *
     * @param id
     * @param data the block data or -1 for default value of this block (like the image for white wool for a wool block)
     * @return an image representing the block. DO NOT CHANGE THIS IMAGE as all block instances will use it
     */
    public final static synchronized BufferedImage getImageByBlockOrItemID(short id, byte data) {
        switch (id) {
            case 0:
                return ImageProvider.getAdditionalImage("air");
            case 1:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("stone_granite");
                    case 2:
                        return ImageProvider.getImage("stone_granite_polished");
                    case 3:
                        return ImageProvider.getImage("stone_diorite");
                    case 4:
                        return ImageProvider.getImage("stone_diorite_polished");
                    case 5:
                        return ImageProvider.getImage("stone_andesite");
                    case 6:
                        return ImageProvider.getImage("stone_andesite_polished");
                    case 0:
                    default:
                        return ImageProvider.getImage("stone");
                }
            case 2:
                return ImageProvider.getAdditionalImage("grass");
            case 3:
                switch (data) {
                    case 2:
                        return ImageProvider.getImage("dirt_coarse");
                    case 1:
                        return ImageProvider.getImage("coarse_dirt");
                    case 0:
                    default:
                        return ImageProvider.getImage("dirt");
                }

            case 4:
                return ImageProvider.getImage("cobblestone"); // Cobblestone
            case 5:    // Wooden Plank
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("wood_spruce");
                    case 2:
                        return ImageProvider.getImage("wood_birch");
                    case 3:
                        return ImageProvider.getImage("wood_jungle");
                    case 4:
                        return ImageProvider.getImage("wood_acacia");
                    case 5:
                        return ImageProvider.getImage("wood_dark_oak");
                    case 0:
                    default:
                        return ImageProvider.getImage("wood"); // oak
                }
            case 6:    // Sapling
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("sapling_spruce");
                    case 2:
                        return ImageProvider.getImage("sapling_birch");
                    case 3:
                        return ImageProvider.getImage("sapling_jungle");
                    case 4:
                        return ImageProvider.getImage("sapling_acacia");
                    case 5:
                        return ImageProvider.getImage("sapling_dark_oak");
                    case 0:
                    default:
                        return ImageProvider.getImage("sapling"); // oak
                }
            case 7:
                return ImageProvider.getImage("bedrock");
            case 8:        //water
            case 9:
                return ImageProvider.getAdditionalImage("water");
            case 10:    //lava
            case 11:
                return ImageProvider.getAdditionalImage("lava");
            case 12:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("sand_red");
                    case 0:
                    default:
                        return ImageProvider.getImage("sand");
                }


            case 13:
                return ImageProvider.getImage("gravel");
            case 14:
                return ImageProvider.getImage("oreGold");
            case 15:
                return ImageProvider.getImage("oreIron");
            case 16:
                return ImageProvider.getImage("oreCoal");
            case 17: // Wood (Log)
                byte typedata = (byte) (data & 0x3);
                switch (typedata) {
                    case 1:
                        return ImageProvider.getImage("tree_spruce");
                    case 2:
                        return ImageProvider.getImage("tree_birch");
                    case 3:
                        return ImageProvider.getImage("tree_jungle");
                    case 0:
                    default:
                        return ImageProvider.getImage("tree_side"); // Oak
                }
            case 161:
            case 18:
                return ImageProvider.getAdditionalImage("leaves");    // (use same image for all leaf-types)
            case 19:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("sponge_wet");
                    case 0:
                    default:
                        return ImageProvider.getImage("sponge");
                }
            case 20:
                return ImageProvider.getImage("glass");
            case 21:
                return ImageProvider.getImage("oreLapis"); // Lapis Lazuli ore
            case 22:
                return ImageProvider.getImage("blockLapis"); // Lapis Lazuli Block
            case 23:
                return ImageProvider.getImage("dispenser_front");
            case 24:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("sandstone_carved");
                    case 2:
                        return ImageProvider.getImage("sandstone_smooth");
                    case 0:
                    default:
                        return ImageProvider.getImage("sandstone_side");
                }
            case 25:
                return ImageProvider.getImage("musicBlock");
            case 26: // Bed
                if ((data & 8) == 0) { // foot
                    return ImageProvider.getImage("bed_feet_top");
                }
                return ImageProvider.getImage("bed_head_top");
            case 27: // Powered rails
                if ((data & 8) == 0) { // off
                    return ImageProvider.getImage("goldenRail");
                }
                return ImageProvider.getImage("goldenRail_powered");
            case 28:
                return ImageProvider.getImage("detectorRail");
            case 29:                                                    // Sticky piston (uses same image as normal piston)
            case 33:                                                    // Normal piston
                switch (data & 7) {
                    case 0:
                        return ImageProvider.getImage("piston_bottom");
                    case 1:
                        return ImageProvider.getImage("piston_inner_top");
                    default: // side (is turned in piston class)
                        if ((data & 8) != 0) {
                            return ImageProvider.getAdditionalImage("piston_side_extended");
                        }
                        return ImageProvider.getImage("piston_side");
                }
            case 30:
                return ImageProvider.getImage("web");
            case 31:    // Tall Grass
                switch (data) {
                    case 0:
                        return ImageProvider.getImage("deadbush");
                    case 2:
                        return ImageProvider.getAdditionalImage("fern");
                    case 1:
                    default:
                        return ImageProvider.getAdditionalImage("grass_tall");
                }
            case 32:
                return ImageProvider.getImage("deadbush");
            case 34: // Piston Extension
                switch (data & 7) {
                    case 0:
                        return ImageProvider.getAdditionalImage("piston_extension_down");
                    case 1: // up
                        if ((data & 8) != 0) {
                            return ImageProvider.getImage("piston_top_sticky");
                        }
                        return ImageProvider.getImage("piston_top");
                    default:                                            // side
                        if ((data & 8) != 0) {
                            return ImageProvider.getAdditionalImage("piston_extension_sticky");
                        }
                        return ImageProvider.getAdditionalImage("piston_extension");
                }
            case 35: // wool
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("cloth_1");    // orange
                    case 2:
                        return ImageProvider.getImage("cloth_2");    // magenta
                    case 3:
                        return ImageProvider.getImage("cloth_3");    // light blue
                    case 4:
                        return ImageProvider.getImage("cloth_4");    // yellow
                    case 5:
                        return ImageProvider.getImage("cloth_5");    // light green
                    case 6:
                        return ImageProvider.getImage("cloth_6");    // pink
                    case 7:
                        return ImageProvider.getImage("cloth_7");    // gray
                    case 8:
                        return ImageProvider.getImage("cloth_8");    // light gray
                    case 9:
                        return ImageProvider.getImage("cloth_9");    // cyan
                    case 10:
                        return ImageProvider.getImage("cloth_10");    // purple
                    case 11:
                        return ImageProvider.getImage("cloth_11");    // blue
                    case 12:
                        return ImageProvider.getImage("cloth_12");    // brown
                    case 13:
                        return ImageProvider.getImage("cloth_13");    // dark green
                    case 14:
                        return ImageProvider.getImage("cloth_14");    // red
                    case 15:
                        return ImageProvider.getImage("cloth_15");    // black
                    case 0:
                    default:
                        return ImageProvider.getImage("cloth_0");    // white
                }
            case 37:
                return ImageProvider.getImage("flower"); // Dandelion
            case 38:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("flower_blue_orchid");
                    case 2:
                        return ImageProvider.getImage("flower_allium");
                    case 3:
                        return ImageProvider.getImage("flower_azure_bluet");
                    case 4:
                        return ImageProvider.getImage("flower_red_tulip");
                    case 5:
                        return ImageProvider.getImage("flower_orange_tulip");
                    case 6:
                        return ImageProvider.getImage("flower_white_tulip");
                    case 7:
                        return ImageProvider.getImage("flower_pink_tulip");
                    case 8:
                        return ImageProvider.getImage("flower_oxeye_daisy");
                    case 0:
                    default:
                        return ImageProvider.getImage("flower-poppy");
                }
            case 39:
                return ImageProvider.getImage("mushroom_brown");
            case 40:
                return ImageProvider.getImage("mushroom_red");
            case 41:
                return ImageProvider.getImage("blockGold");
            case 42:
                return ImageProvider.getImage("blockIron");
            case 43:// stone doubleslab
            case 44:// stone slab
                // all double slabs beside stone look just like the regular blocks and the slabs like half of the regular blocks
                // it makes no sense to do that here so for this one block, we use the blocks getImage functionality
                byte slabdata = (data < 0 || data > 6) ? 0 : data;
                return new StoneSlab(id, slabdata).getImage(1);
            case 45:
                return ImageProvider.getImage("brick");
            case 46:
                return ImageProvider.getImage("tnt_side");
            case 47:
                return ImageProvider.getImage("bookshelf");
            case 48:
                return ImageProvider.getImage("stoneMoss");
            case 49:
                return ImageProvider.getImage("obsidian");
            case 50:
                return ImageProvider.getImage("torch");
            case 51:
                return ImageProvider.getAdditionalImage("fire");
            case 52:
                return ImageProvider.getImage("mobSpawner");
            case 53: // oak wood stair
            case 67: // cobble stair
            case 108: // brick stair
            case 109: // stone brick stair
            case 114: // nether brick stair
            case 128: // sandstone stair
            case 134: // spruce wood stair
            case 135: // birch wood stair
            case 136: // jungle wood stair
            case 156: // quartz
            case 163: // Acacia Wood Stairs
            case 164: // Dark Oak Wood Stairs
            case 180: // Red Sandstone Stairs
                // for the stairs, one edge has to be cut off. since the edge is cut off according to its direction,
                // it makes no sense to do that here so for this block, we use the blocks getImage functionality (data 0 = direction E)
                return new Stair(id, (byte) 0).getImage(1);
            case 54:
                return ImageProvider.getAdditionalImage("chest");
            case 55:                                                    // redstone wire
                if (data < 1) { // off
                    return ImageProvider.getAdditionalImage("redstone_wire_off");
                }
                return ImageProvider.getAdditionalImage("redstone_wire_on");
            case 56:
                return ImageProvider.getImage("oreDiamond");
            case 57:
                return ImageProvider.getImage("blockDiamond");
            case 58:
                return ImageProvider.getImage("workbench_top");
            case 59: // Crops
                switch (data) {
                    case 0: // crops below 3 are so tiny that you
                    case 1: // wouldn't recognise them, so we
                    case 2: // start at 3
                    case 3:
                        return ImageProvider.getImage("crops_3");
                    case 4:
                        return ImageProvider.getImage("crops_4");
                    case 5:
                        return ImageProvider.getImage("crops_5");
                    case 6:
                        return ImageProvider.getImage("crops_6");
                    case 7:
                    default:
                        return ImageProvider.getImage("crops_7");
                }
            case 60: // farmland
                if (data >= 4) return ImageProvider.getImage("farmland_wet");
                return ImageProvider.getImage("farmland_dry");
            case 61:
                return ImageProvider.getImage("furnace_front");
            case 62:
                return ImageProvider.getImage("furnace_front_lit");
            case 63:
                return ImageProvider.getItemImage("sign");
            case 64: // wooden door
                if ((data & 8) == 0) return ImageProvider.getImage("doorWood_lower");
                return ImageProvider.getImage("doorWood_upper");
            case 65:
                return ImageProvider.getImage("ladder");
            case 66: // rails
                if (data <= 5) { // flat or ascending track
                    return ImageProvider.getImage("rail");
                }
                return ImageProvider.getImage("rail_turn"); // corner
            case 68:
                return ImageProvider.getAdditionalImage("sign_wall");
            case 69:
                return ImageProvider.getImage("lever");
            case 70:
                return ImageProvider.getAdditionalImage("pressureplate_stone");
            case 71: // iron door
                if ((data & 8) == 0) return ImageProvider.getImage("doorIron_lower");
                return ImageProvider.getImage("doorIron_upper");
            case 72:
                return ImageProvider.getAdditionalImage("pressureplate_wood");
            case 73:
                return ImageProvider.getImage("oreRedstone");
            case 74:
                return ImageProvider.getImage("oreRedstone"); // Glowing
            case 75:
                return ImageProvider.getImage("redtorch");
            case 76:
                return ImageProvider.getImage("redtorch_lit");
            case 77:
                return ImageProvider.getAdditionalImage("button");
            case 78:
                return ImageProvider.getImage("snow");
            case 79:
                return ImageProvider.getImage("ice");
            case 80:
                return ImageProvider.getImage("snow");
            case 81:
                return ImageProvider.getImage("cactus_side");
            case 82:
                return ImageProvider.getImage("clay");
            case 83:
                return ImageProvider.getImage("reeds");
            case 84:
                return ImageProvider.getImage("jukebox_top");
            case 85:
                return ImageProvider.getAdditionalImage("fence");
            case 86:
                return ImageProvider.getImage("pumpkin_top");
            case 87:
                return ImageProvider.getImage("hellrock"); // Netherrack
            case 88:
                return ImageProvider.getImage("hellsand"); // Soul Sand
            case 89:
                return ImageProvider.getImage("lightgem");            // Glowstone Block
            case 90:
                return ImageProvider.getAdditionalImage("portal");
            case 91:
                return ImageProvider.getImage("pumpkin_jack"); // Jack'o'lantern
            case 92:
                return ImageProvider.getImage("cake_side");
            case 93:
                return ImageProvider.getImage("repeater");
            case 94:
                return ImageProvider.getImage("repeater_lit");
            case 95:
                switch (data) {//stained glass
                    case 1:
                        return ImageProvider.getImage("glasspane_orange");
                    case 2:
                        return ImageProvider.getImage("glasspane_magenta");
                    case 3:
                        return ImageProvider.getImage("glasspane_light_blue");
                    case 4:
                        return ImageProvider.getImage("glasspane_yellow");
                    case 5:
                        return ImageProvider.getImage("glasspane_lime");
                    case 6:
                        return ImageProvider.getImage("glasspane_pink");
                    case 7:
                        return ImageProvider.getImage("glasspane_gray");
                    case 8:
                        return ImageProvider.getImage("glasspane_lightgray");
                    case 9:
                        return ImageProvider.getImage("glasspane_cyan");
                    case 10:
                        return ImageProvider.getImage("glasspane_purple");
                    case 11:
                        return ImageProvider.getImage("glasspane_blue");
                    case 12:
                        return ImageProvider.getImage("glasspane_brown");
                    case 13:
                        return ImageProvider.getImage("glasspane_green");
                    case 14:
                        return ImageProvider.getImage("glasspane_red");
                    case 15:
                        return ImageProvider.getImage("glasspane_black");
                }
            case 96:
                return ImageProvider.getImage("trapdoor");
            case 97:        //monster eggs look the same as normal stones.  Maybe a superimposed egg would be a nice touch.
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("cobblestone"); // Cobblestone Monster Egg
                    case 2:
                        return ImageProvider.getImage("stonebricksmooth"); // Stone Brick Monster Egg
                    case 3:
                        return ImageProvider.getImage("stonebricksmooth_mossy"); // Mossy Stone Brick Monster Egg
                    case 4:
                        return ImageProvider.getImage("stonebricksmooth_cracked"); // Cracked Stone Brick Monster Egg
                    case 5:
                        return ImageProvider.getImage("stonebricksmooth_carved"); // Chiseled Stone Brick Monster Egg
                    case 0:
                    default:
                        return ImageProvider.getImage("stone"); // Stone Monster Egg";
                }
            case 98:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("stonebricksmooth_mossy");
                    case 2:
                        return ImageProvider.getImage("stonebricksmooth_cracked");
                    case 3:
                        return ImageProvider.getImage("stonebricksmooth_carved"); // chiseled
                    case 0:
                    default:
                        return ImageProvider.getImage("stonebricksmooth");
                }
            case 99: // huge brown mushroom
            case 100:// huge red mushroom
                switch (data) {
                    case 0:
                        return ImageProvider.getImage("mushroom_inside");
                    case 10:
                        return ImageProvider.getImage("mushroom_skin_stem");
                    default: // hood piece
                        if (id == 99) return ImageProvider.getImage("mushroom_skin_brown");
                        return ImageProvider.getImage("mushroom_skin_red");
                }
            case 101:
                return ImageProvider.getImage("fenceIron"); // Iron Bars
            case 102:
                return ImageProvider.getAdditionalImage("glass_pane");
            case 103:
                return ImageProvider.getImage("melon_top");
            case 104: // Pumpkin Stem
            case 105:
                return ImageProvider.getAdditionalImage("melon_stem");
            case 106:
                return ImageProvider.getAdditionalImage("vine");
            case 107:
                return ImageProvider.getAdditionalImage("fence_gate");
            case 110:
                return ImageProvider.getImage("mycel_side");
            case 111:
                return ImageProvider.getAdditionalImage("waterlily");
            case 112:
                return ImageProvider.getImage("netherBrick");
            case 113:
                return ImageProvider.getAdditionalImage("fence_nether");
            case 115: // Nether wart
                switch (data) {
                    case 0:
                        return ImageProvider.getImage("netherStalk_0");
                    case 3:
                        return ImageProvider.getImage("netherStalk_2");
                    default:
                        return ImageProvider.getImage("netherStalk_1");
                }
            case 116:
                return ImageProvider.getImage("enchantment_top"); // Enchanting table
            case 379:
            case 117:
                return ImageProvider.getItemImage("brewingStand");
            case 118:
                return ImageProvider.getItemImage("cauldron");
            case 119:
                return ImageProvider.getImage("dragonEgg"); // End Portal
            case 120: // end portal frame
                if ((data & 4) != 0) return ImageProvider.getAdditionalImage("endframe_fixed");
                return ImageProvider.getImage("endframe_top");
            case 121:
                return ImageProvider.getImage("whiteStone"); // End Stone
            case 122:
                return ImageProvider.getAdditionalImage("egg_dragon");
            case 123:
                return ImageProvider.getImage("redstoneLight"); // lamp
            case 124:
                return ImageProvider.getImage("redstoneLight_lit"); // lamp
            case 125:// wooden doubleslab
            case 126:// wooden slab
                // all double slabs look just like the regular blocks and the slabs like half of the regular blocks
                // it makes no sense to do that here so for this one block, we use the blocks getImage functionality
                byte woodenslabdata = (data < 0 || data > 5) ? 0 : data;
                return new WoodenSlab(id, woodenslabdata).getImage(1);
            case 127:                                                    // cocoa pod
                switch (data >> 2) {
                    case 1:
                        return ImageProvider.getAdditionalImage("cocoa_pod_medium");
                    case 2:
                        return ImageProvider.getAdditionalImage("cocoa_pod_large");
                    case 0:
                    default:
                        return ImageProvider.getAdditionalImage("cocoa_pod_small");
                }
            case 129:
                return ImageProvider.getImage("oreEmerald");
            case 130:
                return ImageProvider.getAdditionalImage("chest_ender");
            case 131:
                return ImageProvider.getImage("tripWireSource"); // hook
            case 132:
                return ImageProvider.getAdditionalImage("tripwire");
            case 133:
                return ImageProvider.getImage("blockEmerald");
            case 137:
                return ImageProvider.getImage("commandBlock");
            case 138:
                return ImageProvider.getImage("beacon");
            case 139:
                if (data == 1) return ImageProvider.getAdditionalImage("walls_mossycobble");
                return ImageProvider.getAdditionalImage("walls_cobble");
            case 140:
                return ImageProvider.getItemImage("flowerPot");
            case 141: // carrot
                switch (data) {
                    case 0:
                    case 1:
                        return ImageProvider.getImage("carrots_0");
                    case 2:
                    case 3:
                        return ImageProvider.getImage("carrots_1");
                    case 4:
                    case 5:
                    case 6:
                        return ImageProvider.getImage("carrots_2");
                    case 7:
                    default:
                        return ImageProvider.getImage("carrots_3");
                }
            case 142: // potatoes
                switch (data) {
                    case 0:
                    case 1:
                        return ImageProvider.getImage("potatoes_0");
                    case 2:
                    case 3:
                        return ImageProvider.getImage("potatoes_1");
                    case 4:
                    case 5:
                    case 6:
                        return ImageProvider.getImage("potatoes_2");
                    case 7:
                    default:
                        return ImageProvider.getImage("potatoes_3");
                }
            case 143:
                return ImageProvider.getAdditionalImage("button_wood");
            case 144:
                return ImageProvider.getItemImage("skull_char"); // other heads determined through tile entity
            case 145:
                return ImageProvider.getAdditionalImage("anvil");
            case 146:
                return ImageProvider.getAdditionalImage("chest_trapped");
            case 147:
                return ImageProvider.getAdditionalImage("weightedpressureplate_light");
            case 148:
                return ImageProvider.getAdditionalImage("weightedpressureplate_heavy");
            case 149:
                return ImageProvider.getImage("comparator");
            case 150:
                return ImageProvider.getImage("comparator_lit");
            case 151:
                return ImageProvider.getImage("daylightDetector_top");
            case 152:
                return ImageProvider.getImage("blockRedstone");
            case 153:
                return ImageProvider.getImage("netherquartz");
            case 154:
                return ImageProvider.getItemImage("hopper");
            case 155:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("quartzblock_chiseled");
                    case 2:
                        return ImageProvider.getImage("quartzblock_lines_top");
                    case 3:
                        return ImageProvider.rotateImage(90, ImageProvider.getImage("quartzblock_lines"));
                    case 4:
                        return ImageProvider.getImage("quartzblock_lines");
                    case 0:
                    default:
                        return ImageProvider.getImage("quartzblock_side");
                }
            case 157: // Activator rails
                if ((data & 8) == 0) { // off
                    return ImageProvider.getImage("activatorRail");
                }
                return ImageProvider.getImage("activatorRail_powered");
            case 158:
                return ImageProvider.getImage("dropper_front");
            case 159: // stained clay
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("stainedclay-orange");    // orange
                    case 2:
                        return ImageProvider.getImage("stainedclay-magenta");    // magenta
                    case 3:
                        return ImageProvider.getImage("stainedclay-lightblue");    // light blue
                    case 4:
                        return ImageProvider.getImage("stainedclay-yellow");    // yellow
                    case 5:
                        return ImageProvider.getImage("stainedclay-lime");    // lime
                    case 6:
                        return ImageProvider.getImage("stainedclay-pink");    // pink
                    case 7:
                        return ImageProvider.getImage("stainedclay-gray");    // gray
                    case 8:
                        return ImageProvider.getImage("stainedclay-lightgray");    // light gray
                    case 9:
                        return ImageProvider.getImage("stainedclay-cyan");    // cyan
                    case 10:
                        return ImageProvider.getImage("stainedclay-purple");    // purple
                    case 11:
                        return ImageProvider.getImage("stainedclay-blue");    // blue
                    case 12:
                        return ImageProvider.getImage("stainedclay-brown");    // brown
                    case 13:
                        return ImageProvider.getImage("stainedclay-green");    //  green
                    case 14:
                        return ImageProvider.getImage("stainedclay-red");    // red
                    case 15:
                        return ImageProvider.getImage("block_coal");    // black
                    case 0:
                    default:
                        return ImageProvider.getImage("stainedclay-white");    // white
                }
            case 160:  //stained glass pane
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("glasspane_orange");    // orange
                    case 2:
                        return ImageProvider.getImage("glasspane_magenta");    // magenta
                    case 3:
                        return ImageProvider.getImage("glasspane_lightblue");    // light blue
                    case 4:
                        return ImageProvider.getImage("glasspane_yellow");    // yellow
                    case 5:
                        return ImageProvider.getImage("glasspane_lime");    // lime
                    case 6:
                        return ImageProvider.getImage("glasspane_pink");    // pink
                    case 7:
                        return ImageProvider.getImage("glasspane_gray");    // gray
                    case 8:
                        return ImageProvider.getImage("glasspane_lightgray");    // light gray
                    case 9:
                        return ImageProvider.getImage("glasspane_cyan");    // cyan
                    case 10:
                        return ImageProvider.getImage("glasspane_purple");    // purple
                    case 11:
                        return ImageProvider.getImage("glasspane_blue");    // blue
                    case 12:
                        return ImageProvider.getImage("glasspane_brown");    // brown
                    case 13:
                        return ImageProvider.getImage("glasspane_green");    //  green
                    case 14:
                        return ImageProvider.getImage("glasspane_red");    // red
                    case 15:
                        return ImageProvider.getImage("glasspane_black");    // black
                    case 0:
                    default:
                        return ImageProvider.getImage("glasspane_white");    // white
                }


            case 162: // Wood2 (Log)
                byte wood2TypeData = (byte) (data & 0x3);
                switch (wood2TypeData) {
                    case 1:
                        return ImageProvider.getImage("tree_dark_oak");
                    case 2:
                        return ImageProvider.getImage("tree_undefined");
                    case 3:
                        return ImageProvider.getImage("tree_undefined");
                    case 0:
                    default:
                        return ImageProvider.getImage("tree_acacia"); // Acacia
                }

            case 165:
                return ImageProvider.getImage("block_slime");   // minecraft:slime
            case 168:
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("prismarine_bricks");
                    case 2:
                        return ImageProvider.getImage("prismarine_dark");
                    case 0:
                    default:
                        return ImageProvider.getImage("prismarine");
                }
            case 169:
                return ImageProvider.getImage("sea_lantern");   // minecraft:sea_lantern
            case 170:
                return ImageProvider.getImage("hay_block");        // Hay Bale
            case 171: // wool
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("cloth_1");    // orange
                    case 2:
                        return ImageProvider.getImage("cloth_2");    // magenta
                    case 3:
                        return ImageProvider.getImage("cloth_3");    // light blue
                    case 4:
                        return ImageProvider.getImage("cloth_4");    // yellow
                    case 5:
                        return ImageProvider.getImage("cloth_5");    // light green
                    case 6:
                        return ImageProvider.getImage("cloth_6");    // pink
                    case 7:
                        return ImageProvider.getImage("cloth_7");    // gray
                    case 8:
                        return ImageProvider.getImage("cloth_8");    // light gray
                    case 9:
                        return ImageProvider.getImage("cloth_9");    // cyan
                    case 10:
                        return ImageProvider.getImage("cloth_10");    // purple
                    case 11:
                        return ImageProvider.getImage("cloth_11");    // blue
                    case 12:
                        return ImageProvider.getImage("cloth_12");    // brown
                    case 13:
                        return ImageProvider.getImage("cloth_13");    // dark green
                    case 14:
                        return ImageProvider.getImage("cloth_14");    // red
                    case 15:
                        return ImageProvider.getImage("cloth_15");    // black
                    case 0:
                    default:
                        return ImageProvider.getImage("cloth_0");    // white
                }
            case 172:
                return ImageProvider.getImage("hardened_clay");
            case 173:
                return ImageProvider.getImage("stainedclay-black"); //minecraft:coal_block
            case 174:
                return ImageProvider.getImage("packed_ice"); //minecraft:packed_ice
            case 175:
                typedata = (byte) (data & 0x7);
                switch (typedata) {
                    case 1:
                        return ImageProvider.getImage("largeflower-lilac");
                    case 2:
                        return ImageProvider.getImage("largeflower-doubletallgrass");
                    case 3:
                        return ImageProvider.getImage("largeflower-largefern");
                    case 4:
                        return ImageProvider.getImage("largeflower-rosebush");
                    case 5:
                        return ImageProvider.getImage("largeflower-peony");
                    case 0:
                    default:
                        return ImageProvider.getImage("largeflower-sunflower");
                }

            case 179:        // red sandstone
                switch (data) {
                    case 1:
                        return ImageProvider.getImage("redsandstone_chiseled");
                    case 2:
                        return ImageProvider.getImage("redsandstone_smooth");
                    case 0:
                    default:
                        return ImageProvider.getImage("redsandstone_side");
                }


            case 193: // Spruce door
                if ((data & 8) == 0) return ImageProvider.getImage("door_spruce_bottom");
                return ImageProvider.getImage("door_spruce_top");
            case 196:  // Acacia Door
                if ((data & 8) == 0) return ImageProvider.getImage("door_acacia_bottom");
                return ImageProvider.getImage("door_acacia_top");
            case 201: return ImageProvider.getImage("purpur_block");
            case 202: return ImageProvider.getImage("purpur_pillar_top");
            case 206: return ImageProvider.getImage("end_bricks");
            // items
            case 256:
                return ImageProvider.getItemImage("shovelIron");
            case 257:
                return ImageProvider.getItemImage("pickaxeIron");
            case 258:
                return ImageProvider.getItemImage("hatchetIron");
            case 259:
                return ImageProvider.getItemImage("flintAndSteel");
            case 260:
                return ImageProvider.getItemImage("apple");
            case 261:
                return ImageProvider.getItemImage("bow");
            case 262:
                return ImageProvider.getItemImage("arrow");
            case 263:
                return ImageProvider.getItemImage("coal");
            case 264:
                return ImageProvider.getItemImage("diamond");
            case 265:
                return ImageProvider.getItemImage("ingotIron");
            case 266:
                return ImageProvider.getItemImage("ingotGold");
            case 267:
                return ImageProvider.getItemImage("swordIron");
            case 268:
                return ImageProvider.getItemImage("swordWood");
            case 269:
                return ImageProvider.getItemImage("shovelWood");
            case 270:
                return ImageProvider.getItemImage("pickaxeWood");
            case 271:
                return ImageProvider.getItemImage("hatchetWood");
            case 272:
                return ImageProvider.getItemImage("swordStone");
            case 273:
                return ImageProvider.getItemImage("shovelStone");
            case 274:
                return ImageProvider.getItemImage("pickaxeStone");
            case 275:
                return ImageProvider.getItemImage("hatchetStone");
            case 276:
                return ImageProvider.getItemImage("swordDiamond");
            case 277:
                return ImageProvider.getItemImage("shovelDiamond");
            case 278:
                return ImageProvider.getItemImage("pickaxeDiamond");
            case 279:
                return ImageProvider.getItemImage("hatchetDiamond");
            case 280:
                return ImageProvider.getItemImage("stick");
            case 281:
                return ImageProvider.getItemImage("bowl");
            case 282:
                return ImageProvider.getItemImage("mushroomStew");
            case 283:
                return ImageProvider.getItemImage("swordGold");
            case 284:
                return ImageProvider.getItemImage("shovelGold");
            case 285:
                return ImageProvider.getItemImage("pickaxeGold");
            case 286:
                return ImageProvider.getItemImage("hatchetGold");
            case 287:
                return ImageProvider.getItemImage("string");
            case 288:
                return ImageProvider.getItemImage("feather");
            case 289:
                return ImageProvider.getItemImage("sulphur");        // Gunpowder
            case 290:
                return ImageProvider.getItemImage("hoeWood");
            case 291:
                return ImageProvider.getItemImage("hoeStone");
            case 292:
                return ImageProvider.getItemImage("hoeIron");
            case 293:
                return ImageProvider.getItemImage("hoeDiamond");
            case 294:
                return ImageProvider.getItemImage("hoeGold");
            case 295:
                return ImageProvider.getItemImage("seeds");
            case 296:
                return ImageProvider.getItemImage("wheat");
            case 297:
                return ImageProvider.getItemImage("bread");
            case 298:
                return ImageProvider.getItemImage("helmetCloth");
            case 299:
                return ImageProvider.getItemImage("chestplateCloth");
            case 300:
                return ImageProvider.getItemImage("leggingsCloth");
            case 301:
                return ImageProvider.getItemImage("bootsCloth");
            case 302:
                return ImageProvider.getItemImage("helmetChain");
            case 303:
                return ImageProvider.getItemImage("chestplateChain");
            case 304:
                return ImageProvider.getItemImage("leggingsChain");
            case 305:
                return ImageProvider.getItemImage("bootsChain");
            case 306:
                return ImageProvider.getItemImage("helmetIron");
            case 307:
                return ImageProvider.getItemImage("chestplateIron");
            case 308:
                return ImageProvider.getItemImage("leggingsIron");
            case 309:
                return ImageProvider.getItemImage("bootsIron");
            case 310:
                return ImageProvider.getItemImage("helmetDiamond");
            case 311:
                return ImageProvider.getItemImage("chestplateDiamond");
            case 312:
                return ImageProvider.getItemImage("leggingsDiamond");
            case 313:
                return ImageProvider.getItemImage("bootsDiamond");
            case 314:
                return ImageProvider.getItemImage("helmetGold");
            case 315:
                return ImageProvider.getItemImage("chestplateGold");
            case 316:
                return ImageProvider.getItemImage("leggingsGold");
            case 317:
                return ImageProvider.getItemImage("bootsGold");
            case 318:
                return ImageProvider.getItemImage("flint");
            case 319:
                return ImageProvider.getItemImage("porkchopRaw");
            case 320:
                return ImageProvider.getItemImage("porkchopCooked");
            case 321:
                return ImageProvider.getItemImage("painting");
            case 322:
                return ImageProvider.getItemImage("appleGold");
            case 323:
                return ImageProvider.getItemImage("sign");
            case 324:
                return ImageProvider.getItemImage("doorWood");
            case 325:
                return ImageProvider.getItemImage("bucket");
            case 326:
                return ImageProvider.getItemImage("bucketWater");
            case 327:
                return ImageProvider.getItemImage("bucketLava");
            case 328:
                return ImageProvider.getItemImage("minecart");
            case 329:
                return ImageProvider.getItemImage("saddle");
            case 330:
                return ImageProvider.getItemImage("doorIron");
            case 331:
                return ImageProvider.getItemImage("redstone");
            case 332:
                return ImageProvider.getItemImage("snowball");
            case 333:
                return ImageProvider.getItemImage("boat");
            case 334:
                return ImageProvider.getItemImage("leather");
            case 335:
                return ImageProvider.getItemImage("milk");
            case 336:
                return ImageProvider.getItemImage("brick");
            case 337:
                return ImageProvider.getItemImage("clay");
            case 338:
                return ImageProvider.getItemImage("reeds"); // sugar cane
            case 339:
                return ImageProvider.getItemImage("paper");
            case 340:
                return ImageProvider.getItemImage("book");
            case 341:
                return ImageProvider.getItemImage("slimeball");
            case 342:
                return ImageProvider.getItemImage("minecartChest"); // Storage Mine cart
            case 343:
                return ImageProvider.getItemImage("minecartFurnace"); // Powered Mine cart
            case 344:
                return ImageProvider.getItemImage("egg");
            case 345:
                return ImageProvider.getAdditionalImage("compass");
            case 346:
                return ImageProvider.getItemImage("fishingRod");
            case 347:
                return ImageProvider.getAdditionalImage("clock");
            case 348:
                return ImageProvider.getItemImage("yellowDust"); // Glowstone dust
            case 349:
                return ImageProvider.getItemImage("fishRaw");
            case 350:
                return ImageProvider.getItemImage("fishCooked");
            case 351: // dye
                switch (data) {
                    case 1:
                        return ImageProvider.getItemImage("dyePowder_red");            // Rose Red
                    case 2:
                        return ImageProvider.getItemImage("dyePowder_green");        // Cactus Green
                    case 3:
                        return ImageProvider.getItemImage("dyePowder_brown");        // Cocoa Beans
                    case 4:
                        return ImageProvider.getItemImage("dyePowder_blue");        // Lapis Lazuli
                    case 5:
                        return ImageProvider.getItemImage("dyePowder_purple");
                    case 6:
                        return ImageProvider.getItemImage("dyePowder_cyan");
                    case 7:
                        return ImageProvider.getItemImage("dyePowder_silver");        // Light Gray Dye
                    case 8:
                        return ImageProvider.getItemImage("dyePowder_gray");
                    case 9:
                        return ImageProvider.getItemImage("dyePowder_pink");
                    case 10:
                        return ImageProvider.getItemImage("dyePowder_lime");
                    case 11:
                        return ImageProvider.getItemImage("dyePowder_yellow");        // Dandelion Yellow
                    case 12:
                        return ImageProvider.getItemImage("dyePowder_lightBlue");
                    case 13:
                        return ImageProvider.getItemImage("dyePowder_magenta");
                    case 14:
                        return ImageProvider.getItemImage("dyePowder_orange");
                    case 15:
                        return ImageProvider.getItemImage("dyePowder_white");        // Bone Meal
                    case 0:
                    default:
                        return ImageProvider.getItemImage("dyePowder_black");    // Ink Sac
                }

            case 352:
                return ImageProvider.getItemImage("bone");
            case 353:
                return ImageProvider.getItemImage("sugar");
            case 354:
                return ImageProvider.getItemImage("cake");
            case 355:
                return ImageProvider.getItemImage("bed");
            case 356:
                return ImageProvider.getItemImage("diode");     // Redstone repeater
            case 357:
                return ImageProvider.getItemImage("cookie");
            case 358:
                return ImageProvider.getItemImage("map");
            case 359:
                return ImageProvider.getItemImage("shears");
            case 360:
                return ImageProvider.getItemImage("melon"); // Melon slice
            case 361:
                return ImageProvider.getItemImage("seeds_pumpkin");
            case 362:
                return ImageProvider.getItemImage("seeds_melon");
            case 363:
                return ImageProvider.getItemImage("beefRaw");
            case 364:
                return ImageProvider.getItemImage("beefCooked"); // Steak
            case 365:
                return ImageProvider.getItemImage("chickenRaw");
            case 366:
                return ImageProvider.getItemImage("chickenCooked");
            case 367:
                return ImageProvider.getItemImage("rottenFlesh");
            case 368:
                return ImageProvider.getItemImage("enderPearl");
            case 369:
                return ImageProvider.getItemImage("blazeRod");
            case 370:
                return ImageProvider.getItemImage("ghastTear");
            case 371:
                return ImageProvider.getItemImage("goldNugget");
            case 372:
                return ImageProvider.getItemImage("netherStalkSeeds"); // Nether Wart
            case 373:
            case 374:
                return ImageProvider.getItemImage("glassBottle"); // Potion and glass bottle (color of potions is added in ItemPotion)
            case 375:
                return ImageProvider.getItemImage("spiderEye");
            case 376:
                return ImageProvider.getItemImage("fermentedSpiderEye");
            case 377:
                return ImageProvider.getItemImage("blazePowder");
            case 378:
                return ImageProvider.getItemImage("magmaCream");
            case 380:
                return ImageProvider.getItemImage("cauldron");
            case 381:
                return ImageProvider.getItemImage("eyeOfEnder");
            case 382:
                return ImageProvider.getItemImage("speckledMelon"); // Glistening Melon
            case 383:
                return ImageProvider.getAdditionalImage("egg_spawn");
            case 384:
                return ImageProvider.getItemImage("expBottle");     // Bottle o' Enchanting
            case 385:
                return ImageProvider.getItemImage("fireball"); // Fire Charge
            case 386:
                return ImageProvider.getItemImage("writingBook"); // Book and Quill
            case 387:
                return ImageProvider.getItemImage("writtenBook");
            case 388:
                return ImageProvider.getItemImage("emerald");
            case 389:
                return ImageProvider.getItemImage("frame");
            case 390:
                return ImageProvider.getItemImage("flowerPot");
            case 391:
                return ImageProvider.getItemImage("carrots");
            case 392:
                return ImageProvider.getItemImage("potato");
            case 393:
                return ImageProvider.getItemImage("potatoBaked");
            case 394:
                return ImageProvider.getItemImage("potatoPoisonous");
            case 395:
                return ImageProvider.getItemImage("emptyMap");
            case 396:
                return ImageProvider.getItemImage("carrotGolden");
            case 397: // heads
                switch (data) {
                    case 0:
                        return ImageProvider.getItemImage("skull_skeleton");
                    case 1:
                        return ImageProvider.getItemImage("skull_wither");
                    case 2:
                        return ImageProvider.getItemImage("skull_zombie");
                    case 4:
                        return ImageProvider.getItemImage("skull_creeper");
                    case 3:
                    default:
                        return ImageProvider.getItemImage("skull_char");
                }
            case 398:
                return ImageProvider.getItemImage("carrotOnAStick");
            case 399:
                return ImageProvider.getItemImage("netherStar");
            case 400:
                return ImageProvider.getItemImage("pumpkinPie");
            case 401:
                return ImageProvider.getItemImage("fireworks");
            case 402:
                return ImageProvider.getItemImage("fireworksCharge");
            case 403:
                return ImageProvider.getItemImage("enchantedBook");
            case 404:
                return ImageProvider.getItemImage("comparator");
            case 405:
                return ImageProvider.getItemImage("netherbrick");
            case 406:
                return ImageProvider.getItemImage("netherquartz");
            case 407:
                return ImageProvider.getItemImage("minecartTnt");
            case 408:
                return ImageProvider.getItemImage("minecartHopper");
            case 2256:
                return ImageProvider.getItemImage("record_13");
            case 2257:
                return ImageProvider.getItemImage("record_cat");
            case 2258:
                return ImageProvider.getItemImage("record_blocks");
            case 2259:
                return ImageProvider.getItemImage("record_chirp");
            case 2260:
                return ImageProvider.getItemImage("record_far");
            case 2261:
                return ImageProvider.getItemImage("record_mall");
            case 2262:
                return ImageProvider.getItemImage("record_mellohi");
            case 2263:
                return ImageProvider.getItemImage("record_stal");
            case 2264:
                return ImageProvider.getItemImage("record_strad");
            case 2265:
                return ImageProvider.getItemImage("record_ward");
            case 2266:
                return ImageProvider.getItemImage("record_11");
            case 2267:
                return ImageProvider.getItemImage("record_wait");
            default:
                System.err.println("Unknown block encountered - " + id + ":" + data + " ");
                return ImageProvider.getAdditionalImage("unknown");
        }
    }

    private static TreeMap<String, BufferedImage> getImagesFromJarDir(ClassLoader classLoader, String dirPath) throws IOException, URISyntaxException {
        TreeMap<String, BufferedImage> images = new TreeMap<String, BufferedImage>();
        String paramPath = dirPath;
        if (!paramPath.endsWith("/")) paramPath += "/";

        String[] fileNames = getResourceListing(classLoader, paramPath);

        for (String fileName : fileNames) {
            if (!fileName.endsWith(".png")) continue;

            // save the name and the image to the map
            BufferedImage img = ImageIO.read(ClassLoader.getSystemResource(paramPath + fileName));
            fileName = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase();
            images.put(fileName, img);
        }
        return images;
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation. Works for regular files and also
     * JARs.
     *
     * @param classLoader
     * @param path        Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     * @author Greg Briggs (slightly modified by klaue)
     */
    private static String[] getResourceListing(ClassLoader classLoader, String path)
            throws URISyntaxException, IOException {
        URL dirURL = classLoader.getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory. Have
			 * to assume the same jar as this.
			 */
            String me = ImageProvider.class.getName().replace(".", "/") + ".class";
            dirURL = classLoader.getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
            Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { // filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }
}
