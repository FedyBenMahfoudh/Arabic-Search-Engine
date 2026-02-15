package org.example.arabicsearchengine.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.arabicsearchengine.models.ValidationResult;
import org.example.arabicsearchengine.services.RootService;
import org.example.arabicsearchengine.services.ValidationService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class ValidationController {

    @FXML
    private RadioButton rbValidateAgainstRoot;
    @FXML
    private RadioButton rbIdentifyWord;
    @FXML
    private TextField txtWord;
    @FXML
    private TextField txtRootInput;
    @FXML
    private Label lblRootInput;
    @FXML
    private VBox resultPane;
    @FXML
    private FontIcon iconResult;
    @FXML
    private Label lblResultStatus;
    @FXML
    private Label lblWordResult;
    @FXML
    private Label lblRootResult;
    @FXML
    private Label lblPatternResult;
    @FXML
    private Label lblExplanation;
    @FXML
    private ListView<String> historyList;

    private ValidationService validationService;
    private RootService rootService;
    private ObservableList<String> historyItems;

    @FXML
    public void initialize() {
        historyItems = FXCollections.observableArrayList();
        historyList.setItems(historyItems);

        // Toggle root input visibility based on validation mode
        rbValidateAgainstRoot.selectedProperty().addListener((obs, old, newVal) -> {
            txtRootInput.setVisible(newVal);
            txtRootInput.setManaged(newVal);
            lblRootInput.setVisible(newVal);
            lblRootInput.setManaged(newVal);
        });

        rbIdentifyWord.selectedProperty().addListener((obs, old, newVal) -> {
            txtRootInput.setVisible(!newVal);
            txtRootInput.setManaged(!newVal);
            lblRootInput.setVisible(!newVal);
            lblRootInput.setManaged(!newVal);
        });
    }

    public void setServices(ValidationService validationService, RootService rootService) {
        this.validationService = validationService;
        this.rootService = rootService;
    }

    @FXML
    private void validate() {
        String word = txtWord.getText().trim();

        // Validate word input
        if (word.isEmpty()) {
            showError("إدخال فارغ", "الرجاء إدخال الكلمة للتحقق");
            return;
        }

        ValidationResult result;

        if (rbValidateAgainstRoot.isSelected()) {
            result = validateAgainstRoot(word);
        } else {
            result = identifyWordMode(word);
        }

        if (result != null) {
            displayResult(result, word);
            addToHistory(word, result);
        }
    }

    /**
     * Validates word against specific root.
     */
    private ValidationResult validateAgainstRoot(String word) {
        String rootLetters = txtRootInput.getText().trim();

        if (rootLetters.isEmpty()) {
            showError("إدخال فارغ", "الرجاء إدخال الجذر");
            return null;
        }

        if (!validationService.isValidRootFormat(rootLetters)) {
            showError("صيغة خاطئة", "الجذر يجب أن يكون 3 أحرف عربية (مثال: كتب)");
            return null;
        }

        return validationService.validateWord(word, rootLetters);
    }

    /**
     * Identifies word by finding root and pattern.
     */
    private ValidationResult identifyWordMode(String word) {
        return validationService.identifyWord(word);
    }

    /**
     * Displays validation result with visual feedback.
     */
    private void displayResult(ValidationResult result, String word) {
        resultPane.setVisible(true);
        resultPane.setManaged(true);

        // Clear previous styles
        resultPane.getStyleClass().removeAll("success", "error", "warning");

        if (result.isValid()) {
            displaySuccess(result, word);
        } else {
            displayFailure(result, word);
        }

        lblExplanation.setText(result.getExplanation());
    }

    /**
     * Displays successful validation.
     */
    private void displaySuccess(ValidationResult result, String word) {
        resultPane.getStyleClass().add("success");
        iconResult.setIconLiteral("mdi2c-check-circle");
        iconResult.setIconColor(Color.web("#27AE60"));

        lblResultStatus.setText("✓ صحيح / Valid");
        lblResultStatus.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold;");

        lblWordResult.setText(word);

        String rootText = (result.getIdentifiedRoot() != null)
                ? result.getIdentifiedRoot().getRootLetters()
                : "-";
        lblRootResult.setText(rootText);

        String patternText = (result.getIdentifiedPattern() != null)
                ? result.getIdentifiedPattern().getPatternId()
                : "-";
        lblPatternResult.setText(patternText);
    }

    /**
     * Displays failed validation.
     */
    private void displayFailure(ValidationResult result, String word) {
        resultPane.getStyleClass().add("error");
        iconResult.setIconLiteral("mdi2c-close-circle");
        iconResult.setIconColor(Color.web("#E74C3C"));

        lblResultStatus.setText("✗ غير صحيح / Invalid");
        lblResultStatus.setStyle("-fx-text-fill: #E74C3C; -fx-font-weight: bold;");

        lblWordResult.setText(word);
        lblRootResult.setText("-");
        lblPatternResult.setText("-");
    }

    /**
     * Adds validation entry to history list.
     */
    private void addToHistory(String word, ValidationResult result) {
        String status = result.isValid() ? "✓" : "✗";
        String entry = status + " " + word;

        if (result.isValid() && result.getIdentifiedRoot() != null) {
            entry += " (" + result.getIdentifiedRoot().getRootLetters() + ")";
            if (result.getIdentifiedPattern() != null) {
                entry += " [" + result.getIdentifiedPattern().getPatternId() + "]";
            }
        }

        historyItems.add(0, entry);

        // Keep history limited to 20 items
        while (historyItems.size() > 20) {
            historyItems.remove(historyItems.size() - 1);
        }
    }

    /**
     * Shows error alert dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
