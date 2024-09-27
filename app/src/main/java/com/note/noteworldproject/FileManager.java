package com.note.noteworldproject;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileManager {
    private final WebView webView;

    private final String null_file = "{ \"type\" : \"nodeworldfile\", \"version\" : \"1.0\", \"objects\": [] }";

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

        String fileName = title + ".nw";
        File jsonFile = new File(notesDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(jsonFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) { // Используем OutputStreamWriter с кодировкой UTF-8
             writer.write(null_file);
             writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Функция для получения списка всех файлов в директории "notes"
    @JavascriptInterface
    public void getAllJsonFiles() {
        File notesDir = getNotesDirectory();
        File[] files = notesDir.listFiles();
        StringBuilder noteFiles = new StringBuilder();

        if (files != null) {
            for (File file : files) {
                noteFiles.append("'").append(file.getName()).append("'").append(",");
            }
            noteFiles.deleteCharAt(noteFiles.length()-1);
        }



        webView.post(() -> webView.loadUrl("javascript:getFiles(["+noteFiles+"]);"));
    }




    // Функция для удаления файла по имени
    @JavascriptInterface
    public void deleteFile(String fileName) {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    @JavascriptInterface
    public void changeTitle(String fileName, String title) {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        if (file.exists()) {
            file.renameTo(new File(notesDir,title+".nw"));
        }
    }


    @JavascriptInterface
    public void readFile(String fileName) throws IOException {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }


            webView.post(() -> webView.loadUrl("javascript:data = JSON.parse(" + sb.toString() + ");"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            br.close();
        }
    }



    @JavascriptInterface
    public void writeFile(String fileName, String data) throws IOException {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file); OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
