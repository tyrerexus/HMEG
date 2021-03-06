// hmeg_room.js
// Copyright (C) 2015 Henrik Bjorkman www.eit.se/hb
// Created 2015-02-21 by Henrik Bjorkman www.eit.se/hb


// subclass extends superclass
HmegEntity.prototype = Object.create(MirrorBase.prototype);
HmegEntity.prototype.constructor = HmegEntity;




function HmegEntity(world, parent, arg)
{
	MirrorBase.call(this, world, parent, arg); // call super constructor
	
	this.unknown= new DivImg("unknown");
	this.img = [];
	this.initSelf();
}

HmegEntity.prototype.initSelf=function()
{


    // Entities & Blocks
    //     id_rotate_arg
	this.img["0_0_0"] = new DivImg("air");
	this.img["1_0_0"] = new DivImg("dirt");
	this.img["2_0_0"] = new DivImg("ladder");
	this.img["3_0_0"] = new DivImg("grass");
	this.img["7_0_0"] = new DivImg("door");
	this.img["8_0_0"] = new DivImg("wood");
	this.img["9_0_0"] = new DivImg("woodstairright");
	this.img["11_0_0"] = new DivImg("controlpanel");
	this.img["13_0_0"] = new DivImg("avatar_0");
	this.img["13_1_0"] = new DivImg("avatar_1");
	this.img["13_2_0"] = new DivImg("avatar_2");
	this.img["13_3_0"] = new DivImg("avatar_3");
	this.img["14_0_0"] = new DivImg("ballon")
}


HmegEntity.prototype.readSelf=function(arg)
{
	var n = MirrorBase.prototype.readSelf.call(this, arg);

	this.unitOwner=parseInt(arg[n]);
	this.sectorTerrain=parseInt(arg[n+1]);

	this.x = parseInt(arg[n]);
	this.y = parseInt(arg[n+1]);
	this.health = parseInt(arg[n+2]);
	this.itemtype = parseInt(arg[n+3]);
	this.stack = parseInt(arg[n+4]);
	this.state = parseInt(arg[n+5]);

	return n+6;	
}


HmegEntity.prototype.selfToString=function()
{
	var str="name="+this.objName;

	str+=", id="+this.id;

	var n=this.getNChildObjects();
	if (n>0)
	{
		str+=", n="+n;
	}

	return str;
}

HmegEntity.prototype.showSelfContextXY=function(context, x, y, width, height)
{
	var argu = 0;
	var n=""+this.itemtype+"_"+this.state+"_"+argu;
	
	if (n in this.img)
	{
		this.img[n].showSelfContextXY(context, x, y, width, height);
	}
	else
	{
		this.unknown.showSelfContextXY(context, x, y, width, height);
	}
}

