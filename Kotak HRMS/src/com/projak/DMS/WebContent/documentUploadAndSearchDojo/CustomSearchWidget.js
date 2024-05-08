/**
 * 
 */
define([
	"dojo/_base/declare",
	"dojo/_base/lang",
	"dijit/ConfirmDialog",
	"dojo/Deferred",
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
	"dijit/MenuItem",
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
	"ecm/widget/listView/gridModules/RowContextMenuLoadMenu",
	"ecm/widget/ActionMenu",
	"ecm/model/Request",
	"ecm/model/Desktop",
	"ecm/model/ContentItem",
	"ecm/model/Item",
	"ecm/model/SearchQuery",
	"dijit/form/Form",
	"ecm/widget/layout/CommonActionsHandler",
	"ecm/widget/dialog/AddContentItemDialog",
	"dojo/text!./templates/CustomSearch.html",
	"dojo/_base/json",
	"dojo/request/xhr",
	"dojox/encoding/crypto/SimpleAES",
	"dojox/encoding/base64"
],
function(declare,
		lang,
		ConfirmDialog,
		Deferred,
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
        MenuItem,
      
        _LaunchBarPane,
        _TemplatedMixin,
        _WidgetsInTemplateMixin,
        ResultSet,
        _LaunchBarPane,
        _RepositorySelectorMixin, 
        ContentList, 
        RowContextMenu, 
        Toolbar, 
        DocInfo, 
        DndRowMoveCopy,
        DndFromDesktopAddDoc,
        Bar, 
        ViewDetail,
        ViewMagazine, 
        ViewFilmStrip, 
        Breadcrumb,
        RowContextMenuLoadMenu,
        ActionMenu,
        Request,
        Desktop, 
        ContentItem, 
        Item,
        SearchQuery,
        Form,
        ComminActionHandler,
        AddContentItemDialog,
		CustomSearchTemplate,
		dojo,
		xhr,
		SimpleAES,
		base64) {
	/**
	 * @name uploadSearchDocumentPluginDojo.UploadSearchDocumentFeature
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	return declare("documentUploadAndSearchDojo.CustomSearchWidget", [
		_LaunchBarPane, _TemplatedMixin, _WidgetsInTemplateMixin
	], {
		/** @lends uploadSearchDocumentPluginDojo.UploadSearchDocumentFeature.prototype */

		templateString: CustomSearchTemplate,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,
		_searchSelf:null,
		resultSetTemp:null,
    
		postCreate: function() {
			
			console.log("Post Created Executed");
			////debugger;;
			_searchSelf = this;
			resultSetTemp = _searchSelf.searchcontentList.getResultSet();
			this.inherited(arguments);
			console.log(_searchSelf);
			console.log("TEST\n\n");
			 var menu =  new ActionMenu();//.getMenu("DMSdownloaddocumentmeny");
			 console.log(menu);
			this.searchcontentList.setContentListModules(_self.getContentListModulesForSearch());
			/*this.searchcontentList.setGridExtensionModules([
			{
				moduleClass: RowContextMenuLoadMenu,
//			    loadMenu: lang.hitch(this, this._loadContextMenu(menu, null, grid, cell))
			}]);*/
            this.searchcontentList.startup();
            
			var someData=[];
			
			_searchSelf.doctype = _self.doctype;
			for(var i=0;i<_searchSelf.doctype.length;i++ ){
				someData.push({name:_searchSelf.doctype[i], id: _searchSelf.doctype[i] })
				
			}
			
            var stateStore = new Memory({
                data: someData
        });
           
            
            this.documenttype.store = stateStore;
            this.documenttype.onChange = function(e) {
                console.log(e);
                //debugger;;
                someData1 = [];
                _searchSelf.documentsubtype.reset();
                _searchSelf.docsubtype  = _self.responseJSON[e];
                for(var i=0;i<_searchSelf.docsubtype.length;i++ ){
    				someData1.push({name:_searchSelf.docsubtype[i], id: _searchSelf.docsubtype[i] })
    				
    			}
    			
                var stateStore1 = new Memory({
                    data: someData1
            });
               
                
                _searchSelf.documentsubtype.store = stateStore1;
                _searchSelf.documentsubtype.onChange = function(e) {
                	
                }
                //alert(e);
            };
            
            _searchSelf.allDocSubTypes = [];
            
			for(var i=0;i<_searchSelf.doctype.length;i++ ){
				var doctype = _searchSelf.doctype[i];
				var tempdocsubtypes = _self.responseJSON[doctype];
				for(var j= 0;j<tempdocsubtypes.length;j++){
					_searchSelf.allDocSubTypes.push(tempdocsubtypes[j]);
				}
				
			}
			
			
			//_searchSelf.downloadButton = _searchSelf.createDownloadButton(_searchSelf,_searchSelf.searchcontentList);  By bhavik 15-11-2019
			if (_self.downloadAllowed) // changed by bhavik
   		 {
//   			domStyle.set(_self.uploadButton.domNode, 'display', 'none');
//   			_self.uploadSectionButton.children[0].children[0].hidden=true;
//				_searchSelf.downloadButton.setDisabled(true);
				_searchSelf.createDownloadButton(_searchSelf,_searchSelf.searchcontentList);
   			
   		 }
			
			this.logEntry("postCreate");
			this.logExit("postCreate");
		},
		
		openDocument: function(connector, contentList){
			
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
          	  
//          	  dialogResult.destroy();
          	  //debugger;;
          	  var selectedItem = contentList.getSelectedItems();
          	  var k=selectedItem.length;
          	  var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
//          	  CommonActionsHandler  
          	  var resultSet = "";
          	  commonActionsHandler=Desktop.getActionsHandler();
          	  console.log(commonActionsHandler);
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
              			  //debugger;;
              			  console.log(items);
              		  }, null, 
    	                		resultSet, null, null,doc.vsId);
          		  }
          		  
          		  
          	  }*/
          	  
          	  commonActionsHandler.actionDownloadAll(repository, selectedItem, function(item){
      			  //debugger;;
      			  console.log(items);
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
					 
				
            	
//          	  _self.docDecision(connector,contentList,"Submit",folderpath);
            
            }
        
        });
       
			
			
			var statusButton = new dijit.form.Button({
          "label": "Download",
          "style": "float:right; padding-right: 30px;padding-top: 12px;padding-bottom: 10px",
          "onClick": function() {
              //debugger;;
              
            
          
           
              myDialog.show();
          }
      }).placeAt(contentList.topContainer);
			statusButton.startup();
			myDialog.hide();
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
         
		 
	        
	        
	        
         runSearch: function() {
        	 //debugger;;
             var requestParams = {};
             var searchcriteria ="";
             var searchEmployeeId = _searchSelf.searchEmployeeId.getValue();
             var employeeId = _self.loginempid.innerText;
             var lob = _searchSelf.LOB.getValue();
             var loc = _searchSelf.LOC.getValue();
             var cc = _searchSelf.CC.getValue();
             var doj = _searchSelf.DOJ.getValue();
             var dogj = _searchSelf.DOGJ.getValue();
             var lastDate = _searchSelf.lastDate.getValue();
             var documenttype = _searchSelf.documenttype.getValue();
             var documentsubtype = _searchSelf.documentsubtype.getValue();
             var active = _searchSelf.active.checked;
             var inactive = _searchSelf.inactive.checked;
             var all = _searchSelf.all.checked;
             var key = _self.getConfigValue("AESKey");
            
//             var doj = _searchSelf.DOJ.getValue();
             
             
//             var encodedSearchEmployeeId1 = SimpleAES.encrypt(searchEmployeeId,key);
//             var encodedSearchEmployeeId = _searchSelf.encoding(encodedSearchEmployeeId1);
             
//             var encodedEmployeeId1 = SimpleAES.encrypt(employeeId,key);
//             var encodedEmployeeId = _searchSelf.encoding(encodedEmployeeId1);
             
//             var temp2 = _searchSelf.encoding(employeeId);
//             var encodedEmployeeId = _searchSelf.encoding(temp2);
             
             query="SELECT * FROM DMS_HRDocuments WHERE ";
            
//             var CTCFilter = _self.getConfigValue("documentTypeParam")+" <>'Compensation' AND";
//             
//             var alldocsubtypes="";
//             if(documenttype === null || documenttype === ""){
//             for(var i = 0;i< _searchSelf.allDocSubTypes.length;i++){
//            	 if ( _searchSelf.allDocSubTypes[i] != "" && _searchSelf.allDocSubTypes[i] != null) {
//            		 alldocsubtypes = alldocsubtypes + "'" +_searchSelf.allDocSubTypes[i] + "',";
//                 } 
//             }
//             alldocsubtypes = alldocsubtypes.substring(0, alldocsubtypes.length-1);
//             query = query +_self.getConfigValue("documentSubTypeParam")+"  in (" +  alldocsubtypes + ") AND ";
//             }
//             
//             if (documentsubtype != null && documentsubtype != ""){
//                 query = query +_self.getConfigValue("documentSubTypeParam")+"  = (" + "'" + documentsubtype + "'" + ") AND ";
//             }
             
//             if (documenttype != null && documenttype != "" && (documentsubtype == null || documentsubtype == "")){
//                 query = query +_self.getConfigValue("documentTypeParam")+"  = (" + "'" + documenttype + "'" + ") AND";
//             }
//             
//             var docsubTypeSearch = ""
//             if (documenttype != null && documenttype != "" && (documentsubtype == null || documentsubtype == "")){
//                query = query +_self.getConfigValue("documentTypeParam")+"  = (" + "'" + documenttype + "'" + ") AND ";
//                for(var i = 0;i<  _searchSelf.docsubtype.length;i++){
//                	if ( _searchSelf.docsubtype[i] != "" && _searchSelf.docsubtype[i] != null) {
//                		docsubTypeSearch = docsubTypeSearch + "'" +_searchSelf.docsubtype[i] + "',";
//                	}  
//                }
//                query = query +_self.getConfigValue("documentSubTypeParam")+"  in (" +  docsubTypeSearch + ") AND ";  
//            }
             
//             var serverBase = window.location.protocol + "\/\/" + window.location.host;
//             var serverBase = "http://192.168.1.132:8080";
             var serverBase = _self.getConfigValue("upmParam").trim();
             var feedURL = "";
             //var statusFilter= "( "+_self.getConfigValue("dMSDocumentStatusParam")+"='Approve' OR "+_self.getConfigValue("dMSDocumentStatusParam")+ " is null ) AND ";
             //var brancFilter= _self.getConfigValue("preferedBranchCodeParam")+" in ('000','"+_self.branchno.innerText+"' ) AND ";
             
             
             
             if(searchEmployeeId!=null && searchEmployeeId != ""){
            	 
            	 if(searchEmployeeId===employeeId){
            		 alert("Can't Search Own details");
            		 _searchSelf.searchEmployeeId.reset();
            	 }
            	 else{
            	var url = "/dms/searchWithEmployeeID/?loggedInUserID="+employeeId+"&&searchUserId="+searchEmployeeId;
            	feedURL = serverBase+url;
            	_searchSelf.responseJSON="";
            	var xhrArgs = {            
    					url: feedURL,                        
    					handleAs: "json",
    					sync: true,
    					preventCache: true,
    					headers: { "Content-Type": "application/json"},            
    					load: function(data){
    						
//    						_searchSelf.responseJSON=data.Is_HR;
//    						var temp = data.message;
//    						var temp2 = _searchSelf.decoding(temp);
    						_searchSelf.responseJSON = data.message;
    						searchcriteria = "empid";
    					} ,            
    					error: function(error)            
    					{            
    						console.log("External services query failed due to " + error); 
    	
    					}            

    			};
            	
            
            		  // Call the asynchronous xhrGet
            		  var deferred = dojo.xhrGet(xhrArgs);
            		  
            		  if (_searchSelf.responseJSON === "true" ) {
                          query = query + " "+_self.getConfigValue("employeeIdParam")+" = '" + searchEmployeeId + "' AND ";
                      }else {
                    	  alert ("User Not Accessible");
                    	  searchcriteria ="";
                      }
             }	  
             }
//             else if ((lob!=null&&lob !="")&&(loc!=null&&loc !="")&&(cc!=null&&cc !="")){
//            	 
//            	 feedURL = serverBase + "/dms/searchWithRelation";
//            	 _searchSelf.data = {};
//            	 _searchSelf.data.employeeCode = employeeId;
//            	 if (lob!=null&&lob !=""){
//            		 _searchSelf.data.lob = _searchSelf.LOB.getValue();
//            	 }
//            	 if (loc!=null&&loc !=""){
//            		 _searchSelf.data.loc = _searchSelf.LOC.getValue();
//            	 }
//            	 if (cc!=null&&cc !=""){
//            		 _searchSelf.data.cc = _searchSelf.CC.getValue();
//            	 }
//            	 
//            	 if (doj!=null&&doj !=""){
//            		 _searchSelf.data.dateOfJoining = doj;
//            	 }
//            	 if (dogj!=null&&dogj !=""){
//            		 _searchSelf.data.dateOfGroupJoining = dogj;
//            	 }
//            	 if (lastDate!=null&&lastDate !=""){
//            		 _searchSelf.data.lastDate = lastDate;
//            	 }
//            	 
//            	 if(documenttype!=null&&documenttype !=""){
//                	 var doctypeFilter = _self.getConfigValue("documentTypeParam") +" in ('"+_searchSelf.documenttype+"' ) AND ";
//                	 query = query + doctypeFilter;
//                 }
//                 if(documenttype!=null&&documenttype !=""){
//                	 var docsubtypeFilter = _self.getConfigValue("documentSubTypeParam") + " in ('"+_searchSelf.documentsubtype+"' ) AND ";
//                	 query = query + docsubtypeFilter;
//                 }
//                 var radioValue= "";
//                 if(active){
//                	 radioValue = _searchSelf.active.value;
//                 }
//                 else if(inactive){
//                	 radioValue = _searchSelf.inactive.value;
//                 }
//                 else if(all){
//                	 radioValue = _searchSelf.all.value;
//                 }
//                 if (radioValue!=""){
//                	 _searchSelf.data.status = radioValue;
//            	 }
//                 
//                 
//            	 
//            	 
//            	 var xhrArgs = { 
//            		      url: feedURL,
//            		      postData: dojo.toJson(_searchSelf.data),
//            		      handleAs: "json",
//            		      headers: { "Content-Type": "application/json"},
//            		      sync : true,
//            		      load: function(data){
//            		    	  _searchSelf.empList = data.EmployeeList;
//            		    	  if(_searchSelf.empList.length > 0)
//            		    	  searchcriteria = "relation";
//            		      },
//            		      error: function(error){
//            		        // We'll 404 in the demo, but that's okay.  We don't have a 'postIt' service on the
//            		        // docs server.
//            		       alert(error);
//            		      }
//            		    }
//            		    
//            		    // Call the asynchronous xhrPost
//            		    var deferred = dojo.xhrPost(xhrArgs);
//            	 var emp="";
//                 if(_searchSelf.empList !=null&&_searchSelf.empList!=""){
//                 for(var i = 0;i< _searchSelf.empList.length;i++){
//                	 if ( _searchSelf.empList[i] != "" && _searchSelf.empList[i] != null && _searchSelf.empList[i] !== employeeId) {
//                         emp = emp + "'" +_searchSelf.empList[i] + "',";
//                     } 
//                 }
//                 emp = emp.substring(0, emp.length-1);
//                 query = query + " "+_self.getConfigValue("employeeIdParam")+"  in (" +  emp + ") AND ";
//                 }  
//                 
//                 /*xhr(feedURL, {
//                	 	data : dojo.toJson(_searchSelf.data),
//                	 	headers: { "Content-Type": "application/json"},
//                	    handleAs: "json",
//                	    sync : true
//                	  }).then(function(data){
//                		  _searchSelf.empList = data.EmployeeList;
//                		  console.log("INside POst callback");
//                	    // Do something with the handled data
//                	  }, function(err){
//                	    // Handle the error condition
//                		  alert(error);
//                	  }, function(evt){
//                	    // Handle a progress event from the request if the
//                	    // browser supports XHR2
//                	  });*/
//            	 
//            	 
//            	 
//             } 
//             else if(doj!=null&&doj !=""){
//            	 
//            	 feedURL = serverBase + "/dms/searchWithRelation";
//            	 
//            	 _searchSelf.data = {};
//            	 _searchSelf.data.employeeCode = employeeId;
//            	 if (doj!=null&&doj !=""){
//            		 _searchSelf.data.dateOfJoining = doj;
//            	}
//            	 
//            	 var xhrArgs = { 
//           		      url: feedURL,
//           		      postData: dojo.toJson(_searchSelf.data),
//           		      handleAs: "json",
//           		      headers: { "Content-Type": "application/json"},
//           		      sync : true,
//           		      load: function(data){
//           		    	  _searchSelf.empList = data.EmployeeList;
//           		    	if(_searchSelf.empList.length > 0)
//          		    	  searchcriteria = "DOJ";
//           		      },
//           		      error: function(error){
//           		        // We'll 404 in the demo, but that's okay.  We don't have a 'postIt' service on the
//           		        // docs server.
//           		       alert(error);
//           		      }
//           		    }
//           		    
//           		    // Call the asynchronous xhrPost
//           		    var deferred = dojo.xhrPost(xhrArgs);
//           	 var emp="";
//                if(_searchSelf.empList !=null&&_searchSelf.empList!=""){
//                for(var i = 0;i< _searchSelf.empList.length;i++){
//               	 if ( _searchSelf.empList[i] != "" && _searchSelf.empList[i] != null && _searchSelf.empList[i] !== employeeId) {
//                        emp = emp + "'" +_searchSelf.empList[i] + "',";
//                    } 
//                }
//                emp = emp.substring(0, emp.length-1);
//                query = query + " "+_self.getConfigValue("employeeIdParam")+"  in (" +  emp + ") AND ";
//                }  
//            	 
//             }
             
             
             //debugger;;
             
             
             
            if(searchcriteria!=""){
            	var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
                var requestParams = {};
                requestParams.repositoryId = repository.id;
                requestParams.repositoryType = repository.type;
                if (!_self.CTCAllowed) {
                    query = query + CTCFilter;
                }
                
                query=query.substring(0, query.length - 4);
                requestParams.query = query;
                /**converted to model API**/
                var queryParams = {};
                queryParams.pageSize = 100;
                queryParams.query = query;            
                queryParams.retrieveAllVersions = false;
                queryParams.retrieveLatestVersion = true;
                queryParams.repository = repository;
                queryParams.resultsDisplay = {
               		//made sortAsc false for descending data and added column as per the SRS-Nitin
                        "sortBy": "LastModifier","sortAsc": false,
                       "columns": [_self.getConfigValue("employeeIdParam"),"DocumentTitle",_self.getConfigValue("documentTypeParam"),_self.getConfigValue("documentSubTypeParam"), "ContentSize","LastModifier", "Creator","DateCreated"],
                       "honorNameProperty": true};
                         
                     
                
                
                var searchQuery = new SearchQuery(queryParams);
                
               	 searchQuery.search(function(resultSet){
                        console.debug("Search ResultSet::",resultSet);
//                        _searchSelf.checkSearchAccountNumber();
                        
                        for(var i=0;i<resultSet.totalCount;i++)
                       	 {
                       	 
                       	 
                       	
                       	 /*if(_searchSelf.accstat){
                       		 resultSet.items[i].privileges=0;
                       	 }
                       	 if( (resultSet.items[i].attributes.DMS_SecurityLevel) > (_self.workerclass.innerText) || ('200'>_self.workerclass.innerText)){
                       	 resultSet.items[i].privileges=0;}*/
                       	 }
                        if(resultSetTemp === null){
                        	resultSetTemp = resultSet;
                        }else{
                        	resultSetTemp.append(resultSet);
                        }
                        _searchSelf.searchcontentList.setResultSet(resultSetTemp);
                    },null,null,null,function(error){
                        alert("Search error::"+error);
                        console.debug("Search error::",error);                
                    }); 
            } else{
            	alert ("Please enter valid Search Criteria ");
            	
            } 
             
             
         },
         
         clearSearch : function(){
        	//debugger;;
        	var requestParams = {};
            var searchcriteria ="";
            _searchSelf.searchEmployeeId.reset();
//            var employeeId = _self.loginempid.innerText;
            _searchSelf.LOB.reset();
            _searchSelf.LOC.reset();
            _searchSelf.CC.reset();
            _searchSelf.DOJ.reset();
            _searchSelf.DOGJ.reset();
            _searchSelf.lastDate.reset();
            _searchSelf.documenttype.reset();
            _searchSelf.documentsubtype.reset();
            _searchSelf.active.reset();
            _searchSelf.inactive.reset();
            _searchSelf.all.reset();
            this.searchcontentList.setResultSet(null);
        	
        	 
        	 
         },
         
         encoding : function(text){
        	 //debugger;;
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
        	 //debugger;;
        	 
        	 var decoded = base64.decode(text)
        	 var decodedString = String.fromCharCode.apply(null,decoded);
        	 
        	 return decodedString;
        	 
         }
          
         
	});
});
