var files = []

function getFiles(files_temp) {
    var file_container = document.getElementById("files-container");
    var file_container_select = document.getElementById("files-container-select");

    var inner = ""
    var inner2 = ""
    if(files_temp.length == 0){
        inner = '<div class="no-files">Нет файлов</div>'
        inner2 = '<div class="no-files">Нет файлов</div>'
    }

    for (var i = 0; i < files_temp.length; i++) {
        inner += '\
        <div class="file">\
            <img class="file-img" src="img/file.png" onclick="android.openFile('+i+')">\
            <div class="file-title">'+files_temp[i]+'</div>\
        </div>'

        inner2 += '\
        <div class="file">\
            <img class="file-img" src="img/checkFile.png" onclick="android.chooseFile('+i+')">\
            <div class="file-title">'+files_temp[i]+'</div>\
        </div>'
    }

    file_container.innerHTML = inner
    file_container_select.innerHTML = inner2

    files = files_temp
}


function createFile() {
    var name = document.getElementById("name-input")
    if(name != ""){
        android.createFile(name)
    }
}


function downloadFile() {
    var id = document.getElementById("id-input")
    if(id != ""){
        android.downloadFile(id)
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
}




function openFile(json){
    if(json == ""){

    }

    show(4)
}



getFiles(["asdfas", "asdas"])




function settings(){
    
}

function changeInstrument(id){

}

function save(){
    show(0)
}