package klaue.schematic2blueprint;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import klaue.mcschematictool.ImageProvider;
import klaue.mcschematictool.NameProvider;
import klaue.mcschematictool.Slice;
import klaue.mcschematictool.SliceStack;
import klaue.mcschematictool.blocktypes.Bed;
import klaue.mcschematictool.blocktypes.Block;
import klaue.mcschematictool.blocktypes.Door;
import klaue.mcschematictool.blocktypes.Furnace;
import klaue.mcschematictool.blocktypes.PoweredRail;
import klaue.mcschematictool.blocktypes.RedstoneTorch;

/**
 * @author klaue
 *
 */
public class BlockCounterDialog extends JDialog {
	JPanel all = new JPanel();
	JPanel labelPanel = new JPanel();
	JPanel buttonPanel = new JPanel();

	JLabel lblTitle = new JLabel("The current schematic contains the following blocks:");
	
	JTable table = null;

	JButton btnPrint = new JButton("Print");
	JButton btnClose = new JButton("Close");
	
	SliceStack sliceStack = null;
	
	/**
	 * @param parent 
	 * @param sliceStack
	 */
	public BlockCounterDialog(Component parent, SliceStack sliceStack) {
		this.sliceStack = sliceStack;
		
		this.all.setLayout(new BoxLayout(this.all, BoxLayout.Y_AXIS));
		this.labelPanel.setLayout(new BoxLayout(this.labelPanel, BoxLayout.X_AXIS));
		this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS));

		this.labelPanel.add(this.lblTitle);
		this.labelPanel.add(Box.createHorizontalGlue());
		
		this.buttonPanel.add(Box.createHorizontalGlue());
		this.buttonPanel.add(this.btnPrint);
		this.buttonPanel.add(Box.createHorizontalStrut(10));
		this.buttonPanel.add(this.btnClose);
		
		this.table = buildTable();
