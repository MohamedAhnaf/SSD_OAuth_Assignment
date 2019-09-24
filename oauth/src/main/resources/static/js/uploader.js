var totFileLength, totFileUploaded, totFileCount, uploadedFiles;

// To send log to  console
function debug(s) {
	var debug = document.getElementById('debug');
	if (debug) {
		debug.innerHTML = debug.innerHTML + '<br/>' + s;
	}
}

// called when the upload is finished
function onUploadFinished(e) {
	totFileUploaded += document.getElementById('files').files[uploadedFiles].size;
	uploadedFiles++;
	debug('complete ' + uploadedFiles + " of " + totFileCount);
	debug('totalFileUploaded: ' + totFileUploaded);
	if (uploadedFiles < totFileCount) {
		uploadNextFile();
	} else {
		var bar = document.getElementById('bar');
		bar.style.width = '100%';
		bar.innerHTML = '100 % completed';
		bootbox.alert('File uploading Finished');
	}

}

// called when user choose files from file control
function onFileSelection(e) {
	var files = e.target.files;
	var output = [];
	totFileCount = files.length;
	totFileLength = 0;
	for (var i = 0; i < totFileCount; i++) {
		var file = files[i];
		output.push(file.name, ' (', file.size, ' bytes, ',
			file.lastModifiedDate.toLocaleDateString(), ')');
		output.push('<br/>');
		debug('add ' + file.size);
		totFileLength += file.size;
	}
	document.getElementById('selectedFiles').innerHTML = output.join('');
}

//  updates the progress bar based on the percentage of file uploaded
function onFileUploadProgress(e) {
	if (e.lengthComputable) {
		var percentComplete = parseInt((e.loaded + totFileUploaded) * 100 / totFileLength);

		if(percentComplete>100)
			percentComplete = 100;
		var bar = document.getElementById('bar');
		bar.style.width = percentComplete + '%';
		bar.innerHTML = percentComplete + ' % completed';
		bootbox.alert('File uploading Finished');
	} else {
		debug('computation failed');
	}
}

//  handle other errors when uploading files .
function onFileUploadFailed(e) {
	bootbox.alert("Error uploading file");
}

// get the next file in queue and upload it to server
function uploadNextFile() {
	var xhr = new XMLHttpRequest();
	var fd = new FormData();
	var file = document.getElementById('files').files[uploadedFiles];
	fd.append("multipartFile", file);
	xhr.upload.addEventListener("progress", onFileUploadProgress, false);
	xhr.addEventListener("load", onUploadFinished, false);
	xhr.addEventListener("error", onFileUploadFailed, false);
	xhr.open("POST", "upload");
	console.log("fd", file);
	xhr.send(fd);
}

function nullFileValidation(){
	var file = document.getElementById("files").files;
	var length = file.length;

	if(length <= 0) {

		return false;
	}
	else {
		return true;
	}
}

// the upload process
function startFileUpload() {
	if(!nullFileValidation()){
		bootbox.alert("Please choose an image to upload!");
	}
	else{
		totFileUploaded = uploadedFiles = 0;
		uploadNextFile();
	}
}

function allReset(){
	document.getElementById("imgUpload").reset();
	document.getElementById("selectedFiles").value=" ";
	var bar = document.getElementById('bar');
	bar.style.width = 0;
	bar.innerHTML = " ";

}

// Button clicks Event listeners
window.onload = function() {
	document.getElementById('files').addEventListener('change', onFileSelection, false);
	document.getElementById('uploadBtn').addEventListener('click', startFileUpload, false);
	document.getElementById('resetButton').addEventListener('click', allReset, false);

}

