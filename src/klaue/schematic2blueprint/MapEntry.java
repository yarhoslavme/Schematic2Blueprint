package klaue.schematic2blueprint;

import java.awt.image.BufferedImage;

class MapEntry2 implements Comparable<MapEntry>
{
  public String name = "";
  public BufferedImage image = null;
  public short id = 0;
  public int amount = 1;
  
  public String toString()
  {
    return "name = " + this.name + " id = " + this.id;
  }
  
  public int compareTo(MapEntry o)
  {
    return this.name.compareTo(o.name);
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MapEntry other = (MapEntry)obj;
    if (this.name == null)
    {
      if (other.name != null) {
        return false;
      }
    }
    else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }
}
