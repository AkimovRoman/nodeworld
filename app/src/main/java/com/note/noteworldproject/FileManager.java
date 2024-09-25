package com.note.noteworldproject;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.OutputStreamWriter;

public class FileManager {

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    // Получаем директорию для хранения заметок
    private File getNotesDirectory() {
        File notesDir = new File(context.getFilesDir(), "notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs(); // создаем папку, если её нет
        }
        return notesDir;
    }

    // Функция для создания нового JSON файла
    public void createJsonFile(String jsonString) {
        File notesDir = getNotesDirectory();

        // Генерируем уникальное имя файла (UUID для уникальности)
        String fileName = "note_" + UUID.randomUUID().toString() + ".json";
        File jsonFile = new File(notesDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(jsonFile);
             OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8")) { // Используем OutputStreamWriter с кодировкой UTF-8
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Функция для получения списка всех файлов в директории "notes"
    public List<NoteFile> getAllJsonFiles() {
        File notesDir = getNotesDirectory();
        // фильтр для JSON файлов
        File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".json"));
        List<NoteFile> noteFiles = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                noteFiles.add(new NoteFile(file.getName(), file.getAbsolutePath()));
            }
        }

        return noteFiles;
    }

    // Функция для удаления файла по имени
    public void deleteJsonFile(String fileName) {
        File notesDir = getNotesDirectory();
        File file = new File(notesDir, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    // Класс для хранения имени и пути к файлу
    public static class NoteFile {
        private String fileName;
        private String filePath;

        public NoteFile(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFilePath() {
            return filePath;
        }
    }
}
