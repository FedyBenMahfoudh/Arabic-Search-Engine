package org.example.arabicsearchengine.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.models.ValidationResult;
import org.example.arabicsearchengine.services.RootService;
import org.example.arabicsearchengine.services.ValidationService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class ValidationController {
    @FXML
    private RadioButton rbValidateAgainstRoot;
    @FXML private RadioButton rbIdentifyWord;
    @FXML private TextField txtWord;
    @FXML private TextField txtRootInput;
    @FXML private Label lblRootInput;
    @FXML private VBox resultPane;
    @FXML private FontIcon iconResult;
    @FXML private Label lblResultStatus;
    @FXML private Label lblWordResult;
    @FXML private Label lblRootResult;
    @FXML private Label lblPatternResult;
    @FXML private Label lblExplanation;
    @FXML private ListView<String> derivedList;


    private ValidationService validationService;
    private RootService rootService;
    private ObservableList<String> historyItems;

    @FXML
    public void initialize() {
        historyItems = FXCollections.observableArrayList();
        derivedList.setItems(historyItems);

        derivedList.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        // Toggle root input visibility based on mode
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

        if (word.isEmpty()) {
            showError("الرجاء إدخال الكلمة");
            return;
        }

        ValidationResult result;

        if (rbValidateAgainstRoot.isSelected()) {
            // Validate against specific root
            String rootLetters = txtRootInput.getText().trim();
            if (rootLetters.isEmpty()) {
                showError("الرجاء إدخال الجذر");
                return;
            }
            if (rootLetters.length() != 3) {
                showError("الجذر يجب أن يكون 3 أحرف");
                return;
            }

            result = validationService.validateWord(word, rootLetters);
        } else {
            // Identify word (find root and pattern)
            result = validationService.identifyWord(word);
        }

        displayResult(result, word);
        addToHistory(word, result);
    }

    private void displayResult(ValidationResult result, String word) {
        resultPane.setVisible(true);
        resultPane.setManaged(true);

        resultPane.getStyleClass().removeAll("success", "error");

        if (result.isValid()) {
            resultPane.getStyleClass().add("success");
            iconResult.setIconLiteral("mdi2c-check-decagram");
            iconResult.setIconColor(Color.web("#4caf50"));
            lblResultStatus.setText("صالح - Valid");

            lblWordResult.setText(word);
            lblRootResult.setText(result.getIdentifiedRoot().getRootLetters());
            lblPatternResult.setText(result.getIdentifiedPattern().getPatternId());

            Root root = result.getIdentifiedRoot();

            List<String> derived = root.getDerivedWords()
                    .stream()
                    .map(DerivedWord::getWord)
                    .toList();

            derivedList.getItems().setAll(derived);

        } else {
            resultPane.getStyleClass().add("error");
            iconResult.setIconLiteral("mdi2c-close-circle");
            iconResult.setIconColor(Color.web("#f44336"));
            lblResultStatus.setText("غير صالح - Invalid");

            lblWordResult.setText(word);
            lblRootResult.setText("-");
            lblPatternResult.setText("-");
            derivedList.getItems().clear();
        }

        lblWordResult.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        lblRootResult.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        lblPatternResult.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        lblExplanation.setText(result.getExplanation());
    }

    private void addToHistory(String word, ValidationResult result) {
        String status = result.isValid() ? "✓" : "✗";
        String entry = status + " " + word;

        if (result.isValid() && result.getIdentifiedRoot() != null) {
            entry += " (" + result.getIdentifiedRoot().getRootLetters() + ")";
        }

        // Add to top of history
        historyItems.add(0, entry);

        // Keep history limited
        if (historyItems.size() > 20) {
            historyItems.remove(historyItems.size() - 1);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("خطأ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
