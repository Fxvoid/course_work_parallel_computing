package com.parallel;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class IndexCreatorThread extends Thread {
    ConcurrentHashMap<String, List<Path>> inverted_index;
    List<Path> file_paths;
    Integer thread_index, max_threads;

    public IndexCreatorThread(ConcurrentHashMap<String, List<Path>> inverted_index, List<Path> file_paths,
                              Integer thread_index, Integer max_threads) {
        this.inverted_index = inverted_index;
        this.file_paths = file_paths;
        this.thread_index = thread_index;
        this.max_threads = max_threads;
    }

    @Override
    public void run() {
        List<Path> files = getListOfFiles();
        for (Path path : files) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(path.toFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (scanner.hasNext()) {
                String word = normalizeWord(scanner.next());
                if (!isBannedWord(word))
                    if (!inverted_index.containsKey(word)) {
                        List<Path> values = new ArrayList<>();
                        values.add(path);
                        inverted_index.putIfAbsent(word, values);
                    } else
                        inverted_index.get(word).add(path);
            }
        }
    }

    private List<Path> getListOfFiles() {
        int block_size = file_paths.size() / max_threads;
        int from_index = thread_index * block_size;
        int to_index = thread_index == (max_threads - 1) ? (file_paths.size() - 1) : (thread_index + 1) * block_size;
        return file_paths.subList(from_index, to_index);
    }

    private boolean isBannedWord(String word) {
        String[] banned_words = {"", "a", "br", "hr"};
        return Arrays.asList(banned_words).contains(word);
    }

    private String normalizeWord(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z]", "");
    }

}
