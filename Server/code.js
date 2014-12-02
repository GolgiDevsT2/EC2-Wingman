var AWS = require('aws-sdk');
var fs = require('fs');

var lookupsInFlight = 0;
var reloadInProgress = false;
var params = {
    //     DryRun: false,
    //     MaxResults: 100,
};

var instanceHash = {};
var prevInstanceHash = {};
var watcherHash = {};
var watcher;


var raiseOrangeCpuAlarm = function(name, cpu){
    Object.keys(watcherHash).forEach(function (watcher) {
	Wingman.WingmanSvc.raiseOrangeCpuAlarm({
	    success : function(){
		console.log("raiseOrangeCpuAlarm: success(1)");
	    },
	    failWithGolgiException : function(golgiException){
		console.log("raiseOrangeCpuAlarm: failed: '" + golgiException.getErrText() + "'/ " + golgiException.getErrType());
		delete watcherHash[watcher];
		writeWatchers();
	    }
	},
	watcher,
	stdGto,
	name,
	cpu);
    });
};

var raiseRedCpuAlarm = function(name, cpu){
    Object.keys(watcherHash).forEach(function (watcher) {
	Wingman.WingmanSvc.raiseRedCpuAlarm({
	    success : function(){
		console.log("raiseRedCpuAlarm: success(1)");
	    },
	    failWithGolgiException : function(golgiException){
		console.log("raiseRedCpuAlarm: failed: '" + golgiException.getErrText() + "'/ " + golgiException.getErrType());
		delete watcherHash[watcher];
		writeWatchers();
	    }
	},
	watcher,
	stdGto,
	name,
	cpu);
    });
};

var raiseStatusCheckAlarm = function(name){
    Object.keys(watcherHash).forEach(function (watcher) {
	Wingman.WingmanSvc.raiseStatusCheckAlarm({
	    success : function(){
		console.log("raiseStatusCheckAlarm: success(1)");
	    },
	    failWithGolgiException : function(golgiException){
		console.log("raiseStatusCheckAlarm: failed: '" + golgiException.getErrText() + "'/ " + golgiException.getErrType());
		delete watcherHash[watcher];
		writeWatchers();
	    }
	},
	watcher,
	stdGto,
	name);
    });
};

var raiseStateChangeAlarm = function(name, oldState, newState){
    Object.keys(watcherHash).forEach(function (watcher) {
	Wingman.WingmanSvc.raiseStateChangeAlarm({
	    success : function(){
		console.log("raiseStateChangeAlarm: success(1)");
	    },
	    failWithGolgiException : function(golgiException){
		console.log("raiseStateChangeAlarm: failed: '" + golgiException.getErrText() + "'/ " + golgiException.getErrType());
		delete watcherHash[watcher];
		writeWatchers();
	    }
	},
	watcher,
	stdGto,
	name,
	oldState,
	newState);
    });
};

var writeWatchers = function(){
    var fData = "";
    
    Object.keys(watcherHash).forEach(function (id) {
	if(watcherHash[id] == 1){
	    fData = fData + id + "\n";
	}
    });
    
    console.log("Will write: '" + fData + "'");
    
    fs.writeFile(".APP-INSTANCES.tmp", fData, function(e){
	if(e == undefined || e == null){
	    console.log("Write: OK");
	    console.log("Renaming: temporary file");
	    fs.renameSync(".APP-INSTANCES.tmp", "APP-INSTANCES");
	    console.log("OK");
	}
	else{
	    console.log("Write: FAIL: " + e);
	}
    });
};

