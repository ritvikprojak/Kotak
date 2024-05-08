define([
        "dojo/_base/declare",
        "dijit/_TemplatedMixin",
        "dijit/_WidgetsInTemplateMixin",
        "dojo/store/Memory",
        "ecm/model/Desktop",
        "dijit/form/ComboBox",
        "ecm/widget/DatePicker",		
        "ecm/model/Request",
        "ecm/widget/admin/PluginConfigurationPane",
        "ecm/widget/dialog/MessageDialog",
        "dojo/html",
        "dojo/date",
        "dojo/date/locale",
        "dojo/aspect",
        "dojo/text!./templates/ReportsFeature.html"
        ],
        function(declare, _TemplatedMixin, _WidgetsInTemplateMixin, Memory, Desktop, ComboBox, DatePicker,Request,PluginConfigurationPane,MessageDialog,html,date,locale,aspect, template) {

	var item=null;
	var comboBox;
	var startFromDate;
	var endToDate;
	var th;
	var doccomboBox;
	var objStorecomboBox;
	return declare("UserReportDojo.ReportsFeature", [ PluginConfigurationPane, _TemplatedMixin, _WidgetsInTemplateMixin], {

		templateString: template,
		widgetsInTemplate: true,

		load: function(callback) {
		},

		postCreate: function() {

			th=this;
			th.doccomboBox=null; 
			this.inherited(arguments);
			console.log("this scope is::::::",th);
			html.set(th.objStorelabel,"ObjectStore    ");
			th.buttondiv.style.display='none';
			th.reports.style.visibility='hidden';
			th.reports.style.display='none';
			th.tableDates.style.visibility='hidden';
			var objStore = new Memory({
				data : [
				        {name:"KMBL"},
				        {name:"KMPL"}

				        ]
			});
			console.log("objStore for OBJSTORE",objStore);
//			if(th.objStorecomboBox == null){
				th.objStorecomboBox = new ComboBox({
					placeHolder:"Select Object Store",
					store: objStore,
					searchAttr: "name",
					width:"15em"
				}, th.objStoreCombo);
				th.objStorecomboBox.startup();
				console.log("objStorecomboBox",th.objStorecomboBox);
//			}
			dojo.connect(th.objStorecomboBox,"onChange", function(objStore) {
				
				if(th.comboBox != null ){
					th.comboBox.set('value',"");
					th.documentdiv.style.display='none';
					}
				if(th.doccomboBox != null ){
					th.doccomboBox.set('value',"");
					}
				th.documentdiv.style.display='none';
				th.reports.style.visibility='visible';
				th.reports.style.display='block';
			var reportStore = new Memory({
				data: [
				       {name:"Last Login Report"},
				       {name:"User Sign On/Sign Off"},
				       {name:"Group Privilege Report"}
				       ]
			});

			console.log("reportStore",reportStore);
			console.log("before combobox this :: ",this);
			console.log("before combobox th :: ",th);
			
			
			html.set(th.reports,"User Reports");
			if(th.comboBox == null){
			th.comboBox = new ComboBox({

				placeHolder:"Select",
				store: reportStore,
				searchAttr: "name",
				width:"15em"

			}, th.reportsComboBox);

			th.comboBox.startup();
			console.log("comboBox",th.comboBox);
			}	
			dojo.connect(th.comboBox,"onChange", function(item) {

				console.log("inside of dates",item);

				th.buttondiv.style.display='block';

				if(item=="User Sign On/Sign Off"){
					
					html.set(th.sdate,"Start Date");
					html.set(th.edate,"End Date");
					console.log("th.documentdiv.style :: ",th.documentdiv.style);
//					th.objStorediv.style.visibility='hidden';
//					th.objStorediv.style.display='none';					
					th.documentdiv.style.visibility='hidden';
					th.documentdiv.style.display='none';
					if(th.tableDates.style.display='none'){
						th.tableDates.style.visibility = 'visible';
						th.tableDates.style.display='block';
					}
				}else if( item=="Last Login Report"){
//					th.objStorediv.style.display='none';
					th.documentdiv.style.display='none';
					th.tableDates.style.display='none';
				}
				else if(item=="Group Privilege Report"){
					th.buttondiv.style.display='none';
					console.log("tableDatestableDatestableDatestableDates",th.tableDates);
					//th.tableDates.style.visibility='hidden';
					th.tableDates.style.display = 'none';
//					if(th.objStorediv.style.display = 'none'){
//						th.objStorediv.style.visibility = 'visible';
//						th.objStorediv.style.display = 'block';
//					}
					
//					dojo.connect(th.objStorecomboBox,"onChange", function(objStore) {
						if(th.documentdiv.style.display = 'none'){
							html.set(th.doclabel,"Documents    ");
							th.documentdiv.style.visibility = 'visible';
							th.documentdiv.style.display = 'block';
						}
						th.buttondiv.style.display='block';
						
						var reqParams = new Object();
						reqParams.reqtype=item;
						reqParams.objStore=th.objStorecomboBox.value;
						Request.invokePluginService("UserReportSMS", "GetDocumentClassServiceId",{
							requestParams : reqParams,
							requestCompleteCallback : function(response) {
								console.log("response Id::::::::::::",response);
								var docStore = new Memory({
									data : response
								});
								if(th.doccomboBox == null){
									th.doccomboBox = new ComboBox({
										placeHolder:"Select Classes",
										store: docStore,
										searchAttr: "name",
										width:"15em"
									}, th.documentCombo);
									th.doccomboBox.startup();
									console.log("if doccomboBox",th.doccomboBox);
								}
								else{
									th.doccomboBox.set('store',docStore);
									console.log("else doccomboBox",th.doccomboBox);
								}
							}
						});
//						});
				}
					});
				
//			});
			}); //ComboBox connect close
		}, //post Create Close
//		------------------------------------------if user selected TOdate is greater than From Date then this validation works 		
		fromDateValidation: function() {
			if (this.meetingToDate < this.meetingFromDate) {
				this.meetingToDate.set('value', "");
			}
		},
		toDateValidation: function() {
			if (this.meetingToDate < this.meetingFromDate) {
				alert('ToDate cannot be less than From Date' );
				this.meetingFromDate.set('value', "");
				this.meetingToDate.set('value', "");
			}
		},
		ReportsGeneration: function(){
			this.inherited(arguments);
			console.log("this :: ",this);
			console.log("th :: ",th);
			console.log("in  onchange comboBox Name::::",th.comboBox.value);
			var reportType = th.comboBox.value;
			console.log(":::reportType:::::",reportType);
			if(reportType=="User Sign On/Sign Off"){ 
				var start_Date =  this.meetingFromDate.value;
				var end_Date =  this.meetingToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setMonth(d.getMonth()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('Gap Between two Dates must be within 30 Days only');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=th.objStorecomboBox.value;
					console.log("-----serviceParams-------------",serviceParams);
					Request.invokePluginService("UserReportSMS", "KotakUserReportsCheckServiceId",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "No Results Found" 
								});
								errorMessage.show();
							}
							else if(response.key=="full") {
								var array=new Array();
								array = Desktop.repositories;
								console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = th.objStorecomboBox.value;
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=UserReportSMS&action=KotakUserReportGenerationServiceId"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
							}
						}
					});		
				}
			}
			else if(reportType=="Last Login Report"){
				var array=new Array();
				array = Desktop.repositories;
				console.log("array Id::::::::::::",array);
				var repository=array[0];
				var repoId = th.objStorecomboBox.value;
				var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=UserReportSMS&action=KotakLastUserReportGenerationServiceId"+"&repoId="+repoId+"&reportType="+reportType;
				console.log("url::::",uri);
				var appendSecurityToken=Request.appendSecurityToken(uri);
				console.log("appendSecurityToken::::::::::::",appendSecurityToken);
				window.open(appendSecurityToken);
			}else if(reportType=="Group Privilege Report"){			
				var reqParameters = new Object();
//				var reparray=new Array();
//				reparray = Desktop.repositories;
//				console.log("array Id::::::::::::",reparray);
//				var reposit=reparray[0];
				var repositId = th.objStorecomboBox.value;
				console.log("doccomboBox:::::::",th.doccomboBox);
				if(repositId != null){
					var docName=th.doccomboBox.value;
					console.log("docName",docName);
					}
				console.log("repository Id::::::::::::",repositId);
				var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=UserReportSMS&action=KotakDocumentSecurityReportsServiceId"+"&docName="+docName+"&repositId="+repositId;
				console.log("url::::",uri);
				var appendSecurityToken=Request.appendSecurityToken(uri);
				console.log("appendSecurityToken::::::::::::",appendSecurityToken);
				window.open(appendSecurityToken);
			}
		},
		validate: function() {
			return true;
		}
	});
});
