import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;

public class Group extends JMenuItem {
    /**
     * 
     */
    private static final long serialVersionUID = -5145904402821940816L;

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> answers = new ArrayList<>();
    private String name;
    private boolean showAnswer = false;
    private int card = 0;

    public Group(String name, NoteCardWindow window) {
        super(name.trim());
        System.out.println("Making group with name: " + name.trim());
        this.name = name.trim();
        addActionListener(new GroupObj(window));
    }

    public void rename(String newName) {
        name = newName;
    }

    public boolean isAnswer() {
        return showAnswer;
    }

    public void set(int index, String title, String answer) {
        setTitle(index, title);
        setAnswer(index, answer);
    }

    public void goTo(int index) {
        if (index < titles.size() && index >= 0)
            card = index;
    }

    public void setTitle(int index, String title) {
        titles.set(index, title);
    }

    public void setAnswer(int index, String answer) {
        answers.set(index, answer);
    }

    public void add(String title, String answer) {
        if (title != null && answer != null) {
            addTitle(title);
            addAnswer(answer);
        }
    }

    public String[] getTitleSearchOptions() {
        String[] briefTitles = new String[titles.size()];
        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            if (title.length() > 30)
                title = title.substring(0, 28) + "...";
            briefTitles[i] = "Card " + (i + 1) + ": " + title;
        }
        return briefTitles;
    }

    public void addTitle(String title) {
        if (title != null)
            titles.add(title);
        else
            titles.add(" ");
    }

    public void addAnswer(String answer) {
        if (answer != null)
            answers.add(answer);
        else
            answers.add(" ");
    }

    public void deleteCard(int index) {
        titles.remove(index);
        answers.remove(index);
        emptyCheck();
        prevCard();
    }

    public int cardsSize() {
        emptyCheck();
        return titles.size();
    }

    public String getTitle(int index) {
        emptyCheck();
        if (index <= cardsSize() - 1)
            return titles.get(index);
        else
            return " ";
    }

    public String getAnswer(int index) {
        emptyCheck();
        if (index <= cardsSize() - 1)
            return answers.get(index);
        else
            return " ";
    }

    public String getTitle() {
        emptyCheck();
        return titles.get(card);
    }

    public String getAnswer() {
        emptyCheck();
        return answers.get(card);
    }

    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String getSaveName() {
        String[] saveNameArr = name.split(" ");
        String saveName = "";
        for (int i = 0; i < saveNameArr.length; i++) {
            if (i == saveNameArr.length - 1)
                saveName += saveNameArr[i];
            else
                saveName += saveNameArr[i] + "_";
        }
        return saveName;
    }

    public void nextCard() {
        showAnswer = false;
        card++;
        if (card > cardsSize() - 1)
            card = 0;
    }

    public void prevCard() {
        showAnswer = false;
        card--;
        if (card < 0)
            card = cardsSize() - 1;
    }

    public void flipCard() {
        if (showAnswer)
            showAnswer = false;
        else
            showAnswer = true;
    }

    public void deleteCard() {
        titles.remove(card);
        answers.remove(card);
        emptyCheck();
        prevCard();
    }

    public void deleteAllCards() {
        titles.clear();
        answers.clear();
        emptyCheck();
    }

    public void emptyCheck() {
        if (titles.size() <= 0) {
            add("", "");
            card = 0;
        } else if (titles.size() > 1) {
            removeBlankCards();
        }
    }



    /**
     * 
     */
    private void removeBlankCards() {
        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).trim().equals("") && answers.get(i).trim().equals("")) {
                deleteCard(i);
                i--;
            }
        }

    }

    public int getCard() {
        return card;
    }

    public int compareTo(Group g) {
        String gName = g.getName().toLowerCase();
        String thisName = name.toLowerCase();

        int shortestLength;
        if (gName.length() < thisName.length())
            shortestLength = gName.length();
        else
            shortestLength = thisName.length();

        char gNameLetter;
        char nameLetter;
        for (int i = 0; i < shortestLength; i++) {
            gNameLetter = gName.charAt(i);
            nameLetter = thisName.charAt(i);
            if (gNameLetter < nameLetter)
                return -1;
            else if (gNameLetter > nameLetter)
                return 1;
        }
        if (gName.length() > thisName.length())
            return 1;
        else
            return -1;
    }

    private class GroupObj implements ActionListener {

        private NoteCardWindow window;

        public GroupObj(NoteCardWindow window) {
            this.window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Changing to group: " + name);
            window.changeGroup(name);

        }

    }
}
