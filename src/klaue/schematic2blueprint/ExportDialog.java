package klaue.schematic2blueprint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.plaf.ColorUIResource;

import klaue.mcschematictool.ImageGridStack;
import klaue.mcschematictool.SliceStack;

import com.fmsware.AnimatedGifEncoder;

/**
 * @author klaue
 *
 */
public class ExportDialog extends JDialog {
	String fileNamePrefix = "";
	ImageGridStack currentGrids;
	SliceStack sliceStack;
	boolean isGifExport;
	
	JPanel all = new JPanel();
	JPanel warningPanel = new JPanel();
	JPanel targetPanel = new JPanel();
	JPanel zoomPanel = new JPanel();
	JPanel colorPanel = new JPanel();
	JPanel delayPanel = new JPanel();
	JPanel buttonPanel = new JPanel();

	JTextField targetField = new JTextField();
	JTextField delayField = new JTextField("1000");

	JLabel lblWarning = new JLabel("<html><body>Exporting is memory expensive. If you have a large schematic, please use low zoom values.</body></html>");
	JLabel lblZoom = new JLabel("Zoom:");
	JLabel lblColor = new JLabel("Color:");
	JLabel lblDelay = new JLabel("Delay between images in ms:");
	
	JSlider zoomSlider = new JSlider();
	
	JCheckBox chkTransparent = new JCheckBox("Transparent");

	JButton btnTarget = new JButton("Target...");
	JButton btnColorBack = new JButton("Background");
	JButton btnColorLine = new JButton("Lines");
	JButton btnAbort = new JButton("Abort");
	JButton btnSave = new JButton("Save");
	
