require(["dojo/_base/connect",
         "ecm/model/Request",
         "dojo/aspect",
         "ecm/model/Desktop",
         "ecm/widget/dialog/MessageDialog",
         "dojo/dom-style",
         "dojo/domReady!"
         ], 
         function(connect,
        		 Request, 
        		 aspect,
        		 Desktop,
        		 MessageDialog,
        		 domStyle) {

	var userName;
	var serviceParams = new Object();

	connect.connect(Desktop, "onLogin", function() {

		console.log("On Login::::::::",Desktop);

		var array=new Array();

		array = Desktop.repositories;

		console.log("array Id::::::::::::",array);

		var repository=array[0];

		var repoId = repository.id;

		userName=Desktop.userId;

		serviceParams.userName=userName;

		serviceParams.repoId=repoId;

		Request.invokePluginService("UserReportSMS","UserLoginPluginService",{

			requestParams:serviceParams,
			requestCompleteCallback:function(response){
				console.log(response);
			}
		});			
		/*var features=ecm.model.desktop.features;		
		console.log("features:::::::::are",features);
		for(var i=0;i<features.length;i++){
			var currentFeature = features[i];			
			console.log("currentFeature:::::::::",currentFeature);			
			if(currentFeature.id =="ReportsFeatureKMPLId"){				
				disablefeature=currentFeature.id;
			}
		}			var userid = Desktop.userId;
					var admFeature = Desktop.adminFeature;
			console.log("admFeature",admFeature);
			//if the feature hasn't been added to the desktop or the launch bar container yet
//			&& !typeof(disablefeature)=='undefined' && !disablefeature==null
			if (admFeature==null ) {
				var button = Desktop.getLayout().launchBarContainer.getButtons()[disablefeature];
				console.log("buttons are :::: ",button);
				domStyle.set(button.domNode,"display", "none"); 
			}
		 */	});
	aspect.before(Desktop, "logoff", function() {
		console.log("On LogOut::::::::");
		var array=new Array();
		array = Desktop.repositories;
		console.log("array Id::::::::::::",array);
		var repository=array[0];
		var repoId = repository.id;
		console.log("repoId",repoId);
		userName=Desktop.userId;
		var response = Request.invokeSynchronousPluginService("UserReportSMS","UserLogoffPluginService",
				{
			userName:userName,
			repositoryid:repoId,
			userAction:"Logoff"
				});
	});
});
