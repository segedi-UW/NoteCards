import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

public class NoteCardCreator extends JDialog implements WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1007166695881593053L;

    private JTextArea answerArea = new JTextArea();
    private JTextArea titleArea = new JTextArea();

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();

    public NoteCardCreator(JFrame frame, NoteCardWindow window, boolean superScreen) {
        requestFocus();
        setLayout(new BorderLayout());

        JScrollPane titleScroll = new JScrollPane(titleArea);
        JScrollPane answerScroll = new JScrollPane(answerArea);


        JMenuBar menuBar = new JMenuBar();
        add(menuBar, BorderLayout.NORTH);

        JMenuItem addCard = new JMenuItem("Add Note Card");
        addCard.addActionListener(new AddCard());
        menuBar.add(addCard);

        JMenuItem exit = new JMenuItem("Exit and Save");
        exit.addActionListener(new Exit(this, window));
        menuBar.add(exit);

        /*
         * gridx The initial gridx value.
         * gridy The initial gridy value.
         * gridwidth The initialgridwidth value.
         * gridheight The initial gridheight value.
         * weightx The initial weightx value.
         * weighty The initial weighty value.
         * anchor The initial anchor value.
         * fill The initial fill value.
         * insets The initial insets value.
         * ipadx The initial ipadx value.
         * ipady The initial ipady value
         */
        GridBagConstraints gC = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 20, 0);
        GridBagLayout gL = new GridBagLayout();

        JPanel panel = new JPanel(gL);

        JLabel titleLabel = new JLabel("Front Side of Note Card");
        JLabel answerLabel = new JLabel("Back Side of Note Card");

        gL.setConstraints(titleLabel, gC);
        gC.gridx = 1;
        gL.setConstraints(answerLabel, gC);
        gC.gridx = 0;
        gC.gridy = 1;
        gC.weighty = 1;
        gC.weightx = 1;
        gL.setConstraints(titleScroll, gC);
        gC.gridx = 1;
        gL.setConstraints(answerScroll, gC);

        panel.add(titleLabel);
        panel.add(answerLabel);
        panel.add(titleScroll);
        panel.add(answerScroll);

        add(panel, BorderLayout.CENTER);

        titleArea.setToolTipText("Note Card Title or Question");
        answerArea.setToolTipText("Note Card Answer");
        titleArea.setLineWrap(true);
        answerArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        answerArea.setWrapStyleWord(true);
        titleArea.setMaximumSize(new Dimension(250, 300));
        answerArea.setMaximumSize(new Dimension(250, 300));
        titleArea.setBorder(new LineBorder(Color.BLACK, 2));
        answerArea.setBorder(new LineBorder(Color.BLACK, 2));

        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(superScreen);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private class AddCard implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (answerArea.getText() != null && titleArea.getText() != null
                && answerArea.getText().trim() != "" && titleArea.getText().trim() != "") {
                System.out.println("Adding: " + titleArea.getText());
                System.out.println("Adding to back: " + answerArea.getText());
                titles.add(titleArea.getText().trim());
                answers.add(answerArea.getText().trim());
                answerArea.setText("");
                titleArea.setText("");
                System.out.println("Note card added successfully.");
            } else
                System.out.println("ERROR: Note card was not added.");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.dispose();
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

    private class Exit implements ActionListener {

        private JDialog pane;
        private NoteCardWindow window;

        public Exit(JDialog pane, NoteCardWindow window) {
            this.pane = pane;
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            window.readNoteCardsFromNoteCardAdder(titles, answers);
            pane.dispose();
        }

    }
}
