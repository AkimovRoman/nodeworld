var files = []
var title = ""

var data = null

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
        inner += '<div class="file"><img class="file-img" src="img/file.png" onclick="openFile(\'' + files_temp[i] + '\')"><div class="file-title">'+files_temp[i]+'</div></div>'
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

    document.getElementById("exit").style.display = "none"
    document.getElementById("settings").style.display = "none"
}

android.getAllJsonFiles()


function openFile(title_temp){
    title = title_temp
    android.readFile(title)
    show(4)
}


function settings(){
    if(document.getElementById("settings").style.display != "flex"){
     document.getElementById("settings").style.display = "flex"
     document.getElementById("exit").style.display = "none"
    }
    else document.getElementById("settings").style.display = "none"
}

function changeTitle(){
    var new_title = document.getElementById("name-file-input").value
    if(new_title != ""){
        android.changeTitle(title, new_title)
        settings()
    }
}

function deleteFile(){
    android.deleteFile(title)
    show(0)
}




function changeInstrument(id){

}

function saveWindow(){
    document.getElementById("exit").style.display = "flex"
    document.getElementById("settings").style.display = "none"
}

function save(param){
    if(param) android.writeFile(title, JSON.stringify(data))
    show(0)
}