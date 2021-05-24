package com.parallel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndexManager implements InvertedIndexInterface {
    private ConcurrentHashMap<String, List<Path>> inverted_index = new ConcurrentHashMap<>();
    private static final int small_folder_index_start = 10500;
    private static final int small_folder_index_finish = 10750;
    private static final int big_folder_index_start = 42000;
    private static final int big_folder_index_finish = 43000;

    public void createInvertedIndex(int thread_number) throws IOException {
        IndexCreatorThread[] ThreadArray = new IndexCreatorThread[thread_number];

        long startTime = System.currentTimeMillis();
        // initializing and starting threads
        for (int i = 0; i < thread_number; i++) {
            ThreadArray[i] = new IndexCreatorThread(inverted_index, getListOfFiles(), i, thread_number);
            ThreadArray[i].start();
        }
        for (int i = 0; i < thread_number; i++) {
            try {
                ThreadArray[i].join();
            } catch (InterruptedException e) {
                System.out.println("Error with thread-" + i);
                e.printStackTrace();
            }
        }
        System.out.println("Ended in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public String search(String search_request) throws RemoteException {
        String[] words = search_request.split(" ");
        List<Path> result = new ArrayList<>(inverted_index.get(words[0]));
        for (int i = 1; i < words.length; i++) {
            List<Path> wordSearchResult = new ArrayList<>(inverted_index.get(words[i]));
            result = result.stream().distinct().filter(wordSearchResult::contains).toList();
        }
        return result.toString();
    }

    private List<Path> getListOfFiles() throws IOException {
        List<Path> filePaths = new ArrayList<>();
        filePaths.addAll(Files.list(Paths.get("C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb\\test\\neg")).filter(i -> parseFileID(i) >= small_folder_index_start && parseFileID(i) < small_folder_index_finish).toList());
        filePaths.addAll(Files.list(Paths.get("C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb\\test\\pos")).filter(i -> parseFileID(i) >= small_folder_index_start && parseFileID(i) < small_folder_index_finish).toList());
        filePaths.addAll(Files.list(Paths.get("C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb\\train\\neg")).filter(i -> parseFileID(i) >= small_folder_index_start && parseFileID(i) < small_folder_index_finish).toList());
        filePaths.addAll(Files.list(Paths.get("C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb\\train\\pos")).filter(i -> parseFileID(i) >= small_folder_index_start && parseFileID(i) < small_folder_index_finish).toList());
        filePaths.addAll(Files.list(Paths.get("C:\\Users\\Foxxx\\Desktop\\CourseWork\\aclImdb\\train\\unsup")).filter(i -> parseFileID(i) >= big_folder_index_start && parseFileID(i) < big_folder_index_finish).toList());
        return filePaths;
    }

    private int parseFileID(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int _i = fileName.indexOf("_");
        return Integer.parseInt(fileName.substring(0, _i));
    }

}
