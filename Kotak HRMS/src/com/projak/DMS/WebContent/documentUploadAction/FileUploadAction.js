require(["dojo/aspect",
         "ecm/widget/dialog/AddContentItemDialog"
], 
function(aspect,
		AddContentItemDialog) {
	
	
    aspect.around(AddContentItemDialog.prototype, "onAdd", function advisor(original) {
        return function around() {

            var files = this.addContentItemGeneralPane.getFileInputFiles();
            var containsInvalidFiles = dojo.some(files, function isInvalid(file) {
                var fileName = file.name.toLowerCase();

                var extensionOK = fileName.endsWith(".pdf") || fileName.endsWith(".jpeg") || fileName.endsWith(".tiff") || fileName.endsWith(".jpg");
//                var fileSizeOK = file.size <= 10 * 1024 * 1024;
                
                

                return !(extensionOK);
            });

            if (containsInvalidFiles) {
                alert("You can't add that :)");
            }else{
                original.apply(this, arguments);
            }

        }
    });
});