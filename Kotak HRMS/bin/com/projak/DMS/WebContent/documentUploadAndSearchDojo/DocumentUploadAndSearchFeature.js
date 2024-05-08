define([
	"dojo/_base/declare",
	"dojo/_base/lang",
	"dojo/Deferred",
	"dijit/ConfirmDialog",
	"dijit/Dialog",
	"dojo/json",
	"dojo/_base/array",
	"dojo/dom-attr",
	"dojo/dom-style",
	"dojo/aspect",
	"dojo/dom-construct",
	"idx/layout/BorderContainer",
	"dijit/form/TextBox",
	"dijit/form/Button",
	"dijit/layout/TabContainer",
	"dijit/layout/ContentPane",
	"dojo/store/Memory",
	"dijit/form/FilteringSelect",

	"ecm/widget/layout/_LaunchBarPane",
	"dijit/_TemplatedMixin",
	"dijit/_WidgetsInTemplateMixin",
	"ecm/model/ResultSet",
	"ecm/widget/layout/_LaunchBarPane",
	"ecm/widget/layout/_RepositorySelectorMixin",
	"ecm/widget/listView/ContentList",
	"ecm/widget/listView/gridModules/RowContextMenu",
	"ecm/widget/listView/modules/Toolbar",
	"ecm/widget/listView/modules/DocInfo",
	"ecm/widget/listView/gridModules/DndRowMoveCopy",
	"ecm/widget/listView/gridModules/DndFromDesktopAddDoc",
	"ecm/widget/listView/modules/Bar",
	"ecm/widget/listView/modules/ViewDetail",
	"ecm/widget/listView/modules/ViewMagazine",
	"ecm/widget/listView/modules/ViewFilmStrip",
	"ecm/widget/listView/modules/Breadcrumb",
	"ecm/model/Request",
	"ecm/model/Desktop",
	"ecm/model/ContentItem",
	"ecm/model/Item",
	"ecm/model/SearchQuery",
	"ecm/model/admin/ApplicationConfig",
	"dijit/form/Form",
	"ecm/widget/layout/CommonActionsHandler",
	"ecm/widget/dialog/AddContentItemDialog",
	"documentUploadAndSearchDojo/CustomUploadWidget",
	"documentUploadAndSearchDojo/CustomSearchWidget",
	"dojo/text!./templates/DocumentUploadAndSearchFeature.html",
	"dojox/encoding/base64",
	"dojox/encoding/crypto/SimpleAES"
],
function(declare,
		lang,
		Deferred,
		ConfirmDialog,
		Dialog,
		json,
		array,
		domAttr,
		domStyle,
		aspect,
		domConstruct,
		idxBorderContainer,
		TextBox,
		Button,
		TabContainer,
        ContentPane,
        Memory,
        FilteringSelect,
       
        _LaunchBarPane,
        _TemplatedMixin,
        _WidgetsInTemplateMixin,
        ResultSet,
        _LaunchBarPane,
        _RepositorySelectorMixin, 
        ContentList, RowContextMenu, 
        Toolbar, 
        DocInfo, 
        DndRowMoveCopy,
        DndFromDesktopAddDoc,
        Bar, 
        ViewDetail,
        ViewMagazine, 
        ViewFilmStrip, 
        Breadcrumb, 
        Request,
        Desktop, 
        ContentItem, 
        Item,
        SearchQuery,
        ApplicationConfig,
        Form,
        CommonActionsHandler,
        AddContentItemDialog,
        CustomUploadWidget,
        CustomSearchWidget,
        template,
        base64,
        SimpleAES) {
	/**
	 * @name uploadSearchDocumentPluginDojo.UploadSearchDocumentFeature
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	return declare("documentUploadAndSearchDojo.DocumentUploadAndSearchFeature", [
		_LaunchBarPane, _TemplatedMixin, _WidgetsInTemplateMixin
	], {
		/** @lends uploadSearchDocumentPluginDojo.UploadSearchDocumentFeature.prototype */

		templateString: template,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,
		uploadButton:null,
		searchButton:null,
		uploadTabContainer:null,
		searchTabContainer:null,
		
		_self:null,
		_configJSON:null,
		constructor: function () {
	    	// debugger;
			if(top !== self){
				window.location.replace("https://10.240.20.22:9443/kotak/Sumit");
			}
	    	_self=this;
//	    	console.log("constructor called");
	    	this.inherited(arguments);
	    	
	    	
	    	
	    },
	
	    postMixInProperties: function () {
	    	// debugger;
//	    	getConfigValue: function(configProp){
//		    	// debugger;
//		    	for(j=0; j<_self._configJSON.length; j++){
//		    		if(_self._configJSON[j].name==configProp){
//		    	    	return(_self._configJSON[j].value);
//		    		}
//		    	}
//		    }
//	    	console.log("postMixInProperties called");
	    },
	
	    startup: function () {
	    	// debugger;
	    	
//	    	console.log("startup called");
	    },
	
	    destroy: function () {
	    	// debugger;
//	    	console.log("destroy called");
	    },
	    
	    getConfigValue: function(configProp){
	    	// debugger;
	    	for(j=0; j<_self._configJSON.length; j++){
	    		if(_self._configJSON[j].name==configProp){
	    	    	return(_self._configJSON[j].value);
	    		}
	    	}
	    },
		postCreate: function() {
			_self = this;
			
			// debugger;
			
			
			
//			console.log("this", this);
//			console.log("Desktop", Desktop)
			this.logEntry("postCreate");
			/*
			 * For First time rendering
			 */
			_self.destroyMainButtons();
			_self.createMainButtons();
			_self.gotohome.style.display="none";

			aspect.after(Desktop, "onLogin", lang.hitch(_self, function() {
				// debugger;
				if(top !== self){
					window.location.replace("https://10.240.20.22:9443/kotak/Sumit");
				}
				//API to get all cinfiguration files.
				ApplicationConfig.getPluginObjects(function (plugins){
					for(var i=0; i<plugins.length; i++){
						if(plugins[i].id=="DocumentUploadAndSearch"){
							var configuration = JSON.parse(plugins[i]._attributes.configuration);
//							console.log(configuration["configuration"]);
						// configuarations for the Plugin id DOcumentUploadAndSearch added to configJSON.
							_self._configJSON = configuration["configuration"];
							
							
						}
						
					}
					// debugger;
					_self.login();
//					_self.createMainButtons();
			    });

				_self.loadHome();
				

				_self.destroyMainButtons();
				_self.createMainButtons();
			}));
			
			aspect.after(Desktop, "onLogout", lang.hitch(_self, function() {
				// debugger;
				_self.loadHome();
				_self.destroyMainButtons();
				_self.logout();
			}));
			/**
			 * Add custom logic (if any) that should be necessary after the feature pane is created. For example,
			 * you might need to connect events to trigger the pane to update based on specific user actions.
			 */
			
			this.logExit("postCreate");
			
		},
		
			
		createMainButtons: function(){
			// debugger;
			_self.uploadButton = _self.createButton("Upload",_self.uploadSectionButton, "uploadButtonIcon");
			_self.searchButton = _self.createButton("Search",_self.searchSectionButton, "searchButtonIcon");
		},
		
		destroyMainButtons: function(){
			if(_self.uploadButton && _self.searchButton){
				_self.uploadButton.destroy();
				_self.searchButton.destroy();
			}
		},
		
		/**
		 * Optional method that sets additional parameters when the user clicks on the launch button associated with 
		 * this feature.
		 */
		setParams: function(params) {
			this.logEntry("setParams", params);
			
			if (params) {
				
				if (!this.isLoaded && this.selected) {
					this.loadContent();
				}
			}
			
			this.logExit("setParams");
		},

		/**
		 * Loads the content of the pane. This is a required method to insert a pane into the LaunchBarContainer.
		 */
		loadContent: function() {
			this.logEntry("loadContent");
						
			this.logExit("loadContent");
		},

		/**
		 * Resets the content of this pane.
		 */
		reset: function() {
			this.logEntry("reset");
			
			/**
			 * This is an option method that allows you to force the LaunchBarContainer to reset when the user
			 * clicks on the launch button associated with this feature.
			 */
			this.needReset = false;
			
			this.logExit("reset");
		},
		
		createButton: function(label, attachPoint, className){
			var customButton = new dijit.form.Button({
				label:label,
                "class":className,
//                "iconClass" : className,
                "showLabel": false,
                "onClick": function() {
                	if(label=="Upload"){
                		 _self.createUploadArea();
                		 _self.destroySearchArea();
                	}else{
                		 _self.createSearchArea();
                		 _self.destroyUploadArea();
                	}
                }
            }).placeAt(attachPoint);
			customButton.startup();
			return customButton;
		},
				
		createUploadArea: function(){
			// debugger;
//			var columns = [_self.getConfigValue("employeeIdParam"),"DocumentTitle", "ContentSize","LastModifier", "Creator"];
			
			 //Added on 09/04 at 8:21AM
			
//			var draftValues = _self.getConfigValue("draftSearchParam").split(","); 
       	 /*if (draftValues.indexOf(_self.workerclass.innerText) > -1){
       		employeeWidget.browse.disabled = false;
       		
           	} else {
           		employeeWidget.browse.disabled = true;
           		
           	}*/
			_self.buttonArea.style.display="none";
			_self.gotohome.style.display="block";
//			console.log(_self);
			_self.uploadTabContainer = new dijit.layout.TabContainer({
		        style: "height: 100%; width: 100%;"
		    }).placeAt(_self.sectionArea);
			// debugger;
			if(_self.createAllowed){
		    	var uploadWidget = new CustomUploadWidget();
		    	var cp1 = new dijit.layout.ContentPane({
			    	
			    	 style: "height: 100%; width: 100%;",
			         title: "Upload",
			         content: uploadWidget
			    });
		    	_self.uploadTabContainer.addChild(cp1);
		    }
		    
		    //Added on 09/04 at 8:21AM
		    _self.uploadTabContainer.startup();
		    _self.uploadTabContainer.resize();
		},
		
		destroyUploadArea: function(){
			//alert("destryo area");
			// debugger;
			if(_self.uploadTabContainer){
				_self.uploadTabContainer.destroy();
			}
		},
		
		createSearchArea: function(){
//			var customEmployeeSearch = new CustomEmployeeSearchWidget();
			_self.buttonArea.style.display="none";
			_self.gotohome.style.display="block";
			_self.searchTabContainer = new dijit.layout.TabContainer({
		        style: "height: 100%; width: 100%;"
		    }).placeAt(_self.sectionArea);
			
			 if(_self.searchAllowed){
				 
				 var searchWidget = new CustomSearchWidget();
				 var rp1 = new dijit.layout.ContentPane({
					 style: "height: 100%; width: 100%;",
			         title: "Search",
			         content: searchWidget
			    });
				 _self.searchTabContainer.addChild(rp1);
			 }
			_self.searchTabContainer.startup();
		    _self.searchTabContainer.resize();
		},
		destroySearchArea: function(){
			// debugger;
			if(_self.searchTabContainer){
				
				 _self.searchTabContainer.destroy();
			}
		},
		
		login: function(){
			// debugger;
            var self = this;
            
//            console.log();
            var userId=ecm.model.desktop.userId;
            
//            var key = _self.getConfigValue("dMSAESKey").trim();
//            var userId2 = encodingKey.encrypt(userId, key);
            //var serverBase = window.location.protocol + "\/\/" + window.location.host;
            var serverBase = _self.getConfigValue("upmParam").trim();
//            var key = _self.getConfigValue("AESKey");
            //var serverBase = "http://192.168.1.132:8080/";
//            var temp = SimpleAES.encrypt(userId, key);
//            var encodedUserId = _self.encoding(temp);
            
            var feedURL = serverBase + "/dms/getUserDetailsWithAD/?userId="+userId;
        	// debugger;
        	var buttonDisable=false;
        	var responseJSON="";
        	var xhrArgs = {            
					url: feedURL,                        
					handleAs: "json",
					sync: true,
					preventCache: true,
					headers: { "Content-Type": "application/json"},            
					load: function(data){
						
						responseJSON=data;
						
					} ,            
					error: function(error)            
					{            
						alert ("External services query failed due to " + error);    
	
					}            

			};
        	
        
        		  // Call the asynchronous xhrGet
        		  var deferred = dojo.xhrGet(xhrArgs);
            
            
           // var responseJSON = JSON.parse(responseText);
        	//var responseJSON = JSON.parse("{\r\n    \t\t\t\"FIRST_NAME\" : \"p8admin\",\r\n    \t\t\t\"LAST_NAME\" : \"Filenet\",\r\n    \t\t\t\"Employee_ID\" : \"KMBL876\",\r\n    \t\t\t\"DOCUMENT_TYPE\" :[\"Compensation\",\"Exit Management\"],\r\n    \t\t\t\"Compensation\" : [\"CTC\",\"Appraisal\"],\r\n    \t\t\t\"Exit Management\" : [\"Relieving Letter\",\"Resignation\"],\r\n    \t\t\t\"ROLE_NAME\" : \"Vertical HR\",\r\n    \t\t\t\"CreateAllowed\" : true,\r\n    \t\t\t\"View_Allowed\" : true,\r\n    \t\t\t\"CTCAllowed\" : true\r\n    \t\t\t}	" );
        	// debugger;
        	if(responseJSON !== ""){	  
           
//        		console.log(responseJSON);
        		/*_self.workerclass.innerText = responseJSON.FINACLE_WORK_CLASS;
        		_self.branchno.innerText = responseJSON.SOL_DIV_ID;
        		_self.branchname.innerText = responseJSON.SOL_NAME;*/
        		_self.loginusername.innerText = responseJSON.FIRST_NAME + " " + responseJSON.LAST_NAME;
        		_self.loginempid.innerText = responseJSON.Employee_ID;
        		//_self.docClass = responseJSON.DOCUMENT_CLASS;
        		_self.doctype = responseJSON.DOCUMENT_TYPE;
        		_self.firstName = responseJSON.FIRST_NAME;
        		_self.lastName = responseJSON.LAST_NAME;
        		_self.roleName = responseJSON.ROLE_NAME;
        		_self.createAllowed = responseJSON.CreateAllowed;
        		_self.searchAllowed = responseJSON.View_Allowed;
        		_self.CTCAllowed = responseJSON.CTC_Allowed;
        		_self.responseJSON = responseJSON;
        		_self.downloadAllowed = responseJSON.Download_Allowed;
        		//_self.userdata.divno=responseJSON.DIV_ID;
        		if (!_self.createAllowed)
        		 {
//        			domStyle.set(_self.uploadButton.domNode, 'display', 'none');
//        			_self.uploadSectionButton.children[0].children[0].hidden=true;
        			_self.uploadButton.domNode.style.display="none";
        			_self.uploadButton.setDisabled(true);
        			
        		 }
        		if (!_self.searchAllowed){
//        			_self.searchSectionButton.children[0].children[0].hidden=true; 
        			_self.searchButton.domNode.style.display="none";
        			_self.searchButton.setDisabled(true);
        		}
        		//Added on 09/04 at 08:11 AM
        		_self.docsubtype =[];
        		for(i=0;i<_self.doctype.length;i++){
        		// debugger;
        		      var docName = _self.doctype[i];
        		      
        		      _self.docsubtype[docName] = responseJSON[docName];
        		}

        		
            
        	} else{
        		alert("User is not Allowed to login in DMS System");
        		_self.uploadButton.domNode.style.display="none";
        		_self.searchButton.domNode.style.display="none";
        		_self.uploadButton.setDisabled(true);
        		_self.searchButton.setDisabled(true);
        	}
        	
        },
        
        encoding : function(text){
       	 // debugger;
       	 var bytes = [];
       	 for(var i =0; i< text.length; i++){
       	 	var realBytes = unescape(encodeURIComponent(text[i]));
       	 	for(var j =0; j< realBytes.length ; j++){
       	 		bytes.push(realBytes[j].charCodeAt(0));
       	 	}
       	 }
       	 return base64.encode(bytes);
        },
        
        decoding : function(text){
       	 // debugger;
       	 
       	 var decoded = base64.decode(text)
       	 var decodedString = String.fromCharCode.apply(null,decoded);
       	 
       	 return decodedString;
       	 
        },
        
        
        logout: function(){
			// debugger;
            var self = this;
//            console.log();
            var userId=ecm.model.desktop.userId;
            var serverBase = _self.getConfigValue("upmParam").trim();
            var feedURL = serverBase + "/dms/logoutTime/?userId="+userId;
        	// debugger;
        	var buttonDisable=false;
        	var responseJSON="";
        	var xhrArgs = {            
					url: feedURL,                        
					handleAs: "json",
					sync: true,
					preventCache: true,
					headers: { "Content-Type": "application/json"},            
					load: function(data){
						
						responseJSON=data;
						
					} ,            
					error: function(error)            
					{            
						alert ("External services query failed due to " + error);    
	
					}            

			};
        	
        	var deferred = dojo.xhrPost(xhrArgs)
        	
        	
			if(responseJSON != ""){
//				console.log(responseJSON);
			}
        },
        
		loadHome: function(){
			_self.buttonArea.style.display="block";
			_self.gotohome.style.display="none";
			_self.destroyUploadArea();
			_self.destroySearchArea();
		},
		
		
		
		
		
		///nitinsss code
		
		
		
		
		//method for buttononclick///
		
		//Method used to chnage the document status
		docDecision:function(connector,contentList,status,folderpath,obj)
		{
			

     	   var selectedItem = contentList.getSelectedItems();
     	   var statusvalue=status;
     	  var k=selectedItem.length;
     	   for(var p=0; p<selectedItem.length;p++){
     		   
     		   
     		   var tempId = selectedItem[p].id;
                var newId = tempId.split(",");
                var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
                var requestParams = {};
                var childDocs = null;
                requestParams.repositoryId = repository.id;
                requestParams.repositoryType = repository.type;
                requestParams.propertyName = _self.getConfigValue("dMSDocumentStatusParam");
                requestParams.docId = newId[2];
                
                requestParams.propertyValue = statusvalue;
              
                
                
              
                lang.hitch(connector, Request.invokePluginService("DocumentManagement", "updateDocumentPropService", {
                    requestParams: requestParams,
                    requestCompleteCallback: function(response) {
                 // debugger;
//                        console.log(response);
                        
                    
                        if (k == selectedItem.length){
                        	
                        	if(response.error !=undefined ){
                            	alert("Document Action Failed due to-"+response.error);	k--;
                            }else if(response.Success !=undefined){
                            	alert("Document Action Successful-"+response.Success);	k--;
                            }
                        	
                        	
                        }
                        
                        
                        
                    }
                
                }))
     		   
     		   
     	   }
     	   
     	   _self.updateContentList1(_self.getConfigValue("repoIdParam"),  contentList,obj);
     	 
     	
     
			
			
			
		},
		
		
		performAction : function(repository, itemList, callback,
				teamspace, resultSet, parameterMap) {
				var item = itemList[0];
				if (!item.isFolder()) {
				var params = {
				repositoryId: item.repository.id,
				docid: item.id,
				template_name: item.template,
				disposition: "attachment",
				transform: "native",
				version: "released"
				};
				ecm.model.Request.setSecurityToken(params);
				var url = Request.getServiceRequestUrl("getDocument", item.repository.type, params);
				var download = "download"; 
				var iframe = dojo.io.iframe.create(download);
				dojo.io.iframe.setSrc(iframe, url, true);
				}},
		
		
		/////buttton creations/////////
		createSubmitButton: function(connector, dialog, status, contentList,folderpath){
			
			
			  var myDialog = new ConfirmDialog({
				  
                  title: "My ConfirmDialog",
                  content: "Do you want to Submit the selected documents ?",
                  buttonCancel: "Label of cancel button",
                  buttonOk: "Label of OK button",
                  style: "width: 300px",
                  onCancel: function(){
                      //Called when user has pressed the Dialog's cancel button, to notify container.
                  	
                  },
                  onExecute: function(){
                  	
                	  _self.docDecision(connector,contentList,"Submit",folderpath);
                  
                  }
              
              });
             
			
			
			var statusButton = new dijit.form.Button({
                "label": "Submit",
                "style": "float:right; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
                "onClick": function() {
                    // debugger;
                    
                  
                
                 
                    myDialog.show();
                }
            }).placeAt(contentList.topContainer);
			statusButton.startup();
			myDialog.hide();
		},
		
		
		
		createReApprovalButton: function(connector, dialog, status, contentList,folderpath){
			
			  var myDialog = new ConfirmDialog({
                  title: "My ConfirmDialog",
                  content: "Do you want to Reapprove the selected documents ?",
                  buttonCancel: "Label of cancel button",
                  buttonOk: "Label of OK button",
                  style: "width: 300px",
                  onCancel: function(){
                      
               },
                  onExecute: function(){
                	  _self.docDecision(connector,contentList,"Approve",folderpath);
                  
                  }
              
              });
			
			var statusButton = new dijit.form.Button({
                "label": "Reapprove",
                "style": "float:right; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
                "onClick": function() {
                	
                	myDialog.show();
                }
            }).placeAt(contentList.topContainer);
			statusButton.startup();
		},
		
		
		
		
		
		///
		createRejectionButton:function(connector, dialog, status, contentList,folderpath){
			 var myDialog = new ConfirmDialog({
                 title: "My ConfirmDialog",
                 content: "Do you want to Reject the selected documents ?",
                 buttonCancel: "Label of cancel button",
                 buttonOk: "Label of OK button",
                 style: "width: 300px",
                 onCancel: function(){
                     
              },
                 onExecute: function(){
                 	
                	 _self.docDecision(connector,contentList,"Reject",folderpath);
                 }
             
             });
			
			var statusButton = new dijit.form.Button({
                "label": "Reject",
                "style": "float:right; padding-right: 20px;padding-top: 12px;padding-bottom: 10px",
                "onClick": function() {
                	myDialog.show();
                	
                }
            }).placeAt(contentList.topContainer);
			statusButton.startup();
		
			
			
		},
		
		createApprovalButton: function(connector, dialog, status, contentList,folderpath){
			
			 var myDialog = new ConfirmDialog({
                 title: "My ConfirmDialog",
                 content: "Do you want to Approve the selected documents ?",
                 buttonCancel: "Label of cancel button",
                 buttonOk: "Label of OK button",
                 style: "width: 300px",
                 onCancel: function(){
                     //Called when user has pressed the Dialog's cancel button, to notify container.
              },
                 onExecute: function(){
                	 _self.docDecision(connector,contentList,"Approve",folderpath);
                 
                	 
                 }
             
             });
			
			var statusButton = new dijit.form.Button({
                "label": "Approve",
                "style": "float:right; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
                "onClick": function() {
                	myDialog.show();

                }
            }).placeAt(contentList.topContainer);
			statusButton.startup();
		},
		
		createDownloadButton: function(connector, contentList){
			
			
			  var myDialog = new ConfirmDialog({
				  
              title: "My ConfirmDialog",
              content: "Do you want to Download the selected documents ?",
              buttonCancel: "Label of cancel button",
              buttonOk: "Label of OK button",
              style: "width: 300px",
              onCancel: function(){
                  //Called when user has pressed the Dialog's cancel button, to notify container.
              	
              },
              onExecute: function(){
            	 /* var url =  "http://192.168.1.54:9081/navigator/jaxrs/p8/getDocument?desktop=HRDMS";
            	  _self.postdata.data = {};
            	  
            	  
            	  var xhr = {
            			  url: url,
            		      postData: dojo.toJson(_self.data),
            		      handleAs: "json",
            		      headers: { "Content-Type": "application/json"},
            		      sync : true,
            		      load: function(data){
            		    	  
            		    	  
            		    	  
            		    	  _searchSelf.empList = data.EmployeeList;
            		    	  if(_searchSelf.empList.length > 0)
            		    	  searchcriteria = "relation";
            		      },
            		      error: function(error){
            		        // We'll 404 in the demo, but that's okay.  We don't have a 'postIt' service on the
            		        // docs server.
            		       alert(error);
            		      }
            			  
            	  }*/
            	  
//            	  dialogResult.destroy();
            	  // debugger;
            	  var selectedItem = contentList.getSelectedItems();
            	  var k=selectedItem.length;
            	  var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
//            	  CommonActionsHandler  
            	  var resultSet = "";
            	  commonActionsHandler=Desktop.getActionsHandler();
//            	  console.log(commonActionsHandler);
            	  var objstr = repository.objectStore.id;
           
            	  
            	  /*commonActionsHandler.actionDownload(repository, selectedItem, null, null, 
	                		resultSet, null, null,selectedItem[0].vsId);
            	  
            	  console.log(resultSet);*/
            	 /* for (var i = 0; i<selectedItem.length;i++){
            		  var doc = [selectedItem[i]];
            		  if(!_self.downloadAllowed){
            			  alert("You don't have roghts to Download this Document");
            			  break;
            		  }
            		  else{
            			  commonActionsHandler.actionDownloadAll(repository, doc, function(item){
                			  // debugger;
                			  console.log(items);
                		  }, null, 
      	                		resultSet, null, null,doc.vsId);
            		  }
            		  
            		  
            	  }*/
            	  
            	  commonActionsHandler.actionDownloadAll(repository, selectedItem, function(item){
        			  // debugger;
//        			  console.log(items);
        		  }, null, 
	                		resultSet, null, null,null);
            	  
            	  
            	  
            	  
					//var docItems =[items];
	                /*commonActionsHandler.actionDownload(repository, selectedItem, callback, teamspace, 
	                		resultSet, parameterMap, null,selectedItem[i].vsId);*/
							
	                //commonActionsHandler.actionRefreshGrid(repository, itemList, callback, teamspace, resultSet);
				
	                
            	  /*for (var i = 0; i<selectedItem.length;i++){
            		  var docId =  selectedItem[i].id.split(",")[2];
            			var def = repository.retrieveItem(docId, dojo.hitch(connector, function(items)
            					{ 
            						
            						var docItems =[items];
            		                commonActionsHandler.actionDownload(repository, docItems, callback, teamspace, 
            		                		resultSet, parameterMap, null,selectedItem[i].vsId);
            								
            		                //commonActionsHandler.actionRefreshGrid(repository, itemList, callback, teamspace, resultSet);
            					}),selectedItem[i].template_name,selectedItem[i].version,selectedItem[i].vsId,selectedItem[i].icnRepid);
            	  }*/
					
//					dialogResult.destroyRecursive(); 
					 
				
              	
//            	  _self.docDecision(connector,contentList,"Submit",folderpath);
              
              }
          
          });
         
			
			
			var statusButton = new dijit.form.Button({
            "label": "Download",
            "style": "float:right; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
            "onClick": function() {
                // debugger;
                
              
            
             
                myDialog.show();
            }
        }).placeAt(contentList.topContainer);
			statusButton.startup();
			myDialog.hide();
		},
		
		
		
		
		getContentListModules: function() {
            var viewModules = [];
            viewModules.push(ViewDetail);
            viewModules.push(RowContextMenu);
            

            var array = [];
            /*array.push(DocInfo);*/
            array.push({
                moduleClass: Bar,
                top: [
                    [
                        [
                        ]
                    ],
                    [
                        [{
                            moduleClass: Breadcrumb
                        }]
                    ]
                ]
            });
            return array;
        },
        
        getContentListModulesForSearch: function() {
            var viewModules = [];
            viewModules.push(RowContextMenu);
           
            var array = [];
            /*array.push(DocInfo);*/
            array.push({
    			moduleClass: Bar,
    			top: [
    					[
    						[
    							{
    								moduleClass: Toolbar
    								
    							},
    							{
    								moduleClass: FilterData,
    								"className": "BarFilterData"
    							},
    							{
    								moduleClasses: viewModules,
    								"className": "BarViewModules"
    							}
    						]
    					],
    					[
    						[
    							{
    								moduleClass: Breadcrumb,
    							}
    						]
    					],
    					[
    						[
    							{
    								moduleClass: InlineMessage
    							}
    						]
    					]
    				],
    			bottom: [
    						[
    							[
    								{
    									moduleClass: TotalCount
    								}
    							]
    						]
    					]
    		});
            return array;
        },
        _loadContextMenu: function(menu, selectedComments, grid, cell) {
            var def = new Deferred();
            

            def.resolve(menu);

            return def;
        },
        
      
      
        
        
        
        //nitin's code
        
        updateContentList1: function(repotId,  contentList,obj) {
            // Get the default repository object from the desktop
            // debugger;
            var repository = Desktop.getRepository(repotId);
            // Retrieve the root folder
            
            
            
            	_self.LoadDocs(contentList,obj);
            	
            
            
            
        },
        

        //nitinss code
        LoadDocs:function(contentList,obj){
        	
        var id=contentList.class;
        var capturerfilter="";
        	var docStatus="";
        	var columns;
        	// debugger;
             var requestParams = {}; 
             
             if(contentList.dojoAttachPoint=="contentListDraftEmployee" ||contentList.dojoAttachPoint=="contentListSubmitEmployee" || contentList.dojoAttachPoint=="contentListRejectedEmployee")
          	{
              query="SELECT * FROM "+_self.getConfigValue("employeeDocClassParam")+" WHERE ";
              columns = [_self.getConfigValue("employeeIdParam"),"DocumentTitle",_self.getConfigValue("documentSubTypeParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"), "ContentSize","LastModifier", "Creator","DateCreated"];
          	}//Added on 09/04 at 8:27AM
             else  if(contentList.dojoAttachPoint=="contentListDraftHrInternal" ||contentList.dojoAttachPoint=="contentListSubmitHrInternal" || contentList.dojoAttachPoint=="contentListRejectedHrInternal")
          	{
                query="SELECT * FROM "+_self.getConfigValue("HrDocClassParam")+" WHERE ";
                columns = [_self.getConfigValue("referenceIdParam"),"DocumentTitle",_self.getConfigValue("documentSubTypeParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"), "ContentSize","LastModifier", "Creator","DateCreated"];
            	}
             /*if(contentList.dojoAttachPoint=="contentListDraftCreditCard" ||contentList.dojoAttachPoint=="contentListSubmitCreditCard" || contentList.dojoAttachPoint=="contentListRejectedCreditCard")
         	{
             query="SELECT * FROM "+_self.getConfigValue("creditCardDocClassParam")+" WHERE ";
             columns = [_self.getConfigValue("creditCardNumberParam"),_self.getConfigValue("nICNumberParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"),_self.getConfigValue("boxNumberParam"),_self.getConfigValue("customerIdParam"),"DocumentTitle", "ContentSize","LastModifier", "Creator"];
         	}
             else if(contentList.dojoAttachPoint=="contentListDraftMandates" ||contentList.dojoAttachPoint=="contentListSubmitMandates" ||contentList.dojoAttachPoint=="contentListRejectedMandates"){
             query="SELECT * FROM "+_self.getConfigValue("mendateDocClassParam")+" WHERE ";
             columns = [_self.getConfigValue("accountNumberParam"),_self.getConfigValue("nICNumberParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"),_self.getConfigValue("boxNumberParam"),_self.getConfigValue("customerIdParam"),"DocumentTitle", "ContentSize","LastModifier", "Creator"];
             }
             else {
            	 query="SELECT * FROM "+_self.getConfigValue("reportDocClassParam")+" WHERE ";
            	 columns = [_self.getConfigValue("accountNumberParam"),_self.getConfigValue("nICNumberParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"),_self.getConfigValue("boxNumberParam"),_self.getConfigValue("customerIdParam"),"DocumentTitle", "ContentSize","LastModifier", "Creator"];
             }*/
             
             
             
             if(id=="D")
            	 {
            	 docStatus='Draft';
            	 }else if(id=="S")
            		 {
            		 docStatus='Submit';	 
            		 }else{
            			 docStatus='Reject';
            		 }
             
             
             if (id == "D" || id == "R")
         	{
         	 capturerfilter=" (  "+_self.getConfigValue("dMSCapturerSecurityParam")+" = "+parseInt(_self.workerclass.innerText)+" ) AND ";
         	}else{
         		 capturerfilter=" (  "+_self.getConfigValue("dMSCapturerSecurityParam")+" < "+parseInt(_self.workerclass.innerText)+" ) AND ";
         	}
                     
            var statusFilter= "( "+_self.getConfigValue("dMSDocumentStatusParam")+"="+"'"+docStatus+"'"+" OR "+_self.getConfigValue("dMSDocumentStatusParam")+ " is null ) AND ";
          //   var statusFilter= "( "+_self.getConfigValue("dMSDocumentStatusParam")+"='Draft'"+" OR "+_self.getConfigValue("dMSDocumentStatusParam")+ " is null ) AND "; 
            
           //  var brancFilter= _self.getConfigValue("preferedBranchCodeParam")+" in ('000','"+_self.branchno.innerText+"' ) AND ";
            var brancFilter= _self.getConfigValue("dMSCapturerBranchParam")+" in ('"+_self.branchno.innerText+"' )  ";
         //    var accessLevelFilter= " ( "+_self.getConfigValue("securityLevelParam")+" = 00 OR "+_self.getConfigValue("securityLevelParam")+" <= "+parseInt(_self.workerclass.innerText)+" ) ";
             query=query+statusFilter+capturerfilter+brancFilter;
            
          
           	 
            	
            	 
            	
             var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
             var requestParams = {};
             requestParams.repositoryId = repository.id;
             requestParams.repositoryType = repository.type;
         

             requestParams.query = query;
             /**converted to model API**/
             var queryParams = {};
             queryParams.pageSize = 100;
             queryParams.query = query;            
             queryParams.retrieveAllVersions = false;
             queryParams.retrieveLatestVersion = true;
             queryParams.repository = repository;
             
             queryParams.resultsDisplay = {
                     //made sortAsc false for descending data added column as per the SRS-Nitin
            		 "sortBy": "LastModifier","sortAsc": false,
                    "columns": columns,//[_self.getConfigValue("creditCardNumberParam"),_self.getConfigValue("nICNumberParam"),_self.getConfigValue("documentSubTypeParam"),_self.getConfigValue("preferedBranchCodeParam"),_self.getConfigValue("securityLevelParam"),_self.getConfigValue("boxNumberParam"),_self.getConfigValue("customerIdParam"),"DocumentTitle", "ContentSize","LastModifier", "Creator"],
                    "honorNameProperty": true};
        
                                            
             var searchQuery = new SearchQuery(queryParams);
             searchQuery.search(function(resultSet){
//                 console.debug("Search ResultSet::",resultSet);
               
                 contentList.setResultSet(resultSet);
             },null,null,null,function(error){
                 alert("Search error::"+error);
//                 console.debug("Search error::",error);                
             });
         
        	
        	
        	
        	
        	
        },
        
        
        
        
        
        
        //
        
        
      
        
     
        
       
        doesFolderExist: function(repository, folderPath) {
            // debugger;
            var res = new Deferred();
            var requestParams = {
                repositoryId: repository.id,
                objectStoreId: repository.objectStore.id || "",
                docid: folderPath,
                template_name: "Folder",
                version: ""
            };
            Request.invokeService("getContentItems", repository.type, requestParams, lang.hitch(this, function(response) {
                res.resolve(true);
            }), false, false, lang.hitch(this, function() {
                res.resolve(false);
            }));
            return res.promise;
        },
        
        addNewFolder: function(repository, path) {
            var res = new Deferred();

            var finalItem;
            repository.retrieveItem(path, lang.hitch(self, function(rootFolder) {
//                console.log("retrieving items is started for cretaing folder");
                //// debugger;
                var folderExist = _self.doesFolderExist(repository, path );
                folderExist.then(function(result) {
//                    console.log("Folder exists");
                    console.log(result);
                    if (result) {
                        // debugger;
                        repository.retrieveItem(path , lang.hitch(self, function(rootFolderOld) {
                            res.resolve(rootFolderOld);
                        }), null, null, null, null, null);

                    } 
                });
//                console.log("retrieving items is ended for cretaing folder");
            }), null, null, null, null, null);
            return res.promise;
        },

        
	});
});

