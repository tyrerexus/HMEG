//World.java
//
//Copyright (C) 2013 Henrik Björkman www.eit.se
//
//History:
//Adapted for use with RSB. Henrik 2013-05-04


package se.eit.citypvp_package;
import se.eit.rsb_package.*;
import se.eit.db_package.*;
import se.eit.web_package.*;


//import se.eit.rsb_package.*;


public class CityPvpWorld extends WorldBase {

	long tickTimeMs=0;

	int turn=0;

	//StringFifo stringFifo;	
	
	
	RoundBuffer roundBuffer=new RoundBuffer(32);
	
	public static String className()
	{	
		// http://stackoverflow.com/questions/936684/getting-the-class-name-from-a-static-method-in-java		
		return CityPvpWorld.class.getSimpleName();	
	}


	public CityPvpWorld(DbContainer parent, String name, String createdBy) 
	{
		super(parent);
		
		regName(name);
		setCreatedBy(createdBy);
		
		// we don't generate here, the caller must write lock and call generateSelf in a separate call.
		//generateSelf();
	}

	public CityPvpWorld()
	{	
		super();
	}

	
	public void generateSelf()
	{
		
		debug("creating new world " + getNameAndPath("/"));
		//WorldRoot2d worldRoot = new WorldRoot2d();
		//worldRoot.generateSelf();
		//worldRoot.registerSelfInDbIdListAndAdd(this);

		
		// Vi måste skapa ett spawn room här, det ska heta "spawnRoom" för annar hittas det inte ifrån playerJoined.
		CityPvpRoom spawnRoom = new CityPvpRoom(this, "spawnRoom");
/*
		spawnRoom.map[3][3] = 1;
		spawnRoom.map[0][1] = 1;
		spawnRoom.map[2][3] = 2;
		spawnRoom.map[10][10] = 3;
		spawnRoom.map[11][10] = 3;
		spawnRoom.map[12][10] = 3;
		spawnRoom.map[13][10] = 3;
		spawnRoom.map[14][10] = 3;
*/		
		spawnRoom.changeTile(3, 3, 0, 1);
		spawnRoom.changeTile(0, 1, 0, 1);
		spawnRoom.changeTile(2, 3, 0, 2);
		spawnRoom.changeTile(10, 10, 0, 3);
		spawnRoom.changeTile(11, 10, 0, 3);
		spawnRoom.changeTile(12, 10, 0, 3);
		spawnRoom.changeTile(13, 10, 0, 3);
		spawnRoom.changeTile(14, 10, 0, 3);
		// Set Rotation
		spawnRoom.changeTile(3, 3, 2, 0);
		spawnRoom.changeTile(0, 1, 2, 0);
		spawnRoom.changeTile(2, 3, 2, 0);
		spawnRoom.changeTile(10, 10, 2, 0);
		spawnRoom.changeTile(11, 10, 2, 0);
		spawnRoom.changeTile(12, 10, 2, 0);
		spawnRoom.changeTile(13, 10, 2, 0);
		spawnRoom.changeTile(14, 10, 2, 0);
		
		CityPvpRoom secondRoom = new CityPvpRoom(spawnRoom, "secondRoom");
		
		secondRoom.x = 1;
		secondRoom.y = 2;
		secondRoom.outerX=4;
		secondRoom.outerY=2;
		
		secondRoom.itemtype = CityPvpBlock.doorIn;
		
		secondRoom.changeTile(3, 3, 0, 1);
		secondRoom.changeTile(0, 1, 0, 1);
		secondRoom.changeTile(2, 3, 0, 2);
		secondRoom.changeTile(10, 10, 0, 3);
		secondRoom.changeTile(11, 10, 0, 3);
		secondRoom.changeTile(12, 10, 0, 3);
		secondRoom.changeTile(13, 10, 0, 3);
		secondRoom.changeTile(14, 10, 0, 3);
		
		
		
		secondRoom.changeTile(3, 3, 2, 0);
		secondRoom.changeTile(0, 1, 2, 0);
		secondRoom.changeTile(2, 3, 2, 0);
		secondRoom.changeTile(10, 10, 2, 0);
		secondRoom.changeTile(11, 10, 2, 0);
		secondRoom.changeTile(12, 10, 2, 0);
		secondRoom.changeTile(13, 10, 2, 0);
		secondRoom.changeTile(14, 10, 2, 0);	
		
		for(int x=0;x<secondRoom.xSectors;x++)
		{
			secondRoom.changeTile(x, 0, 0, 1);
			secondRoom.changeTile(x, secondRoom.ySectors-1, 0, 1);
			
			secondRoom.changeTile(x, 0, 2, 0);
			secondRoom.changeTile(x, secondRoom.ySectors-1, 2, 0);
		}
		for(int y=0;y<secondRoom.ySectors;y++)
		{
			secondRoom.changeTile(0, y, 2, 0);
			secondRoom.changeTile(secondRoom.xSectors-1, y, 2, 0);
		}
		secondRoom.changeTile(0, 0, 0, 0);
		
		
	//	CityPvpRoom thirdRoom = new CityPvpRoom(spawnRoom, "thirdRoom");
	//	
	//	thirdRoom.x = 12;
	//	thirdRoom.y = 8;
	//	thirdRoom.xSectors = 8;
	//	thirdRoom.ySectors = 8;
	//	thirdRoom.itemtype = 6;
	//	thirdRoom.map[3][3] = 1;
	//	thirdRoom.map[0][1] = 1;
	//	thirdRoom.map[2][3] = 2;
	//	thirdRoom.map[10][10] = 2;
	//	thirdRoom.map[11][10] = 2;
	//	thirdRoom.map[12][10] = 2;
	//	thirdRoom.map[13][10] = 1;
	//	thirdRoom.map[14][10] = 1;
	//	for(int y=0;y<thirdRoom.ySectors;y++)
	//	{
	//		thirdRoom.map[0][y] = 1;
	//		thirdRoom.map[thirdRoom.xSectors-1][y] = 1;
	//	}
		
	}
	
	
	@Override
	public void readSelf(WordReader wr)	
	{
		super.readSelf(wr);
		//debug("created unit " + name + " at "+pir);
		// It seems we should check that database is locked here. It is not always...
		turn = wr.readInt();
			
		
	}

