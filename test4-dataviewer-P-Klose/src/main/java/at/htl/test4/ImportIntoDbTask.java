package at.htl.test4;

import javafx.concurrent.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ImportIntoDbTask extends Task {
    String src;
    DataRepoo repo = new DataRepoo();

    public ImportIntoDbTask(String srcFile) {
        this.src = srcFile;
    }

    @Override
    protected Object call() throws Exception {
        if (src == null) {
            return null;
        }
        long zeilen = Files.lines(Path.of(src)).count();
        AtomicInteger i = new AtomicInteger(0);
        System.out.println("test");
        List<Boolean> mylist = Files.lines(Path.of(src))
                .skip(1)
                .map(s -> s.split(";"))
                .flatMap(strings -> {
                    System.out.println("InsertClient");
                    updateProgress(i.incrementAndGet(), zeilen);
                    Stream<Boolean> l = Stream.of(repo.insert(Integer.valueOf(strings[1]), strings[2], Integer.valueOf(strings[3]), Integer.valueOf(strings[4])));
                    return l;
                }).toList();
        repo.commit();


        return mylist;
    }
}
