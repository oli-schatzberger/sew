package at.htl.test4;

import javafx.concurrent.Task;
import javafx.scene.control.ChoiceBox;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ImportTask extends Task<List<String>> {

    String src;

    public ImportTask(File file) {
        this.src = file.getAbsolutePath();
    }

    @Override
    protected List<String> call() throws Exception {
        //System.out.println(src);
        long zeilen = Files.lines(Path.of(src)).count() - 1;
        AtomicInteger i = new AtomicInteger(0);
        List<String> mylist = Files.lines(Path.of(src))
                .skip(1)
                .map(s -> s.split(";"))
                .map(strings -> {
                    updateProgress(i.incrementAndGet(),zeilen);
                    return strings[2];
                })
                /*.flatMap(strings -> {
                    updateProgress(i.incrementAndGet(),zeilen);
                    Stream<String> l= Stream.of(strings[2]);
                    return l;
                })*/
                .sorted()
                .distinct()
                .toList();

        //mylist.stream().forEach(System.out::println);

        return mylist;

    }
}
