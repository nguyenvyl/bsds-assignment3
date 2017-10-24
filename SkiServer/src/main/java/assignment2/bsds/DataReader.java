package assignment2.bsds;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import bsdsass2testdata.RFIDLiftData;

/**
 * Reads in a file of skier data to be returned as an ArrayList of RFIDLiftData objects
 */
public class DataReader {

  public List<RFIDLiftData> readData() {

    // System independent newline
    String newline = System.getProperty("line.separator");
    String filePath = "/Users/irenakushner/Documents/Northeastern/CS 6650/Assignment 2/BSDSAssignment2Day1.ser";
    //String filePath = "/Users/irenakushner/Documents/Northeastern/CS 6650/Assignment 2/BSDSAssignment2Day2.ser";
    // file and stream for input
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    ArrayList<RFIDLiftData> RFIDDataIn = new ArrayList<RFIDLiftData>();

    try
    {
      fis = new FileInputStream(filePath);
      ois = new ObjectInputStream(fis);

      // read data from serialized file
      RFIDDataIn = (ArrayList) ois.readObject();

      ois.close();
      fis.close();

    }catch(IOException ioe){
      ioe.printStackTrace();
    }catch(ClassNotFoundException c){
      System.out.println("Class not found");
      c.printStackTrace();
    }

    return RFIDDataIn;
  }
}