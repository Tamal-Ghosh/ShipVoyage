package org.example.shipvoyage.controller.admin;

import java.io.File;
import java.util.List;

import org.example.shipvoyage.dao.PhotoDAO;
import org.example.shipvoyage.model.FeaturedPhoto;
import org.example.shipvoyage.util.ThreadPool;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class FeaturedPhotosController {

    @FXML private TextField imagePathField;
    @FXML private TextField titleField;
    @FXML private TextArea descField;
    @FXML private ImageView preview;
    @FXML private GridPane tilesPane;

    private Integer editingId = null;

    @FXML
    public void initialize() {
        refreshTiles();
    }

    @FXML
    private void onNew() {
        editingId = null;
        onReset();
    }

    @FXML
    private void onBrowse() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image");
        File file = chooser.showOpenDialog(imagePathField.getScene().getWindow());
        if (file != null) {
            imagePathField.setText(file.getAbsolutePath());
            try { preview.setImage(new Image("file:" + file.getAbsolutePath(), 320, 180, true, true)); } catch (Exception ignored) {}
        }
    }

    @FXML
    private void onSave() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String desc = descField.getText() == null ? "" : descField.getText().trim();
        String path = imagePathField.getText() == null ? "" : imagePathField.getText().trim();
        if (title.isEmpty()) { showAlert("Title is required"); return; }
        if (editingId == null) {
            PhotoDAO.insert(new FeaturedPhoto(0, title, desc, path));
        } else {
            FeaturedPhoto p = new FeaturedPhoto(editingId, title, desc, path);
            PhotoDAO.update(p);
        }
        refreshTiles();
        onReset();
    }

    @FXML
    private void onReset() {
        imagePathField.clear();
        titleField.clear();
        descField.clear();
        preview.setImage(null);
        editingId = null;
    }

    private void refreshTiles() {
        ThreadPool.getExecutor().execute(() -> {
            List<FeaturedPhoto> all = PhotoDAO.getAll();
            Platform.runLater(() -> {
                tilesPane.getChildren().clear();
                int max = 6;
                for (int i = 0; i < max; i++) {
                    Node tile;
                    if (i < all.size()) tile = buildTile(all.get(i));
                    else tile = buildEmptyTile();
                    tilesPane.add(tile, i % 3, i / 3);
                    GridPane.setVgrow(tile, Priority.NEVER);
                    GridPane.setHgrow(tile, Priority.ALWAYS);
                }
            });
        });
    }

    private VBox buildTile(FeaturedPhoto p) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E5E7EB; -fx-padding: 10;");
        ImageView iv = new ImageView();
        iv.setFitWidth(300); iv.setFitHeight(140); iv.setPreserveRatio(true);
        try { if (p.getImagePath()!=null && !p.getImagePath().isBlank()) iv.setImage(new Image("file:"+p.getImagePath(), 300, 140, true, true)); } catch(Exception ignored) {}
        Label t = new Label(p.getTitle()); t.setStyle("-fx-font-weight: bold; -fx-text-fill:#1F2937;");
        Label d = new Label(p.getDescription()); d.setStyle("-fx-text-fill:#6B7280; -fx-font-size:12px;"); d.setWrapText(true);
        HBox actions = new HBox(8);
        Button edit = new Button("Edit"); edit.setOnAction(e->loadForEdit(p));
        Button del = new Button("Delete"); del.setStyle("-fx-text-fill:#DC2626;"); del.setOnAction(e->{ PhotoDAO.delete(p.getId()); refreshTiles(); });
        actions.getChildren().addAll(edit, del);
        box.getChildren().addAll(iv, t, d, actions);
        return box;
    }

    private VBox buildEmptyTile() {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E5E7EB; -fx-padding: 10; -fx-alignment: CENTER;");
        Label t = new Label("Empty Slot"); t.setStyle("-fx-text-fill:#6B7280;");
        Button add = new Button("Add Here"); add.setOnAction(e->{ onNew(); });
        box.getChildren().addAll(t, add);
        return box;
    }

    private void loadForEdit(FeaturedPhoto p) {
        editingId = p.getId();
        titleField.setText(p.getTitle());
        descField.setText(p.getDescription());
        imagePathField.setText(p.getImagePath());
        try { preview.setImage(new Image("file:" + p.getImagePath(), 320, 180, true, true)); } catch (Exception ignored) {}
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }
}
