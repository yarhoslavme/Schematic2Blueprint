package klaue.schematic2blueprint;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import klaue.mcschematictool.ImageGridStack;
import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.SchematicReader;
import klaue.mcschematictool.SliceStack;
import klaue.mcschematictool.exceptions.ClassicNotSupportedException;
import klaue.mcschematictool.exceptions.ParseException;

/**
 * The main frame and initializer of the app
 * @author klaue
 */
public class MainFrame extends JFrame implements ActionListener, ChangeListener {
	JFileChooser fc = new JFileChooser();
	SliceStack stack = null;
	ImageGridStack images = null;
	GridBagConstraints defaultContraints = new GridBagConstraints();
	double currentZoom = 0;
	Color gridLineColor = Color.BLACK;
	Color markColor = Color.RED;
	int currentLayer = 0;
	
	MyAlmightyListener myAlmightyListener;
	
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu, exportMenu, colorMenu, toolMenu, printMenu;
	JMenuItem miOpen, miExportImages, miExportGif, miExportLayer, miExportTxt, miBackgroundColor, miLineColor, miMarkColor, miBlockCounter, miPrintSlice, miPrintAll;

	JButton btnRotateCCW;
	JButton btnRotateCW;
	
	JSlider sldZoom = new JSlider();
	JSlider sldLayer = new JSlider(SwingConstants.VERTICAL);

	JPanel pnlAll = new JPanel();
	JPanel pnlControl = new JPanel();
	JPanel pnlRotate = new JPanel();
	JPanel pnlSchematic = new JPanel();
	JPanel pnlGrid = new JPanel();
	JScrollPane scrGrid;
	
	JLabel lblSize = new JLabel();
	
