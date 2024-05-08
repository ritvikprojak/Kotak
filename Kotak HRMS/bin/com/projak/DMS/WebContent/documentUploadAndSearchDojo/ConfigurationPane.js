define([
		"dojo/_base/declare",
		"dijit/_TemplatedMixin",
		"dijit/_WidgetsInTemplateMixin",
		"ecm/widget/admin/PluginConfigurationPane",
		"dojo/text!./templates/ConfigurationPane.html"
	],
	function(declare, _TemplatedMixin, _WidgetsInTemplateMixin, PluginConfigurationPane, template) {

		return declare("DocumentUploadAndSearchDojo.ConfigurationPane", [ PluginConfigurationPane, _TemplatedMixin, _WidgetsInTemplateMixin], {
		
			templateString: template,
			widgetsInTemplate: true,
		
			load: function(callback) {
				debugger;
				console.log("\n\n\n\n This is configuration \n\n\n\n");
				//initialize fields
				if (this.configurationString) {
					//evaluate the string to form valid json
					this.jsonConfig = eval('(' + this.configurationString + ')');
					
					//set the first field based on the configuration values
					if(this.jsonConfig.configuration[0])
						this.repoParam.set("value", this.jsonConfig.configuration[0].value);
					if(this.jsonConfig.configuration[1])
						this.rootFolderParam.set("value", this.jsonConfig.configuration[1].value);
					if(this.jsonConfig.configuration[2])
						this.documentTypeParam.set("value", this.jsonConfig.configuration[2].value);
					if(this.jsonConfig.configuration[3])
						this.documentSubTypeParam.set("value", this.jsonConfig.configuration[3].value);
					if(this.jsonConfig.configuration[4])
						this.employeeIdParam.set("value", this.jsonConfig.configuration[4].value);
					if(this.jsonConfig.configuration[5])
						this.compensationDocClassParam.set("value", this.jsonConfig.configuration[5].value);
					
					if(this.jsonConfig.configuration[6])
						this.discDocClassParam.set("value", this.jsonConfig.configuration[6].value);
					
					if(this.jsonConfig.configuration[7])
						this.exitmanagementDocClassParam.set("value", this.jsonConfig.configuration[7].value);
					
					if(this.jsonConfig.configuration[8])
						this.joiningDocClassParam.set("value", this.jsonConfig.configuration[8].value);
					if(this.jsonConfig.configuration[9])
						this.miscDocClassParam.set("value", this.jsonConfig.configuration[9].value);
					
					if(this.jsonConfig.configuration[10])
						this.nominationsDocClassParam.set("value", this.jsonConfig.configuration[10].value);
					
					if(this.jsonConfig.configuration[11])
						this.testimonialDocClassParam.set("value", this.jsonConfig.configuration[11].value);
					
					if(this.jsonConfig.configuration[12])
						this.dMSDocumentStatusParam.set("value", this.jsonConfig.configuration[12].value);
					
					if(this.jsonConfig.configuration[13])
						this.scannedUserParam.set("value", this.jsonConfig.configuration[13].value);
					if(this.jsonConfig.configuration[14])
						this.scannedDateParam.set("value", this.jsonConfig.configuration[14].value);
					if(this.jsonConfig.configuration[15])
						this.upmParam.set("value", this.jsonConfig.configuration[15].value);
					if(this.jsonConfig.configuration[16])
						this.aesKey.set("value", this.jsonConfig.configuration[16].value);
				}
			},
			
			/**
			 * Saves all the values from fields onto the configuration string which will be stored into the admin's configuration.
			 */
			save: function(){
				debugger;
				var configArray = [];
				
				var configString = {  
					name: "repoIdParam",
					value: this.repoParam.get("value")
				}; 
				configArray.push(configString);
				
				
				configString = {  
						name: "rootFolderParam",
						value: this.rootFolderParam.get("value")
					}; 
				configArray.push(configString);
				
				configString = {  
						name: "documentTypeParam",
						value: this.documentTypeParam.get("value")
					}; 
				configArray.push(configString);
				configString = {  
						name: "documentSubTypeParam",
						value: this.documentSubTypeParam.get("value")
					}; 
				configArray.push(configString);
				configString = {  
						name: "employeeIdParam",
						value: this.employeeIdParam.get("value")
					}; 
				configArray.push(configString);
				
				configString = {  
						name: "compensationDocClassParam",
						value: this.compensationDocClassParam.get("value")
					}; 
				configArray.push(configString);
				configString = {  
						name: "discDocClassParam",
						value: this.discDocClassParam.get("value")
					}; 
				configArray.push(configString);
				
				
				configString = {  
						name: "exitmanagementDocClassParam",
						value: this.exitmanagementDocClassParam.get("value")
					}; 
				configArray.push(configString);
				
				var configString = {  
						name: "joiningDocClassParam",
						value: this.joiningDocClassParam.get("value")
					}; 
					configArray.push(configString);
					
				var configString = {  
							name: "miscDocClassParam",
							value: this.miscDocClassParam.get("value")
						}; 
						configArray.push(configString);	
						
				var configString = {  
								name: "nominationsDocClassParam",
								value: this.nominationsDocClassParam.get("value")
							}; 
							configArray.push(configString);			
				
				var configString = {  
									name: "testimonialDocClassParam",
									value: this.testimonialDocClassParam.get("value")
								}; 
							configArray.push(configString);	
							
				var configString = {  
									name: "dMSDocumentStatusParam",
									value: this.dMSDocumentStatusParam.get("value")
								}; 
							configArray.push(configString);
				var	configString = {  
									name: "scannedUserParam",
									value: this.scannedUserParam.get("value")
								}; 
							configArray.push(configString);
				var	configString = {  
									name: "scannedDateParam",
									value: this.scannedDateParam.get("value")
								}; 
							configArray.push(configString);
				var	configString = {  
									name: "upmParam",
									value: this.upmParam.get("value")
								}; 
							configArray.push(configString);
				var	configString = {  
									name: "AESKey",
									value: this.aesKey.get("value")
								}; 
							configArray.push(configString);	
							
				var configJson = {
					"configuration" : configArray
				};
				
				
							
				this.configurationString = JSON.stringify(configJson);
			},
			
			_onParamChange: function() {
				this.onSaveNeeded(true);
			},
			
			/**
			 * Validates this feature configuration pane.
			 */
			validate: function() {
				return true;
			}
	});
});
