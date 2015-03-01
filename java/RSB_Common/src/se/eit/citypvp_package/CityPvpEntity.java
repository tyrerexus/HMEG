package se.eit.citypvp_package;
import se.eit.rsb_package.*;
import se.eit.db_package.*;
import se.eit.web_package.*;

public class CityPvpEntity extends GameBase {
	public int x = 0;
	public int y = 0;
	public int a = 0;
	public int health = 20;
	public int Oldx = 0;
	public int Oldy = 0;
	// entity i entity (item) comer att display'a 
	public int state = 0;
	public int stack = 0;
	public int itemtype = 0;
	public int fill_wood = 0;
	public int fill_stone = 0;
	public int fill_mineral = 0;
	
	
	
	public CityPvpEntity(DbBase parent, String name) 
	{
		super();
		linkSelf(parent);
		this.setName(name);
	}
	
	
	public CityPvpEntity() 
	{
		super();	
	}
	

	@Override
	public void readSelf(WordReader wr)	
	{
		super.readSelf(wr);
		x = wr.readInt();
		y = wr.readInt();
		health = wr.readInt();
		itemtype = wr.readInt();
		stack = wr.readInt();
		state = wr.readInt();
	}
	
	
	@Override
	public void writeSelf(WordWriter ww)
	{		
		super.writeSelf(ww);
		ww.writeInt(x);
		ww.writeInt(y);
		ww.writeInt(health);
		ww.writeInt(itemtype);
		ww.writeInt(stack);
		ww.writeInt(state);
	}
	
	// Denna metod anropas av game engine periodiskt
	public void tickEntityMs(long deltaMs)
	{
		a+=deltaMs;
		if (a>100)
		{	
			if (this.getContainingObj() instanceof CityPvpRoom)
			{
			CityPvpRoom cpr=(CityPvpRoom)this.getContainingObj();
			
			
			// NONE GRAVITY EFFECT BLOCKS
			if (cpr.map[x][y]==2)
			{
				return;
			}

			
			
			// DO GRAVITY
			move(0,CityPvpBlock.inBlockGravity(cpr.map[x][y]));
			a-=1000;
			}
		}
	}
	

