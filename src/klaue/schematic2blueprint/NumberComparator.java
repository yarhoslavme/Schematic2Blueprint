package klaue.schematic2blueprint;

import java.util.Comparator;

class NumberComparator2 implements Comparator<Object>
{
  public int compare(Object f1, Object f2)
  {
    if (((f1 instanceof String)) && ((f2 instanceof String))) {
      return ((String)f1).compareTo((String)f2);
    }
    if (((f1 instanceof Byte)) && ((f2 instanceof Byte))) {
      return ((Byte)f1).compareTo((Byte)f2);
    }
    if (((f1 instanceof Short)) && ((f2 instanceof Short))) {
      return ((Short)f1).compareTo((Short)f2);
    }
    if (((f1 instanceof Integer)) && ((f2 instanceof Integer))) {
      return ((Integer)f1).compareTo((Integer)f2);
    }
    return 0;
  }
}
