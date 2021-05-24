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
    List<Path> filePaths;
    Integer threadIdx, maxThreads;

    public IndexCreatorThread(ConcurrentHashMap<String, List<Path>> inverted_index, List<Path> filePaths,
                              Integer threadIdx, Integer maxThreads) {
        this.inverted_index = inverted_index;
        this.filePaths = filePaths;
        this.threadIdx = threadIdx;
        this.maxThreads = maxThreads;
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
        int block_size = filePaths.size() / maxThreads;
        int from_index = threadIdx * block_size;
        int to_index = threadIdx == (maxThreads - 1) ? (filePaths.size() - 1) : (threadIdx + 1) * block_size;
        return filePaths.subList(from_index, to_index);
    }

    private boolean isBannedWord(String word) {
        String[] bannedWords = {"", "a", "br", "hr"};
        return Arrays.asList(bannedWords).contains(word);
    }

    private String normalizeWord(String word) {
        return word.toLowerCase().replaceAll("[^a-zA-Z]", "");
    }

}
