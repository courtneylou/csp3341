package com.nook.controller;

import com.nook.model.Book;
import com.nook.model.BookList;
import com.nook.model.Review;
import com.nook.model.User;
import com.nook.service.BookListService;
import com.nook.service.ReviewService;
import com.nook.service.UserService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class BookDetailController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label genreLabel;
    @FXML private Label isbnLabel;
    @FXML private Label publishedLabel;
    @FXML private Text descriptionText;
    @FXML private ImageView coverImage;
    @FXML private VBox reviewsBox;
    @FXML private VBox addReviewBox;
    @FXML private TextArea reviewTextArea;
    @FXML private CheckBox spoilerCheckBox;
    @FXML private Label errorLabel;
    @FXML private Button wishlistButton;
    @FXML private Button star1;
    @FXML private Button star2;
    @FXML private Button star3;
    @FXML private Button star4;
    @FXML private Button star5;

    private static Book selectedBook;
    private int selectedRating = 0;
    private final ReviewService reviewService = new ReviewService();
    private final BookListService bookListService = new BookListService();
    private final UserService userService = new UserService();

    public static void setSelectedBook(Book book) {
        selectedBook = book;
    }

    @FXML
    public void initialize() {
        if (selectedBook == null) return;

        titleLabel.setText(selectedBook.getTitle());
        authorLabel.setText("by " + selectedBook.getAuthorsAsString());
        genreLabel.setText(selectedBook.getGenre() != null ? selectedBook.getGenre() : "");
        isbnLabel.setText("ISBN: " + selectedBook.getIsbn());
        publishedLabel.setText(selectedBook.getPublishedDate() != null ? "Published: " + selectedBook.getPublishedDate() : "");
        descriptionText.setText(selectedBook.getDescription() != null ? selectedBook.getDescription() : "No description available.");

        if (selectedBook.getCoverUrl() != null && !selectedBook.getCoverUrl().isEmpty()) {
            try {
                coverImage.setImage(new Image(selectedBook.getCoverUrl(), true));
            } catch (Exception e) {
                System.out.println("Could not load cover image.");
            }
        }

        boolean loggedIn = SessionManager.getInstance().isLoggedIn();
        addReviewBox.setVisible(loggedIn);
        addReviewBox.setManaged(loggedIn);
        wishlistButton.setVisible(loggedIn);

        if (loggedIn) {
            updateWishlistButton();
        }

        loadReviews();
    }

    // ===== STAR RATING =====

    @FXML
    private void handleStarClick(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();
        if (clicked == star1) selectedRating = 1;
        else if (clicked == star2) selectedRating = 2;
        else if (clicked == star3) selectedRating = 3;
        else if (clicked == star4) selectedRating = 4;
        else if (clicked == star5) selectedRating = 5;
        updateStars();
    }

    private void updateStars() {
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < selectedRating) {
                stars[i].setText("★");
                stars[i].setStyle("-fx-font-size: 24px; -fx-text-fill: #f1c40f; -fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-font-size: 24px; -fx-text-fill: #cccccc; -fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
            }
        }
    }

    // ===== WISHLIST =====

    private void updateWishlistButton() {
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            boolean inWishlist = bookListService.isBookInWishlist(userId, selectedBook.getId());
            wishlistButton.setText(inWishlist ? "♥" : "♡");
            wishlistButton.setStyle(inWishlist ?
                    "-fx-font-size: 22px; -fx-text-fill: #e94560; -fx-background-color: transparent; -fx-cursor: hand;" :
                    "-fx-font-size: 22px; -fx-text-fill: #aaaaaa; -fx-background-color: transparent; -fx-cursor: hand;");
        } catch (Exception e) {
            System.out.println("Error checking wishlist: " + e.getMessage());
        }
    }

    @FXML
    private void handleWishlist() {
        if (!SessionManager.getInstance().isLoggedIn()) return;
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            boolean inWishlist = bookListService.isBookInWishlist(userId, selectedBook.getId());
            if (inWishlist) {
                bookListService.removeFromWishlist(userId, selectedBook.getId());
            } else {
                bookListService.addToWishlist(userId, selectedBook.getId());
            }
            updateWishlistButton();
        } catch (Exception e) {
            System.out.println("Error updating wishlist: " + e.getMessage());
        }
    }

    // ===== ADD TO LIST =====

    @FXML
    private void handleAddToList() {
        if (!SessionManager.getInstance().isLoggedIn()) return;

        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Add to List");
        dialogStage.setResizable(false);

        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #ffffff; -fx-padding: 30px; -fx-min-width: 380px;");

        Label title = new Label("Add to a Book List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #222222;");

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 12px;");

        VBox listOptions = new VBox(8);
        final BookList[] selectedList = {null};

        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            List<BookList> lists = bookListService.getUserBookLists(userId);

            if (lists.isEmpty()) {
                Label noLists = new Label("You have no book lists yet.");
                noLists.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 13px;");
                listOptions.getChildren().add(noLists);
            } else {
                for (BookList list : lists) {
                    Button listBtn = new Button("📚 " + list.getName() + (list.isPrivate() ? "  🔒" : ""));
                    listBtn.setMaxWidth(Double.MAX_VALUE);
                    listBtn.setStyle("-fx-background-color: #f9f9f9; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;");

                    listBtn.setOnMouseEntered(e -> {
                        if (selectedList[0] != list) {
                            listBtn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;");
                        }
                    });
                    listBtn.setOnMouseExited(e -> {
                        if (selectedList[0] != list) {
                            listBtn.setStyle("-fx-background-color: #f9f9f9; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;");
                        }
                    });
                    listBtn.setOnAction(e -> {
                        selectedList[0] = list;
                        // Reset all buttons
                        listOptions.getChildren().forEach(node -> {
                            if (node instanceof Button) {
                                node.setStyle("-fx-background-color: #f9f9f9; -fx-text-fill: #333333; -fx-background-radius: 6px; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT;");
                            }
                        });
                        // Highlight selected
                        listBtn.setStyle("-fx-background-color: #fde8ec; -fx-text-fill: #e94560; -fx-background-radius: 6px; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-font-size: 13px; -fx-alignment: CENTER_LEFT; -fx-border-color: #e94560; -fx-border-radius: 6px; -fx-border-width: 1;");
                    });

                    listOptions.getChildren().add(listBtn);
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading lists: " + e.getMessage());
        }

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button confirmButton = new Button("Add to List");
        confirmButton.setStyle("-fx-background-color: #e94560; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10px 20px; -fx-cursor: hand; -fx-font-size: 13px;");
        confirmButton.setOnAction(e -> {
            if (selectedList[0] == null) {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("Please select a list first.");
                return;
            }
            try {
                bookListService.addBookToList(selectedList[0].getId(), selectedBook.getId());
                statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
                statusLabel.setText("Added to \"" + selectedList[0].getName() + "\"!");
                confirmButton.setDisable(true);
            } catch (Exception ex) {
                statusLabel.setStyle("-fx-text-fill: #e94560; -fx-font-size: 12px;");
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        buttons.getChildren().add(confirmButton);
        root.getChildren().addAll(title, listOptions, statusLabel, buttons);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    // ===== REVIEWS =====

    private void loadReviews() {
        try {
            reviewsBox.getChildren().clear();
            List<Review> reviews = reviewService.getReviewsForBook(selectedBook.getId());

            if (reviews.isEmpty()) {
                Label noReviews = new Label("No reviews yet. Be the first!");
                noReviews.getStyleClass().add("no-reviews-label");
                reviewsBox.getChildren().add(noReviews);
                return;
            }

            for (Review review : reviews) {
                VBox reviewCard = createReviewCard(review);
                reviewsBox.getChildren().add(reviewCard);
            }
        } catch (Exception e) {
            System.out.println("Error loading reviews: " + e.getMessage());
        }
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(5);
        card.getStyleClass().add("review-card");

        String username = "";
        try {
            username = reviewService.getReviewerUsername(review.getUserId());
        } catch (Exception e) {
            username = "Unknown";
        }

        final String finalUsername = username;
        final int reviewUserId = review.getUserId();

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label usernameLabel = new Label(finalUsername);
        usernameLabel.getStyleClass().add("review-username");
        usernameLabel.setStyle("-fx-cursor: hand;");
        usernameLabel.setOnMouseClicked(e -> {
            try {
                User user = userService.getUserById(reviewUserId);
                if (user != null) {
                    ProfileController.setViewedUser(user);
                    NavigationUtil.navigateTo("/com/nook/views/profile.fxml");
                }
            } catch (Exception ex) {
                System.out.println("Error navigating to profile: " + ex.getMessage());
            }
        });

        Label ratingLabel = new Label("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
        ratingLabel.getStyleClass().add("review-rating");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(usernameLabel, ratingLabel, spacer);

        Label reviewText = new Label(review.getReviewText());
        reviewText.setWrapText(true);
        reviewText.getStyleClass().add("review-text");

        Label likesLabel = new Label("♥ " + review.getLikesCount());
        likesLabel.getStyleClass().add("review-likes");

        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.getChildren().add(likesLabel);

        if (SessionManager.getInstance().isLoggedIn()) {
            int currentUserId = SessionManager.getInstance().getCurrentUser().getId();
            if (currentUserId != reviewUserId) {
                try {
                    boolean alreadyLiked = reviewService.hasUserLikedReview(currentUserId, review.getId());
                    Button likeButton = new Button(alreadyLiked ? "Liked" : "Like");
                    likeButton.getStyleClass().add("like-button");
                    likeButton.setDisable(alreadyLiked);
                    likeButton.setOnAction(e -> {
                        try {
                            reviewService.likeReview(review.getId());
                            reviewService.addReviewLike(currentUserId, review.getId());
                            review.setLikesCount(review.getLikesCount() + 1);
                            likesLabel.setText("♥ " + review.getLikesCount());
                            likeButton.setText("Liked");
                            likeButton.setDisable(true);
                        } catch (Exception ex) {
                            System.out.println("Error liking review: " + ex.getMessage());
                        }
                    });
                    bottomRow.getChildren().add(likeButton);
                } catch (Exception e) {
                    System.out.println("Error checking like status: " + e.getMessage());
                }
            }
        }

        // Add children in correct order
        card.getChildren().add(topRow);

        if (review.isHasSpoiler()) {
            Label spoilerWarning = new Label("⚠ Spoiler Warning");
            spoilerWarning.getStyleClass().add("spoiler-warning");
            card.getChildren().add(spoilerWarning);
        }

        card.getChildren().add(reviewText);
        card.getChildren().add(bottomRow);

        return card;
    }

    @FXML
    private void handleSubmitReview() {
        if (!SessionManager.getInstance().isLoggedIn()) return;

        String text = reviewTextArea.getText().trim();
        boolean hasSpoiler = spoilerCheckBox.isSelected();
        int userId = SessionManager.getInstance().getCurrentUser().getId();

        if (selectedRating == 0) {
            errorLabel.setText("Please select a star rating.");
            return;
        }

        if (text.isEmpty()) {
            errorLabel.setText("Please write a review.");
            return;
        }

        try {
            reviewService.addReview(selectedBook.getId(), userId, selectedRating, text, hasSpoiler);
            reviewTextArea.clear();
            spoilerCheckBox.setSelected(false);
            selectedRating = 0;
            updateStars();
            errorLabel.setText("");
            loadReviews();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Something went wrong. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }
}