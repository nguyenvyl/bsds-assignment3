package assignment3.bsds.model;

import java.io.Serializable;

/**
 * Created by irenakushner on 10/23/17.
 */
public class SkierData implements Serializable, Comparable<SkierData>{

  private int numLifts;
  private int totalVert;

  public SkierData(int numLifts, int totalVert) {
    this.numLifts = numLifts;
    this.totalVert = totalVert;
  }

  public int getNumLifts() {
    return numLifts;
  }

  public void setNumLifts(int numLifts) {
    this.numLifts = numLifts;
  }

  public int getTotalVert() {
    return totalVert;
  }

  public void setTotalVert(int totalVert) {
    this.totalVert = totalVert;
  }

  @Override
  public String toString() {
    return "SkierData{" +
        "numLifts=" + numLifts +
        ", totalVert=" + totalVert +
        '}';
  }

  @Override
  public int compareTo(SkierData compareData) {
    int compareVert = ((SkierData) compareData).getTotalVert();

    //ascending order
    return this.totalVert - compareVert;
  }
}
