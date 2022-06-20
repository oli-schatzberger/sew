package at.htl.test4;

import javafx.concurrent.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LoadDataTask extends Task {
    String gemeinde;
    String src;

    public LoadDataTask(String gemeinde, String src) {
        this.gemeinde = gemeinde;
        this.src = src;
    }

    @Override
    protected List<Integer> call() throws Exception {

        if (src == null) {
            return null;
        }
        long zeilen = Files.lines(Path.of(src)).count();
        AtomicInteger i = new AtomicInteger(0);
        return Files.lines(Path.of(src))
                .filter(s -> s.contains(gemeinde))
                .map(s -> s.split(";"))
                .flatMap(strings -> {
                    updateProgress(i.incrementAndGet(), zeilen);
                    Stream<Integer> s = Stream.of(Integer.valueOf(strings[3]), Integer.valueOf(strings[4]));
                    return s;
                }).toList();

    }
}
