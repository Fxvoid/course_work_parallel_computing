package com.parallel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndexManager implements InvertedIndexInterface {
    private final ConcurrentHashMap<String, List<Path>> inverted_index = new ConcurrentHashMap<>();
    private static final int SMALL_FOLDER_INDEX_START = 10500;
    private static final int SMALL_FOLDER_INDEX_FINISH = 10750;
    private static final int BIG_FOLDER_INDEX_START = 42000;
    private static final int BIG_FOLDER_INDEX_FINISH = 43000;
    private static final String PATH_TO_FILES_FOLDER = "C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb";

    public void createInvertedIndex(int thread_number) throws IOException {
        IndexCreatorThread[] thread_array = new IndexCreatorThread[thread_number];

        long start_time = System.currentTimeMillis();
        // initializing and starting threads
        for (int i = 0; i < thread_number; i++) {
            thread_array[i] = new IndexCreatorThread(inverted_index, getListOfFiles(), i, thread_number);
            thread_array[i].start();
        }
        for (int i = 0; i < thread_number; i++) {
            try {
                thread_array[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error with thread-" + i);
                e.printStackTrace();
            }
        }
        System.out.println("Ended in " + (System.currentTimeMillis() - start_time) + " ms");
    }

    public String search(String search_request) throws RemoteException {
        try {
            String[] words = search_request.split(" ");
            List<Path> result = inverted_index.get(normalizeWord(words[0]));
            for (int i = 1; i < words.length; i++) {
                List<Path> word_search_result = inverted_index.get(normalizeWord(words[i]));
                result = result.stream().distinct().filter(word_search_result::contains).toList();
            }
            return result.toString();
        } catch (NullPointerException ignored) {
            return "No data matching your request found :(";
        }
    }

    private List<Path> getListOfFiles() throws IOException {
        List<Path> file_paths = new ArrayList<>();
        file_paths.addAll(Files.list(Paths.get(PATH_TO_FILES_FOLDER + "\\test\\neg")).filter(i -> parseFileID(i) >= SMALL_FOLDER_INDEX_START && parseFileID(i) < SMALL_FOLDER_INDEX_FINISH).toList());
        file_paths.addAll(Files.list(Paths.get(PATH_TO_FILES_FOLDER + "\\test\\pos")).filter(i -> parseFileID(i) >= SMALL_FOLDER_INDEX_START && parseFileID(i) < SMALL_FOLDER_INDEX_FINISH).toList());
        file_paths.addAll(Files.list(Paths.get(PATH_TO_FILES_FOLDER + "\\train\\neg")).filter(i -> parseFileID(i) >= SMALL_FOLDER_INDEX_START && parseFileID(i) < SMALL_FOLDER_INDEX_FINISH).toList());
        file_paths.addAll(Files.list(Paths.get(PATH_TO_FILES_FOLDER + "\\train\\pos")).filter(i -> parseFileID(i) >= SMALL_FOLDER_INDEX_START && parseFileID(i) < SMALL_FOLDER_INDEX_FINISH).toList());
        file_paths.addAll(Files.list(Paths.get(PATH_TO_FILES_FOLDER + "\\train\\unsup")).filter(i -> parseFileID(i) >= BIG_FOLDER_INDEX_START && parseFileID(i) < BIG_FOLDER_INDEX_FINISH).toList());
        return file_paths;
    }

    private int parseFileID(Path filepath) {
        String filename = filepath.getFileName().toString();
        int _i = filename.indexOf("_");
        return Integer.parseInt(filename.substring(0, _i));
    }

    private String normalizeWord(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z]", "");
    }

}
