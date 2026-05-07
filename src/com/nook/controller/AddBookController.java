package com.nook.controller;

import com.nook.model.Book;
import com.nook.service.BookService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddBookController {

    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private TextField authorsField;
    @FXML private TextField genreField;
    @FXML private TextField coverUrlField;
    @FXML private TextField publishedDateField;
    @FXML private TextArea descriptionArea;
    @FXML private Label errorLabel;
    @FXML private Label lookupStatusLabel;
    @FXML private Button lookupButton;
    @FXML private ImageView coverPreview;

    private final BookService bookService = new BookService();

    @FXML
    private void handleIsbnLookup() {
        String isbn = isbnField.getText().trim().replace("-", "");
        if (isbn.isEmpty()) {
            errorLabel.setText("Please enter an ISBN first.");
            return;
        }

        lookupButton.setDisable(true);
        lookupStatusLabel.setText("Looking up...");
        errorLabel.setText("");

        Thread thread = new Thread(() -> {
            try {
                // Step 1 - get book details
                String bookUrlStr = "https://openlibrary.org/isbn/" + isbn + ".json";
                String bookJson = fetchUrl(bookUrlStr);

                if (bookJson.isEmpty()) {
                    Platform.runLater(() -> {
                        lookupStatusLabel.setText("No book found for this ISBN.");
                        lookupButton.setDisable(false);
                    });
                    return;
                }

                // Parse title
                String title = extractValue(bookJson, "\"title\": \"", "\"");
                if (title.isEmpty()) title = extractValue(bookJson, "\"title\":\"", "\"");

                // Parse published date
                String publishDate = extractValue(bookJson, "\"publish_date\": \"", "\"");
                if (publishDate.isEmpty()) publishDate = extractValue(bookJson, "\"publish_date\":\"", "\"");

                // Parse cover ID for image
                String coverId = extractValue(bookJson, "\"covers\": [", "]");
                if (coverId.isEmpty()) coverId = extractValue(bookJson, "\"covers\":[", "]");
                coverId = coverId.trim();
                String coverUrl = coverId.isEmpty() ? "" : "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";

                // Step 2 - get author details from works
                String authorsBuilder = "";
                String worksKey = extractValue(bookJson, "\"works\": [{\"key\": \"", "\"");
                if (worksKey.isEmpty()) worksKey = extractValue(bookJson, "\"works\":[{\"key\":\"", "\"");

                if (!worksKey.isEmpty()) {
                    String worksUrl = "https://openlibrary.org" + worksKey + ".json";
                    String worksJson = fetchUrl(worksUrl);

                    StringBuilder authors = new StringBuilder();
                    int idx = 0;
                    while (true) {
                        int authorKeyIndex = worksJson.indexOf("\"author\": {\"key\": \"", idx);
                        if (authorKeyIndex == -1) authorKeyIndex = worksJson.indexOf("\"author\":{\"key\":\"", idx);
                        if (authorKeyIndex == -1) break;

                        String authorKey = extractValue(worksJson.substring(authorKeyIndex), "\"key\": \"", "\"");
                        if (authorKey.isEmpty()) authorKey = extractValue(worksJson.substring(authorKeyIndex), "\"key\":\"", "\"");

                        if (!authorKey.isEmpty()) {
                            String authorUrl = "https://openlibrary.org" + authorKey + ".json";
                            String authorJson = fetchUrl(authorUrl);
                            String authorName = extractValue(authorJson, "\"name\": \"", "\"");
                            if (authorName.isEmpty()) authorName = extractValue(authorJson, "\"name\":\"", "\"");
                            if (!authorName.isEmpty()) {
                                if (!authors.isEmpty()) authors.append(", ");
                                authors.append(authorName);
                            }
                        }
                        idx = authorKeyIndex + 10;
                    }
                    authorsBuilder = authors.toString();
                }

                final String finalTitle = title;
                final String finalAuthors = authorsBuilder;
                final String finalPublishDate = publishDate;
                final String finalCoverUrl = coverUrl;

                Platform.runLater(() -> {
                    if (!finalTitle.isEmpty()) titleField.setText(finalTitle);
                    if (!finalAuthors.isEmpty()) authorsField.setText(finalAuthors);
                    if (!finalPublishDate.isEmpty()) publishedDateField.setText(finalPublishDate);
                    if (!finalCoverUrl.isEmpty()) {
                        coverUrlField.setText(finalCoverUrl);
                        try {
                            coverPreview.setImage(new Image(finalCoverUrl, true));
                            coverPreview.setVisible(true);
                        } catch (Exception e) {
                            System.out.println("Could not load cover preview.");
                        }
                    }
                    lookupStatusLabel.setText(finalTitle.isEmpty() ? "Book found but some details missing." : "✓ Book found!");
                    lookupButton.setDisable(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    lookupStatusLabel.setText("Lookup failed: " + e.getMessage());
                    lookupButton.setDisable(false);
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private String fetchUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Nook/1.0 (nookapptemp@gmail.com)");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String extractValue(String json, String startKey, String endKey) {
        int start = json.indexOf(startKey);
        if (start == -1) return "";
        start += startKey.length();
        int end = json.indexOf(endKey, start);
        if (end == -1) return "";
        return json.substring(start, end);
    }

    @FXML
    private void handleAddBook() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String authorsText = authorsField.getText().trim();
        String genre = genreField.getText().trim();
        String coverUrl = coverUrlField.getText().trim();
        String publishedDate = publishedDateField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || authorsText.isEmpty()) {
            errorLabel.setText("ISBN, title and at least one author are required.");
            return;
        }

        List<String> authorNames = Arrays.stream(authorsText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        int userId = SessionManager.getInstance().getCurrentUser().getId();

        try {
            Book book = bookService.addBook(isbn, title, authorNames, genre, description, coverUrl, publishedDate, userId);
            BookDetailController.setSelectedBook(book);
            NavigationUtil.navigateTo("/com/nook/views/book-detail.fxml");

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.startsWith("DUPLICATE:")) {
                int bookId = Integer.parseInt(message.split(":")[1]);
                try {
                    Book existing = bookService.getBookById(bookId);
                    errorLabel.setText("This book already exists! Redirecting...");
                    BookDetailController.setSelectedBook(existing);
                    NavigationUtil.navigateTo("/com/nook/views/book-detail.fxml");
                } catch (Exception ex) {
                    errorLabel.setText("This book already exists.");
                }
            } else {
                errorLabel.setText(message);
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }
}