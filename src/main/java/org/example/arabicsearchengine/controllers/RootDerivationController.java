package org.example.arabicsearchengine.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.services.MorphologyService;
import org.example.arabicsearchengine.services.PatternService;
import org.example.arabicsearchengine.services.RootService;

import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controller for the RootDerivation view.
 * Displays all roots alongside their derived words in a single searchable table.
 */
public class RootDerivationController {

    @FXML private TextField txtSearchRoot;
    @FXML private Label lblResultCount;
    @FXML private TableView<RootDerivationRow> rootDerivationTable;
    @FXML private TableColumn<RootDerivationRow, String> colRootLetters;
    @FXML private TableColumn<RootDerivationRow, String> colDerivedWord;
    @FXML private TableColumn<RootDerivationRow, String> colPattern;
    @FXML private TableColumn<RootDerivationRow, String> colDescription;
    @FXML private TableColumn<RootDerivationRow, Integer> colFrequency;

    private RootService rootService;
    private PatternService patternService;
    private MorphologyService morphologyService;
    private ObservableList<RootDerivationRow> tableItems;

    @FXML
    public void initialize() {
        tableItems = FXCollections.observableArrayList();
        rootDerivationTable.setItems(tableItems);

        colRootLetters.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getRootLetters()));
        colDerivedWord.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDerivedWord()));
        colPattern.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPatternId()));
        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));
        colFrequency.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getFrequency()).asObject());


        // Live filtering as user types in the search bar
        txtSearchRoot.textProperty().addListener((obs, oldVal, newVal) -> filterByRoot(newVal));
    }

    public void setServices(RootService rootService, PatternService patternService, MorphologyService morphologyService) {
        this.rootService = rootService;
        this.patternService = patternService;
        this.morphologyService = morphologyService;

        loadAllRootDerivations();
    }

    /**
     * Loads all roots and generates their derived words using all available patterns,
     * populating the table.
     */
    private void loadAllRootDerivations() {
        tableItems.clear();

        if (rootService == null || patternService == null || morphologyService == null) {
            return;
        }

        List<Root> allRoots = rootService.getAllRoots();

        for (Root root : allRoots) {
            // First check if the root already has derived words
            if (root.getDerivedWords() != null && !root.getDerivedWords().isEmpty()) {
                for (DerivedWord dw : root.getDerivedWords()) {
                    tableItems.add(new RootDerivationRow(
                            root.getRootLetters(),
                            dw.getWord(),
                            dw.getPattern() != null ? dw.getPattern().getPatternId() : "",
                            dw.getPattern() != null ? dw.getPattern().getDescription() : "",
                            dw.getFrequency()
                    ));
                }
            }
        }

        updateResultCount();
    }

    @FXML
    private void searchByRoot() {
        String searchTerm = txtSearchRoot.getText().trim();
        filterByRoot(searchTerm);
    }

    @FXML
    private void showAll() {
        txtSearchRoot.clear();
        loadAllRootDerivations();
    }

    private void filterByRoot(String filter) {
        if (filter == null || filter.isEmpty()) {
            loadAllRootDerivations();
            return;
        }

        if (rootService == null || patternService == null || morphologyService == null) {
            return;
        }

        tableItems.clear();

        List<Root> allRoots = rootService.getAllRoots();

        for (Root root : allRoots) {
            if (root.getRootLetters().contains(filter)) {
                // Check for existing derived words first
                if (root.getDerivedWords() != null && !root.getDerivedWords().isEmpty()) {
                    for (DerivedWord dw : root.getDerivedWords()) {
                        tableItems.add(new RootDerivationRow(
                                root.getRootLetters(),
                                dw.getWord(),
                                dw.getPattern() != null ? dw.getPattern().getPatternId() : "",
                                dw.getPattern() != null ? dw.getPattern().getDescription() : "",
                                dw.getFrequency()
                        ));
                    }
                }
            }
        }

        updateResultCount();
    }

    @FXML
    private void exportResults() {
        if (tableItems.isEmpty()) {
            showError("لا توجد نتائج للتصدير");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("حفظ النتائج");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("root_derivations.txt");

        File file = fileChooser.showSaveDialog(rootDerivationTable.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
                writer.println("جدول الجذور والمشتقات");
                writer.println("=".repeat(50));

                String currentRoot = "";
                for (RootDerivationRow row : tableItems) {
                    if (!row.getRootLetters().equals(currentRoot)) {
                        currentRoot = row.getRootLetters();
                        writer.println();
                        writer.println("── الجذر: " + currentRoot + " ──");
                    }
                    writer.println("  " + row.getPatternId() + " → " + row.getDerivedWord()
                            + " (" + row.getDescription() + ")");
                }

                showInfo("تم حفظ النتائج بنجاح");
            } catch (Exception e) {
                showError("خطأ في الحفظ: " + e.getMessage());
            }
        }
    }

    private void updateResultCount() {
        long rootCount = rootService.getRootCount();
        lblResultCount.setText(rootCount + " جذر | " + tableItems.size() + " كلمة مشتقة");
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

    /**
     * A simple row model for the root derivation table.
     * Each row represents one derived word associated with a root.
     */
    public static class RootDerivationRow {
        private final String rootLetters;
        private final String derivedWord;
        private final String patternId;
        private final String description;
        private final int frequency;

        public RootDerivationRow(String rootLetters, String derivedWord, String patternId, String description, int frequency) {
            this.rootLetters = rootLetters;
            this.derivedWord = derivedWord;
            this.patternId = patternId;
            this.description = description;
            this.frequency = frequency;
        }

        public String getRootLetters() { return rootLetters; }
        public String getDerivedWord() { return derivedWord; }
        public String getPatternId() { return patternId; }
        public String getDescription() { return description; }
        public int getFrequency() { return frequency; }
    }
}