var maybeDone = function(){
    if(lookupsInFlight == 0){
	Object.keys(instanceHash).forEach(function (id) {
	    var prevInst = prevInstanceHash[id]; 
	    var inst = instanceHash[id];
	    if(prevInst == undefined){
		console.log("New Instance: " + inst.getName() + " (" + inst.getRegion() + ")");
	    }
	    else{
		var oldCpu = prevInst.getCpuUsage();
		var newCpu = inst.getCpuUsage();
		var oldState = prevInst.getState();
		var newState = inst.getState();
		var oldScFailing = prevInst.getStatusCheckFailed();
		var newScFailing = inst.getStatusCheckFailed();
		
		if(oldState != newState){
		    //
		    // Send alert about a state change
		    //
		    console.log(inst.getName() + " state change " + oldState + " ===> " + inst.newState);
		    if(oldState == 16){
			//
			// Was running, now something else
			//
			raiseStateChangeAlarm(inst.getName(), oldState, newState);
		    }
		}

		if(oldState == 16 && newState == 16){
		    //
		    // Only do routine CPU and StatusCheckFailing checks on instances that are running
		    // for at least this and the previous sample
		    //
		
		    if(oldCpu < 50 && newCpu >= 50 && newCpu < 80){
			//
			// Alert about CPU going to ORANGE
			//
			console.log(inst.getName() + " CPU change " + oldCpu + " ===> " + newCpu);
			raiseOrangeCpuAlarm(inst.getName(), newCpu);
		    }
		
		    if(oldCpu < 80 && newCpu >= 80){
			//
			// Alert about CPU going to RED
			//
			console.log(inst.getName() + " CPU change " + oldCpu + " ===> " + newCpu);
			raiseRedCpuAlarm(inst.getName(), newCpu);
		    }
		
		    if(oldScFailing == 0 && newScFailing != 0){
			//
			// Send alert about status check failure
			//
			console.log(inst.getName() + "Raising status check alarm");
			raiseStatusCheckAlarm(inst.getName());
		    }
		
		    if(oldScFailing != newScFailing){
			console.log(inst.getName() + " Statuc Check Failed change " + oldScFailing + " ===> " + newScFailing);
		    }
		}
		delete prevInstanceHash[id];
	    }
	    /*
	    if(inst.state == 16){
		console.log(inst.region + ": " + inst.name + ": CPU: " + inst.cpuUsage + "% StatusCheckFailed: " + inst.statusCheckFailed);
	    }
	    else{
		console.log(inst.region + ": " + inst.name + ": STOPPED");
	    }
	    */
	    
	});
	Object.keys(prevInstanceHash).forEach(function (id) {
	    delete instanceHash[id];
	});
	reloadInProgress = false;
    }
};

var lookup = function(ec2, cloudwatch, instanceId, instanceName, cpu)
{
    var now = new Date();

    var nowMs = Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), now.getUTCHours(), now.getUTCMinutes(), now.getUTCSeconds());

    var eTime = new Date(nowMs + 1800000);

    var cwParams = {
    		EndTime: eTime,

    		// 	MetricName: 'CPUUtilization', /* required */

    		Namespace: 'AWS/EC2', /* required */
    		Period: 60, /* required */
    		// StartTime: sTime,
    		Statistics: [ /* required */
    		           	   'Average',
    		           	   /* more items */
    		           	],
    		Dimensions: [
    		             {
    		            	 Name: 'InstanceId', /* required */
    		            	 Value: instanceId /* required */
    		             },
    		             /* more items */
    		            ],
    		             // 	Unit: 'Percent'
    };
    
    if(cpu){
        cwParams.MetricName = 'CPUUtilization';
        cwParams.StartTime = new Date(nowMs - 900000);
        cwParams.Unit = 'Percent';
    }
    else{
        cwParams.MetricName = 'StatusCheckFailed', 
        cwParams.StartTime = new Date(nowMs - 300000);
        cwParams.Unit = 'Count';
    }

    // console.log("UNIT: " + cwParams.Unit);
    
    cloudwatch.getMetricStatistics(cwParams, function(err, data) {
            if (err){
        	console.log("***********************************");
                console.log("Failed to lookup " + instanceName);
                console.log(err, err.stack);
            }
            else{
                var inst = instanceHash[instanceId];
                var dataPoints = data.Datapoints;
                var newestTs = 0;
        	
                if(cpu){
                    var pc = -1;
                    for(var i = 0; dataPoints[i] != undefined; i++){
                	var ts = dataPoints[i].Timestamp;
                	if(ts.getTime() > newestTs){
                	    pc = Math.round(dataPoints[i].Average);
                	}
                    }
                    inst.setCpuUsage(pc);
                }
                else{
                    // console.log(data);
                    var scFailed = -1;
                    for(var i = 0; dataPoints[i] != undefined; i++){
                	var ts = dataPoints[i].Timestamp;
                	if(ts.getTime() > newestTs){
                	    scFailed = dataPoints[i].Average;
                	}
                    }
                    inst.setStatusCheckFailed(scFailed);
                }
            }
            lookupsInFlight--;
            maybeDone();
        });
    
};


var examineRegion = function(ec2, cloudwatch, region){
    ec2.describeInstances(params, function(err, data) {
	if (err){
	    if(region == "eu-central-1"){
		//
		// A new region that we currently have issues with
		//
	    }
	    else{
		console.log("****************************");
		console.log("Failed to lookup instances in " + region);
		console.log(err, err.stack); // an error occurred
	    }
	}
	else{
	    // console.log(region + " OK");
	    
	    // console.log(data);           // successful response
	    for(var i = 0; data.Reservations[i] != undefined; i++){
		var r = data.Reservations[i];
		for(var j = 0; r.Instances[j] != undefined; j++){
		    var inst = r.Instances[j];
		    var name = inst.InstanceId;
            
		    // From AWS Docs: 0 (pending), 16 (running), 32 (shutting-down), 48 (terminated), 64 (stopping), and 80 (stopped)

		    // 	console.log(inst);
		    // console.log(inst.Tags);
		    for(var k = 0; inst.Tags[k] != undefined; k++){
			if(inst.Tags[k].Key == 'Name'){
			    name = inst.Tags[k].Value;
			}
		    }
		    if(instanceHash[inst.InstanceId] == undefined){
			var newInst = Wingman.InstanceData();
			newInst.setInstanceId(inst.InstanceId);
			newInst.setName(name);
			newInst.setRegion(region);
			instanceHash[newInst.getInstanceId()] = newInst;
		    }
            
		    instanceHash[inst.InstanceId].setState(inst.State.Code);
            
		    if(inst.State.Code == 16){
			//
			// It is running so let's interrogate it
			//
            
			lookupsInFlight++;
			lookup(ec2, cloudwatch, inst.InstanceId, name, false);
			
			lookupsInFlight++;
			lookup(ec2, cloudwatch, inst.InstanceId, name, true);
		    }
		}
	    }
	}
	lookupsInFlight--;
	maybeDone();
    });
};



