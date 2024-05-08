require([ "dojo/_base/declare", "dojo/_base/lang", "dojo/_base/array", "dojo/aspect",
		"ecm/model/Desktop", "ecm/model/ContentItem", "ecm/model/Request",
		"dojo/on", "ecm/widget/listView/ContentList",
		"ecm/widget/dialog/AddContentItemDialog",
		"ecm/widget/layout/BrowsePane","dojo/Deferred" ], function(declare, lang, array,
		aspect, desktop, contentitem, Request, on, contentlist, AddContentItemDialog,BrowsePane,Deferred) {

	window.loanProposalDropDown = ['Loan Application Form','KYC documents','Personal Discussion and Business Inspection report','Credit Information Reports','Property Inspection report','Valuation report by Panel Valuer','Regional Office recommendation note','Financial documents including Audit report/ ITR/ Schedules forming part of the financials','Bank statements','Corporate Office note such as Note placed before HOD Committee and Executive Committee','Sanction letter issued to Regional Office'];
	
	_self = this;
	
	aspect.after(desktop, "onLogin", function() {

		//var deferred = new Deferred();
		debugger;

		console.log("Desktop.onLogin.entry()");

		var params = new Object();

		ecm.model.Request.invokePluginService("FolderStructurePlugin",
				"UserAccess", {
					requestParams : params,
					requestCompleteCallback : function(response) {

						if (response) {

							userDetails = response.Message;
							_self.userDetails = userDetails;

						}

					}
				});
				
		//return deferred.promise;
	});

	aspect.around(Request, "invokeService", lang.hitch(_self, function advisor(original) {

		return function around() {

			console.log(arguments);
			console.log("printSelf : "+JSON.stringify(_self.userDetails));
			if (window.userDetails === undefined) {
				window.userDetails = _self.userDetails;
			}

			if (arguments[0] == 'openFolder' || arguments[0] == 'continueQuery') {

				console.log(arguments[2]);
				console.log("_self: "+_self);

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

	}));
	
	function openEntryTemplate(entryTemplate){

		var addContentItemDialog = new AddContentItemDialog();
		
		var repository = ecm.model.desktop.repositories[0];
		
		var resultSet = resultSetSelf.getResultSet();
		
		var docFolderid = resultSet.parentFolder.id;

		var folderName = resultSet.parentFolder.name;
		
		aspect.before(addContentItemDialog.addContentItemPropertiesPane, "beforeRenderAttributes", function(attributes,item,reason,readonnly){
			debugger;
			
			// add or modify choicelist in entry template
			array.forEach(attributes, function(item){
				
				if(item.id === "Office"){

					if(window.userDetails){
						
						var locations = Object.keys(window.userDetails);

						var choiceListLocation = {
								'displayName' : 'Location',
								'choices' : []
						};

						for(var i = 0; i < locations.length; i++){
							var locationJson = {};
							locationJson.displayName = locations[i];
							locationJson.value = locations[i];    
							choiceListLocation.choices.push(locationJson);
						}
						
						item.choiceList = choiceListLocation;
						
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
					
					if(window.userDetails){
						
						var slocations = Object.keys(window.userDetails);

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
			
			debugger;
			
             addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentType", documentType);
             addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "DocumentSubType" , documentSubType);
             addContentItemDialog.addContentItemPropertiesPane.setPropertyValue( "Department" , department);
             
			
			
		},true);
		
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
	//added by vara/*
lang.extend(BrowsePane,{
	/* _getRootFolder:function (repository, callback) {
		 debugger;
            var folderUrlParameter = this._getFolderUrlParameter();
            if (folderUrlParameter.length > 0) {
                repository.retrieveItem(folderUrlParameter, callback, null, null, null, null, "browseFolder");
            } else {
                callback();
            }
        }, _getFolderUrlParameter:function () {
	debugger;
            var folderId = "";
            var params = location.search.substring(1).split("&");
            for (var i = 0; i < params.length; i++) {
                var param = params[i].split("=");
                if (param[0] == "folder") {
                    folderId = decodeURIComponent(param[1]);
                    break;
                }
            }
            return "{E6CCEDBF-1175-4320-806A-D72F362DC88E}";
        }, setRepository:function (repository) {
	debugger;
            this._setFolderOrRepository(repository);
        },
         _setFolderOrRepository:function (folderOrRepository) {
	debugger;
            var folder = null;
            if (!folderOrRepository) {
                this.repository = folderOrRepository;
            } else {
                if (folderOrRepository.isInstanceOf && folderOrRepository.isInstanceOf(ecm.model.Repository)) {
                    this.repository = folderOrRepository;
                } else {
                    folder = folderOrRepository;
                    this.repository = folder.repository;
                }
            }
            this.clear();
            if (this.repository && this.selected) {
                if (this.repository && this.repository.type == "od") {
                    this.openButtonFlyout("browsePane");
                    this.isLoaded = false;
                } else {
                    if (this.repository && this.repository.getPrivilege("foldering")) {
                        if (this.repositorySelector) {
                            this.repositorySelector.getDropdown().set("value", this.repository.id);
                        }
                        if (this.folderTree && this.showTreeView) {
                            this.folderTree.filterType = this.repository.isSearchTemplateSupported && this.repository.isSearchTemplateSupported() && ((this.repository._isCM && this.repository._isCM()) || (this.repository._isP8 && this.repository._isP8())) ? "searchAndFolderSearch" : "folderSearch";
                            if (folder == null) {
                                this.folderTree.setRepository(this.repository);
                            } else {
                                this.folderTree.setFolder(folder);
                            }
                        } else {
                            if (folder) {
                                this._setRootFolder(folder);
                            } else {
                                this._retrieveTopFolder(this.repository);
                            }
                        }
                        this.needReset = false;
                        this.isLoaded = true;
                        this.disableButtonClick = false;
                    } else {
                        if (this.folderTree && this.showTreeView) {
                            this.folderTree.setRepository(null);
                        }
                        this.folderContents.setResultSet(null);
                        if (this.repository) {
                            if (this.repositorySelector) {
                                this.repositorySelector.getDropdown().set("value", this.repository.id);
                                if (this.repositorySelector.getNumRepositories() == 1 && this.repositorySelector.getDropdown().get("value") != this.repository.id) {
                                    domClass.remove(this.domNode, "hideNavContainerTop");
                                    this.repositorySelectorArea.domNode.appendChild(this.repositorySelector.domNode);
                                    this.setRepository(this.repositorySelector.setSelected(0));
                                } else {
                                    this.needReset = false;
                                    this.isLoaded = true;
                                }
                            } else {
                                this.needReset = false;
                                this.isLoaded = true;
                            }
                        }
                    }
                }
            }
        },
        loadContent:function () {
		debugger;
            if (this.repository && this.repository.connected) {
                if (this.repository.type == "od") {
                    this.openButtonFlyout("browsePane");
                    this.isLoaded = false;
                } else {
                    if (this.repositorySelector) {
                        this.repositorySelector.getDropdown().set("value", this.repository.id);
                    }
                    if (this.showTreeView) {
                        this.folderTree.filterType = this.repository.isSearchTemplateSupported() && (this.repository._isCM() || this.repository._isP8()) ? "searchAndFolderSearch" : "folderSearch";
                        this.folderTree.setRepository(this.repository);
                        this.isLoaded = true;
                        this.needReset = false;
                        this.disableButtonClick = false;
                    } else {
                       // this._retrieveTopFolder(this.repository);
                       this._setRootFolder();
                    }
                }
            } else {
                this.setPaneDefaultLayoutRepository();
                if (this.repository) {
                    this.loadContent();
                }
            }
        },*/
         _retrieveTopFolder:function (repository) {
			debugger;
            if (repository && repository.canListFolders()) {
                var rootItemId = this.rootFolderId || "/Location";
                repository.retrieveItem(rootItemId, lang.hitch(this, function (rootFolder) {
                    if (rootFolder && !this.rootFolderId) {
                        rootFolder.name = repository.name;
                    }
                    this._setRootFolder(rootFolder);
                }), null, null, null, this._objectStore ? this._objectStore.id : "");
            }
        },/* _setRootFolder:function (item) {
	debugger;
            if (!this.folderContents.getResultSet() || !this.folderContents.getResultSet().isResultSetForItem(item)) {
                this.folderContents.openItem(item);
                Desktop.setSelectedItems([item]);
                this.isLoaded = true;
            }
        },*/
	
});
});