//		TreeSet<MapEntry> bla = countBlocks();
//		for (Iterator<MapEntry> iter = bla.keySet().iterator(); iter.hasNext(); ) {
//			MapEntry entry = iter.next();
//			System.out.println(bla.get(entry) + " x " + entry);
//		}
		
		JScrollPane scrollPane = new JScrollPane(this.table);
		this.table.setFillsViewportHeight(true);

		this.all.add(this.labelPanel);
		this.all.add(Box.createVerticalStrut(10));
		this.all.add(scrollPane);
		this.all.add(Box.createVerticalStrut(10));
		this.all.add(this.buttonPanel);
		
		this.all.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.add(this.all);

		this.btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BlockCounterDialog.this.dispose();
			}
		});
		

		this.btnPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					BlockCounterDialog.this.table.print();
				} catch (PrinterException pe) {
					JOptionPane.showMessageDialog(BlockCounterDialog.this, "Error printing:\n"
							+ pe.getLocalizedMessage(), "Could not print", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		this.setSize(new Dimension(490, 255));
		this.setTitle("Count Blocks");
		this.setLocationRelativeTo(parent);
		//this.setModal(true);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private JTable buildTable () {
		Vector<String> colNames = new Vector<String>();
		colNames.add("Amount");
		colNames.add("Icon");
		colNames.add("Name");
		colNames.add("ID");
		
		Vector<Vector<Object> > rows = new Vector<Vector<Object> >();
		
		TreeSet<MapEntry> countedBlocks = countBlocks();
		for (Iterator<MapEntry> iter = countedBlocks.iterator(); iter.hasNext(); ) {
			Vector<Object> currentRow = new Vector<Object>();
			MapEntry entry = iter.next();
			
			currentRow.add(entry.amount);
			currentRow.add(entry.image);
			currentRow.add(entry.name);
			currentRow.add(entry.id);
			
			rows.add(currentRow);
		}
		
		JTable table = new JTable(rows, colNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table.getColumnModel().getColumn(1).setCellRenderer(new IconTableCellRenderer());
		table.setRowHeight((int)(16 * 1.5));
	//	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth((int)(16 * 1.5) + 10);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		
		table.setAutoCreateRowSorter(true);
		TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>)table.getRowSorter();
		sorter.setSortable(1, false); // excl. image column
		Comparator<Object> comp = new NumberComparator();
		sorter.setComparator(0, comp);
		sorter.setComparator(2, comp);
		sorter.setComparator(3, comp);
		
		sorter.toggleSortOrder(0);
		
		return table;
	}
	
	private TreeSet<MapEntry> countBlocks() {
		TreeSet<MapEntry> blockSet = new TreeSet<MapEntry>();
		for (int i = 0; i < this.sliceStack.getHeight(); i++) {
			Slice s = this.sliceStack.getSlice(i);
			for (int j = 0; j < s.getWidth(); j++) {
				for (int k = 0; k < s.getHeight(); k++) {
					Block b = s.getBlockAt(j, k);
					
					if (b.getId() == 0) { // air
						continue;
					}
					
					short id = b.getId();
					byte data = b.getData();
					
					// only count one block for: bed, door, furnace (burning and off), reds. torch (burn. and off), redst. ore (glowing and not),
					// powered rail (on, off), redst. repeater (on, off), redst. wire (on, off) and sign (wall, ground)
					if (b.isBed()) {
						if (((Bed)b).isFoot()) continue; // only save head as "Bed"
						id = (short)355; // item id
					} else if (b.isDoor()) {
						Door door = (Door)b;
						if (door.isBottomHalf()) continue; // only sabe top half as door
						if (door.getDoorType() == Door.DoorType.IRON) {
							id = (short)324; // item id
						} else {
							id = (short)330; // item id
						}
					} else if (b.isFurnace() && ((Furnace)b).isOn()) {
						id = (short)61; // furnace off
					} else if (b.isRedstoneTorch() && !((RedstoneTorch)b).isOn()) {
						id = (short)76; // burning rs torch
					} else if (id == 74) { // glowing redstone ore
						id = (short)73; // redstone ore (w/o glowing)
					} else if (b.isPoweredRail() && !((PoweredRail)b).isOn()) {
						data = -1; // default value
					} else if (b.isRepeater()) {
						id = (short)356; // item id
					} else if (b.isRedstoneWire()) {
						id = (short)331; // item id of redstone
					} else if (b.isTripwire()) {
						id = (short)287; // item id of string
					} else if (b.isSign()) {
						// both wall and freestanding sign replaced by item id
						id = (short)323;
					}
					
					
					// add new entry or increase number of old
					// for performance reasons, the icon is only added for new etries
					MapEntry entry = new MapEntry();
					entry.name = NameProvider.getNameOfBlockOrItem(id, data);
					Iterator<MapEntry> iter = blockSet.iterator();
					while (iter.hasNext()) {
						MapEntry e = iter.next();
						if (e.equals(entry)) {
							e.amount = e.amount + 1;
							iter = blockSet.iterator(); // reset to start for if below 
							break;
						}
					}
					if (!iter.hasNext()) {
						// new entry
						entry.id = id;
						entry.image = ImageProvider.zoom(1.5, ImageProvider.getImageByBlockOrItemID(id, data));
						blockSet.add(entry);
					}
				}
			}
		}
		return blockSet;
	}
}

class MapEntry implements Comparable<MapEntry> {
	// a struct
	public String name = "";
	public BufferedImage image = null;
	public short id = 0;
	public int amount = 1;
	
	@Override
	public String toString() {
		return "name = " + this.name + " id = " + this.id;
	}

	@Override
	public int compareTo(MapEntry o) {
		return this.name.compareTo(o.name);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapEntry other = (MapEntry) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}
}

class IconTableCellRenderer extends DefaultTableCellRenderer {
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value,
    		boolean isSelected, boolean hasFocus, int row, int column) {

    	JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    	if(value instanceof BufferedImage){
    		label.setText("");
    		label.setIcon(new ImageIcon((BufferedImage) value));
    		label.setHorizontalAlignment(SwingConstants.CENTER);
    	}else{
    		label.setText(value.toString());
            label.setIcon(null);
    	}
    	return label;
    }
}

class NumberComparator implements Comparator<Object> {
	@Override
	public int compare(final Object f1, final Object f2) {
		if (f1 instanceof String && f2 instanceof String) {
			return ((String)f1).compareTo((String)f2);
		} else if (f1 instanceof Byte && f2 instanceof Byte) {
			return ((Byte)f1).compareTo((Byte)f2);
		} else if (f1 instanceof Short && f2 instanceof Short) {
			return ((Short)f1).compareTo((Short)f2);
		} else if (f1 instanceof Integer && f2 instanceof Integer) {
			return ((Integer)f1).compareTo((Integer)f2);
		} else {
			return 0;
		}
	}
}
