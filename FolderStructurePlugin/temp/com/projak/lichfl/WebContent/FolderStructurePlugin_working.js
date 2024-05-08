require([ "dojo/_base/declare", "dojo/_base/lang", "dojo/_base/array", "dojo/aspect",
		"ecm/model/Desktop", "ecm/model/ContentItem", "ecm/model/Request",
		"dojo/on", "ecm/widget/listView/ContentList",
		"ecm/widget/dialog/MessageDialog",
		"ecm/widget/dialog/AddContentItemDialog",
		"ecm/widget/layout/BrowsePane","dojo/_base/kernel"], function(declare, lang, array,
		aspect, desktop, contentitem, Request, on, contentlist,MessageDialog, AddContentItemDialog,BrowsePane,kernel) {

	window.loanProposalDropDown = ['Loan Application Form','KYC documents','Personal Discussion and Business Inspection report','Credit Information Reports','Property Inspection report','Valuation report by Panel Valuer','Regional Office recommendation note','Financial documents including Audit report/ ITR/ Schedules forming part of the financials','Bank statements','Corporate Office note such as Note placed before HOD Committee and Executive Committee','Sanction letter issued to Regional Office'];
	
	aspect.after(desktop, "onLogin", function() {

		debugger;

		console.log("Desktop.onLogin.entry()");

		var params = new Object();

		ecm.model.Request.invokePluginService("FolderStructurePlugin",
				"UserAccess", {
					requestParams : params,
					requestCompleteCallback : function(response) {

						if (response) {

							//kernel.global.globalVariable = response.Message ;
							userDetails = response.Message;

						}

					}
				});

	});
	aspect.around(desktop, "onDesktopLoaded", function() {

		debugger;

		console.log("Desktop.onDesktopLoaded.entry()");

		var params = new Object();

		ecm.model.Request.invokePluginService("FolderStructurePlugin",
				"UserAccess", {
					requestParams : params,
					requestCompleteCallback : function(response) {

						if (response) {

							//kernel.global.globalVariable = response.Message ;
							userDetails = response.Message;

						}

					}
				});

	});


	aspect.around(Request, "invokeService", function advisor(original) {

		return function around() {

			console.log(arguments);

			if (arguments[0] == 'openFolder' || arguments[0] == 'continueQuery') {

				console.log(arguments[2]);

				if (window.userDetails) {
					
					arguments[2].userDetails = JSON.stringify(window.userDetails);
					
					if(window.location_){
						
						arguments[2].userLocation = window.location_;
						
						if(window.department_){
							
							arguments[2].userDepartment = window.department_;
							
							if(window.documentType_){
								
								arguments[2].userDocumentType = window.documentType_;
								
							}
							
						}
						
					}

				}

			}

			original.apply(this, arguments);

		};

	});
	
	function openEntryTemplate(entryTemplate){

		var addContentItemDialog = new AddContentItemDialog();
		
		var repository = ecm.model.desktop.repositories[0];
		
		var resultSet = resultSetSelf.getResultSet();
		
		var docFolderid = resultSet.parentFolder.id;

		var folderName = resultSet.parentFolder.name;
		
		var params = new Object();

		ecm.model.Request.invokePluginService("FolderStructurePlugin",
				"LocationDetails", {
					requestParams : params,
					requestCompleteCallback : function(response) {

						if (response) {
	
							//kernel.global.globalVariable = response.Message ;
							locationDetails = response.Message;

						}

					}
				});
		aspect.before(addContentItemDialog.addContentItemPropertiesPane, "beforeRenderAttributes", function(attributes,item,reason,readonnly){
			//debugger;
			var documentType = window.documentType_;
			
			var department = window.department_;
			
			// add or modify choicelist in entry template
			array.forEach(attributes, function(item){
				
				if(item.id === "RereferenceNumber"){

					if(department === 'Credit Appraisal' && (documentType === 'Notes and Decision' || documentType === 'Correspondence')){
						
						item.required = true; //its working to make the required
						
					}

				}else if(item.id === "ApplNumber"){
					
					if(department === 'Credit Appraisal' && documentType === 'Loan Proposal'){
						
						item.required = true; //its working to make the required
						
					}
					
				}else if(item.id === "DocumentSubType"){
					
					if(folderName === 'Loan Proposal'){
						
						var dropDown = window.loanProposalDropDown;
						
						var choiceListDocumentSubType = {
								'displayName' : 'DocumentSubType',
								'choices' : []
						};
						
						for(var i = 0; i < dropDown.length; i++){
							
							var documentSubTypeJson = {};
							documentSubTypeJson.displayName = dropDown[i];
							documentSubTypeJson.value = dropDown[i];    
							choiceListDocumentSubType.choices.push(documentSubTypeJson);
						}
						
						item.choiceList = choiceListDocumentSubType;
						item.readOnly = false;
						
					}
					
				}else if(item.id === "ScanLocation"){
					
					if(window.locationDetails.attachLocation){
						
						var slocations = Object.values(window.locationDetails.attachLocation);

						var choiceListLocation = {
								'displayName' : 'ScanLocation',
								'choices' : []
						};

						for(var i = 0; i < slocations.length; i++){
							var locationJson = {};
							locationJson.displayName = slocations[i];
							locationJson.value = slocations[i];    
							choiceListLocation.choices.push(locationJson);
						}
						
						item.choiceList = choiceListLocation;
						
					}
					
				}
				
			});
			
		});
		
		aspect.after(addContentItemDialog.addContentItemPropertiesPane, "onCompleteRendering", function() {
			
			var documentType = window.documentType_;
			
			var documentSubType = resultSetSelf.getResultSet().parentFolder.name;
			
			var department = window.department_;
			
			//debugger;
             addContentItemDialog.addContentItemPropertiesPane._commonProperties.setPropertyValue("Office",window.locationDetails.baseLocation);
             addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentType", documentType);
             
             //commented by vara
             addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "Department" , department);
            
             var isDocSubTypeChioceList = addContentItemDialog.addContentItemPropertiesPane._commonProperties.attributeDefinitionsById.DocumentSubType.choiceList;
             
             if(isDocSubTypeChioceList != null){
				var choicesList = addContentItemDialog.addContentItemPropertiesPane._commonProperties.attributeDefinitionsById.DocumentSubType.choiceList.choices;
					for(var p = 0;p<choicesList.length; p++){
						if(choicesList[p].displayName.localeCompare(documentSubType, undefined, { sensitivity: 'accent' }) == 0){
							    console.log("Match");
								addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentSubType" , documentSubType);
							}else{
								addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentSubType" , "");
							}
					}
				
             	}else{
					addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentSubType" , documentSubType);
				}
			
			
		},true);
		
		//Fix by Vara
		aspect.around(AddContentItemDialog.prototype, "onAdd", function advisor(original) {
        return function around() {

            var files = this.addContentItemGeneralPane.getFileInputFiles();
            if(files.length==0){
				//alert("Please choose the document");
				var messageDialog = new ecm.widget.dialog.MessageDialog({
					text: "Please upload the document before proceeding"
				});
				messageDialog.show();


			}else{
                original.apply(this, arguments);
            }

        }
    });
    
		addContentItemDialog.show(repository, docFolderid, true, false, function(callback){
			
			if(resultSet){
				resultSet.parentFolder.addToFolder(callback, function(){
					console.log("Document Filed Successfully");
				});
			}
			
		}, null, true, entryTemplate, false);
		
	}
	

	window.openEntryTemplate = openEntryTemplate;
	
	

	on(contentlist.prototype, "SetResultSet", function(listParent) {
		
		resultSetSelf = this;

		if (dijit.byId('ecm_widget_button_add_document_1')) {
			dijit.byId('ecm_widget_button_add_document_1').destroy();
		}
		
		if(resultSetSelf.getResultSet() && resultSetSelf.getResultSet().parentFolder != null){
			
			var resultSet = resultSetSelf.getResultSet();
			
			var folderid = resultSet.parentFolder.template;
			
			var folderName = resultSet.parentFolder.name;
			
			var docFolderid = resultSet.parentFolder.id;
			
			if((folderid === 'DocumentType' && folderName === 'Loan Proposal') || (folderid === 'DocumentSubType')){
				var button = new ecm.widget.Button({
					"id" : "ecm_widget_button_add_document_1",
					"label" : "Add",
					"onClick" : function(e) {
						
						var repository = ecm.model.desktop.repositories[0];
						
						repository.retrieveEntryTemplates(lang.hitch(this, function(response){
							
							var template = null;
							
							var nonLoanTemplate = null;
							
							var department = window.department_;
							
							if(department == undefined || department == null){
								alert("Department is not set.");
								return;
							}
							
							for(var i = 0; i < response.length; i++){
								
								if(response[i].name === "Non Loan Document"){
									
									nonLoanTemplate = response[i];
									
								}else if(response[i].name === department +" Entry Template"){
									template = response[i];
								}
								
							}
							
							if(template != null){
								
								template.retrieveEntryTemplate(lang.hitch(this,openEntryTemplate ));
								
							}else if(nonLoanTemplate != null){
								nonLoanTemplate.retrieveEntryTemplate(lang.hitch(this,openEntryTemplate));
							}
							
						}), "Document", null, null, repository.objectStore);

					}
				});

				button.startup();

				if (this.grid) {

					var childList = this.topContainer.getChildren();

					for (var i = 0; i < childList.length; i++) {

						var widget = childList[i];

						if (widget.declaredClass) {

							if (widget.declaredClass === 'ecm.widget.Toolbar') {

								this.topContainer.getChildren()[0]
										.addChild(button, '1');

							}

						}

					}

				}
			}
			
			if(folderid == 'Location'){
				window.location_ = folderName;
				window.department_ = undefined;
				window.documentType_ = undefined;
			}else if(folderid == 'Department'){
				window.department_ = folderName;
				window.documentType_ = undefined;
			}else if(folderid == 'DocumentType'){
				window.documentType_ = folderName;
			}else if(folderid == 'DocumentSubType'){
				console.log("FolderName - " + folderName);
			}
			
		}		

	});
	

});
