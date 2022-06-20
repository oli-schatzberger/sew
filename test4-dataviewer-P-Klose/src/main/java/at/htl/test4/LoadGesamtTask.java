package at.htl.test4;

import javafx.concurrent.Task;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadGesamtTask extends Task<List<Stream<Integer>>> {
    String src;

    @Override
    protected List<Stream<Integer>> call() throws Exception {

        if (src == null){

            return null;
        }
        long zeilen = Files.lines(Path.of(src)).count();
        AtomicInteger i = new AtomicInteger(0);
        return Files.lines(Path.of(src))
                .skip(1)
                .map(s -> s.split(";"))
                .collect(Collectors.groupingBy(strings -> strings[3]))
                .entrySet().stream()
                .map(stringListEntry -> {
                    String year = stringListEntry.getKey();
                    int population = stringListEntry.getValue().stream()
                            .map(strings -> Integer.valueOf(strings[4]))
                            .collect(Collectors.summingInt(Integer::intValue));
                    Stream<Integer> s= Stream.of(Integer.valueOf(year),Integer.valueOf(population));
                    return s;
                }).toList();


                /*
                .flatMap(strings -> {
                    updateProgress(i.incrementAndGet(),zeilen);
                    Stream<Integer> s= Stream.of(Integer.valueOf(strings[3]),Integer.valueOf(strings[4]));
                    return s;
                }).toList();*/

    }

    public LoadGesamtTask(String srcFile) {
        this.src = srcFile;
    }
}
