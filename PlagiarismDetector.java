import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class PlagiarismDetector {

    private JFrame frame;
    private JTextArea inputTextArea;
    private JLabel resultLabel;
    private JLabel percentageLabel;
    private JButton detectButton;

    public PlagiarismDetector() {
        // Set up the frame
        frame = new JFrame("Plagiarism Detection Tool");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create input text area (smaller, in the center)
        inputTextArea = new JTextArea(5, 30);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        inputTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        inputTextArea.setBackground(Color.BLACK);
        inputTextArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(inputTextArea);

        // Create detect button and customize it
        detectButton = new JButton("Check Plagiarism");
        detectButton.setFont(new Font("Arial", Font.BOLD, 16));
        detectButton.setBackground(new Color(70, 130, 180));  // SteelBlue color
        detectButton.setForeground(Color.WHITE);
        detectButton.setFocusPainted(false);
        detectButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        // Center the button in a JPanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(detectButton);

        // Create result labels and customize them
        resultLabel = new JLabel("Plagiarism Detection Status: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setForeground(Color.WHITE);

        percentageLabel = new JLabel("Similarity Percentage: ");
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        percentageLabel.setForeground(Color.WHITE);

        // Add components to frame
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("Enter Text Below:", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1));
        resultPanel.setBackground(Color.BLACK);
        resultPanel.add(resultLabel);
        resultPanel.add(percentageLabel);
        frame.add(resultPanel, BorderLayout.NORTH);  // Move results towards the top

        // Add action listener for the button
        detectButton.addActionListener(new DetectButtonListener());

        // Make the frame visible
        frame.setVisible(true);
    }

    // Action listener for detecting plagiarism
    private class DetectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputText = inputTextArea.getText();
            if (inputText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter some text to check.");
            } else {
                // Perform plagiarism check
                int percentage = checkPlagiarism(inputText);

                if (percentage > 0) {
                    resultLabel.setText("Plagiarism Detected!");
                    resultLabel.setForeground(Color.RED);
                } else {
                    resultLabel.setText("No Plagiarism Detected.");
                    resultLabel.setForeground(Color.GREEN);
                }

                percentageLabel.setText("Similarity Percentage: " + percentage + "%");
            }
        }
    }

    // Data structure for storing word counts
    static class HashMapWithCollision {
        private static final int TABLE_SIZE = 100;
        private LinkedList<Entry>[] table;

        public HashMapWithCollision() {
            table = new LinkedList[TABLE_SIZE];
            for (int i = 0; i < TABLE_SIZE; i++) {
                table[i] = new LinkedList<>();
            }
        }

        // Entry class for LinkedList
        static class Entry {
            String word;
            int count;

            Entry(String word) {
                this.word = word;
                this.count = 1;
            }
        }

        // Hash function
        private int hashFunction(String word) {
            return word.hashCode() % TABLE_SIZE;
        }

        // Insert word into HashMap
        public void insertWord(String word) {
            int index = hashFunction(word);
            for (Entry entry : table[index]) {
                if (entry.word.equals(word)) {
                    entry.count++;
                    return;
                }
            }
            table[index].add(new Entry(word));
        }

        // Get word count from the table
        public int getWordCount(String word) {
            int index = hashFunction(word);
            for (Entry entry : table[index]) {
                if (entry.word.equals(word)) {
                    return entry.count;
                }
            }
            return 0;
        }
    }

    // Method to check plagiarism
    private int checkPlagiarism(String text) {
        HashMapWithCollision wordCountMap = new HashMapWithCollision();
        StringTokenizer tokenizer = new StringTokenizer(text, " ,.-\n");
        int totalWords = 0, plagiarizedWords = 0;

        // Process each word
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().toLowerCase();  // Case insensitive
            totalWords++;
            wordCountMap.insertWord(word);
        }

        // Count plagiarized words (words with multiple occurrences)
        StringTokenizer tokenizerCheck = new StringTokenizer(text, " ,.-\n");
        while (tokenizerCheck.hasMoreTokens()) {
            String word = tokenizerCheck.nextToken().toLowerCase();
            if (wordCountMap.getWordCount(word) > 1) {
                plagiarizedWords++;
            }
        }

        // Calculate plagiarism percentage
        if (totalWords == 0) return 0;
        return (plagiarizedWords * 100) / totalWords;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismDetector::new);
    }
}
