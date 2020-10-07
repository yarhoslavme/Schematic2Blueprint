package klaue.mcschematictool;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import klaue.mcschematictool.exceptions.ClassicNotSupportedException;
import klaue.mcschematictool.exceptions.ParseException;

/**
 * A simple test class for the SchematicsReader
 * @author klaue
 */
public class Test {
	/**
	 * Tests console output
	 * @param f
	 */
	public static void testConsole(File f) {
		try {
			SliceStack s = SchematicReader.readSchematicsFile(f);
			System.out.println(s);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassicNotSupportedException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test displaying the read schematics graphically (one slice of it)
	 * @param f
	 * @param level the part of the slicestack to display, for example, level 0 is the bottom slice
	 * @throws URISyntaxException 
	 */
	public static void testGraphical(File f, int level) throws URISyntaxException {
		try {
			ImageProvider.initialize();
			
			JFrame frm = new JFrame();
			frm.setTitle("Test graphical");
			frm.setSize(250, 250);

			SliceStack stack = SchematicReader.readSchematicsFile(f);
			Slice slice = stack.getSlice(level);
			
			JScrollPane scrPn = new JScrollPane(slice.getImages(2, true));
			
			frm.add(scrPn);
			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frm.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassicNotSupportedException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a schematic file, outputs it and then writes it to a different file
	 * @param source
	 * @param target
	 */
	public static void testReadWrite(File source, File target) {
		try {
			SliceStack stack = SchematicReader.readSchematicsFile(source);
			System.out.println(stack);
			SchematicWriter.writeSchematicsFile(stack, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		//testReadWrite(new File("/home/klaue/Desktop/bukkit/plugins/WorldEdit/schematics/132test.schematic"),
		//		new File("/home/klaue/Desktop/bukkit/plugins/WorldEdit/schematics/132test2.schematic"));
		// testGraphical(new File("/home/klaue/Desktop/bukkit/plugins/WorldEdit/schematics/132test.schematic"), 0);
		//testConsole(new File("/home/klaue/.minecraft/schematics/hugeshroom.schematic"));
	}
}
