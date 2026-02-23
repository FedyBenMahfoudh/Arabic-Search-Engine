package org.example.arabicsearchengine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.example.arabicsearchengine.datastructures.tree.AVLNode;
import org.example.arabicsearchengine.datastructures.tree.AVLTree;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.services.RootService;

/**
 * Controller for the Statistics view.
 * Visualises the AVL tree that stores the Arabic roots.
 */
public class StatisticsController {

    @FXML private Label lblTreeHeight;
    @FXML private Label lblNodeCount;
    @FXML private Label lblStatus;
    @FXML private ScrollPane treeScrollPane;
    @FXML private Pane treeCanvas;

    private RootService rootService;

    // Drawing constants
    private static final double NODE_RADIUS = 28;
    private static final double V_GAP = 80;        // vertical gap between levels
    private static final Color NODE_FILL = Color.web("#1B5E59");
    private static final Color NODE_STROKE = Color.web("#0F3E3A");
    private static final Color LEAF_FILL = Color.web("#34A89C");
    private static final Color LINE_COLOR = Color.web("#B8936A");
    private static final Color TEXT_COLOR = Color.WHITE;

    public void setServices(RootService rootService) {
        this.rootService = rootService;
        drawTree();
    }

    /**
     * Redraws the tree visualisation from scratch.
     */
    @FXML
    private void refreshTree() {
        drawTree();
    }

    private void drawTree() {
        treeCanvas.getChildren().clear();

        if (rootService == null) {
            lblStatus.setText("لا توجد خدمة متاحة");
            return;
        }

        AVLTree<Root> tree = rootService.getRepository().getTree();

        if (tree == null || tree.isEmpty()) {
            lblTreeHeight.setText("0");
            lblNodeCount.setText("0");
            lblStatus.setText("الشجرة فارغة — قم بإضافة جذور أولاً");
            return;
        }

        int height = tree.getTreeHeight();
        int size = tree.getSize();

        lblTreeHeight.setText(String.valueOf(height));
        lblNodeCount.setText(String.valueOf(size));
        lblStatus.setText("تم رسم الشجرة بنجاح (" + size + " عقدة)");

        // Calculate canvas dimensions based on tree structure
        // Width needs to accommodate the widest level (bottom), which can have up to 2^(h-1) nodes
        double canvasWidth = Math.max(800, Math.pow(2, height) * (NODE_RADIUS * 2 + 10));
        double canvasHeight = Math.max(400, height * V_GAP + 100);

        treeCanvas.setPrefWidth(canvasWidth);
        treeCanvas.setPrefHeight(canvasHeight);
        treeCanvas.setMinWidth(canvasWidth);
        treeCanvas.setMinHeight(canvasHeight);

        // Start drawing from the root: centred horizontally, near the top
        double startX = canvasWidth / 2;
        double startY = 50;
        // hGap is the horizontal offset for children of the root
        double hGap = canvasWidth / 4;

        drawNode(tree.getRoot(), startX, startY, hGap);
    }

    /**
     * Recursively draws a node and its children.
     *
     * @param node  AVL node to draw
     * @param x     centre-x of this node
     * @param y     centre-y of this node
     * @param hGap  horizontal offset for children
     */
    private void drawNode(AVLNode<Root> node, double x, double y, double hGap) {
        if (node == null) return;

        boolean isLeaf = (node.getLeft() == null && node.getRight() == null);

        // --- Draw lines to children first (so they sit behind the circles) ---
        if (node.getLeft() != null) {
            double childX = x - hGap;
            double childY = y + V_GAP;
            Line line = new Line(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
            line.setStroke(LINE_COLOR);
            line.setStrokeWidth(2.5);
            treeCanvas.getChildren().add(line);

            drawNode(node.getLeft(), childX, childY, hGap / 2);
        }

        if (node.getRight() != null) {
            double childX = x + hGap;
            double childY = y + V_GAP;
            Line line = new Line(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS);
            line.setStroke(LINE_COLOR);
            line.setStrokeWidth(2.5);
            treeCanvas.getChildren().add(line);

            drawNode(node.getRight(), childX, childY, hGap / 2);
        }

        // --- Draw this node's circle ---
        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(isLeaf ? LEAF_FILL : NODE_FILL);
        circle.setStroke(NODE_STROKE);
        circle.setStrokeWidth(2);
        treeCanvas.getChildren().add(circle);

        // --- Draw the root letters text inside the circle ---
        String label = node.getData().getRootLetters();
        Text text = new Text(label);
        text.setFont(Font.font("Noto Naskh Arabic", FontWeight.BOLD, 16));
        text.setFill(TEXT_COLOR);
        // Centre the text on the circle
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();
        text.setX(x - textWidth / 2);
        text.setY(y + textHeight / 4);
        treeCanvas.getChildren().add(text);

        // --- Draw height badge (small label below the node) ---
        Text heightLabel = new Text("h=" + node.getHeight());
        heightLabel.setFont(Font.font("Segoe UI", 10));
        heightLabel.setFill(Color.web("#5F5F5F"));
        double hlWidth = heightLabel.getLayoutBounds().getWidth();
        heightLabel.setX(x - hlWidth / 2);
        heightLabel.setY(y + NODE_RADIUS + 14);
        treeCanvas.getChildren().add(heightLabel);
    }
}
