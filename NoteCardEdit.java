import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class NoteCardEdit extends JDialog implements WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = 3133817165613172891L;

    JTextArea txtArea = new JTextArea();
    private String finishedEdit = null;
    private String originalText = null;
    private JFrame frame;

    public NoteCardEdit(JFrame frame, String text, boolean superScreen) {
        if (text != null)
            txtArea.setText(text.trim());
        else
            text = "";
        originalText = text;
        this.frame = frame;
        setLayout(new BorderLayout());
        add(new JLabel("Edit the text below as you see fit."), BorderLayout.NORTH);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        add(txtArea, BorderLayout.CENTER);
        JButton finishedEditBtn = new JButton("Save Edit");
        finishedEditBtn.addActionListener(new SaveEditBtn(this));
        add(finishedEditBtn, BorderLayout.SOUTH);

        addWindowListener(this);
        setSize(400, 400);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setAlwaysOnTop(superScreen);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

    public String getEdit() {
        if (finishedEdit != null)
            return finishedEdit;
        else
            return "";
    }

    private class SaveEditBtn implements ActionListener {

        JDialog pane = null;

        public SaveEditBtn(JDialog pane) {
            this.pane = pane;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            finishedEdit = txtArea.getText();
            pane.dispose();

        }

    }

    @Override
    public void windowOpened(WindowEvent e) {
        // Do nothing

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int choice =
            JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit without saving?",
                "Exit Without Saving?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.OK_OPTION) {
            finishedEdit = originalText.trim();
            this.dispose();
        }

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
}
