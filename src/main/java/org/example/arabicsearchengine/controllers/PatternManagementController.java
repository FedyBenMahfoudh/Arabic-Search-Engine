package org.example.arabicsearchengine.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.services.PatternService;

public class PatternManagementController {

    @FXML
    private TextField txtPatternId;
    @FXML
    private TextField txtStructure;
    @FXML
    private TextField txtDescription;
    @FXML
    private ComboBox<String> cmbCategory;
    @FXML
    private TableView<Pattern> patternTable;
    @FXML
    private TableColumn<Pattern, String> colPatternId;
    @FXML
    private TableColumn<Pattern, String> colStructure;
    @FXML
    private TableColumn<Pattern, String> colDescription;
    @FXML
    private TableColumn<Pattern, String> colCategory;
    @FXML
    private TableColumn<Pattern, Void> colActions;

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

        // Setup actions column with edit and delete buttons
        colActions.setCellFactory(param -> new TableCell<Pattern, Void>() {
            private final Button editBtn = new Button("تعديل");
            private final Button deleteBtn = new Button("حذف");

            {
                editBtn.setStyle("-fx-padding: 5; -fx-font-size: 12;");
                deleteBtn.setStyle("-fx-padding: 5; -fx-font-size: 12;");
                editBtn.setOnAction(event -> {
                    Pattern pattern = getTableView().getItems().get(getIndex());
                    showEditModal(pattern);
                });
                deleteBtn.setStyle(
                        "-fx-padding: 5; -fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #f44336;");
                deleteBtn.setOnAction(event -> {
                    Pattern pattern = getTableView().getItems().get(getIndex());
                    deletePattern(pattern);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    hbox.setStyle("-fx-alignment: center;");
                    hbox.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(hbox);
                }
            }
        });

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
                "general"));
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

        // Clear inputs
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
        deletePattern(selected);
    }

    private void deletePattern(Pattern pattern) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تأكيد الحذف");
        confirm.setHeaderText("هل تريد حذف الوزن: " + pattern.getPatternId() + "؟");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                patternService.deletePattern(pattern.getPatternId());
                refreshPatternList();
                statusUpdater.run();
            }
        });
    }

    @FXML
    private void editPattern() {
        Pattern selected = patternTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("الرجاء اختيار وزن للتعديل");
            return;
        }
        showEditModal(selected);
    }

    private void showEditModal(Pattern pattern) {
        Stage modalStage = new Stage();
        modalStage.setTitle("تعديل الوزن");
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(patternTable.getScene().getWindow());

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12;");

        // Header
        Label headerLabel = new Label("تعديل تفاصيل الوزن");
        headerLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        // Form fields
        VBox formBox = new VBox(10);

        HBox patternIdBox = new HBox(10);
        Label patternIdLabel = new Label("معرّف الوزن:");
        patternIdLabel.setPrefWidth(100);
        TextField editPatternId = new TextField(pattern.getPatternId());
        editPatternId.setEditable(false);
        editPatternId.setPrefWidth(300);
        patternIdBox.getChildren().addAll(patternIdLabel, editPatternId);

        HBox structureBox = new HBox(10);
        Label structureLabel = new Label("البنية:");
        structureLabel.setPrefWidth(100);
        TextField editStructure = new TextField(pattern.getStructure());
        editStructure.setPrefWidth(300);
        structureBox.getChildren().addAll(structureLabel, editStructure);

        HBox descriptionBox = new HBox(10);
        Label descriptionLabel = new Label("الوصف:");
        descriptionLabel.setPrefWidth(100);
        TextField editDescription = new TextField(pattern.getDescription());
        editDescription.setPrefWidth(300);
        descriptionBox.getChildren().addAll(descriptionLabel, editDescription);

        HBox categoryBox = new HBox(10);
        Label categoryLabel = new Label("الفئة:");
        categoryLabel.setPrefWidth(100);
        ComboBox<String> editCategory = new ComboBox<>();
        editCategory.setItems(FXCollections.observableArrayList(
                "active_participle",
                "passive_participle",
                "verbal_noun",
                "verb_form",
                "intensive",
                "noun_place",
                "noun_instrument",
                "adjective",
                "general"));
        editCategory.setValue(pattern.getCategory());
        editCategory.setPrefWidth(300);
        categoryBox.getChildren().addAll(categoryLabel, editCategory);

        formBox.getChildren().addAll(patternIdBox, structureBox, descriptionBox, categoryBox);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setStyle("-fx-alignment: center-right;");

        Button saveBtn = new Button("حفظ التعديلات");
        saveBtn.setStyle("-fx-padding: 8 20; -fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #4CAF50;");
        saveBtn.setOnAction(event -> {
            String newStructure = editStructure.getText().trim();
            String newDescription = editDescription.getText().trim();
            String newCategory = editCategory.getValue();

            if (newStructure.isEmpty()) {
                showError("الرجاء إدخال البنية");
                return;
            }

            // Update pattern using the service
            try {
                patternService.modifyPattern(pattern.getPatternId(), newStructure, newDescription, newCategory);
                refreshPatternList();
                statusUpdater.run();
                showInfo("تم تحديث الوزن بنجاح");
                modalStage.close();
            } catch (Exception e) {
                showError("حدث خطأ أثناء التحديث: " + e.getMessage());
                e.printStackTrace();
            }
        });

        Button cancelBtn = new Button("إلغاء");
        cancelBtn.setStyle("-fx-padding: 8 20; -fx-font-size: 12;");
        cancelBtn.setOnAction(event -> modalStage.close());

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        vbox.getChildren().addAll(headerLabel, new Separator(), formBox, new Separator(), buttonBox);

        Scene scene = new Scene(vbox, 500, 350);
        modalStage.setScene(scene);
        modalStage.showAndWait();
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
