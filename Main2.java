import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main2 {

    private static JTextField filePathField1;
    private static JTextField filePathField2;
    private static JLabel resultLabel;

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Text Similarity Checker");
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));

        // Create file selection components for File 1
        JPanel filePanel1 = new JPanel(new BorderLayout());
        filePathField1 = new JTextField();
        JButton browseButton1 = new JButton("Browse...");
        filePanel1.add(new JLabel("File 1: "), BorderLayout.WEST);
        filePanel1.add(filePathField1, BorderLayout.CENTER);
        filePanel1.add(browseButton1, BorderLayout.EAST);

        // Create file selection components for File 2
        JPanel filePanel2 = new JPanel(new BorderLayout());
        filePathField2 = new JTextField();
        JButton browseButton2 = new JButton("Browse...");
        filePanel2.add(new JLabel("File 2: "), BorderLayout.WEST);
        filePanel2.add(filePathField2, BorderLayout.CENTER);
        filePanel2.add(browseButton2, BorderLayout.EAST);

        // Button to calculate similarity
        JButton calculateButton = new JButton("Calculate Similarity");

        // Label to show the result
        resultLabel = new JLabel("Similarity score: ", JLabel.CENTER);

        // Add components to frame
        frame.add(filePanel1);
        frame.add(filePanel2);
        frame.add(calculateButton);
        frame.add(resultLabel);

        // Browse button 1 action listener
        browseButton1.addActionListener(e -> selectFile(filePathField1));

        // Browse button 2 action listener
        browseButton2.addActionListener(e -> selectFile(filePathField2));

        // Calculate button action listener
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    calculateSimilarityAndDisplay();
                } catch (IOException ioException) {
                    resultLabel.setText("Error reading files: " + ioException.getMessage());
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    // Function to open a file chooser and set the selected file path in the given JTextField
    private static void selectFile(JTextField filePathField) {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    // Function to read the files, calculate similarity, and display the result
    private static void calculateSimilarityAndDisplay() throws IOException {
        String filePath1 = filePathField1.getText();
        String filePath2 = filePathField2.getText();

        if (filePath1.isEmpty() || filePath2.isEmpty()) {
            resultLabel.setText("Please select both files.");
            return;
        }

        String text1 = new String(Files.readAllBytes(Paths.get(filePath1)));
        String text2 = new String(Files.readAllBytes(Paths.get(filePath2)));

        double similarityScore = Math.floor(calculateSimilarity(text1, text2) * 100);
        resultLabel.setText("Similarity score: " + similarityScore + "%");

        if (similarityScore > 50) {
            resultLabel.setText(resultLabel.getText() + " - Plagiarism detected!");
        } else {
            resultLabel.setText(resultLabel.getText() + " - No plagiarism detected.");
        }
    }

    // Similarity calculation logic (same as provided)
    private static double calculateSimilarity(String text1, String text2) {
        List<String> tokens1 = tokenize(text1);
        List<String> tokens2 = tokenize(text2);

        List<String> filteredTokens1 = removeStopwords(tokens1);
        List<String> filteredTokens2 = removeStopwords(tokens2);

        List<String> ngrams1 = generateNgrams(filteredTokens1, 2);
        List<String> ngrams2 = generateNgrams(filteredTokens2, 2);

        return cosineSimilarity(ngrams1, ngrams2);
    }

    // Tokenization logic
    private static java.util.List<String> tokenize(String text) {
        return Arrays.asList(text.split("\\s+"));
    }

    // Stopword removal logic
    private static java.util.List<String> removeStopwords(java.util.List<String> tokens) {
        java.util.List<String> stopwords = Arrays.asList("the", "and");
        return tokens.stream().filter(token -> !stopwords.contains(token)).collect(Collectors.toList());
    }

    // N-gram generation logic
    private static java.util.List<String> generateNgrams(java.util.List<String> tokens, int n) {
        java.util.List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            ngrams.add(String.join(" ", tokens.subList(i, i + n)));
        }
        return ngrams;
    }

    // Cosine similarity logic
    private static double cosineSimilarity(java.util.List<String> ngrams1, java.util.List<String> ngrams2) {
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
            return 0.0;
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }
}
