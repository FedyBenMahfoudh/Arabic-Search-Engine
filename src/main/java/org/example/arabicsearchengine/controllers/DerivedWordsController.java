package org.example.arabicsearchengine.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.services.RootService;

public class DerivedWordsController {
    @FXML private TextField txtRootSearch;
    @FXML private TableView<DerivedWord> tableView;
    @FXML private TableColumn<DerivedWord, String> colWord;
    @FXML
    private TableColumn<DerivedWord, String> colPattern;
    @FXML private TableColumn<DerivedWord, Integer> colFrequency;

    private RootService rootService;

    @FXML
    public void initialize() {

        colWord.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getWord()));

        colPattern.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPattern().getPatternId()));

        colFrequency.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getFrequency()).asObject());
    }

    public void setRootService(RootService rootService) {
        this.rootService = rootService;
    }

    @FXML
    private void loadDerivedWords() {

        String rootLetters = txtRootSearch.getText().trim();

        Root root = rootService.findByLetters(rootLetters);

        if (root == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("الجذر غير موجود");
            alert.show();
            return;
        }

        tableView.setItems(FXCollections.observableArrayList(root.getDerivedWords()));
    }
}
