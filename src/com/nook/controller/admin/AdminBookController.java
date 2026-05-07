package com.nook.controller.admin;

import com.nook.model.Book;
import com.nook.service.BookService;
import com.nook.util.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class
AdminBookController {

    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private Label statusLabel;

    @FXML private TextField editTitleField;
    @FXML private TextField editAuthorField;
    @FXML private TextField editGenreField;
    @FXML private TextField editCoverUrlField;
    @FXML private TextField editPublishedDateField;
    @FXML private TextArea editDescriptionArea;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthorsAsString()));
        isbnColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));

        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) populateEditFields(newVal);
        });

        loadBooks();
    }

    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            booksTable.setItems(FXCollections.observableArrayList(books));
        } catch (Exception e) {
            statusLabel.setText("Error loading books: " + e.getMessage());
        }
    }

    private void populateEditFields(Book book) {
        editTitleField.setText(book.getTitle());
        editAuthorField.setText(book.getAuthorsAsString());
        editGenreField.setText(book.getGenre() != null ? book.getGenre() : "");
        editCoverUrlField.setText(book.getCoverUrl() != null ? book.getCoverUrl() : "");
        editPublishedDateField.setText(book.getPublishedDate() != null ? book.getPublishedDate() : "");
        editDescriptionArea.setText(book.getDescription() != null ? book.getDescription() : "");
    }

    private Book getSelectedBook() {
        Book selected = booksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a book first.");
        }
        return selected;
    }

    @FXML
    private void handleSaveEdit() {
        Book book = getSelectedBook();
        if (book == null) return;

        String title = editTitleField.getText().trim();
        String author = editAuthorField.getText().trim();
        String genre = editGenreField.getText().trim();
        String coverUrl = editCoverUrlField.getText().trim();
        String publishedDate = editPublishedDateField.getText().trim();
        String description = editDescriptionArea.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {
            statusLabel.setText("Title and author are required.");
            return;
        }

        try {
            List<String> authorNames = Arrays.stream(editAuthorField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            bookService.updateBook(book, title, authorNames, genre, description, coverUrl, publishedDate);
            statusLabel.setText("Book updated successfully.");
            loadBooks();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteBook() {
        Book book = getSelectedBook();
        if (book == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Book");
        confirm.setHeaderText("Delete \"" + book.getTitle() + "\"?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    bookService.deleteBook(book.getId());
                    statusLabel.setText("Book deleted successfully.");
                    loadBooks();
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/admin/admin-dashboard.fxml");
    }
}