	// serialize to ww
	@Override
	public void writeSelf(WordWriter ww)
	{		
		super.writeSelf(ww);
		ww.writeInt(turn);
		
	}
	
	/*
	public String getType()
	{
		return super.getType()+".World";
	}
	*/

	/*
	public void tick(int phase)
	{
		super.tick(phase);
		if (phase==GameObj.TICK_PHASE_LAST)
		{
			turn++;
		}		
	}
	*/
	
	// Non recursive search, will not find avatar if its not in the top of the world
	/*
	public Avatar findAvatar(String name)
	{
		DbBase bo=iterateStoredObjects(null);		
		while(bo!=null)
		{
			if (bo instanceof Avatar)
			{
				Avatar a=(Avatar)bo;
				if (a.getName().equals(name))
				{
					return a;
				}
			}
			bo=iterateStoredObjects(bo);
		}
		return null;
	}
	*/

	/*@Override
	public void tickMs(int deltaMs)
	{
		//final long currentTime=System.currentTimeMillis();
		super.tickMs(deltaMs); // This will do tick for all non DbIdObj. The objects that are subclasses of DbIdObj get their tick from DbIdList instead.	
	}*/

	
	// Find a room, optionally a room with a given name.
	// This method should probably been located in some other class.
	/*
	public Room findRoom(String name)
	{
		this.lockRead();
		try
		{
			DbBase bo=iterateStoredObjects(null);		
			while(bo!=null)
			{
				if (bo instanceof Room)
				{
					Room r=(Room)bo;
					if (name==null)
					{
						return r;
					}
					else
					{
						final String roomName=r.getName();
						if ((roomName!=null) && (roomName.equals(name)))
						{
							return r;
						}
					}
				}
				
				
				bo=iterateStoredObjects(bo);
			}
		}
		finally
		{
			this.unlockRead();
		}
		return null;
	}
	*/

	public void messageFromPlayer(PlayerData player, String msg)
	{		
		//stringFifo.put(msg);
		debug("messageFromPlayer "+player.getName()+" "+msg);
		
		roundBuffer.put(player.getName()+": "+msg);
		

        /*
		for (int i=0;i<conncetedPlayers.length;i++)
		{
			if (conncetedPlayers[i]!=null)
			{
				conncetedPlayers[i].notify(conncetedPlayersRef[i], this.getId());
			}
			
		}
		*/
        notifySubscribers(this.getId());
		
	}



	
	public String getMsg(int updateCounter)
	{		
		return roundBuffer.get(updateCounter);
	}
	

	public CityPvpAvatar playerJoined(PlayerData player)
	{
		CityPvpAvatar avatar=null;
		
		this.lockWrite();
		try{
			
			// Har spelaren en avatar redan
			avatar = (CityPvpAvatar) this.findDbNamedRecursive(player.getName(), 999);						
	
			if (avatar==null)
			{
				// Ingen hittad, skapa en ny,
				DbContainer spawnRoom = (DbContainer)this.findDbNamedRecursive("spawnRoom", 5);	
				
				if (spawnRoom==null)
				{
					error("did not find spawn room");
					
				}
				else
				{
				    avatar = new CityPvpAvatar(spawnRoom , player.getName());
				}
			}
		}
		finally
		{
			this.unlockWrite();			
		}
		
		return avatar;
	}

	@Override
	public void tickMsCallback(long tickTimeMs)
	{
		final long deltaTimeMs = tickTimeMs - this.tickTimeMs;
		this.tickTimeMs += deltaTimeMs;

		if (notificationDataList.size()!=0)
		{
			// TODO: Here we do tick on all objects in this game regardless if those need it or not. We could use CPU more efficiently...
			for (DbIdObj dio : idList.dbListId)
			{
				if (dio instanceof CityPvpEntity)
				{
					CityPvpEntity cpe = (CityPvpEntity)dio;
					cpe.tickEntityMs(deltaTimeMs);
				}
			}
		}
	}

	// This returns the name of the server object that clients shall use to play this world.
	@Override
	public String serverForThisWorld()
	{
		return "CityPvpServer";
	}

}