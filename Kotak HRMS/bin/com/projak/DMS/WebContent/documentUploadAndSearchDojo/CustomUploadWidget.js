/**
 * 
 */
define([
	"dojo/_base/declare",
	"dojo/_base/lang",
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
	"ecm/model/ContentClass",
	"ecm/model/SearchQuery",
	"dijit/form/Form",
	"ecm/widget/layout/CommonActionsHandler",
	"ecm/widget/dialog/AddContentItemDialog",
	"dojo/text!./templates/CustomContent.html"
],
function(declare,
		lang,
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
        ContentClass,
        SearchQuery,
        Form,
        ComminActionHandler,
        AddContentItemDialog,
		mendatesTemplate) {
	/**
	 * @name uploadSearchDocumentPluginDojo.UploadSearchDocumentFeature
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	return declare("documentUploadAndSearchDojo.CustomUploadWidget", [
		_LaunchBarPane, _TemplatedMixin, _WidgetsInTemplateMixin
	], {
		

		templateString: mendatesTemplate,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,
		_uploadSelf:null,
    
		postCreate: function() {
			_uploadSelf = this;
			// debugger;
			console.log("MIMETYPE_Testing");
			this.inherited(arguments);
			console.log(_uploadSelf);
			
			var someData=[];
			
			_uploadSelf.doctype = _self.doctype;
			for(var i=0;i<_uploadSelf.doctype.length;i++ ){
				someData.push({name:_uploadSelf.doctype[i], id: _uploadSelf.doctype[i] })
				
			}
			
            var stateStore = new Memory({
                data: someData
        });
           
            
            this.documenttype.store = stateStore;
            this.documenttype.onChange = function(e) {
                console.log(e);
                // debugger;
                someData1 = [];
                _uploadSelf.documentsubtype.reset();
                _uploadSelf.docsubtype  = _self.responseJSON[e];
                for(var i=0;i<_uploadSelf.docsubtype.length;i++ ){
    				someData1.push({name:_uploadSelf.docsubtype[i], id: _uploadSelf.docsubtype[i] })
    				
    			}
    			
                var stateStore1 = new Memory({
                    data: someData1
            });
               
                
                _uploadSelf.documentsubtype.store = stateStore1;
                _uploadSelf.documentsubtype.onChange = function(e) {
                	
                }
                //alert(e);
            };
            
            
            _uploadSelf.columns = ["DocumentTitle", "ContentSize","LastModifier", "Creator"];
            
            
           

            //_self.updateContentList1(_self.getConfigValue("repoIdParam"),  this.contentUploadListDraft, _uploadSelf);
            
           
            // debugger;
           
            
            //_self.createSubmitButton(_uploadSelf, _uploadSelf.dialogOne,_uploadSelf.dialogStatus, this.contentUploadListDraft, _self.getConfigValue("employeeFolderParam"));
            //_self.createApprovalButton(_uploadSelf, _uploadSelf.dialogOne,_uploadSelf.dialogStatus,  _uploadSelf.contentListSubmitEmployee, _self.getConfigValue("employeeFolderParam"));
            //_self.createReApprovalButton(_uploadSelf, _uploadSelf.dialogOne,_uploadSelf.dialogStatus,  _uploadSelf.contentListRejectedMandates, _self.getConfigValue("employeeFolderParam"));
            //_self.createRejectionButton(_uploadSelf, _uploadSelf.dialogOne,_uploadSelf.dialogStatus,  _uploadSelf.contentListSubmitEmployee, _self.getConfigValue("employeeFolderParam"));    
           
            
            
            
            
            //made xhanges for validation issue no 4 
            _uploadSelf.employeeform.validate();
            this.logEntry("postCreate");
			this.logExit("postCreate");
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
		
		
	
		/*updateDraftMandates:function()
		{
			_self.updateContentList1(_self.getConfigValue("repoIdParam"),  this.contentUploadListDraft, _uploadSelf);
			 
			
		},*/
		
		
		
		/*updateSubmitMandates:function()
		{
			 
			
			 _self.updateContentList1(_self.getConfigValue("repoIdParam"), _self.getConfigValue("employeeFolderParam"), this.contentListSubmitEmployee, _uploadSelf);
				
			
			
			
			
			
		},
		
		
		
		updateRejectMandates:function()
		{
			
		
			 _self.updateContentList1(_self.getConfigValue("repoIdParam"), _self.getConfigValue("employeeFolderParam"), this.contentListRejectedEmployee, _uploadSelf);
			
		},*/
		
		
		
		
		browseEmployeeDocument: function() {
            // debugger;
			
            if (_uploadSelf.EmployeeId.getValue()!="" || _uploadSelf.EmployeeId.getValue()!=null) {     // Changes Made to Verify the acc no 
                var repository = Desktop.getRepository(_self.getConfigValue("repoIdParam"));
                var addContentItemDialog = new AddContentItemDialog();
               
                        
                        var finalItem = _self.addNewFolder(repository, _self.getConfigValue("rootFolderParam"));
                        finalItem.then(function(data) {
                            console.log(data);
                            // debugger;
                            repository.retrieveEntryTemplates(lang.hitch(_uploadSelf, function(response) {
                                console.log(repository);
                                
                                // debugger;
                                console.log(finalItem);
                                console.log("response!! ", response);
                                for (var int2 = 0; int2 < response.length; int2++) {
                                    if (response[int2].name == (this.documenttype.getValue()+" Entry Template")) {
                                        console.log("entryTemplate retrieved ? ", response[int2].isRetrieved);
                                        response[int2].retrieveEntryTemplate(lang.hitch(_uploadSelf, function(retrievedEntryTemp) {
                                            console.log("retrievedEntryTemp ", retrievedEntryTemp);
                                           
                                            console.log("this  ", this);
                                            // var p = 0;
                                            aspect.after(addContentItemDialog.addContentItemPropertiesPane, "onCompleteRendering", function() {
                                                // debugger;
                                                //console.log(addContentItemDialog);
                                                //console.log(addContentItemDialog.addContentItemPropertiesPane);

                                                // Setting the destination and lock it

                                                var folderSelector = addContentItemDialog.addContentItemGeneralPane.folderSelector;
                                                //folderSelector.setRoot(finalItem, repository.objectStore);
                                                folderSelector.setDisabled(true);
                                                          
                                                
                                                addContentItemDialog.addContentItemPropertiesPane.setPropertyValue(_self.getConfigValue("scannedUserParam"), _self.loginempid.innerText);
                                                addContentItemDialog.addContentItemPropertiesPane.setPropertyValue(_self.getConfigValue("scannedDateParam"), new Date());
                                                addContentItemDialog.addContentItemPropertiesPane.setPropertyValue(_self.getConfigValue("employeeIdParam"), _uploadSelf.EmployeeId.getValue());
                                                addContentItemDialog.addContentItemPropertiesPane.setPropertyValue(_self.getConfigValue("documentTypeParam"), _uploadSelf.documenttype.getValue());
                                                addContentItemDialog.addContentItemPropertiesPane.setPropertyValue(_self.getConfigValue("documentSubTypeParam"), _uploadSelf.documentsubtype.getValue());
                                                
                                                
                                            }, true);
                                            /*aspect.after(addContentItemDialog.prototype,"onAdd",function(){
                                            	alert("On Add");
                                            	return false;
                                            },true); */
                                            // debugger;
                                            /*addContentItemDialog.onAdd= function(){
                                            	
                                            	
                                            	var mime = addContentItemDialog.addContentItemGeneralPane.getFileInputFiles()[0].type;
                                            	if(mime=="image/jpg"||mime=="image/jpg"||mime=="image/tiff"||mime=="application/pdf"||mime=="image/jpeg"||mime=="image/tif"){
                                            		
                                            		return true;
                                            	} else {
                                            		alert("Please enter the file in the correct Format");
                                            	}
                                            }*/
                                            
                                            aspect.around(AddContentItemDialog.prototype, "onAdd", function advisor(original) {
                                                return function around() {

                                                    var files = addContentItemDialog.addContentItemGeneralPane.getFileInputFiles();
                                                    var containsInvalidFiles = dojo.some(files, function isInvalid(file) {
                                                        var fileName = file.name.toLowerCase();

                                                        var extensionOK = fileName.endsWith(".pdf") || fileName.endsWith(".jpg")||fileName.endsWith(".tiff")||fileName.endsWith(".pdf")||fileName.endsWith(".jpeg")||fileName.endsWith(".tif");
                                                        var fileSizeOK = file.size <= 10 * 1024 * 1024;

                                                        return !(extensionOK && fileSizeOK);
                                                    });

                                                    if (containsInvalidFiles) {
                                                        alert("pdf/jpeg/jpg/tiff files are only allowed for upload");
                                                    }else{
                                                        original.apply(this, arguments);
                                                    }

                                                }
                                            });
                                            
                                            addContentItemDialog.show(repository, null, true, false, function(callback) {
                                                //self.updateContentList(_self.getConfigValue("repoIdParam"), "DMS_CreditCardDocuments", self.contentList, self.structureCreditCard);
                                            	//_self.updateContentList(_self.getConfigValue("repoIdParam"), _self.getConfigValue("employeeFolderParam"), _uploadSelf.contentListMandate);
                                            	 //_self.updateContentList1(_self.getConfigValue("repoIdParam"),  _uploadSelf.contentUploadListDraft,_uploadSelf);
                                            	
                                                _uploadSelf.documenttype.reset();
                                                _uploadSelf.documentsubtype.reset();
                                                _uploadSelf.EmployeeId.reset();
                                            }, null, true, retrievedEntryTemp, false);
                                        }));
                                    }
                                }
                            }), "Document", null, null, repository.objectStore);
                        });
                        
                   /* }
                
                }))*/
            

            }
           
        },
        
   checkEmployee : function(){
	   
	   //var draftValues = _self.getConfigValue("draftSearchParam").split(",");   //Added to Accomodate CSV Values In Submit and Draft Values
	  
        	
        	
        // debugger;
        	
        	if(_uploadSelf.EmployeeId.getValue() !==""){
        	
        	
        	var buttonDisable=false;
        	 var URL = "/dms/empValidation/?empId=";
      	   
            
//      	   var serverBase = window.location.protocol + "\/\/" + window.location.host;
//        	 var serverBase = "http://192.168.1.132:8080/";
        	 var serverBase = _self.getConfigValue("upmParam").trim();
      	   var feedURL = serverBase + URL + _uploadSelf.EmployeeId.getValue();
      	   // debugger;
      	   var buttonDisable=false;
      	   var response;
      	   
      	   var xhrArgs = {            
      				url: feedURL,                        
      				handleAs: "json",
      				sync: true,
      				preventCache: true,
      				headers: { "Content-Type": "application/json"},            
      				load: function(data){
      					
      					response=data.isEmployeePresent;
      					
      				} ,            
      				error: function(error)            
      				{            
      					alert ("External services query failed due to " + error);    

      				}            

      		};
         	
         
         		  // Call the asynchronous xhrGet
         		  var deferred = dojo.xhrGet(xhrArgs);
        	
        	          
					 
					 
        		    	
        		    	if(response){
        		    		
//          		    	  alert("Employee Details found for Employee ");
          		    	  
          		    	 
          		    	buttonDisable=false;
          		    	  
        		    	}else {
          			
          			 
        		    	 
        		    	  buttonDisable = true;
        		    	  _uploadSelf.EmployeeId.reset();
        		    	  //dojo.byId("customernomandates").value=""; 
        		    	  _uploadSelf.documenttype.setValue("");
        		    	  _uploadSelf.documentsubtype.setValue("");
        		    	  
        		    	  alert("Invalid Employee number.");
        		      }
        		      
					            
					         

			}
        	
        
        		  // Call the asynchronous xhrGet
        		 
        		  
        		  
        		 
        		  
        		 if(buttonDisable){
        			 _uploadSelf.browse.disabled = true; // Changed By Nikhil 16/10 for Button Disable
//        			 _uploadSelf.browseMandate.disabled = true;
//        			 // debugger;
        		 }else{
        			 _uploadSelf.browse.disabled = false;
//        			 _uploadSelf.browseMandate.disabled = false;
//        			 // debugger;
        	
        		 }
        		 
        	
   			}
/*        	else{
   				alert("Please Enter Valid Account Number");
   			}*/
        
   
   
        
        
        	
        
	});
});
