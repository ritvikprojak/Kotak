package com.mits.kotak;

import java.util.Locale;

import com.ibm.ecm.extension.PluginFeature;

public class KotakUserReportsGenerationFeature extends PluginFeature {

	 public String getContentClass()
	  {
	    return "userReportDojo.ReportsFeature";
	  }
	  
	  public String getDescription(Locale arg0)
	  {
	    return "ReportsFeatureSMS";
	  }
	  
	  public String getFeatureIconTooltipText(Locale arg0)
	  {
	    return "SMS Reports";
	  }
	  
	  public String getIconUrl()
	  {
	    return "reportspic";
	  }
	  
	  public String getId()
	  {
	    return "ReportsFeatureSMSId";
	  }
	  
	  public String getName(Locale arg0)
	  {
	    return "ReportsFeatureSMS";
	  }
	  
	  public String getPopupWindowClass()
	  {
	    return null;
	  }
	  
	  public String getPopupWindowTooltipText(Locale arg0)
	  {
	    return null;
	  }
	  
	  public boolean isPreLoad()
	  {
	    return false;
	  }
}
