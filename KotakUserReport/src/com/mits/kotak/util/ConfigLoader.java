package com.mits.kotak.util;

import java.util.ResourceBundle;

public class ConfigLoader {
  private static ResourceBundle resourceBundle = null;
  
  public static ResourceBundle getBundle() {
    try {
      resourceBundle = ResourceBundle.getBundle("KOTAKUSERREPORT");
    } catch (Exception e) {
      System.out.println("Exception in ConfigLoader class :: " + e.getMessage());
      e.printStackTrace();
    } 
    return resourceBundle;
  }
}