	public void doMoveAnimation(DbRoot ro)
	{	
		ro.lockWrite();
		try {
		if (state == 0) {state=2;}
		else if (state == 1) {state=3;}
		// Reverse
		else if (state == 2) {state=0;}
		else if (state == 3) {state=1;}	
		}
		finally
		{
			ro.unlockWrite();
		}
	}
	
	
	public void move(int dx, int dy)
	{
		
		DbRoot ro=this.getDbRoot();

		ro.lockWrite();
		try{
			///
			
			
			DbBase p = this.getParent();
			
			if (p instanceof CityPvpRoom)
			{
	
				CityPvpRoom cpr=(CityPvpRoom)p;
				
				final int newX=x+dx;
				final int newY=y+dy;
				
		        // Here we check that we will not move outside current room
				if ( (newX<0) || (newX>=cpr.xSectors) || (newY<0) || (newY>=cpr.ySectors) )
				{
					return;
				}
				// 2 = ladder    walkable block [trancperant]
				if (/*cpr.map[newX][newY] == 0 || (cpr.map[newX][newY]==2)*/CityPvpBlock.isWalkable(cpr.map[newX][newY])==true)
				{ 
					if (cpr.map[newX][newY] == CityPvpBlock.doorOut)
					{
						DbContainer pp=this.getParent().getParent();
						debug("move from "+getParent()+ "to "+pp.getId());
						this.moveToRoomThreadSafe(pp);
					}
					
					// Here we check if we bump into other entities
					DbBase list[] = cpr.getListOfSubObjectsThreadSafe();
					for(int i=0;i<list.length;i++)
					{
						if (list[i] instanceof CityPvpEntity)
						{
							CityPvpEntity cpe = (CityPvpEntity)list[i];
		
							// Get the size of entity
							int eXSize=1;
							int eYSize=1;
							if(cpe instanceof CityPvpRoom)
							{
								CityPvpRoom e=(CityPvpRoom)cpe;
								eXSize=e.outerX;
							}
							
		                    // Check for collision with another entity
							if ( ((newX>=cpe.x) && (newX <(cpe.x+eXSize))) && ((newY>=cpe.y) && (newY<(cpe.y+eYSize))))
							{
								if (cpe.itemtype!=6)
								{
									// Move not possible, we can not move onto this entity
									return;
								}
								else
								{
									if (cpe != this)
									{
									// This entity was a room. We can move into it.
									debug("moveToRoom "+cpe.getId());
									this.moveToRoomThreadSafe(cpe);
									//this.moveToRoom(cpe);
									((NotificationSender)getDbRoot()).notifySubscribers(-2);
									x = 1;
									y = 1;		
									return;
									}
									else
									{
										debug("moveToRoom was not possible beacouse was its self "+cpe.getId());
									
									}
								}
								
							}
						}
					}
				//	finally
				//	{
				//		ro.unlockRead();
			//		}
					
					if (cpr.map[x][y]!=2 && y+1<cpr.ySectors)
					{	
						if (cpr.map[x][y+1]==0 && newY<y)
						{
							return;
						}
					}
					
					// komihåg rutan den var i
					Oldx= x;
					Oldy= y;
					
					// kolla om rutan man vill gå till är tom
					
					
					
					
					// DO zaaa animation.
					
					if (newX > x )
					{
					 if (state==2)
					 {
						state = 1; 
					 }
					
						
					}
					if (newX < x )
					{	
						if (state==1)
						 {
							state = 2; 
						 }
					}
					
					// Här flyttas denna entity
					x=newX;
					y=newY;
					
					
					doMoveAnimation(ro);
					/*if (state == 0) {state=2;}
					else if (state == 1) {state=3;}
					// Reverse
					else if (state == 2) {state=0;}
					else if (state == 3) {state=1;}
					*/
				//	System.out.println("Brah chainging Animation "+state);
			///////////////////////		/////////////////////move(0,CityPvpBlock.inBlockGravity(cpr.map[x][y]));
			
					// Uppdatera rutan den var i, och rutan den kommer till 
					//CityPvpRoom.updateTile(Oldx, Oldy)
					
					// Tala om för servers att något har blivit ändrat
			        //DbRoot dr = getDbRoot();
			        //NotificationSender ns = (NotificationSender)dr;
			       // ns.notifySubscribers(this.getId());
			      //  this.setUpdateCounter();	
				}
			}
		}
		finally
		{
			ro.unlockWrite();
		}
		this.setUpdateCounter();			

	}

	public CityPvpEntity findItem(int id)
	{
		if (this.listOfStoredObjects!=null)
		{
			for(DbStorable ds: this.listOfStoredObjects)
			{
				if (ds instanceof CityPvpEntity)
				{
					CityPvpEntity cpe=(CityPvpEntity)ds;
					if (cpe.itemtype==id)
					{
						return cpe;
					}
				}
			}
		}
		return null;
	}

	
	public int haveItem(int id)
	{
		int count=0;
		for(DbStorable ds: this.listOfStoredObjects)
		{
			if (ds instanceof CityPvpEntity)
			{
				CityPvpEntity cpe=(CityPvpEntity)ds;
				if (cpe.itemtype==id)
				{
					count += cpe.stack ;
				}
			}
		}
		return count;
	}
	
	
//	en natt natt natt min båt jag styrde
	public void giveItem(int itemtype , int count)
	{
		debugWriteLock();
		
		
		CityPvpEntity cpe = this.findItem(itemtype);
		
		if (cpe!=null)
		{
			cpe.stack+=1;
			cpe.setUpdateCounter();
		}
		else
		{	
			// Create a new object
			cpe = new CityPvpEntity ();
			cpe.setName("i"+itemtype); // All objects shall have some name in RSB
			cpe.itemtype = itemtype;
			cpe.stack = count;
			//cpe.state = ite; // temporary test for new JS client.
			cpe.linkSelf(this);			
	
			cpe.setUpdateCounter(); // should not be needed here.

			// assign coordinates to the new object.
			// Objects in the inventory are placed from upper left according to index
			if (this instanceof CityPvpRoom)
			{
				CityPvpRoom cpr=(CityPvpRoom)this;
				cpr.x = cpe.getIndex()%cpr.xSectors;
				cpr.y = cpe.getIndex()/cpr.xSectors;
			}
		}
	}
}
