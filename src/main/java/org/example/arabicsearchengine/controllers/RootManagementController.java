package org.example.arabicsearchengine.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.services.PatternService;
import org.example.arabicsearchengine.services.RootService;

import java.io.File;

public class RootManagementController {
    @FXML
    private TextField txtNewRoot;
    @FXML private TextField txtSearchRoot;
    @FXML private ListView<String> rootListView;
    @FXML private VBox rootInfoPane;
    @FXML private Label lblRootInfo;

    private RootService rootService;
    private PatternService patternService;
    private Runnable statusUpdater;
    private ObservableList<String> rootItems;

    @FXML
    public void initialize() {
        rootItems = FXCollections.observableArrayList();
        rootListView.setItems(rootItems);

        txtSearchRoot.textProperty().addListener((obs, old, newVal) -> filterRoots(newVal));
    }

    public void setServices(RootService rootService, PatternService patternService, Runnable statusUpdater) {
        this.rootService = rootService;
        this.patternService = patternService;
        this.statusUpdater = statusUpdater;
        refreshRootList();
    }

    @FXML
    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("اختر ملف الجذور");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(rootListView.getScene().getWindow());
        if (file != null) {
            try {
                int count = rootService.loadRootsFromFile(file.getAbsolutePath());
                showInfo("تم تحميل " + count + " جذر بنجاح");
                refreshRootList();
                statusUpdater.run();
            } catch (Exception e) {
                showError("خطأ في تحميل الملف: " + e.getMessage());
            }
        }
    }

    @FXML
    private void addRoot() {
        String rootLetters = txtNewRoot.getText().trim();

        if (rootLetters.isEmpty()) {
            showError("الرجاء إدخال الجذر");
            return;
        }

        if (rootLetters.length() != 3) {
            showError("الجذر يجب أن يكون 3 أحرف");
            return;
        }

        try {
            rootService.addRoot(rootLetters);
            txtNewRoot.clear();
            refreshRootList();
            statusUpdater.run();
            showInfo("تمت إضافة الجذر: " + rootLetters);
        } catch (Exception e) {
            showError("خطأ: " + e.getMessage());
        }
    }

    @FXML
    private void searchRoot() {
        String searchTerm = txtSearchRoot.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshRootList();
            return;
        }

        Root found = rootService.searchRoot(searchTerm);
        if (found != null) {
            rootItems.clear();
            rootItems.add(found.getRootLetters());
            showRootDetails(found);
        } else {
            showError("الجذر غير موجود: " + searchTerm);
        }
    }

    @FXML
    private void deleteSelectedRoot() {
        String selected = rootListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("الرجاء اختيار جذر للحذف");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("تأكيد الحذف");
        confirm.setHeaderText("هل تريد حذف الجذر: " + selected + "؟");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                rootService.deleteRoot(selected);
                refreshRootList();
                statusUpdater.run();
                hideRootDetails();
            }
        });
    }

    @FXML
    private void showDerivatives() {
        String selected = rootListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("الرجاء اختيار جذر أولاً");
            return;
        }

        Root root = rootService.searchRoot(selected);
        if (root != null) {
            showRootDetails(root);
        }
    }

    private void refreshRootList() {
        rootItems.clear();
        if (rootService != null) {
            for (Root root : rootService.getAllRoots()) {
                rootItems.add(root.getRootLetters());
            }
        }
    }

    private void filterRoots(String filter) {
        if (filter == null || filter.isEmpty()) {
            refreshRootList();
            return;
        }

        rootItems.clear();
        for (Root root : rootService.getAllRoots()) {
            if (root.getRootLetters().contains(filter)) {
                rootItems.add(root.getRootLetters());
            }
        }
    }

    private void showRootDetails(Root root) {
        rootInfoPane.setVisible(true);
        rootInfoPane.setManaged(true);
        lblRootInfo.setText(
                "الجذر: " + root.getRootLetters() + "\n" +
                        "R1: " + root.getR1() + " | R2: " + root.getR2() + " | R3: " + root.getR3() + "\n" +
                        "التكرار: " + root.getFrequency()
        );
    }

    private void hideRootDetails() {
        rootInfoPane.setVisible(false);
        rootInfoPane.setManaged(false);
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
