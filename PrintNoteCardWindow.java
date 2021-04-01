import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class PrintNoteCardWindow extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1845145580582029163L;

    private JTextArea previewTextArea = new JTextArea();
    private ArrayList<Group> groups;
    private Preview previewSelection = Preview.STANDARD;
    private static final String STANDARD_FORMAT = "Group: < Group >\nTitle: < Title >\n"
        + "Answer: < Answer >\n" + "\n" + "Title: < Title >\n" + "Answer: < Answer >\n" + "< ... >";
    private static final String STANDARD_TABBED_FORMAT = "Group: < Group >\n< Title >\n"
        + "    < Answer >\n" + "\n" + "< Title >\n" + "    < Answer >\n" + "< ... >";
    private static final String ALTERNATE_FORMAT =
        "Group: < Group >\nT: < Title: >     A: < Answer >\n" + "T: < Title: >     A: < Answer >\n"
            + "< ... >";
    private static final String NERD_ALERT_FORMAT =
        "GROUP < Group > {\nTITLE \n\t< Title >\n\nANSWER\n\t< Answer >\n}";

    private enum Preview {
        STANDARD(STANDARD_FORMAT), STANDARD_TABBED(STANDARD_TABBED_FORMAT), ALTERNATE(
            ALTERNATE_FORMAT), NERD_ALERT(NERD_ALERT_FORMAT), NONE(null);

        private String previewFormat;

        private Preview(String format) {
            this.previewFormat = format;
        }

        public String getPreviewFormat() {
            return previewFormat;
        }
    }

    private NoteCardWindow window;
    private JButton printBtn;
    private JCheckBox[] checkBoxes;

    public PrintNoteCardWindow(NoteCardWindow window, ArrayList<Group> groups,
        boolean superScreen) {

        this.window = window;
        this.groups = groups;
        setTitle("Print Preview");

        setAlwaysOnTop(superScreen);
        setLocationRelativeTo(window.getFrame());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        previewTextArea.setEditable(false);
        previewTextArea.add(new JScrollBar());

        JPanel radioBtnsPanel = new JPanel();
        radioBtnsPanel.setBorder(new LineBorder(Color.BLACK, 3));
        JRadioButton standardBtn = new JRadioButton("Stnd", true);

        PreviewSelect radioListener = new PreviewSelect();
        standardBtn.addActionListener(radioListener);
        standardBtn.setActionCommand("stnd");
        radioBtnsPanel.add(standardBtn);

        JRadioButton stndTabbedBtn = new JRadioButton("Stnd-Tab", false);
        stndTabbedBtn.addActionListener(radioListener);
        stndTabbedBtn.setActionCommand("stndTab");
        radioBtnsPanel.add(stndTabbedBtn);

        JRadioButton alternateBtn = new JRadioButton("Alt", false);
        alternateBtn.addActionListener(radioListener);
        alternateBtn.setActionCommand("alt");
        radioBtnsPanel.add(alternateBtn);

        JRadioButton nerdAlertBtn = new JRadioButton("Nerd", false);
        nerdAlertBtn.addActionListener(radioListener);
        nerdAlertBtn.setActionCommand("nerd");
        radioBtnsPanel.add(nerdAlertBtn);

        ButtonGroup previewBtns = new ButtonGroup();
        previewBtns.add(standardBtn);
        previewBtns.add(alternateBtn);
        previewBtns.add(stndTabbedBtn);
        previewBtns.add(nerdAlertBtn);

        JPanel checkBoxesPanel = new JPanel(new GridLayout(0, 1));
        JLabel groupsToPrintLabel = new JLabel("Groups To Print");
        Dimension size = new Dimension(100, 200);
        checkBoxesPanel.setPreferredSize(size);
        groupsToPrintLabel.setBorder(new LineBorder(Color.GRAY.darker(), 1));

        GroupSelect gs = new GroupSelect();
        checkBoxes = new JCheckBox[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            JCheckBox rb = new JCheckBox(groups.get(i).getName(), true);
            rb.addItemListener(gs);
            rb.setPreferredSize(size);
            checkBoxes[i] = rb;
            checkBoxesPanel.add(rb);
        }
        Dimension btnSize = new Dimension(60, 20);
        JButton clearAllBtn = new JButton("Clear");
        JButton selectAllBtn = new JButton("All");
        selectAllBtn.addActionListener(new SelectAllBoxes());
        selectAllBtn.setBorder(new LineBorder(Color.BLACK));
        selectAllBtn.setPreferredSize(btnSize);
        clearAllBtn.addActionListener(new ClearCheckBoxes());
        clearAllBtn.setBorder(new LineBorder(Color.BLACK));
        clearAllBtn.setPreferredSize(btnSize);
        FlowLayout fl = new FlowLayout();
        fl.setHgap(5);
        JPanel boxBtnPanel = new JPanel(fl);
        boxBtnPanel.add(selectAllBtn);
        boxBtnPanel.add(clearAllBtn);

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(checkBoxesPanel, BorderLayout.CENTER);
        eastPanel.add(groupsToPrintLabel, BorderLayout.NORTH);
        eastPanel.add(boxBtnPanel, BorderLayout.SOUTH);

        add(radioBtnsPanel, BorderLayout.NORTH);
        add(eastPanel, BorderLayout.EAST);
        add(previewTextArea, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        FlowLayout layout = new FlowLayout();
        layout.setHgap(30);
        southPanel.setLayout(layout);


        printBtn = new JButton("Print");
        JButton cancelBtn = new JButton("Cancel");
        printBtn.addActionListener(new PrintBtn());
        cancelBtn.addActionListener(new CancelBtn(this));
        southPanel.add(printBtn);
        southPanel.add(cancelBtn);

        add(southPanel, BorderLayout.SOUTH);

        previewTextArea.setText(STANDARD_FORMAT);

        pack();
        setVisible(true);
    }

    private class GroupSelect implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if (allFalse())
                printBtn.setEnabled(false);
            else if (!printBtn.isEnabled())
                printBtn.setEnabled(true);
        }

        /**
         * @return
         */
        private boolean allFalse() {
            for (JCheckBox state : checkBoxes) {
                if (state.isSelected())
                    return false;
            }
            return true;
        }

    }

    private class PreviewSelect implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String btnCmd = e.getActionCommand();
            switch (btnCmd) {
                case "stnd":
                    previewSelection = Preview.STANDARD;
                    break;
                case "alt":
                    previewSelection = Preview.ALTERNATE;
                    break;
                case "stndTab":
                    previewSelection = Preview.STANDARD_TABBED;
                    break;
                case "nerd":
                    previewSelection = Preview.NERD_ALERT;
                    break;
                default:
                    previewSelection = Preview.NONE;
                    break;
            }
            previewTextArea.setText(previewSelection.getPreviewFormat());
        }

    }

    private class CancelBtn implements ActionListener {

        private PrintNoteCardWindow window;

        public CancelBtn(PrintNoteCardWindow window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.dispose();
        }
    }

    private class PrintBtn implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String printout = "";
            String group = "";
            String title = "";
            String ans = "";
            Group gp = null;
            int lastCard = 0;

            for (int i = 0; i < groups.size(); i++) {
                if (!checkBoxes[i].isSelected())
                    continue;
                gp = groups.get(i);
                group = gp.getName();
                lastCard = gp.cardsSize() - 1;
                if (i > 0)
                    printout += "\n";
                for (int card = 0; card < gp.cardsSize(); card++) {
                    title = gp.getTitle(card);
                    ans = gp.getAnswer(card);
                    switch (previewSelection) {
                        case ALTERNATE:
                            if (card == 0)
                                printout += "Group: " + group + "\n";
                            printout += "T: " + title + "     A: " + ans + "\n";
                            break;
                        case NONE:
                            System.out.println(
                                "Broken in PrintNoteCardWindow: NONE selected for printing option.");
                            return;
                        case STANDARD:
                            if (card == 0)
                                printout += "Group: " + group + "\n";
                            printout += "Title: " + title + "\nAnswer: " + ans + "\n\n";
                            break;
                        case STANDARD_TABBED:
                            if (card == 0)
                                printout += "Group: " + group + "\n";
                            printout += title + "\n\t" + ans + "\n\n";
                            break;
                        case NERD_ALERT:
                            if (card == 0)
                                printout += "GROUP " + group + " {\n";
                            printout += "TITLE\n\t" + title + "\n\nANSWER\n\t" + ans + "\n";
                            if (card == lastCard)
                                printout += "}\n\n";
                            else
                                printout += "\n";
                            break;
                        default:
                            System.out.println(
                                "Error, could not determine print formatting. In default case.");
                            return;
                    }
                }
            }
            printFile(printout);
        }

        private void printFile(String printTxt) {
            String overwriteMsg = "This file already exists.\nWould you like to " + "overwrite it?";
            String owTitle = "Replace File?";

            String msg = "What would you like to name the printed file?";
            String title = "File Name";

            String errorMsg = "There was an unknown error saving the file. Please try again.\nIf "
                + "the error persists, you may be entering an illegal character";
            String errorTitle = "Error Saving File";

            String fileName = "";
            boolean saved = false;
            FileWriter printer = null;
            do {
                fileName = JOptionPane.showInputDialog(window.getFrame(), msg, title,
                    JOptionPane.QUESTION_MESSAGE);
                if (fileName == null)
                    return;
                else if (fileName.trim().equals(""))
                    continue;
                try {
                    if (window.fileNameIsRestricted(fileName))
                        continue;
                    File saveFile = new File(fileName);
                    if (saveFile.exists()) {
                        int choice = JOptionPane.showConfirmDialog(window.getFrame(), overwriteMsg,
                            owTitle, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        switch (choice) {
                            case JOptionPane.YES_OPTION:
                                break;
                            case JOptionPane.NO_OPTION:
                                continue;
                            case JOptionPane.CANCEL_OPTION:
                                return;
                            default:
                                return;
                        }
                    }
                    printer = new FileWriter(saveFile);
                    printer.write(printTxt);
                    saved = true;
                } catch (IOException e) {
                    System.out.println("System error trying to save file.");
                    JOptionPane.showConfirmDialog(window.getFrame(), errorMsg, errorTitle,
                        JOptionPane.INFORMATION_MESSAGE, JOptionPane.WARNING_MESSAGE);
                } finally {
                    if (printer != null)
                        try {
                            printer.close();
                        } catch (IOException e) {
                            System.out.println(
                                "Failed to close printer: RESOURCE LEAK ERROR: " + e.getMessage());
                        }
                }
            } while (!saved);
            System.out.println("Saved file as: \"" + fileName + "\" succesfully.");
            dispose();
        }

    }

    private class SelectAllBoxes implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < checkBoxes.length; i++) {
                checkBoxes[i].setSelected(true);
            }
        }

    }

    private class ClearCheckBoxes implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < checkBoxes.length; i++) {
                checkBoxes[i].setSelected(false);
            }
        }

    }
}
