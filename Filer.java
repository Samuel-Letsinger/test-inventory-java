import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Filer {
  public String readFile(String filepath) {
    StringBuilder content = new StringBuilder();
    try {
      File file = new File(filepath);
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        content.append(scanner.nextLine()).append("\n");
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + filepath);
      e.printStackTrace();
    }
    return content.toString();
  }

  public void writeToFile(String filepath, String newContents) {
    try {
      FileWriter writer = new FileWriter(filepath);
      writer.write(newContents);
      writer.close();
    } catch (IOException e) {
      System.out.println("Error writing to file: " + filepath);
      e.printStackTrace();
    }
  }

  public void appendToFile(String filepath, String newContents) {
    try {
      FileWriter writer = new FileWriter(filepath, true);
      writer.write(newContents + "\n");
      writer.close();
    } catch (IOException e) {
      System.out.println("Error appending to file: " + filepath);
      e.printStackTrace();
    }
  }
}
