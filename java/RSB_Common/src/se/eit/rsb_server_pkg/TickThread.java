// TickThread.java
//
// Copyright (C) 2016 Henrik Björkman (www.eit.se/hb)
// License: www.eit.se/rsb/license
//
// History:
// Created by Henrik 2015 


package se.eit.rsb_server_pkg;

import java.text.SimpleDateFormat;

import se.eit.db_package.*;
import se.eit.rsb_factory_pkg.GlobalConfig;
import se.eit.web_package.*;



public class TickThread implements Runnable
{
	protected static SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"); // http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html

	public int timeAcceleration=1; // 0 will pause all games
	
	long simTime;

	DbTickList db=null;

	boolean done=false;

	long sim_tick_time=0; 
	
	GlobalConfig config;
	
	//long nextAutoSaveTime=0;
	
	public static String className()
	{	
		// http://stackoverflow.com/questions/936684/getting-the-class-name-from-a-static-method-in-java		
		return DbBase.class.getSimpleName();	
	}
	
	public TickThread(GlobalConfig config, DbTickList db) 
	{
		this.db=db;
		this.config=config;
		//nextAutoSaveTime=System.currentTimeMillis()+config.MinutesBetweenAutoSave*60*1000;;
	}
	
	public void debug(String str)
	{
		WordWriter.safeDebug(className()+": "+str);
	}

	public void error(String str)
	{
		WordWriter.safeError(className()+": "+str);
		System.exit(1);
	}
	
	
	public void setDone()
	{
		done=true;
	}
	
	public void run() 
	{
		while(!done) 
		{
			final long curTime=System.currentTimeMillis();
			final long timeToWait=simTime - curTime;
			final int desiredFrameRateMs = db.get_desired_frame_rate_ms();
			
			if (timeToWait>desiredFrameRateMs*2)			
			{
				// This can happen if desiredFrameRateMs was changed recently.
				debug("frame rate in future "+ timeToWait + " "+desiredFrameRateMs);
				myWait((int)desiredFrameRateMs);
				simTime = curTime + desiredFrameRateMs;
			}
			else if (timeToWait>0)
			{
				// The normal case
				myWait((int)timeToWait);				   
				simTime += desiredFrameRateMs;
			}
			else if (timeToWait>-desiredFrameRateMs)
			{
				// slightly behind, skip wait but update sim_frame_time normally
				simTime += desiredFrameRateMs;			
			}
			else
			{
				// Far behind, skip sim_frame_time forward
				debug("frame rate far behind");
				simTime = curTime + desiredFrameRateMs/2;
			}

			//debug("tick "+cur_time+" "+sim_frame_time+" "+System.currentTimeMillis());
			//db.tickRecursiveMs(desired_frame_rate_ms*timeAcceleration);
			sim_tick_time += desiredFrameRateMs*timeAcceleration;
			db.tickMsSuper(sim_tick_time);
			
			/*
			// Is it time for auto save?
			if ((cur_time-nextAutoSaveTime)>0)
			{
				System.out.println("auto save "+ sdf.format(cur_time));
				//db.setGlobalConfig(config);
				db.saveRecursive();
				nextAutoSaveTime=cur_time+config.MinutesBetweenAutoSave*60*1000;
			}
			*/
		}
	}
	
	public synchronized void myWait(int time_ms)
	{
		try 
		{
			this.wait(time_ms);
		}  
		catch (InterruptedException e) {;}
	}
	
	
	
}
