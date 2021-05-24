package com.parallel;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.parallel.InvertedIndexManager.isBannedWord;
import static com.parallel.InvertedIndexManager.normalizeWord;

public class IndexCreatorThread extends Thread {
    ConcurrentHashMap<String, Set<Path>> inverted_index;
    List<Path> file_paths;
    Integer thread_index, max_threads;

    public IndexCreatorThread(ConcurrentHashMap<String, Set<Path>> inverted_index, List<Path> file_paths,
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
                        Set<Path> values = new HashSet<>();
                        values.add(path);
                        inverted_index.putIfAbsent(word, values);
                    } else {
                        Set<Path> values = inverted_index.get(word);
                        values.add(path);
                        inverted_index.replace(word, values);
                    }
            }
        }
    }

    private List<Path> getListOfFiles() {
        int block_size = file_paths.size() / max_threads;
        int from_index = thread_index * block_size;
        int to_index = thread_index == (max_threads - 1) ? (file_paths.size() - 1) : (thread_index + 1) * block_size;
        return file_paths.subList(from_index, to_index);
    }

}
