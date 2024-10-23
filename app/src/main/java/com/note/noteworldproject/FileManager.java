package com.note.noteworldproject;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
                line = br.readLine();
            }
            final String fileContent = sb.toString();

            // Отправляем данные файла обратно в WebView
            webView.post(() -> webView.loadUrl("javascript:onFileLoaded('" + fileContent + "');"));
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

    @JavascriptInterface
    public void downloadFileFromServer(String id) {
        new Thread(() -> {
            try {
                // Адрес сервера и путь для скачивания
                String downloadUrl = "https://arriving-glowworm-endlessly.ngrok-free.app/download/" + id;

                // Создаем HTTP запрос
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Проверяем ответ сервера
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Получаем имя файла из заголовка или генерируем на основе идентификатора
                    String contentDisposition = connection.getHeaderField("Content-Disposition");
                    String fileName;

                    if (contentDisposition != null && contentDisposition.contains("filename=")) {
                        // Получаем имя файла из заголовка
                        fileName = contentDisposition.split("filename=")[1].trim().replaceAll("\"", "");
                    } else {
                        // Если имя файла не было передано, создаем его на основе идентификатора
                        fileName = "file_" + id + ".nw";
                    }

                    // Читаем поток данных
                    InputStream inputStream = connection.getInputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    // Получаем папку для заметок
                    File notesDir = getNotesDirectory();
                    File file = new File(notesDir, fileName);

                    // Записываем данные в файл
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    }

                    inputStream.close();
                    connection.disconnect();

                    // Возвращаем результат на WebView
                    webView.post(() -> webView.loadUrl("javascript:alert('Файл успешно скачан и сохранен как: " + fileName + "');"));
                } else {
                    webView.post(() -> {
                        try {
                            webView.loadUrl("javascript:alert('Ошибка при скачивании файла: код " + connection.getResponseCode() + "');");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                webView.post(() -> webView.loadUrl("javascript:alert('Ошибка при скачивании файла: " + e.getMessage() + "');"));
            }
        }).start();
    }

    @JavascriptInterface
    public void saveDownloadedFile(String fileName, String fileData) {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);

        // Логируем имя файла перед сохранением
        System.out.println("Сохранение файла с именем: " + fileName);

        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) {
            writer.write(fileData);
            writer.flush();
            System.out.println("Файл успешно сохранен: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}