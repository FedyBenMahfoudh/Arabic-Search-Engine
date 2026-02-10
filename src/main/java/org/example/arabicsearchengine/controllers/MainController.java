package org.example.arabicsearchengine.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.arabicsearchengine.repositories.PatternRepository;
import org.example.arabicsearchengine.repositories.RootRepository;
import org.example.arabicsearchengine.services.MorphologyService;
import org.example.arabicsearchengine.services.PatternService;
import org.example.arabicsearchengine.services.RootService;
import org.example.arabicsearchengine.services.ValidationService;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private VBox welcomePane;
    @FXML private Button btnRoots, btnPatterns, btnGenerate, btnValidate, btnStats;
    @FXML private Label lblRootCount, lblPatternCount, lblStatus;

    private RootRepository rootRepository;
    private PatternRepository patternRepository;
    private RootService rootService;
    private PatternService patternService;
    private MorphologyService morphologyService;
    private ValidationService validationService;

    @FXML
    public void initialize() {
        rootRepository = new RootRepository();
        patternRepository = new PatternRepository();

        rootService = new RootService(rootRepository);
        patternService = new PatternService(patternRepository);
        morphologyService = new MorphologyService();
        validationService = new ValidationService(rootRepository, patternRepository, morphologyService);

        // Initialize with default patterns
        patternService.initializeDefaultPatterns();

        updateStatusBar();
    }

    @FXML
    private void showRootManagement() {
        loadView("/views/RootManagementView.fxml", controller -> {
            if (controller instanceof RootManagementController rmc) {
                rmc.setServices(rootService, patternService, this::updateStatusBar);
            }
        });
        setActiveButton(btnRoots);
        lblStatus.setText("إدارة الجذور");
    }

    @FXML
    private void showPatternManagement() {
        loadView("/views/PatternManagementView.fxml", controller -> {
            if (controller instanceof PatternManagementController pmc) {
                pmc.setServices(patternService, this::updateStatusBar);
            }
        });
        setActiveButton(btnPatterns);
        lblStatus.setText("إدارة الأوزان");
    }

    @FXML
    private void showWordGeneration() {
        loadView("/views/WordGenerationView.fxml", controller -> {
            if (controller instanceof WordGenerationController wgc) {
                wgc.setServices(rootService, patternService, morphologyService);
            }
        });
        setActiveButton(btnGenerate);
        lblStatus.setText("توليد الكلمات");
    }

    @FXML
    private void showValidation() {
        loadView("/views/ValidationView.fxml", controller -> {
            if (controller instanceof ValidationController vc) {
                vc.setServices(validationService, rootService);
            }
        });
        setActiveButton(btnValidate);
        lblStatus.setText("التحقق الصرفي");
    }

    @FXML
    private void showStatistics() {
        showWelcomeWithStats();
        setActiveButton(btnStats);
        lblStatus.setText("الإحصائيات");
    }

    private void showWelcomeWithStats() {
        welcomePane.setVisible(true);
        welcomePane.setManaged(true);

    }

    private void loadView(String fxmlPath, ControllerInitializer initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            Object controller = loader.getController();
            if (initializer != null && controller != null) {
                initializer.init(controller);
            }

            welcomePane.setVisible(false);
            welcomePane.setManaged(false);

            contentArea.getChildren().removeIf(node -> node != welcomePane);
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
            lblStatus.setText("خطأ في تحميل العرض");
        }
    }

    private void setActiveButton(Button activeBtn) {
        btnRoots.getStyleClass().remove("nav-button-active");
        btnPatterns.getStyleClass().remove("nav-button-active");
        btnGenerate.getStyleClass().remove("nav-button-active");
        btnValidate.getStyleClass().remove("nav-button-active");
        btnStats.getStyleClass().remove("nav-button-active");

        if (activeBtn != null) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }

    public void updateStatusBar() {
        lblRootCount.setText("الجذور: " + rootService.getRootCount());
        lblPatternCount.setText("الأوزان: " + patternService.getPatternCount());
    }

    // Getters for shared services
    public RootService getRootService() { return rootService; }
    public PatternService getPatternService() { return patternService; }
    public MorphologyService getMorphologyService() { return morphologyService; }
    public ValidationService getValidationService() { return validationService; }

    @FunctionalInterface
    interface ControllerInitializer {
        void init(Object controller);
    }
}