	MainFrame() {
		try {
			ImageProvider.initialize();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not read image files, aborting..", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		setTitle("Schematic2Blueprint");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String[] filetypes = {"shematic", "schematic", "shematics", "schematics"};
		this.fc.setFileFilter(new FiletypeFilter(filetypes, "Schematic-Files"));
		
		// make the menu
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(KeyEvent.VK_F);
		this.fileMenu.getAccessibleContext().setAccessibleDescription("The file menu");
		
		this.miOpen = new JMenuItem("Open schematic file", UIManager.getIcon("FileView.directoryIcon"));
		this.miOpen.setMnemonic(KeyEvent.VK_O);
		this.miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		this.miOpen.getAccessibleContext().setAccessibleDescription("This opens a schematic file");
		this.miOpen.setActionCommand("OPEN");
		this.miOpen.addActionListener(this);
		this.fileMenu.add(this.miOpen);

		this.exportMenu = new JMenu("Export");
		this.exportMenu.setMnemonic(KeyEvent.VK_E);
		
		this.miExportLayer = new JMenuItem("Current layer", UIManager.getIcon("FileView.floppyDriveIcon"));
		this.miExportLayer.setMnemonic(KeyEvent.VK_C);
		//this.miExportLayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
		this.miExportLayer.getAccessibleContext().setAccessibleDescription("This saves the current layer as a single file");
		this.miExportLayer.setActionCommand("EXPSINGLE");
		this.miExportLayer.addActionListener(this);
		this.exportMenu.add(this.miExportLayer);
		
		this.miExportImages = new JMenuItem("One image per layer", UIManager.getIcon("FileChooser.upFolderIcon"));
		this.miExportImages.setMnemonic(KeyEvent.VK_O);
		//this.miExportImages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		this.miExportImages.getAccessibleContext().setAccessibleDescription("This saves the current schematic as multiple images");
		this.miExportImages.setActionCommand("EXPMULTI");
		this.miExportImages.addActionListener(this);
		this.exportMenu.add(this.miExportImages);
		
		this.miExportGif = new JMenuItem("To an animated Gif", UIManager.getIcon("FileView.floppyDriveIcon"));
		this.miExportGif.setMnemonic(KeyEvent.VK_G);
		//this.miExportGif.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
		this.miExportGif.getAccessibleContext().setAccessibleDescription("This saves the current schematic as a single animated gif file");
		this.miExportGif.setActionCommand("EXPGIF");
		this.miExportGif.addActionListener(this);
		this.exportMenu.add(this.miExportGif);
		
		this.miExportTxt = new JMenuItem("To a Textfile (Builders-Mod compatible)", UIManager.getIcon("FileView.floppyDriveIcon"));
		this.miExportTxt.setMnemonic(KeyEvent.VK_T);
		//this.miExportGif.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
		this.miExportTxt.getAccessibleContext().setAccessibleDescription("This saves the current schematic as a Builders-compatible text file");
		this.miExportTxt.setActionCommand("EXPTXT");
		this.miExportTxt.addActionListener(this);
		this.exportMenu.add(this.miExportTxt);
		
		this.fileMenu.add(this.exportMenu);
		this.menuBar.add(this.fileMenu);
		
		this.colorMenu = new JMenu("Color");
		this.colorMenu.setMnemonic(KeyEvent.VK_C);
		this.colorMenu.getAccessibleContext().setAccessibleDescription("The color menu");

		this.miBackgroundColor = new JMenuItem("Background color");
		this.miBackgroundColor.setMnemonic(KeyEvent.VK_B);
		this.miBackgroundColor.getAccessibleContext().setAccessibleDescription("This changes the background color");
		this.miBackgroundColor.setActionCommand("COLOR_BACKGROUND");
		this.miBackgroundColor.addActionListener(this);
		this.colorMenu.add(this.miBackgroundColor);

		this.miLineColor = new JMenuItem("Line color");
		this.miLineColor.setMnemonic(KeyEvent.VK_L);
		this.miLineColor.getAccessibleContext().setAccessibleDescription("This changes the line color");
		this.miLineColor.setActionCommand("COLOR_LINE");
		this.miLineColor.addActionListener(this);
		this.colorMenu.add(this.miLineColor);

		this.miMarkColor = new JMenuItem("Marker color");
		this.miMarkColor.setMnemonic(KeyEvent.VK_M);
		this.miMarkColor.getAccessibleContext().setAccessibleDescription("This changes the marker color");
		this.miMarkColor.setActionCommand("COLOR_MARK");
		this.miMarkColor.addActionListener(this);
		this.colorMenu.add(this.miMarkColor);

		this.menuBar.add(this.colorMenu);
		
		this.toolMenu = new JMenu("Tools");
		this.toolMenu.setMnemonic(KeyEvent.VK_T);
		this.toolMenu.getAccessibleContext().setAccessibleDescription("The tool menu");

		this.miBlockCounter = new JMenuItem("Count blocks");
		this.miBlockCounter.setMnemonic(KeyEvent.VK_C);
		this.miBlockCounter.getAccessibleContext().setAccessibleDescription("This counts the blocks");
		this.miBlockCounter.setActionCommand("BLOCKCOUNTER");
		this.miBlockCounter.addActionListener(this);
		this.toolMenu.add(this.miBlockCounter);
		
		this.menuBar.add(this.toolMenu);
		
		this.printMenu = new JMenu("Print");
		this.printMenu.setMnemonic(KeyEvent.VK_R);
		this.printMenu.getAccessibleContext().setAccessibleDescription("The print menu");

		this.miPrintSlice = new JMenuItem("Print current slice");
		this.miPrintSlice.setMnemonic(KeyEvent.VK_P);
		this.miPrintSlice.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		this.miPrintSlice.getAccessibleContext().setAccessibleDescription("This prints the current slice");
		this.miPrintSlice.setActionCommand("PRINTSLICE");
		this.miPrintSlice.addActionListener(this);
		this.printMenu.add(this.miPrintSlice);
		this.miPrintAll = new JMenuItem("Print all (one page per slice)");
		this.miPrintAll.setMnemonic(KeyEvent.VK_A);
		this.miPrintAll.getAccessibleContext().setAccessibleDescription("This prints all slices");
		this.miPrintAll.setActionCommand("PRINTALL");
		this.miPrintAll.addActionListener(this);
		this.printMenu.add(this.miPrintAll);
		
		this.menuBar.add(this.printMenu);
		
		this.setJMenuBar(this.menuBar);

		// layout mgrs
		this.pnlAll.setLayout(new BoxLayout(this.pnlAll, BoxLayout.Y_AXIS));
		this.pnlRotate.setLayout(new BoxLayout(this.pnlRotate, BoxLayout.X_AXIS));
		this.pnlControl.setLayout(new BoxLayout(this.pnlControl, BoxLayout.X_AXIS));
		this.pnlSchematic.setLayout(new BoxLayout(this.pnlSchematic, BoxLayout.X_AXIS));
		
		this.pnlGrid.setLayout(new GridBagLayout());
		//this.pnlGrid.setBackground(Color.BLUE);
		this.pnlGrid.setBackground(Color.LIGHT_GRAY);
		
		// init buttons
		this.btnRotateCW = new JButton(new ImageIcon(ClassLoader.getSystemResource("klaue/schematic2blueprint/CW.gif")));
		this.btnRotateCW.setActionCommand("RCW");
		this.btnRotateCW.addActionListener(this);
		this.btnRotateCCW = new JButton(new ImageIcon(ClassLoader.getSystemResource("klaue/schematic2blueprint/CCW.gif")));
		this.btnRotateCCW.setActionCommand("RCCW");
		this.btnRotateCCW.addActionListener(this);
		
		// init sliders
		this.sldZoom.setBorder(BorderFactory.createTitledBorder("Zoom"));
		this.sldZoom.setMaximum(100);
		this.sldZoom.setMinimum(5);
		this.sldZoom.setValue(10);
		this.sldZoom.setMajorTickSpacing(10);
		this.sldZoom.setMinorTickSpacing(5);
		this.sldZoom.setPaintTicks(true);
		this.sldZoom.setPaintLabels(true);
		this.sldZoom.setSnapToTicks(true);
		this.sldZoom.addChangeListener(this);
		
		Hashtable<Integer, JComponent> zoomLabelTable = new Hashtable<Integer, JComponent>();
		for (int i = 0; i <= 100; i += 10) {
			zoomLabelTable.put( new Integer(i), new JLabel(Integer.toString(i / 10)) );
		}
		this.sldZoom.setLabelTable(zoomLabelTable);


		this.sldLayer.setBorder(BorderFactory.createTitledBorder("Layer"));
		this.sldLayer.setMajorTickSpacing(1);
		this.sldLayer.setValue(this.currentLayer + 1);
		this.sldLayer.setMinimum(1);
		this.sldLayer.setMaximum(10);
		this.sldLayer.setPaintTicks(true);
		this.sldLayer.setPaintLabels(true);
		this.sldLayer.setSnapToTicks(true);
		this.sldLayer.addChangeListener(this);
		
		// default states
		enableSchematicControls(false);
		
		// set layout
		this.pnlRotate.add(this.btnRotateCCW);
		this.pnlRotate.add(Box.createHorizontalStrut(5));
		this.pnlRotate.add(this.btnRotateCW);
		this.pnlRotate.setBorder(BorderFactory.createTitledBorder("Rotate"));
		this.pnlRotate.setMaximumSize(new Dimension(this.pnlRotate.getPreferredSize().width, this.sldZoom.getPreferredSize().height));
		
		this.pnlControl.add(this.pnlRotate);
		this.pnlControl.add(Box.createHorizontalStrut(10));
		this.pnlControl.add(this.sldZoom);
		this.pnlControl.setMaximumSize(new Dimension(this.pnlControl.getMaximumSize().width, this.pnlControl.getPreferredSize().height));
		
		this.scrGrid = new JScrollPane(this.pnlGrid);
		
		long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;
		this.myAlmightyListener = new MyAlmightyListener(this.scrGrid, this.pnlGrid);
		Toolkit.getDefaultToolkit().addAWTEventListener(this.myAlmightyListener, eventMask);
		
		this.pnlSchematic.add(this.scrGrid);
		this.pnlSchematic.add(Box.createHorizontalStrut(5));
		this.pnlSchematic.add(this.sldLayer);
		
		this.lblSize.setAlignmentX(SwingConstants.LEFT);

		this.pnlAll.add(this.pnlControl);
		this.pnlAll.add(Box.createVerticalStrut(5));
		this.pnlAll.add(this.pnlSchematic);
		this.pnlAll.add(this.lblSize);
		this.pnlAll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(this.pnlAll);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void enableSchematicControls(boolean enable) {
		this.btnRotateCCW.setEnabled(enable);
		this.btnRotateCW.setEnabled(enable);
		this.sldLayer.setEnabled(enable);
		this.sldZoom.setEnabled(enable);
		this.exportMenu.setEnabled(enable);
		this.miBlockCounter.setEnabled(enable);
		this.printMenu.setEnabled(enable);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals("OPEN")) {
	    	this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    	
			int returnVal = this.fc.showOpenDialog(this);

		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		    	File file = this.fc.getSelectedFile();
		    	try {
					this.stack = SchematicReader.readSchematicsFile(file);
					this.stack.trim();
					double zoom = ((double)this.sldZoom.getValue() / 10);
					this.currentZoom = zoom;
					this.images = this.stack.getImages(zoom, true);
					this.images.setGridColor(this.gridLineColor);
					this.images.setMarkColor(this.markColor);
					if (SchematicReader.hasErrorHappened()) {
						JOptionPane.showMessageDialog(null, "There were some faulty blocks in the schematic. They were replaced with air.", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				} catch (IOException e) {
					e.printStackTrace();
			    	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Could not read file", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (ClassicNotSupportedException e) {
					e.printStackTrace();
			    	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Classic file format is not supported", "Classic not supported", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (ParseException e) {
					e.printStackTrace();
			    	this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					// Shenanigans to get a multiline option pane
			    	String message = "Could not parse schematics file:\n" + e.getMessage();
			    	if (message.length() > 503) message = message.substring(0, 500) + "...";
			    	JOptionPane cleanupPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE) {
				    		@Override
							public int getMaxCharactersPerLineCount() {
				    			return 100; // this is unimplemented in normal joptionpane for whatever reason
				    		}
				    	};
			    	cleanupPane.createDialog(null, "Invalid file").setVisible(true);
					return;
				} catch (OutOfMemoryError e) {
					System.gc();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null, "Ran out of memory while trying to open schematic", "Out of Memory", JOptionPane.ERROR_MESSAGE);
					return;
				}
				enableSchematicControls(true);
				if (this.currentLayer >= this.images.getStackSize()) this.currentLayer = this.images.getStackSize() - 1;
				this.sldLayer.setMaximum(this.images.getStackSize());
				if (this.images.getStackSize() == 1) this.sldLayer.setEnabled(false);
				
				this.pnlGrid.removeAll();
				this.pnlGrid.add(this.images.getGridAtLevel(this.currentLayer), this.defaultContraints);
				this.pnlGrid.repaint();
				this.scrGrid.validate();
				this.lblSize.setText("Size: " + this.stack.getLength() + " x " + this.stack.getWidth());
		    	System.gc();
		    }
		    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (arg0.getActionCommand().equals("RCCW") || arg0.getActionCommand().equals("RCW")) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (arg0.getActionCommand().equals("RCCW")) {
				this.stack.turnCCW();
			} else {
				this.stack.turnCW();
			}
			double zoom = ((double)this.sldZoom.getValue() / 10);
			this.images = this.stack.getImages(zoom, true);
			
			this.pnlGrid.removeAll();
			this.pnlGrid.add(this.images.getGridAtLevel(this.currentLayer), this.defaultContraints);
			this.pnlGrid.repaint();
			this.scrGrid.validate();
			this.lblSize.setText("Size: " + this.stack.getLength() + " x " + this.stack.getWidth());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (arg0.getActionCommand().equals("EXPSINGLE")) {
			JFileChooser fc = new JFileChooser();
			
			fc.setCurrentDirectory(this.fc.getCurrentDirectory());
			fc.setFileFilter(new FiletypeFilter("png", "PNG"));
			//fc.setAcceptAllFileFilterUsed(false);
			//fc.setDialogTitle("Save to directory");
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				if (!f.getName().toLowerCase().endsWith(".png")) {
					f = new File(f.getAbsolutePath() + ".png");
				}
				if (!f.isDirectory()) {
					if (f.exists()) {
						int reply = JOptionPane.showConfirmDialog(this, "File already exists. Overwrite?", "File already exists", JOptionPane.YES_NO_OPTION);
						if (reply != JOptionPane.YES_OPTION) return;
					}
					try {
						BufferedImage img = this.images.getGridAtLevel(this.currentLayer).exportImage(null, this.gridLineColor);
						try {
							ImageIO.write(img, "png", f);
						} catch (IOException e) {
							JOptionPane.showMessageDialog(this, "Error while saving image " + f.getName().toString() + ":\n"
									+ e.getLocalizedMessage(), "Could not save", JOptionPane.ERROR_MESSAGE);
						}
					} catch(OutOfMemoryError e) {
						System.gc();
						JOptionPane.showMessageDialog(this, "Ran out of memory while trying to generate an image out of the layer.\n" +
								"Try a lower zoom value or start this program with more memory.", "Out of heap memory", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if (arg0.getActionCommand().equals("EXPMULTI")) {
			new ExportDialog(this, this.stack, this.images, this.fc.getSelectedFile(), this.pnlGrid.getBackground(), false);
		} else if (arg0.getActionCommand().equals("EXPGIF")) {
			new ExportDialog(this, this.stack, this.images, this.fc.getSelectedFile(), this.pnlGrid.getBackground(), true);
		} else if (arg0.getActionCommand().equals("EXPTXT")) {
			JFileChooser fc = new JFileChooser();
			
			fc.setCurrentDirectory(this.fc.getCurrentDirectory());
			fc.setFileFilter(new FiletypeFilter("txt", "TXT"));
			//fc.setAcceptAllFileFilterUsed(false);
			//fc.setDialogTitle("Save to directory");
			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				if (!f.getName().toLowerCase().endsWith(".txt")) {
					f = new File(f.getAbsolutePath() + ".txt");
				}
				if (!f.isDirectory()) {
					if (f.exists()) {
						int reply = JOptionPane.showConfirmDialog(this, "File already exists. Overwrite?", "File already exists", JOptionPane.YES_NO_OPTION);
						if (reply != JOptionPane.YES_OPTION) return;
					}
					try {
						String title = f.getName();
						title = title.substring(0, title.lastIndexOf('.'));
						this.stack.exportToTextFile(title, f);
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "Error while saving text file " + f.getName().toString() + ":\n"
								+ e.getLocalizedMessage(), "Could not save", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if (arg0.getActionCommand().equals("COLOR_BACKGROUND")) {
			Color newColor = JColorChooser.showDialog(this, "Choose Background Color", this.pnlGrid.getBackground());
			if (newColor != null) {
				this.pnlGrid.setBackground(newColor);
			}
		} else if (arg0.getActionCommand().equals("COLOR_LINE")) {
			Color newColor = JColorChooser.showDialog(this, "Choose Line Color", this.gridLineColor);
			if (newColor != null) {
				this.gridLineColor = newColor;
				if (this.images != null) {
					this.images.setGridColor(newColor);
					this.pnlGrid.repaint();
				}
			}
		} else if (arg0.getActionCommand().equals("COLOR_MARK")) {
			Color newColor = JColorChooser.showDialog(this, "Choose Marker Color", this.markColor);
			if (newColor != null) {
				this.markColor = newColor;
				if (this.images != null) {
					this.images.setMarkColor(newColor);
					this.pnlGrid.repaint();
				}
			}
		} else if (arg0.getActionCommand().equals("BLOCKCOUNTER")) {
			new BlockCounterDialog(this, this.stack);
		} else if (arg0.getActionCommand().equals("PRINTSLICE")) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(this.images.getGridAtLevel(this.currentLayer));
			if (printJob.printDialog()) {
				try {
					printJob.print();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				} catch(PrinterException pe) {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(this, "Error printing:\n"
							+ pe.getLocalizedMessage(), "Could not print", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (arg0.getActionCommand().equals("PRINTALL")) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(this.images);
			if (printJob.printDialog()) {
				try {
					printJob.print();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				} catch(PrinterException pe) {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(this, "Error printing:\n"
							+ pe.getLocalizedMessage(), "Could not print", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (source.getValueIsAdjusting()) return;
		if (source == this.sldZoom) {
			double zoom = ((double)this.sldZoom.getValue() / 10);
			if (zoom == this.currentZoom) {
				return;
			}
			this.currentZoom = zoom;
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			this.images.setZoom(zoom);

			this.pnlGrid.repaint();
			this.scrGrid.validate();
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (source == this.sldLayer){
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Point markedPoint = this.images.getGridAtLevel(this.currentLayer).getMarkedBlock();
			this.currentLayer = this.sldLayer.getValue() - 1;
			this.images.getGridAtLevel(this.currentLayer).setMarkedBlock(markedPoint);
			this.pnlGrid.removeAll();
			this.pnlGrid.add(this.images.getGridAtLevel(this.currentLayer), this.defaultContraints);
			this.pnlGrid.repaint();
			this.scrGrid.validate();
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}

/**
 * Modified from http://www.java-forums.org/awt-swing/24693-moving-whole-content-jscrollpane-mousedrag.html#post100182
 * To scroll my scrollpane by dragging
 * @author Taiko
 */
class MyAlmightyListener implements AWTEventListener {
	private Point startPt;
	private final Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
	private final JScrollPane scrPane;
	private final JComponent comp;
	private Point move  = new Point();
	private Point ptZero, it;
	private final Rectangle rect = new Rectangle();
	private Rectangle vr;
	private int w, h;
	private MouseEvent event;
	private boolean dragInitialized = false;

	
	public MyAlmightyListener(JScrollPane scrPane, JComponent comp) {
		this.comp = comp;
		this.scrPane = scrPane;
	}
	
	public void eventDispatched(AWTEvent e) {
		
		this.event = (MouseEvent) e;
		
		// catching press of button no. 3 (right button)
		if (this.event.getID() == MouseEvent.MOUSE_PRESSED) {
			
			// do nothing if the source is not inside the scrollpane (scrollbars are not part of the viewport)
			Component source = (Component)e.getSource();
			if (!SwingUtilities.isDescendingFrom(source, this.scrPane.getViewport())) {
				return;
			}
			
			// getting mouse location, when mouse button is pressed
			this.startPt = this.event.getLocationOnScreen();
			
			// changing mouse cursor on my JDesktopPane
			this.comp.setCursor(this.handCursor);
			this.dragInitialized = true;
		}
		
		// catching release of button no. 3 (right button)
		// and changing mouse cursor back on all components
		if (this.event.getID() == MouseEvent.MOUSE_RELEASED) {
			if (this.dragInitialized) {
				this.comp.setCursor(this.defaultCursor);
				this.dragInitialized = false;
			}
		}
		
		// catching mouse move when the right mouse button is pressed
		if (this.event.getID() == MouseEvent.MOUSE_DRAGGED) {
			if (this.dragInitialized) {
				this.it = this.event.getLocationOnScreen();
				
				// calculation of move
				this.move.setLocation(this.it.x - this.startPt.x, this.it.y
						- this.startPt.y);
				this.startPt.setLocation(this.it);
				this.vr = this.scrPane.getViewport().getViewRect();
				this.w = this.vr.width;
				this.h = this.vr.height;
				
				// getting zero point in my JDesktopPane coordinates
				this.ptZero = SwingUtilities.convertPoint(this.scrPane.getViewport(), 0, 0,
						this.comp);
				
				// setting new rectangle to view
				this.rect.setRect(this.ptZero.x - this.move.x, this.ptZero.y
						- this.move.y, this.w, this.h);
				
				// viewing of new rectangle
				this.comp.scrollRectToVisible(this.rect);
			}
		}
	}
}
