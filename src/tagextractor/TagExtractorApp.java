package tagextractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TagExtractorApp extends JFrame {

    private JTextArea textArea;
    private File selectedTextFile;
    private File stopWordsFile;
    private Map<String, Integer> tagFrequencyMap = new TreeMap<>();
    private Set<String> stopWordsSet = new HashSet<>();

    public TagExtractorApp() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton btnChooseText = new JButton("Choose Text File");
        JButton btnChooseStopWords = new JButton("Choose Stop Words File");
        JButton btnExtractTags = new JButton("Extract Tags");
        JButton btnSave = new JButton("Save Tags");

        topPanel.add(btnChooseText);
        topPanel.add(btnChooseStopWords);
        topPanel.add(btnExtractTags);
        topPanel.add(btnSave);

        add(topPanel, BorderLayout.NORTH);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button Listeners
        btnChooseText.addActionListener(e -> chooseTextFile());
        btnChooseStopWords.addActionListener(e -> chooseStopWordsFile());
        btnExtractTags.addActionListener(e -> extractTags());
        btnSave.addActionListener(e -> saveTagsToFile());
    }

    private void chooseTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedTextFile = fileChooser.getSelectedFile();
            textArea.append("Selected text file: " + selectedTextFile.getName() + "\n");
        }
    }

    private void chooseStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            stopWordsFile = fileChooser.getSelectedFile();
            textArea.append("Selected stop words file: " + stopWordsFile.getName() + "\n");
        }
    }

    private void extractTags() {
        if (selectedTextFile == null || stopWordsFile == null) {
            JOptionPane.showMessageDialog(this, "Please select both a text file and a stop words file.");
            return;
        }

        stopWordsSet.clear();
        tagFrequencyMap.clear();

        try {
            // Load stop words into a Set
            BufferedReader stopReader = new BufferedReader(new FileReader(stopWordsFile));
            String stopLine;
            while ((stopLine = stopReader.readLine()) != null) {
                stopWordsSet.add(stopLine.trim().toLowerCase());
            }
            stopReader.close();

            // Process the text file
            BufferedReader textReader = new BufferedReader(new FileReader(selectedTextFile));
            String line;
            while ((line = textReader.readLine()) != null) {
                String[] words = line.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");
                for (String word : words) {
                    if (!word.isEmpty() && !stopWordsSet.contains(word)) {
                        tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
            textReader.close();

            // Display the tags
            textArea.append("\n--- Tags & Frequencies ---\n");
            for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTagsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "Tags saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TagExtractorApp().setVisible(true));
    }
}
