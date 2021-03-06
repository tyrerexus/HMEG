//MibEntry.java
//
// Copyright (C) 2016 Henrik Björkman (www.eit.se/hb)
// License: www.eit.se/rsb/license
//
//History:
//Adapted for use with RSB. Henrik 2013-05-04


package se.eit.rsb_package;
import se.eit.db_package.*;
import se.eit.web_package.*;






public class MibEntry extends DbSubRoot {

	public String value=null;

	public static String className()
	{	
		// http://stackoverflow.com/questions/936684/getting-the-class-name-from-a-static-method-in-java		
		return MibEntry.class.getSimpleName();	
	}
  
	public MibEntry()
	{
		super();	
	}
  
	/*
	public MibEntry(DbBase parent, String name, String value, GlobalConfig globalConfig) {
	      super(parent, name, globalConfig);
	      this.value=value;
	}
	*/
	
	  // deserialize from wr
		@Override
	public void readSelf(WordReader wr)    
	{
	      super.readSelf(wr);
	      value=wr.readString();
	}
	
	// serialize to ww
	@Override
	public void writeSelf(WordWriter ww)
	{        
	      super.writeSelf(ww);
	      ww.writeString(value);
	}
  
  
      
}