package com.nook.controller;

import com.nook.model.Book;
import com.nook.model.BookList;
import com.nook.model.BookListItem;
import com.nook.service.BookListService;
import com.nook.service.BookService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class BookListsController {

    @FXML private VBox bookListsContainer;
    @FXML private Label errorLabel;

    private final BookListService bookListService = new BookListService();
    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
            return;
        }
        loadBookLists();
    }

    @FXML
    private void handleToggleCreate() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("New Book List");
        dialogStage.setResizable(false);

        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 30px; -fx-min-width: 360px;");

        Label title = new Label("Create a New List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #222222;");

        Label nameLabel = new Label("List Name");
        nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. My Favourites");
        nameField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px; -fx-font-size: 13px;");

        CheckBox privateCheck = new CheckBox("Make this list private");
        privateCheck.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button createButton = new Button("Create List");
        createButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLbl.setText("Please enter a list name.");
                return;
            }
            try {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                bookListService.createBookList(userId, name, privateCheck.isSelected(), "custom");
                errorLabel.setText("");
                dialogStage.close();
                loadBookLists();
            } catch (Exception ex) {
                errorLbl.setText("Error: " + ex.getMessage());
            }
        });

        buttons.getChildren().addAll(cancelButton, createButton);
        root.getChildren().addAll(title, nameLabel, nameField, privateCheck, errorLbl, buttons);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void loadBookLists() {
        try {
            bookListsContainer.getChildren().clear();
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            List<BookList> lists = bookListService.getUserBookLists(userId);

            if (lists.isEmpty()) {
                Label noLists = new Label("You have no book lists yet. Click + to create one!");
                noLists.getStyleClass().add("no-reviews-label");
                bookListsContainer.getChildren().add(noLists);
                return;
            }

            for (BookList list : lists) {
                VBox listCard = createListCard(list);
                bookListsContainer.getChildren().add(listCard);
            }
        } catch (Exception e) {
            errorLabel.setText("Error loading book lists: " + e.getMessage());
        }
    }

    private VBox createListCard(BookList list) {
        VBox card = new VBox(12);
        card.getStyleClass().add("list-card");

        // Header row
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(list.getName());
        nameLabel.getStyleClass().add("list-card-title");

        Label privacyLabel = new Label(list.isPrivate() ? "🔒 Private" : "🌐 Public");
        privacyLabel.getStyleClass().add("list-privacy-badge");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Three dots menu
        MenuButton menuButton = new MenuButton("⋯");
        menuButton.getStyleClass().add("list-menu-button");

        MenuItem editItem = new MenuItem("EditList");
        editItem.setOnAction(e -> showEditDialog(list));

        MenuItem deleteItem = new MenuItem("Delete List");
        deleteItem.setOnAction(e -> handleDeleteList(list));

        menuButton.getItems().addAll(editItem, new SeparatorMenuItem(), deleteItem);

        header.getChildren().addAll(nameLabel, privacyLabel, spacer, menuButton);

        // Divider
        Separator divider = new Separator();
        divider.setStyle("-fx-background-color: #f5f5f5;");

        // Books in list
        FlowPane booksGrid = new FlowPane();
        booksGrid.setHgap(20);
        booksGrid.setVgap(20);
        booksGrid.getStyleClass().add("books-grid");

        try {
            List<BookListItem> items = bookListService.getBooksInList(list.getId());
            if (items.isEmpty()) {
                Label empty = new Label("No books in this list yet.");
                empty.getStyleClass().add("list-empty-label");
                booksGrid.getChildren().add(empty);
            } else {
                for (BookListItem item : items) {
                    Book book = bookService.getBookById(item.getBookId());
                    if (book != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nook/views/book-card.fxml"));
                            VBox bookCard = loader.load();
                            BookCardController controller = loader.getController();
                            controller.setBook(book);
                            booksGrid.getChildren().add(bookCard);
                        } catch (Exception ex) {
                            System.out.println("Error loading book card: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading books in list: " + e.getMessage());
        }

        card.getChildren().addAll(header, divider, booksGrid);
        return card;

    }

    private void showEditDialog(BookList list) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Edit Book List");
        dialogStage.setResizable(false);

        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 30px; -fx-min-width: 360px;");

        Label title = new Label("Edit List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #222222;");

        Label nameLabel = new Label("List Name");
        nameLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        TextField nameField = new TextField(list.getName());
        nameField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 10px; -fx-font-size: 13px;");

        CheckBox privateCheck = new CheckBox("Make this list private");
        privateCheck.setSelected(list.isPrivate());
        privateCheck.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        saveButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                errorLbl.setText("List name cannot be empty.");
                return;
            }
            try {
                bookListService.updateBookList(list.getId(), newName, privateCheck.isSelected());
                dialogStage.close();
                loadBookLists();
            } catch (Exception ex) {
                errorLbl.setText("Error: " + ex.getMessage());
            }
        });

        buttons.getChildren().addAll(cancelButton, saveButton);
        root.getChildren().addAll(title, nameLabel, nameField, privateCheck, errorLbl, buttons);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void handleDeleteList(BookList list) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Delete List");
        dialogStage.setResizable(false);

        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 30px; -fx-min-width: 360px;");

        Label title = new Label("Delete List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #222222;");

        Label message = new Label("Are you sure you want to delete \"" + list.getName() + "\"? This action cannot be undone.");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        cancelButton.setOnAction(e -> dialogStage.close());

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        deleteButton.setOnAction(e -> {
            try {
                bookListService.deleteBookList(list.getId());
                dialogStage.close();
                loadBookLists();
            } catch (Exception ex) {
                errorLabel.setText("Error deleting list: " + ex.getMessage());
            }
        });

        buttons.getChildren().addAll(cancelButton, deleteButton);
        root.getChildren().addAll(title, message, buttons);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }
}