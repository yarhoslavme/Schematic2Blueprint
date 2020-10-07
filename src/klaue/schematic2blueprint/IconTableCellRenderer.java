package klaue.schematic2blueprint;

import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class IconTableCellRenderer2 extends DefaultTableCellRenderer
{
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
  {
    JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if ((value instanceof BufferedImage))
    {
      label.setText("");
      label.setIcon(new ImageIcon((BufferedImage)value));
      label.setHorizontalAlignment(0);
    }
    else
    {
      label.setText(value.toString());
      label.setIcon(null);
    }
    return label;
  }
}
