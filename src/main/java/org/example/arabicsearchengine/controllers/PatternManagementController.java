package org.example.arabicsearchengine.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.services.PatternService;

public class PatternManagementController {
    @FXML
    private TextField txtPatternId;
    @FXML private TextField txtStructure;
    @FXML private TextField txtDescription;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TableView<Pattern> patternTable;
    @FXML private TableColumn<Pattern, String> colPatternId;
    @FXML private TableColumn<Pattern, String> colStructure;
    @FXML private TableColumn<Pattern, String> colDescription;
    @FXML private TableColumn<Pattern, String> colCategory;

    private PatternService patternService;
    private Runnable statusUpdater;
    private ObservableList<Pattern> patternItems;

    @FXML
    public void initialize() {
        patternItems = FXCollections.observableArrayList();
        patternTable.setItems(patternItems);

        // Setup table columns
        colPatternId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPatternId()));
        colStructure.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStructure()));
        colDescription.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));

        // Setup category combobox
        cmbCategory.setItems(FXCollections.observableArrayList(
                "active_participle",
                "passive_participle",
                "verbal_noun",
                "verb_form",
                "intensive",
                "noun_place",
                "noun_instrument",
                "adjective",
                "general"
        ));
        cmbCategory.setValue("general");
    }

    public void setServices(PatternService patternService, Runnable statusUpdater) {
        this.patternService = patternService;
        this.statusUpdater = statusUpdater;
        refreshPatternList();
    }

    @FXML
    private void loadDefaultPatterns() {
        patternService.initializeDefaultPatterns();
        refreshPatternList();
        statusUpdater.run();
        showInfo("تم تحميل الأوزان الافتراضية");
    }

    @FXML
    private void addPattern() {
        String id = txtPatternId.getText().trim();
        String structure = txtStructure.getText().trim();
        String description = txtDescription.getText().trim();
        String category = cmbCategory.getValue();

        if (id.isEmpty() || structure.isEmpty()) {
            showError("الرجاء إدخال معرّف الوزن والبنية");
            return;
        }

        patternService.addPattern(id, structure, description, category);

        txtPatternId.clear();
        txtStructure.clear();
        txtDescription.clear();
        cmbCategory.setValue("general");

        refreshPatternList();
        statusUpdater.run();
        showInfo("تمت إضافة الوزن: " + id);
    }

    @FXML
    private void deleteSelectedPattern() {
        Pattern selected = patternTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("الرجاء اختيار وزن للحذف");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تأكيد الحذف");
        confirm.setHeaderText("هل تريد حذف الوزن: " + selected.getPatternId() + "؟");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                patternService.deletePattern(selected.getPatternId());
                refreshPatternList();
                statusUpdater.run();
            }
        });
    }
    @FXML
    private void saveEditedPattern() {
        String id = txtPatternId.getText().trim();
        String structure = txtStructure.getText().trim();
        String description = txtDescription.getText().trim();
        String category = cmbCategory.getValue();

        if (id.isEmpty() || structure.isEmpty()) {
            showError("الرجاء إدخال معرّف الوزن والبنية");
            return;
        }

        patternService.modifyPattern(id, structure, description, category);

        txtPatternId.clear();
        txtStructure.clear();
        txtDescription.clear();
        cmbCategory.setValue("general");

        refreshPatternList();
        statusUpdater.run();
        showInfo("تم تعديل الوزن: " + id);
    }

    @FXML
    private void editPattern() {
        Pattern selected = patternTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("الرجاء اختيار وزن للتعديل");
            return;
        }

        String newStructure = txtStructure.getText().trim();
        String newDescription = txtDescription.getText().trim();
        String newCategory = cmbCategory.getValue();

        if (newStructure.isEmpty()) newStructure = selected.getStructure();
        if (newDescription.isEmpty()) newDescription = selected.getDescription();
        if (newCategory == null || newCategory.isEmpty()) newCategory = selected.getCategory();

        patternService.modifyPattern(selected.getPatternId(), newStructure, newDescription, newCategory);

        refreshPatternList();
        statusUpdater.run();
        showInfo("تم تعديل الوزن: " + selected.getPatternId());

        txtPatternId.clear();
        txtStructure.clear();
        txtDescription.clear();
        cmbCategory.setValue("general");
    }

    private void refreshPatternList() {
        patternItems.clear();
        if (patternService != null) {
            patternItems.addAll(patternService.getAllPatterns());
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
