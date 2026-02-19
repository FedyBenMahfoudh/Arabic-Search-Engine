package org.example.arabicsearchengine.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.services.MorphologyService;
import org.example.arabicsearchengine.services.PatternService;
import org.example.arabicsearchengine.services.RootService;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class WordGenerationController {
    @FXML
    private TextField txtRoot;
    @FXML private ComboBox<String> cmbPattern;
    @FXML private VBox singleResultPane;
    @FXML private Label lblResultRoot;
    @FXML private Label lblResultPattern;
    @FXML private Label lblResultWord;
    @FXML private TableView<DerivedWord> derivativesTable;
    @FXML private TableColumn<DerivedWord, String> colPattern;
    @FXML private TableColumn<DerivedWord, String> colDerivedWord;
    @FXML private TableColumn<DerivedWord, String> colPatternDesc;

    private RootService rootService;
    private PatternService patternService;
    private MorphologyService morphologyService;
    private ObservableList<DerivedWord> derivativeItems;

    @FXML
    public void initialize() {
        derivativeItems = FXCollections.observableArrayList();
        derivativesTable.setItems(derivativeItems);

        colPattern.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPattern().getPatternId()));
        colDerivedWord.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getWord()));
        colPatternDesc.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPattern().getDescription()));
    }

    public void setServices(RootService rootService, PatternService patternService, MorphologyService morphologyService) {
        this.rootService = rootService;
        this.patternService = patternService;
        this.morphologyService = morphologyService;

        // Populate pattern dropdown
        ObservableList<String> patternIds = FXCollections.observableArrayList();
        for (Pattern p : patternService.getAllPatterns()) {
            patternIds.add(p.getPatternId());
        }
        cmbPattern.setItems(patternIds);

        if (!patternIds.isEmpty()) {
            cmbPattern.setValue(patternIds.get(0));
        }
    }

    @FXML
    private void generateWord() {
        String rootLetters = txtRoot.getText().trim();
        String patternId = cmbPattern.getValue();

        if (rootLetters.isEmpty()) {
            showError("الرجاء إدخال الجذر");
            return;
        }

        if (rootLetters.length() != 3) {
            showError("الجذر يجب أن يكون 3 أحرف");
            return;
        }

        if (patternId == null || patternId.isEmpty()) {
            showError("الرجاء اختيار وزن");
            return;
        }

        try {
            // Look up the existing root from the repository so derived words are persisted
            Root root = rootService.searchRoot(rootLetters);
            if (root == null) {
                showError("الجذر غير موجود في القاعدة: " + rootLetters);
                return;
            }

            Pattern pattern = patternService.getPattern(patternId);

            if (pattern == null) {
                showError("الوزن غير موجود");
                return;
            }

            DerivedWord word = morphologyService.generateWord(root, pattern);

            // Show result
            singleResultPane.setVisible(true);
            singleResultPane.setManaged(true);
            lblResultRoot.setText(rootLetters);
            lblResultPattern.setText(patternId);
            lblResultWord.setText(word.getWord());

        } catch (Exception e) {
            showError("خطأ: " + e.getMessage());
        }
    }

    @FXML
    private void generateAllDerivatives() {
        String rootLetters = txtRoot.getText().trim();

        if (rootLetters.isEmpty()) {
            showError("الرجاء إدخال الجذر");
            return;
        }

        if (rootLetters.length() != 3) {
            showError("الجذر يجب أن يكون 3 أحرف");
            return;
        }

        try {
            // Look up the existing root from the repository so derived words are persisted
            Root root = rootService.searchRoot(rootLetters);
            if (root == null) {
                showError("الجذر غير موجود في القاعدة: " + rootLetters);
                return;
            }
            List<Pattern> patterns = patternService.getAllPatterns();

            if (patterns.isEmpty()) {
                showError("لا توجد أوزان متاحة");
                return;
            }

            List<DerivedWord> words = morphologyService.generateAllWords(root, patterns);

            derivativeItems.clear();
            derivativeItems.addAll(words);

            if (!words.isEmpty()) {
                singleResultPane.setVisible(true);
                singleResultPane.setManaged(true);
                lblResultRoot.setText(rootLetters);
                lblResultPattern.setText("جميع الأوزان");
                lblResultWord.setText(words.size() + " كلمة");
            }

        } catch (Exception e) {
            showError("خطأ: " + e.getMessage());
        }
    }

    @FXML
    private void exportResults() {
        if (derivativeItems.isEmpty()) {
            showError("لا توجد نتائج للتصدير");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("حفظ النتائج");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("derivatives.txt");

        File file = fileChooser.showSaveDialog(derivativesTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
                writer.println("العائلة الصرفية للجذر: " + txtRoot.getText());
                writer.println("=".repeat(40));

                for (DerivedWord word : derivativeItems) {
                    writer.println(word.getPattern().getPatternId() + " → " + word.getWord());
                }

                showInfo("تم حفظ النتائج بنجاح");
            } catch (Exception e) {
                showError("خطأ في الحفظ: " + e.getMessage());
            }
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("معلومة");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("خطأ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
