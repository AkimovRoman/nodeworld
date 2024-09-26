var files = []

function getFiles(files_temp){

    var file_container = document.getElementById("files-container");
    var file_container_select = document.getElementById("files-container-select");

    var inner = ""
    var inner2 = ""
    if(files_temp.length == 0){
        inner = '<div class="no-files">Нет файлов</div>'
        inner2 = '<div class="no-files">Нет файлов</div>'
    }

    for (var i = 0; i < files_temp.length; i++) {
        inner += '<div class="file"><img class="file-img" src="img/file.png" onclick="openFile()"><div class="file-title">'+files_temp[i]+'</div></div>'

        inner2 += '<div class="file"><img class="file-img" src="img/checkFile.png" onclick="android.chooseFile('+i+')"><div class="file-title">'+files_temp[i]+'</div></div>'
    }

    file_container.innerHTML = inner
    file_container_select.innerHTML = inner2

    files = files_temp
}


function createFile() {
    var name = document.getElementById("name-input").value
    if(name != ""){
        android.createJsonFile(name)
        show(0)
    }
}


function downloadFile() {
    var id = document.getElementById("id-input").value
    if(id != ""){
        android.downloadFile(id)
        show(0)
    }
}


function uploadFile() {
    show(1)
}



function show(t) {
    var elems = document.getElementsByClassName("block")
    for (var i = 0; i < elems.length; i++) {
        elems[i].style.display = "none"
    }
    elems[t].style.display = "block"

    if(t == 0) android.getAllJsonFiles()
}

android.getAllJsonFiles()


function openFile(json){
    if(json == ""){

    }

    show(4)
}





function settings(){
    
}

function changeInstrument(id){

}

function save(){
    show(0)
}