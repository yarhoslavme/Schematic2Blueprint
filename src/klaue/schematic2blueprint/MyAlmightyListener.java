package klaue.schematic2blueprint;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

class MyAlmightyListener2 implements AWTEventListener
{
  private Point startPt;
  private final Cursor defaultCursor = Cursor.getPredefinedCursor(0);
  private final Cursor handCursor = Cursor.getPredefinedCursor(13);
  private final JScrollPane scrPane;
  private final JComponent comp;
  private Point move = new Point();
  private Point ptZero;
  private Point it;
  private final Rectangle rect = new Rectangle();
  private Rectangle vr;
  private int w;
  private int h;
  private MouseEvent event;
  private boolean dragInitialized = false;
  
  public MyAlmightyListener2(JScrollPane scrPane, JComponent comp)
  {
    this.comp = comp;
    this.scrPane = scrPane;
  }
  
  public void eventDispatched(AWTEvent e)
  {
    this.event = ((MouseEvent)e);
    if (this.event.getID() == 501)
    {
      Component source = (Component)e.getSource();
      if (!SwingUtilities.isDescendingFrom(source, this.scrPane.getViewport())) {
        return;
      }
      this.startPt = this.event.getLocationOnScreen();
      

      this.comp.setCursor(this.handCursor);
      this.dragInitialized = true;
    }
    if ((this.event.getID() == 502) && 
      (this.dragInitialized))
    {
      this.comp.setCursor(this.defaultCursor);
      this.dragInitialized = false;
    }
    if ((this.event.getID() == 506) && 
      (this.dragInitialized))
    {
      this.it = this.event.getLocationOnScreen();
      

      this.move.setLocation(this.it.x - this.startPt.x, this.it.y - 
        this.startPt.y);
      this.startPt.setLocation(this.it);
      this.vr = this.scrPane.getViewport().getViewRect();
      this.w = this.vr.width;
      this.h = this.vr.height;
      

      this.ptZero = SwingUtilities.convertPoint(this.scrPane.getViewport(), 0, 0, 
        this.comp);
      

      this.rect.setRect(this.ptZero.x - this.move.x, this.ptZero.y - 
        this.move.y, this.w, this.h);
      

      this.comp.scrollRectToVisible(this.rect);
    }
  }
}
