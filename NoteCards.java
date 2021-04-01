import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class NoteCards {
    private static NoteCardWindow window;

    static String fileName = "NoteCards.txt";
    private static final String VERSION = "Beta 10.0";
    private static final String LINK =
        "https://drive.google.com/file/d/1xbD3Dn2JiM-iGGHi3uzKLNIx-63tS-zY/view?usp=sharing";
    private static ArrayList<Group> groups = new ArrayList<>();

    public static void main(String[] args) {
        window = new NoteCardWindow(VERSION, LINK);
        load();
        window.initializeVars(groups);
    }


    private static void load() {
        File file = new File(fileName);
        if (!file.exists()) {
            File unixFile = new File("." + fileName);
            if (unixFile.exists())
                file = unixFile;
        }
        System.out.println("Attempting to load from: " + file.getName());
        try {
            readFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("File did not exist in current directory: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException encountered: " + e.getMessage());
        }
    }

    public static boolean readFile(File file) throws IOException {
        Scanner mainScan = new Scanner(file);
        if (!mainScan.hasNextLine()) {
            closeScanner(mainScan);
            throw new IOException("File is empty... Skipping. Loading unnessesary.");
        }
        if (!mainScan.next().equals("NoteCards.mem")) {
            closeScanner(mainScan);
            throw new IOException("The memory file passed was not a NoteCards memory file.");
        }
        if (groups != null && !groups.isEmpty())
            groups.clear();
        while (mainScan.hasNext()) {
            String word = mainScan.next();

            switch (word) {
                case "<group>":
                    String groupName = mainScan.next();
                    // Parsing name
                    String[] unSavedName = groupName.split("_");
                    groupName = "";
                    for (String str : unSavedName) {
                        groupName += str + " ";
                    }
                    groupName.trim();
                    System.out.println("Found group: " + groupName);
                    Group group = new Group(groupName, window);
                    groups.add(group);
                    String groupContent = readUntil("<group>", mainScan);
                    Scanner contentScan = new Scanner(groupContent);
                    while (contentScan.hasNext()) {
                        String contentWord = contentScan.next();
                        switch (contentWord) {
                            case "<title>":
                                String readTitle = readUntil("<title>", contentScan);
                                group.addTitle(readTitle.trim());
                                break;
                            case "<ans>":
                                String readAns = readUntil("<ans>", contentScan);
                                group.addAnswer(readAns.trim());
                                break;
                            default:
                                System.out.println("Word: " + contentWord
                                    + " does not match keywords in contentScan");
                        }
                    }
                    break;
                default:
                    System.out.println("Word: " + word + " does not match keywords in fileScan.");
            }
            System.out.println("Groups Num: " + groups.size());
            for (int i = 0; i < groups.size(); i++) {
                System.out.println("Group " + i + ": " + groups.get(i).getName());
            }

        }
        if (groups.size() == 0) {
            window.addGroup("Default");
        }
        closeScanner(mainScan);
        return true;
    }

    public static boolean saveFile(String saveName) {
        boolean export = false;
        System.out.println("Saving Application.");
        if (!saveName.equals(fileName) && !saveName.equals("." + fileName)) {
            saveName += ".txt";
            export = true;
        }
        File file = new File(saveName);
        if (!export) {
            if (file.exists())
                file.delete();
            else {
                File unixFile = new File("." + saveName);
                if (unixFile.exists())
                    unixFile.delete();
            }
        } else {
            if (file.exists()) {
                String owMsg = "A file of this name already exists. Do yo want to overwrite it?";
                String owTitle = "Overwrite File?";
                int choice = JOptionPane.showConfirmDialog(window.getFrame(), owMsg, owTitle,
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                switch (choice) {
                    case JOptionPane.YES_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        return false;
                    default:
                        return false;
                }
            }
        }
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);
            writer.write("NoteCards.mem");
            for (int g = 0; g < groups.size(); g++) {

                writer.write(" \n<group>\n " + groups.get(g).getSaveName() + " ");

                for (int i = 0; i < groups.get(g).cardsSize(); i++) {

                    writer.write(" \n<title>\n " + groups.get(g).getTitle(i)
                        + " \n<title>\n \n<ans>\n " + groups.get(g).getAnswer(i) + " \n<ans>\n ");
                }

                writer.write(" \n<group>\n ");

            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                System.out.println("Error closing writer: " + e.getMessage());
                return false;
            }
        }
        if (saveName.equals(fileName))
            hide(file);
        return true;
    }

    /**
     * @param string
     * @return
     */
    private static String readUntil(String stopStr, Scanner mainScan) {
        String line = " ";
        String card = " ";

        while (mainScan.hasNextLine()) {
            line = mainScan.nextLine();
            if (line.equals(stopStr)) {
                break;
            }
            card += line + "\n";
        }

        return card;
    }

    private static void closeScanner(Scanner scan) {
        if (scan != null)
            scan.close();
    }

    private static void hide(File file) {

        String os = System.getProperty("os.name");
        if (os.indexOf("Windows") >= 0) {

            try {
                Files.setAttribute(file.toPath(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else if (os.indexOf("Unix") >= 0 || os.indexOf("Mac") >= 0) {
            System.out.println("Registered as Linux or Mac");

            if (file.renameTo(new File("." + file.getName())))
                System.out.println("Succesfully changed file in Unix System.");
            else
                System.out.println("Failed to rename file in Unix System.");
        }

    }
}