	/**
	 * @param parent 
	 * @param sliceStack
	 * @param currentGrids
	 * @param schematicsFile 
	 * @param background 
	 * @param isGifExport 
	 */
	public ExportDialog(Component parent, SliceStack sliceStack, ImageGridStack currentGrids, File schematicsFile, Color background, boolean isGifExport) {
		this.fileNamePrefix = schematicsFile.getName();
		if (this.fileNamePrefix.contains(".")) {
			this.fileNamePrefix = this.fileNamePrefix.substring(0, this.fileNamePrefix.lastIndexOf('.'));
		}
		this.sliceStack = sliceStack;
		this.currentGrids = currentGrids;
		this.isGifExport = isGifExport;
		
		this.zoomSlider.setMaximum(100);
		this.zoomSlider.setMinimum(5);
		this.zoomSlider.setValue(10);
		this.zoomSlider.setMajorTickSpacing(10);
		this.zoomSlider.setMinorTickSpacing(5);
		this.zoomSlider.setPaintTicks(true);
		this.zoomSlider.setPaintLabels(true);
		this.zoomSlider.setSnapToTicks(true);
		Hashtable<Integer, JComponent> zoomLabelTable = new Hashtable<Integer, JComponent>();
		for (int i = 0; i <= 100; i += 10) {
			zoomLabelTable.put( new Integer(i), new JLabel(Integer.toString(i / 10)) );
		}
		this.zoomSlider.setLabelTable(zoomLabelTable);
		
		this.all.setLayout(new BoxLayout(this.all, BoxLayout.Y_AXIS));
		this.targetPanel.setLayout(new BoxLayout(this.targetPanel, BoxLayout.X_AXIS));
		this.warningPanel.setLayout(new BoxLayout(this.warningPanel, BoxLayout.X_AXIS));
		this.zoomPanel.setLayout(new BoxLayout(this.zoomPanel, BoxLayout.X_AXIS));
		this.colorPanel.setLayout(new BoxLayout(this.colorPanel, BoxLayout.X_AXIS));
		if (isGifExport) {
			this.delayPanel.setLayout(new BoxLayout(this.delayPanel, BoxLayout.X_AXIS));
		}
		this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS));

		if (background instanceof ColorUIResource) {
			// background is swings default transparent (which is, for some reason, not just a normal transparent color)
			// to make the button not look like a default button but really have a color, we just set a new default color object
			// of the same color
			Color newColor = new Color(background.getRGB());
			this.btnColorBack.setBackground(newColor);
		} else {
			this.btnColorBack.setBackground(background);
		}
		this.btnColorBack.setForeground(getOppositeColor(background));
		
		if (!isGifExport) {
			this.btnColorBack.setEnabled(false);
			this.chkTransparent.setSelected(true);
		}
		this.btnColorLine.setBackground(currentGrids.getGridColor());
		this.btnColorLine.setForeground(getOppositeColor(currentGrids.getGridColor()));
		
		this.warningPanel.add(this.lblWarning);
		// init with directory
		this.targetField.setText(schematicsFile.getParentFile().getAbsolutePath());
		
		this.targetField.setMaximumSize(new Dimension(this.targetField.getMaximumSize().width, this.btnTarget.getPreferredSize().height));
		this.targetPanel.add(this.targetField);
		this.targetPanel.add(Box.createHorizontalStrut(10));
		this.targetPanel.add(this.btnTarget);

		this.zoomPanel.add(this.lblZoom);
		this.zoomPanel.add(Box.createHorizontalStrut(10));
		this.zoomPanel.add(this.zoomSlider);
		
		this.colorPanel.add(this.lblColor);
		this.colorPanel.add(Box.createHorizontalStrut(10));
		this.colorPanel.add(this.btnColorBack);
		this.colorPanel.add(Box.createHorizontalStrut(10));
		if (!isGifExport) {
			this.colorPanel.add(this.chkTransparent);
			this.colorPanel.add(Box.createHorizontalStrut(10));
		}
		this.colorPanel.add(this.btnColorLine);
		this.colorPanel.add(Box.createHorizontalGlue());
		
		if (isGifExport) {
			this.delayField.setMaximumSize(new Dimension(this.delayField.getMaximumSize().width, this.delayField.getMinimumSize().height + 2));
			this.delayPanel.add(this.lblDelay);
			this.delayPanel.add(Box.createHorizontalStrut(10));
			this.delayPanel.add(this.delayField);
			this.delayPanel.add(Box.createHorizontalGlue());
		}
		
		this.buttonPanel.add(Box.createHorizontalGlue());
		this.buttonPanel.add(this.btnAbort);
		this.buttonPanel.add(Box.createHorizontalStrut(10));
		this.buttonPanel.add(this.btnSave);

		this.all.add(this.warningPanel);
		this.all.add(Box.createVerticalStrut(10));
		this.all.add(this.targetPanel);
		this.all.add(Box.createVerticalStrut(10));
		this.all.add(this.zoomPanel);
		this.all.add(Box.createVerticalStrut(10));
		this.all.add(this.colorPanel);
		this.all.add(Box.createVerticalStrut(10));
		if (isGifExport) {
			this.all.add(this.delayPanel);
			this.all.add(Box.createVerticalStrut(10));
		}
		this.all.add(this.buttonPanel);
		
		this.all.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(this.all);
		
		this.btnAbort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExportDialog.this.dispose();
			}
		});
		
		this.btnTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				File oldFile = new File(ExportDialog.this.targetField.getText());
				if (oldFile.isDirectory()) {
					fc.setCurrentDirectory(oldFile);
				} else {
					fc.setSelectedFile(oldFile);
				}
				fc.setAcceptAllFileFilterUsed(false);
				
				if (ExportDialog.this.isGifExport) {
					fc.setFileFilter(new FiletypeFilter("gif", "Gif files"));
					if (fc.showSaveDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						if (!f.isDirectory()) {
							if (!f.getName().toLowerCase().endsWith(".gif")) {
								f = new File(f.getAbsolutePath() + ".gif");
							}
							ExportDialog.this.targetField.setText(f.getAbsolutePath());
						}
					}
				} else {
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setFileFilter(new FiletypeFilter());
					fc.setDialogTitle("Save to directory");
					if (fc.showSaveDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
						File f = fc.getSelectedFile();
						if (f.isDirectory() && f.exists()) {
							ExportDialog.this.targetField.setText(f.getAbsolutePath());
						}
					}
				}
			}
		});
		
		this.btnColorBack.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(ExportDialog.this, "Choose Background Color", ExportDialog.this.btnColorBack.getBackground());
				if (newColor != null) {
					ExportDialog.this.btnColorBack.setBackground(newColor);
					ExportDialog.this.btnColorBack.setForeground(getOppositeColor(newColor));
				}
			}
		});
		
		this.btnColorLine.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(ExportDialog.this, "Choose Line Color", ExportDialog.this.btnColorLine.getBackground());
				if (newColor != null) {
					ExportDialog.this.btnColorLine.setBackground(newColor);
					ExportDialog.this.btnColorLine.setForeground(getOppositeColor(newColor));
				}
			}
		});
		
		this.chkTransparent.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExportDialog.this.btnColorBack.setEnabled(!ExportDialog.this.chkTransparent.isSelected());
			}
		});
		
		this.btnSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (ExportDialog.this.isGifExport) {
					ExportDialog.this.saveGif();
				} else {
					ExportDialog.this.saveMulti();
				}
			}
		});
		
		if (isGifExport) {
			this.setSize(new Dimension(380, 255));
			this.setTitle("Save to animated gif");
		} else {
			this.setSize(new Dimension(415, 230));
			this.setTitle("Save to multiple files");
		}
		this.setLocationRelativeTo(parent);
		this.setModal(true);
		this.setVisible(true);
	}
	
	/** Let's do this! K'psch! *pelvic motions* */
	void saveMulti() {
		File dir = new File(this.targetField.getText());
		if (!dir.isDirectory() || !dir.exists()) {
			JOptionPane.showMessageDialog(this, this.targetField.getText() + " is not a directory or does not exist", "Directory not found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			ImageGridStack grids = this.currentGrids;
			
			if (((double)this.zoomSlider.getValue() / 10) != grids.getZoom()) {
				double zoom = ((double)this.zoomSlider.getValue() / 10);
				grids = this.sliceStack.getImages(zoom, true);
			}
		
			int numberLength = Integer.toString(grids.getStackSize()).length();
			for (int i = 0; i < grids.getStackSize(); ++i) {
				StringBuffer imgName = new StringBuffer(Integer.toString(i));
				while (imgName.length() < numberLength) {
					imgName.insert(0, "0");
				}
				imgName.insert(0, this.fileNamePrefix).append(".png");
				
				BufferedImage img = null;
				if (this.chkTransparent.isSelected()) {
					img = grids.getGridAtLevel(i).exportImage(null, Color.BLACK);
				} else {
					img = grids.getGridAtLevel(i).exportImage(this.btnColorBack.getBackground(), this.btnColorLine.getBackground());
				}
				
				File file = new File(dir, imgName.toString());
				try {
					ImageIO.write(img, "png", file);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Error while saving image " + imgName.toString() + ":\n"
							+ e.getLocalizedMessage(), "Could not save", JOptionPane.ERROR_MESSAGE);
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			}
			JOptionPane.showMessageDialog(this, "All images successfully saved!", "Yay", JOptionPane.INFORMATION_MESSAGE);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.dispose();
		} catch (OutOfMemoryError e) {
			System.gc();
			JOptionPane.showMessageDialog(this, "Ran out of memory while trying to generate images out of the schematic.\n" +
					"Try a lower zoom value or start this program with more memory.\n" +
					"Note: Some of the images may have been written", "Out of heap memory", JOptionPane.ERROR_MESSAGE);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	void saveGif() {
		File target = new File(this.targetField.getText());
		if (target.isDirectory() || !target.getParentFile().exists()) {
			JOptionPane.showMessageDialog(this, this.targetField.getText() + " is a directory or not a valid path", "Target file not valid", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (target.exists()) {
			int reply = JOptionPane.showConfirmDialog(ExportDialog.this, "File allready exists. Overwrite?", "File allready exists", JOptionPane.YES_NO_OPTION);
			if (reply != JOptionPane.YES_OPTION) return;
		}
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			ImageGridStack grids = this.currentGrids;
			
			if (((double)this.zoomSlider.getValue() / 10) != grids.getZoom()) {
				double zoom = ((double)this.zoomSlider.getValue() / 10);
				grids = this.sliceStack.getImages(zoom, true);
			}
			
			int millis;
			try {
				millis = Integer.parseInt(this.delayField.getText());
			} catch (NumberFormatException ex) {
				millis = 1000;
				this.delayField.setText(Integer.toString(millis));
			}
			
			AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			encoder.setDelay(millis);
			encoder.setRepeat(0);
			encoder.start(target.getAbsolutePath());
			encoder.setTransparent(null);
			
			for (int i = 0; i < grids.getStackSize(); ++i) {
				BufferedImage img = grids.getGridAtLevel(i).exportImage(this.btnColorBack.getBackground(), this.btnColorLine.getBackground());
				
				encoder.addFrame(img);
			}
			encoder.finish();
			JOptionPane.showMessageDialog(this, "All images successfully saved!", "Yay", JOptionPane.INFORMATION_MESSAGE);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.dispose();
		} catch (OutOfMemoryError e) {
			System.gc();
			JOptionPane.showMessageDialog(this, "Ran out of memory while trying to generate a gif out of the schematic.\n" +
					"Try a lower zoom value or start this program with more memory.", "Out of heap memory", JOptionPane.ERROR_MESSAGE);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 * I'm not sure if this really generates the complimentory color, but it's good enough for all I need it for
	 * @param c the color to get the opposite from
	 * @return the opposite color of c
	 */
	static Color getOppositeColor(Color c) {
		int r = 0xFF - c.getRed();
		int g = 0xFF - c.getGreen();
		int b = 0xFF - c.getBlue();
		return new Color(r, g, b);
	}
}
