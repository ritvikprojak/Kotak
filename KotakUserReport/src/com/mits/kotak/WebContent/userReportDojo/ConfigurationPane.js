define([
		"dojo/_base/declare",
		"dijit/_TemplatedMixin",
		"dijit/_WidgetsInTemplateMixin",
		"ecm/widget/admin/PluginConfigurationPane",
		"ecm/widget/ValidationTextBox",
		"dojo/text!./templates/ConfigurationPane.html"
	],
	function(declare, _TemplatedMixin, _WidgetsInTemplateMixin, PluginConfigurationPane,ValidationTextBox, template) {

		return declare("UserReportDojo.ConfigurationPane", [ PluginConfigurationPane, _TemplatedMixin, _WidgetsInTemplateMixin], {
		
		templateString: template,
		widgetsInTemplate: true,
	
		load: function(callback) {
			
			if(this.configurationString){
				var jsonConfig = JSON.parse(this.configurationString);
				
				this.ldapAdminUserKMBL.set('value',jsonConfig.configuration[0].value);
				this.kmblLdapAdminPassword.set('value',jsonConfig.configuration[1].value);				
				this.ldapAdminUserKMPL.set('value',jsonConfig.configuration[2].value);
				this.kmplLdapAdminPassword.set('value',jsonConfig.configuration[3].value);
				this.kmblLdapURL.set('value',jsonConfig.configuration[4].value);				
				this.kmplLdapURL.set('value',jsonConfig.configuration[5].value);
				this.datasource.set('value',jsonConfig.configuration[6].value);
				this.mtomurl.set('value',jsonConfig.configuration[7].value);
				this.domainname.set('value',jsonConfig.configuration[8].value);
				this.admusername.set('value',jsonConfig.configuration[9].value);
				this.adminpwd.set('value',jsonConfig.configuration[10].value);
				}
		},
		
		_onParamChange: function() {
			
			var configArray = new Array();
			
			var configString = {
			name: "ldapAdminUserKMBL",
			value: this.ldapAdminUserKMBL.get('value')
			};
			configArray.push(configString);
			
			configString = {
					name: "kmblLdapAdminPassword",
					value: this.kmblLdapAdminPassword.get('value')
					};
					configArray.push(configString);
					
					
					configString = {
					name: "ldapAdminUserKMPL",
					value: this.ldapAdminUserKMPL.get('value')
					};
					configArray.push(configString);
					
					
					configString = {
							name: "kmplLdapAdminPassword",
							value: this.kmplLdapAdminPassword.get('value')
							};
							configArray.push(configString);
							
							configString = {
									name: "kmblLdapURL",
									value: this.kmblLdapURL.get('value')
									};
									configArray.push(configString);
									
									
									configString = {
									name: "kmplLdapURL",
									value: this.kmplLdapURL.get('value')
									};
									configArray.push(configString);
			
			configString = {
					name: "datasource",
					value: this.datasource.get('value')
					};
			configArray.push(configString);
			
			configString = {
					name: "mtomurl",
					value: this.mtomurl.get('value')
					};
			configArray.push(configString);
			
			configString = {
					name: "domainname",
					value: this.domainname.get('value')
					};
			configArray.push(configString);
			
			configString = {
					name: "admusername",
					value: this.admusername.get('value')
					};
			configArray.push(configString);
			configString = {
					name: "adminpwd",
					value: this.adminpwd.get('value')
					};
			configArray.push(configString);
					
			var configJson = {
					"configuration" : configArray
					};
					this.configurationString = JSON.stringify(configJson);
					this.onSaveNeeded(true);
		},		
		
		validate: function() {
			if(!this.ldapAdminUserKMBL.isValid() || !this.kmblLdapAdminPassword.isValid() || !this.ldapAdminUserKMPL.isValid() || !this.kmplLdapAdminPassword.isValid() || !this.kmblLdapURL.isValid() || !this.kmplLdapURL.isValid() || !this.datasource.isValid() || !this.mtomurl.isValid()  || !this.domainname.isValid() || !this.admusername.isValid() || !this.adminpwd.isValid() )
			return false;
			return true;
			}
	});
});