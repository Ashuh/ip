package duke.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import duke.DukeException;

/**
 * Saves and loads Task data in String format.
 */
public class Storage {
    private static final String SAVE_DIR = "data";
    private static final String FILE_NAME = SAVE_DIR + "/duke.txt";

    /**
     * Saves the data within a list of Tasks into a .txt file.
     *
     * @param tasks The list of Tasks to be saved.
     */
    public void saveTasks(List<Task> tasks) {
        try {
            Path path = Paths.get(SAVE_DIR);
            Files.createDirectories(path);
            PrintWriter writer = new PrintWriter(FILE_NAME);

            for (Task task : tasks) {
                writer.println(task.toSaveData());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the Tasks stored in a .txt file into a list of tasks.
     *
     * @return The loaded list of Tasks.
     */
    public TaskList loadTasks() {
        TaskList tasks = new TaskList();
        Scanner scanner;

        try {
            File file = new File(FILE_NAME);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return tasks;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            tasks.add(parseTask(line));
        }

        return tasks;
    }

    private Task parseTask(String saveData) {
        String[] lineSplit = saveData.split(" \\| ");

        boolean isDone = lineSplit[1].equals("1");
        LocalDateTime dateTime;
        String description = lineSplit[2];

        switch (lineSplit[0]) {
        case "T":
            return new ToDo(description, isDone);
        case "E":
            dateTime = LocalDateTime.parse(lineSplit[3]);
            Duration duration = Duration.parse(lineSplit[4]);
            return new Event(description, dateTime, duration, isDone);
        case "D":
            dateTime = LocalDateTime.parse(lineSplit[3]);
            return new Deadline(description, dateTime, isDone);
        default:
            throw new DukeException("Invalid save data");
        }
    }
}
