import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path for the first text file:");
        String filePath1 = scanner.nextLine();
        System.out.println("Enter the path for the second text file:");
        String filePath2 = scanner.nextLine();

        try {
            String text1 = new String(Files.readAllBytes(Paths.get(filePath1)));
            String text2 = new String(Files.readAllBytes(Paths.get(filePath2)));

            double similarityScore = Math.floor(calculateSimilarity(text1, text2) * 100);
            System.out.println("Similarity score: " + similarityScore + "%");

            if (similarityScore > 50) {
                System.out.println("Plagiarism detected!");
            } else {
                System.out.println("No plagiarism detected.");
            }

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }

        scanner.close(); // Close the scanner to avoid resource leaks
    }

    private static double calculateSimilarity(String text1, String text2) {
        List<String> tokens1 = tokenize(text1);
        List<String> tokens2 = tokenize(text2);

        List<String> filteredTokens1 = removeStopwords(tokens1);
        List<String> filteredTokens2 = removeStopwords(tokens2);

        List<String> ngrams1 = generateNgrams(filteredTokens1, 2);
        List<String> ngrams2 = generateNgrams(filteredTokens2, 2);

        return cosineSimilarity(ngrams1, ngrams2);
    }

    private static List<String> tokenize(String text) {
        return Arrays.asList(text.split("\\s+"));
    }

    private static List<String> removeStopwords(List<String> tokens) {
        List<String> stopwords = Arrays.asList("the", "and");
        return tokens.stream().filter(token -> !stopwords.contains(token)).collect(Collectors.toList());
    }

    private static List<String> generateNgrams(List<String> tokens, int n) {
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            ngrams.add(String.join(" ", tokens.subList(i, i + n)));
        }
        return ngrams;
    }

    private static double cosineSimilarity(List<String> ngrams1, List<String> ngrams2) {
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

        // Check for zero magnitude to avoid division by zero
        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0; // Return 0 similarity if either vector is zero
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }
}