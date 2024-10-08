import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;      // Correct imports for HashSet, ArrayList, etc.
import java.util.stream.Collectors;

public class PlagiarismDetectorGUI extends JFrame {

    private JTextArea textArea1;
    private JTextArea textArea2;
    private JLabel resultLabel;

    public PlagiarismDetectorGUI() {
        // Set up the frame
        setTitle("Plagiarism Detector");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panels and layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create text areas for input
        textArea1 = new JTextArea(5, 40);
        textArea2 = new JTextArea(5, 40);
        textArea1.setLineWrap(true);
        textArea2.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        textArea2.setWrapStyleWord(true);

        // Create labels and buttons
        JLabel label1 = new JLabel("Enter first text:");
        JLabel label2 = new JLabel("Enter second text:");
        resultLabel = new JLabel("Result will appear here");
        JButton detectButton = new JButton("Detect Plagiarism");

        // Add action listener to the button
        detectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detectPlagiarism();
            }
        });

        // Add components to panel
        panel.add(label1, BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea1), BorderLayout.CENTER);
        panel.add(label2, BorderLayout.WEST);
        panel.add(new JScrollPane(textArea2), BorderLayout.CENTER);
        panel.add(detectButton, BorderLayout.SOUTH);
        panel.add(resultLabel, BorderLayout.SOUTH);

        // Set layout for the content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(label1);
        content.add(new JScrollPane(textArea1));
        content.add(label2);
        content.add(new JScrollPane(textArea2));
        content.add(detectButton);
        content.add(resultLabel);

        setContentPane(content);
    }

    private void detectPlagiarism() {
        String text1 = textArea1.getText();
        String text2 = textArea2.getText();

        double similarityScore = Math.floor(calculateSimilarity(text1, text2) * 100);
        String resultText = "Similarity score: " + similarityScore + "%";

        if (similarityScore > 50) {
            resultText += " - Plagiarism detected!";
        } else {
            resultText += " - No plagiarism detected.";
        }

        resultLabel.setText(resultText);
    }

    private double calculateSimilarity(String text1, String text2) {
        java.util.List<String> tokens1 = tokenize(text1);   // Explicitly specify java.util.List
        java.util.List<String> tokens2 = tokenize(text2);

        java.util.List<String> filteredTokens1 = removeStopwords(tokens1);
        java.util.List<String> filteredTokens2 = removeStopwords(tokens2);

        java.util.List<String> ngrams1 = generateNgrams(filteredTokens1, 2);
        java.util.List<String> ngrams2 = generateNgrams(filteredTokens2, 2);

        return cosineSimilarity(ngrams1, ngrams2);
    }

    private java.util.List<String> tokenize(String text) {
        return Arrays.asList(text.split("\\s+"));
    }

    private java.util.List<String> removeStopwords(java.util.List<String> tokens) {
        java.util.List<String> stopwords = Arrays.asList("the", "and", "is", "in", "at", "of", "on");
        return tokens.stream().filter(token -> !stopwords.contains(token)).collect(Collectors.toList());
    }

    private java.util.List<String> generateNgrams(java.util.List<String> tokens, int n) {
        java.util.List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            ngrams.add(String.join(" ", tokens.subList(i, i + n)));
        }
        return ngrams;
    }

    private double cosineSimilarity(java.util.List<String> ngrams1, java.util.List<String> ngrams2) {
        Set<String> allNgrams = new HashSet<>(ngrams1);
        allNgrams.addAll(ngrams2);

        int[] ngramCounts1 = new int[allNgrams.size()];
        int[] ngramCounts2 = new int[allNgrams.size()];

        Map<String, Integer> ngramToIndex = new HashMap<>();
        int index = 0;
        for (String ngram : allNgrams) {
            ngramToIndex.put(ngram, index++);
        }

        for (String ngram : ngrams1) {
            ngramCounts1[ngramToIndex.get(ngram)]++;
        }

        for (String ngram : ngrams2) {
            ngramCounts2[ngramToIndex.get(ngram)]++;
        }

        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (int i = 0; i < ngramCounts1.length; i++) {
            dotProduct += ngramCounts1[i] * ngramCounts2[i];
            magnitude1 += ngramCounts1[i] * ngramCounts1[i];
            magnitude2 += ngramCounts2[i] * ngramCounts2[i];
        }

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0; // Return 0 similarity if either vector is zero
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlagiarismDetectorGUI frame = new PlagiarismDetectorGUI();
            frame.setVisible(true);
        });
    }
}
