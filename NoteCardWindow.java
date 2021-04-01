import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class NoteCardWindow implements WindowListener {
    private JTextPane cardText = new JTextPane();
    private JLabel numberOfCardsLabel = new JLabel();
    private JLabel groupLabel = new JLabel();
    private JFrame frame;

    private boolean superScreen = true;
    private boolean toolbarView = false;
    private final Dimension fullView = new Dimension(900, 500);
    private ArrayList<Group> groups;
    private Group group;
    private boolean needToSort = true;
    String link = null;
    private final String toolbarTxt = "Toggle Toolbar: ";
    private final String superScreenTxt = "Toggle Always Show Card: ";

    JButton nextBtn;
    JButton prevBtn;
    JButton flipBtn;
    JMenuItem editBtn;
    JMenuItem deleteBtn;
    JMenu groupMenu;
    JMenuItem toolbar;
    JMenuItem hideableBtn;

    public NoteCardWindow(ArrayList<Group> groups, Group group, String link) {
        frame = new JFrame("Note Cards App");
        this.groups = groups;
        this.group = group;
        this.link = link;

        initialize();
        showCard();
    }

    public NoteCardWindow(String version, String link) {
        frame = new JFrame("Note Cards App ~" + version);
        this.link = link;
    }

    public void initializeVars(ArrayList<Group> groups) {
        this.groups = groups;
        initialize();
        showCard();
    }

    public void initialize() {
        if (groups == null) {
            System.out.println("Groups is not initialized");
            groups = new ArrayList<Group>();
        }
        if (group == null) {
            System.out.println("Group is not initialized");
            if (groups.size() < 1)
                addGroup("Default");
            group = groups.get(0);
            group.emptyCheck();
        }

        frame.addWindowListener(this);
        frame.setSize(fullView);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setAlwaysOnTop(superScreen);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.add(menuBar, BorderLayout.NORTH);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setBorder(new LineBorder(Color.BLACK));
        menuBar.add(fileMenu);

        groupMenu = new JMenu("Groups");
        groupMenu.setBorder(new LineBorder(Color.BLACK));
        menuBar.add(groupMenu);

        menuBar.add(groupLabel);

        JMenuItem addGroup = new JMenuItem("Add Group");
        addGroup.addActionListener(new AddGroupBtn());
        addGroup.setBorder(new LineBorder(Color.GREEN));
        menuBar.add(addGroup);

        JMenuItem deleteGroup = new JMenuItem("Delete Group");
        deleteGroup.addActionListener(new DeleteGroupBtn());
        deleteGroup.setBorder(new LineBorder(Color.MAGENTA));
        menuBar.add(deleteGroup);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new SaveBtn());
        save.setBorder(new LineBorder(Color.BLUE));
        fileMenu.add(save);

        JMenuItem renameGroupBtn = new JMenuItem("Rename Group");
        renameGroupBtn.addActionListener(new RenameGroupBtn());
        renameGroupBtn.setBorder(new LineBorder(Color.YELLOW.darker()));
        fileMenu.add(renameGroupBtn);

        JMenuItem deleteAll = new JMenuItem("Delete All Note Cards");
        deleteAll.addActionListener(new DeleteAllBtn());
        deleteAll.setBorder(new LineBorder(Color.RED));
        fileMenu.add(deleteAll);

        JMenuItem deleteAllGroups = new JMenuItem("Delete All Groups");
        deleteAllGroups.addActionListener(new DeleteAllGroupsBtn());
        deleteAllGroups.setBorder(new LineBorder(Color.MAGENTA));
        fileMenu.add(deleteAllGroups);

        JMenuItem newNoteCard = new JMenuItem("New Note Cards");
        newNoteCard.addActionListener(new CreateNoteCard());
        newNoteCard.setBorder(new LineBorder(Color.BLUE));
        menuBar.add(newNoteCard);

        editBtn = new JMenuItem("Edit Note Card");
        editBtn.addActionListener(new EditBtn(this));
        editBtn.setBorder(new LineBorder(Color.YELLOW));
        menuBar.add(editBtn);

        deleteBtn = new JMenuItem("Delete Note Card");
        deleteBtn.addActionListener(new DeleteBtn());
        deleteBtn.setBorder(new LineBorder(Color.RED));
        menuBar.add(deleteBtn);

        JMenuItem searchByTitle = new JMenuItem("Search Cards");
        searchByTitle.addActionListener(new SearchByTitle());
        searchByTitle.setBorder(new LineBorder(Color.GRAY));
        fileMenu.add(searchByTitle);

        JMenuItem importBtn = new JMenuItem("Import");
        importBtn.addActionListener(new Importer());
        importBtn.setBorder(new LineBorder(Color.GRAY));
        fileMenu.add(importBtn);

        JMenuItem exportBtn = new JMenuItem("Export");
        exportBtn.addActionListener(new Exporter());
        exportBtn.setBorder(new LineBorder(Color.GRAY));
        fileMenu.add(exportBtn);

        JMenuItem printBtn = new JMenuItem("Print");
        printBtn.addActionListener(new PrintBtn(this));
        printBtn.setBorder(new LineBorder(Color.GRAY));
        fileMenu.add(printBtn);

        toolbar = new JMenuItem();
        toolbar.addActionListener(new ToggleToolbar());
        fileMenu.add(toolbar);
        changeState(toolbar, toolbarView, toolbarTxt);

        hideableBtn = new JMenuItem();
        hideableBtn.addActionListener(new ToggleHideable());
        fileMenu.add(hideableBtn);
        changeState(hideableBtn, superScreen, superScreenTxt);

        JMenuItem updateLink = new JMenuItem("Get Lastest Version");
        updateLink.addActionListener(new UpdateLink(link));
        updateLink.setBorder(new LineBorder(Color.CYAN));
        fileMenu.add(updateLink);

        menuBar.add(numberOfCardsLabel);

        nextBtn = new JButton("->");
        prevBtn = new JButton("<-");
        nextBtn.addActionListener(new NextBtn());
        prevBtn.addActionListener(new PrevBtn());
        frame.add(nextBtn, BorderLayout.EAST);
        frame.add(prevBtn, BorderLayout.WEST);
        flipBtn = new JButton("Flip");
        flipBtn.addActionListener(new FlipBtn());

        cardText.selectAll();
        cardText.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        cardText.setAlignmentY(JFrame.CENTER_ALIGNMENT);
        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setLineSpacing(attribs, 0.2f);
        StyleConstants.setFontSize(attribs, 20);
        StyleConstants.setFontFamily(attribs, "Calibri");
        cardText.setParagraphAttributes(attribs, true);
        cardText.setMargin(new Insets(35, 15, 35, 15));
        frame.add(flipBtn, BorderLayout.SOUTH);
        JScrollPane cardPane = new JScrollPane(cardText);
        frame.add(cardPane, BorderLayout.CENTER);

        // cardText options
        cardText.setEditable(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateGroupMenu();
    }

    private void changeState(JMenuItem item, boolean value, String baseTxt) {
        if (value) {
            item.setText(baseTxt + "ON");
            item.setBorder(new LineBorder(Color.BLUE, 3));
        } else {
            item.setText(baseTxt + "OFF");
            item.setBorder(new LineBorder(Color.GRAY, 3));
        }
    }

    /**
     * 
     */
    public void updateGroupMenu() {
        if (groupMenu == null)
            return;
        groupMenu.removeAll();
        if (needToSort)
            sort();
        for (int i = 0; i < groups.size(); i++) {
            Group obj = groups.get(i);
            groupMenu.add(obj);
        }

    }

    public boolean addGroup(String groupName) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().trim().equals(groupName.trim())) {
                JOptionPane.showMessageDialog(frame,
                    "Cannot make a group with the same name as an already existing group!",
                    "Group Name Matches Existing Name", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        groups.add(new Group(groupName, this));
        needToSort = true;
        showCard();
        return true;
    }

    /**
     * 
     */
    private void deleteAllCards() {
        group.deleteAllCards();
        showCard();
    }

    private void deleteCurrentCard() {
        group.deleteCard();
        showCard();
    }

    public void changeGroup(String groupName) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().equals(groupName)) {
                group = groups.get(i);
                System.out.println("Found group");
                showCard();
                return;
            }
        }
        System.out.println("Could not find group specified");
    }

    public void showCard() {
        if (group == null)
            return;
        if (groupMenu != null)
            updateGroupMenu();
        System.out.println("Showing card");
        String displayName = group.getName();
        if (displayName.length() > 10) {
            displayName = displayName.substring(0, 8).trim() + "...";
        }
        groupLabel.setText("Working Group: " + displayName + "  ");

        if (!toolbarView)
            numberOfCardsLabel.setText((group.getCard() + 1) + " out of " + group.cardsSize());
        else
            numberOfCardsLabel.setText("Cards: " + group.cardsSize());
        if (!group.isAnswer())
            cardText.setText(group.getTitle());
        else
            cardText.setText(group.getAnswer());
    }

    private void sort() {
        needToSort = false;
        System.out.println("Sorting groups...");
        for (int i = 0; i < groups.size(); i++) {
            Group curObj = groups.get(i);
            int a = i;
            while (a > 0 && 1 == curObj.compareTo(groups.get(a - 1))) {
                Group temp = groups.get(a - 1);
                groups.set(a - 1, curObj);
                groups.set(a, temp);
                a--;
            }
        }
        System.out.println("Groups sorted.");
    }

    public String getText() {
        return cardText.getText();
    }

    private void deleteGroup() {
        needToSort = true;
        groups.remove(group);
        if (groups.size() > 0)
            group = groups.get(0);
        if (groups.size() == 0)
            addGroup("Default");
        showCard();
    }

    private void deleteAllGroups() {
        groups.clear();
        String defaultGroup = "Default";
        addGroup("Default");
        changeGroup(defaultGroup);
        showCard();
    }

    private class DeleteGroupBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete the current group?", "Delete Group?",
                JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION)
                deleteGroup();

        }

    }

    private class DeleteAllGroupsBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int choice =
                JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete all groups?",
                    "Delete All Groups", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION)
                deleteAllGroups();
        }

    }

    private class DeleteBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this note card?", "Delete Current Note Card",
                JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION)
                deleteCurrentCard();
        }

    }

    private void openNoteCardAdder() {
        new NoteCardCreator(frame, this, superScreen);
    }

    public void readNoteCardsFromNoteCardAdder(ArrayList<String> titles,
        ArrayList<String> answers) {
        for (int i = 0; i < titles.size(); i++) {
            if (!titles.get(i).trim().equals("") && !answers.get(i).trim().equals(""))
                group.add(titles.get(i), answers.get(i));
            else
                System.out.println("Did not add the blank card.");
        }
        showCard();
    }

    public void editCard(String newText) {
        if (!group.isAnswer())
            group.setTitle(group.getCard(), newText);
        else
            group.setAnswer(group.getCard(), newText);
        showCard();
    }

    private class AddGroupBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean added = false;
            String groupName = "";
            while (!added) {
                groupName = getValidNameInput();
                if (groupName != null)
                    added = addGroup(groupName);
                else
                    added = true;
            }
        }

    }

    private String getValidNameInput() {
        String groupName = "";
        do {
            groupName = JOptionPane.showInputDialog(frame, "What do you want to call this group?",
                "Group Name", JOptionPane.QUESTION_MESSAGE);
            if (groupName == null)
                break;
            if (groupName.length() > 30)
                JOptionPane.showMessageDialog(frame,
                    "Group name was too long. What the hell do you need more than 30 characters for.",
                    "Group Name Exceeds 30 Characters", JOptionPane.WARNING_MESSAGE);

        } while (groupName.length() <= 0 || groupName.length() > 30);
        return groupName;
    }

    /**
     * 
     */
    private void saveFile() {
        String choice = "";
        String msg = ("Could not save the file. There may have been "
            + "a file with the same name or the system encountered an error. "
            + "Please try again.");
        String prompt = "What would you like to name the file?";

        boolean saved = false;
        do {
            choice = JOptionPane.showInputDialog(frame, prompt, "Save File",
                JOptionPane.QUESTION_MESSAGE);
            if (choice == null)
                return;
            if (fileNameIsRestricted(choice))
                continue;
            saved = NoteCards.saveFile(choice);
            if (!saved)
                JOptionPane.showMessageDialog(frame, msg, "Failed to Save!",
                    JOptionPane.WARNING_MESSAGE);
        } while (!saved);

    }

    public boolean fileNameIsRestricted(String choice) {
        if (choice.equals("NoteCards") || choice.equals("NoteCards.txt")) {
            showReservedFileNameError();
            return true;
        }
        return false;
    }

    /**
     * 
     */
    private void showReservedFileNameError() {
        String msg = "This fileName is unavailable to users of NoteCards.\n"
            + "Please use a different name.";
        String title = "Restricted Name Error";
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE);

    }

    private class DeleteAllBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete all note cards in this group?",
                "Delete All Confirmation", JOptionPane.OK_CANCEL_OPTION);
            if (choice == JOptionPane.OK_OPTION)
                deleteAllCards();

        }

    }

    private class NextBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            group.nextCard();
            showCard();
        }

    }

    private class PrevBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            group.prevCard();
            showCard();
        }

    }

    private class FlipBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            group.flipCard();
            showCard();
        }

    }

    private class SaveBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            NoteCards.saveFile(NoteCards.fileName);

        }

    }

    private class CreateNoteCard implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            openNoteCardAdder();
        }

    }

    private class ToggleToolbar implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!toolbarView)
                toolbarView = true;
            else
                toolbarView = false;
            changeState(toolbar, toolbarView, toolbarTxt);
            showNoteCard(!toolbarView);

        }

    }

    private class RenameGroupBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String groupName = getValidNameInput();
            if (groupName != null) {
                group.rename(groupName);
                showCard();
            }
        }

    }

    private class SearchByTitle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = group.getTitleSearchOptions();

            Object choice = JOptionPane.showInputDialog(frame, "Choose from the cards listed.",
                "Search", JOptionPane.QUESTION_MESSAGE, null, options, "Card Options.");
            if (choice == null)
                return;
            String choiceStr = new String(choice.toString());
            for (String option : options) {
                if (option.equals(choiceStr)) {
                    int choiceNum =
                        Integer.parseInt(choiceStr.substring(5, choiceStr.indexOf(":")));
                    group.goTo(choiceNum - 1);
                    showCard();
                }
            }

        }

    }

    private class EditBtn implements ActionListener {

        private NoteCardWindow window;

        public EditBtn(NoteCardWindow window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NoteCardEdit editor = new NoteCardEdit(frame, window.getText(), superScreen);
            window.editCard(editor.getEdit());

        }

    }

    private class Exporter implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Exporting Save File...");
            saveFile();
        }

    }

    private class Importer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Importing Save File...");
            String choice = "";
            String msg =
                ("That import did not work. The file may not have been a NoteCard memory file, or it was corrupted."
                    + "Please try again.");
            boolean imported = false;
            String directory = System.getProperty("user.dir");
            System.out.println("The current directory is: " + directory);
            File dir = new File(directory);
            File[] dirFiles = dir.listFiles();
            String[] options = new String[dirFiles.length];
            for (int i = 0; i < dirFiles.length; i++) {
                options[i] = dirFiles[i].getName();
            }

            do {
                choice = (String) JOptionPane.showInputDialog(frame,
                    "What file would you like to import?\n"
                        + "(Files are listed from directory of application).",
                    "Import File selector", JOptionPane.QUESTION_MESSAGE, null, options, null);
                if (choice == null) {
                    System.out.println("Canceling import.");
                    return;
                }
                try {
                    int saveChoice = JOptionPane.showConfirmDialog(frame, "Would you "
                        + "like to save the current NoteCard File before you import a new "
                        + "one? Any cards created past when the program was started will be lost otherwise.",
                        "Save NoteCards?", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (saveChoice == JOptionPane.YES_OPTION)
                        saveFile();
                    else if (saveChoice == JOptionPane.CANCEL_OPTION) {
                        System.out.println("Canceling import.");
                        return;
                    }
                    imported = NoteCards.readFile(new File(choice));
                } catch (IOException e1) {
                    System.out.println("Error reading file: " + e1.getMessage());
                    imported = false;
                }
                if (!imported)
                    JOptionPane.showMessageDialog(frame, msg, "Failed to Import the File!",
                        JOptionPane.WARNING_MESSAGE);
            } while (!imported);
            showCard();
        }

    }

    private class PrintBtn implements ActionListener {

        private NoteCardWindow window;

        public PrintBtn(NoteCardWindow window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new PrintNoteCardWindow(window, groups, superScreen);
        }

    }

    private class UpdateLink implements ActionListener {

        private URL link = null;

        public UpdateLink(String link) {
            try {
                this.link = new URL(link);
            } catch (MalformedURLException e) {
                System.out.println("Link not valid.");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            openLink(link);
        }

    }

    private class ToggleHideable implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (superScreen)
                superScreen = false;
            else
                superScreen = true;
            changeState(hideableBtn, superScreen, superScreenTxt);
            frame.setAlwaysOnTop(superScreen);
        }

    }

    private void openLink(URL link) {

        boolean error = false;
        if (link == null) {
            JOptionPane.showMessageDialog(frame, "Error, link could not be found.", "Link Error",
                JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(link.toURI());
            } catch (HeadlessException e) {
                error = true;
                System.out.println("Computer is monitorless. " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                error = true;
                System.out.println("System did not allow access: " + e.getMessage());
            } catch (IOException e) {
                error = true;
                System.out.println("IOException occured: " + e.getMessage());
            } catch (URISyntaxException e) {
                error = true;
                System.out.println("URI was incorrectly formated: " + e.getMessage());
            } finally {
                if (error)
                    JOptionPane.showMessageDialog(frame,
                        "Error, computer did not allow access to browser.\n" + "Here is the link:\n"
                            + link.getPath());
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // Do nothing

    }

    /**
     * @param fullMenuView
     */
    public void showNoteCard(boolean fullMenuView) {
        prevBtn.setVisible(fullMenuView);
        nextBtn.setVisible(fullMenuView);
        flipBtn.setVisible(fullMenuView);
        cardText.setVisible(fullMenuView);
        deleteBtn.setVisible(fullMenuView);
        editBtn.setVisible(fullMenuView);
        if (!fullMenuView)
            frame.pack();
        else
            frame.setSize(fullView);
        showCard();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int choice = JOptionPane.showConfirmDialog(frame,
            "Do you want to exit and save? Unsaved content will be unrecoverable.",
            "Exit and Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            NoteCards.saveFile(NoteCards.fileName);
            exit();
        } else if (choice == JOptionPane.NO_OPTION)
            exit();
        else
            return;
    }

    private void exit() {
        System.out.println("Exiting Application.");
        frame.dispose();
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // Do nothing

    }

    /**
     * @return
     */
    public JFrame getFrame() {
        return frame;
    }
}
