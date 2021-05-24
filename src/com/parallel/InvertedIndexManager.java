package com.parallel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class InvertedIndexManager implements InvertedIndexInterface {
    private HashMap<String, List<Path>> invertedIndex = new HashMap<>();
    private static final int small_folder_index_start = 10500;
    private static final int small_folder_index_finish = 10750;
    private static final int big_folder_index_start = 42000;
    private static final int big_folder_index_finish = 43000;


    public void createInvertedIndex() throws IOException {
        List<Path> filePaths = getListOfFiles();
        for (Path path : filePaths) {
            Scanner scanner = new Scanner(path.toFile());
            while (scanner.hasNext()) {
                String word = normalizeWord(scanner.next());
                if (!isBannedWord(word))
                    if (!invertedIndex.containsKey(word)) {
                        List<Path> values = new ArrayList<>();
                        values.add(path);
                        invertedIndex.put(word, values);
                    } else
                        invertedIndex.get(word).add(path);
            }
        }
    }

    public String search(String search_request) throws RemoteException {
        String[] words = search_request.split(" ");
        List<Path> result = new ArrayList<>(invertedIndex.get(words[0]));
        for (int i = 1; i < words.length; i++) {
            List<Path> wordSearchResult = new ArrayList<>(invertedIndex.get(words[i]));
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

    private boolean isBannedWord(String word) {
        String[] bannedWords = {"", "a", "br", "hr"};
        return Arrays.asList(bannedWords).contains(word);
    }

    private String normalizeWord(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z]", "");
    }

}
