package com.note.noteworldproject;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileManager {
    private final WebView webView;

    public FileManager(Context context, WebView webView) {
        this.webView = webView;
    }


    // Получаем директорию для хранения заметок
    private File getNotesDirectory() {
        File notesDir = new File(webView.getContext().getFilesDir(), "notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs(); // создаем папку, если её нет
        }
        return notesDir;
    }


    // Функция для создания нового JSON файла
    @JavascriptInterface
    public void createJsonFile(String title) {
        File notesDir = getNotesDirectory();

        // Генерируем уникальное имя файла (UUID для уникальности)
        String fileName = title + ".json";
        File jsonFile = new File(notesDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(jsonFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) { // Используем OutputStreamWriter с кодировкой UTF-8
             writer.write("");
             writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Функция для получения списка всех файлов в директории "notes"
    @JavascriptInterface
    public void getAllJsonFiles() {
        File notesDir = getNotesDirectory();
        // фильтр для JSON файлов
        File[] files = notesDir.listFiles();
        StringBuilder noteFiles = new StringBuilder();

        if (files != null) {
            for (File file : files) {
                noteFiles.append("'"+file.getName()+"'").append(",");
            }
            noteFiles.deleteCharAt(noteFiles.length()-1);
        }



        webView.post(() -> webView.loadUrl("javascript:getFiles(["+noteFiles+"]);"));
    }


    // Функция для удаления файла по имени
    @JavascriptInterface
    public void deleteJsonFile(String fileName) {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