var housekeep = function(){
    if(!reloadInProgress){
	// console.log("Examining Instances");
    	reloadInProgress = true;
    	prevInstanceHash = {};
	Object.keys(instanceHash).forEach(function (id) { 
	    var inst = instanceHash[id];
	    var oldInst = inst.dupe();
	    // console.log("Duped " + inst.getName() + " to " + oldInst.getName());
	    prevInstanceHash[id] = oldInst;
	});
    	
    	AWS.config.region = 'us-west-1';
    	(new AWS.EC2()).describeRegions(params, function(err, data) {
    	    if (err){
    		reloadInProgress = false;
    		console.log(err, err.stack);
    	    }
    	    else{
    		lookupsInFlight = 0;
    		
    		for(var ridx = 0; data.Regions[ridx] != undefined; ridx++){
    		    var region = data.Regions[ridx].RegionName;
    		    AWS.config.region = region; 
    		    var ec2 = new AWS.EC2();
    		    var cloudwatch = new AWS.CloudWatch();
    		    
    		    lookupsInFlight++;
    		    examineRegion(ec2, cloudwatch, region);
    		}
    	    }
    	});
    }
};


GolgiLib.init();
GolgiNet.init();
Wingman.ServiceInit();
GolgiNet.setCredentials("__PUT_DEVKEY_HERE__", "__PUT_APPKEY_HERE__", "SERVER");

var stdGto = {};
stdGto["EXPIRY"] = 20400;  // 6 hours

Wingman.WingmanSvc.registerListHandler(function(resultHandler){
	var list = Wingman.InstanceList();
	
	var iList = [];
	var idx = 0;
	var sender; 
	var newLister = 0;
	
	sender = resultHandler.getRequestSenderId();
	
	if(watcherHash[sender] != 1){
	    newLister = 1;
	}
	
	watcherHash[sender] = 1;
	
	if(newLister == 0){
	    console.log("      Lister: " + sender);
	}
	else{
	    console.log("[NEW] Lister: " + sender);
	    
	    writeWatchers();
	    
	}
	
	
	Object.keys(instanceHash).forEach(function (id) {
	    var inst = instanceHash[id];
	    iList[idx++] = inst.dupe();
	});
	
	list.setIList(iList);
	
	// console.log("Received list from '" + resultHandler.getRequestSenderId() + "'");
	
	/*
	if(idx > 0){
            Wingman.WingmanSvc.raiseOrangeCpuAlarm({
        	success : function(){
        	    console.log("raiseOrangeCpuAlert: success(1)");
        	},
        	failWithGolgiException : function(golgiException){
        	    console.log("raiseOrangeCpuAlert: failed: '" + golgiException.getErrText() + "'/ " + golgiException.getErrType());
        	}
            },
            resultHandler.getRequestSenderId(),
            stdGto,
            iList[0].getName(),
            iList[0].getCpuUsage());
	}
	*/
        
	resultHandler.success(list);
    });


GolgiNet.register(function(err){
    if(err != undefined){
	console.log("Failed to register: " + err);
    }
    else{
	console.log("Register Success");
	housekeep();
	
	
	fs.readFile("APP-INSTANCES", function(e, data){
		if(e){
		    // console.log("Zoikes, failed to load application instances: " + e);
		}
		else{
		    var txt = '' + data;
		    var lines = txt.trim().split( "\n" );
		    for(var i = 0; i < lines.length; i++){
			console.log("Adding: '" + lines[i] + "'");
			watcherHash[lines[i]] = 1;
		    }
		}
	});

	
	/*
	watcherHash['CRYxA]CEvuuKIMpK_VBm'] = 1;
	watcherHash['TRSQx\imLJHg^TAkMaHu'] = 1;
	watcherHash['y`ssvWM\ZbcWabeIh\`h'] = 1;
	*/

	setInterval(function(){
	    housekeep();
	}
	, 60000);
    }
    
});

