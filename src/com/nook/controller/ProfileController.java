package com.nook.controller;

import com.nook.model.*;
import com.nook.service.BookListService;
import com.nook.service.BookService;
import com.nook.service.ReviewService;
import com.nook.service.UserService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label bioLabel;
    @FXML private VBox reviewsBox;
    @FXML private VBox wishlistBox;
    @FXML private VBox bookListsBox;

    private static User viewedUser;
    private final UserService userService = new UserService();
    private final ReviewService reviewService = new ReviewService();
    private final BookListService bookListService = new BookListService();
    private final BookService bookService = new BookService();

    public static void setViewedUser(User user) {
        viewedUser = user;
    }

    @FXML
    public void initialize() {
        if (viewedUser == null && SessionManager.getInstance().isLoggedIn()) {
            viewedUser = SessionManager.getInstance().getCurrentUser();
        }

        if (viewedUser == null) {
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
            return;
        }

        usernameLabel.setText(viewedUser.getUsername());
        bioLabel.setText(viewedUser.getBio() != null ? viewedUser.getBio() : "No bio yet.");

        loadReviews();
        loadWishlist();
        loadBookLists();
    }

    private void loadReviews() {
        try {
            reviewsBox.getChildren().clear();
            List<Review> reviews = reviewService.getReviewsByUser(viewedUser.getId());

            if (reviews.isEmpty()) {
                Label noReviews = new Label("No reviews yet.");
                noReviews.getStyleClass().add("no-reviews-label");
                reviewsBox.getChildren().add(noReviews);
                return;
            }

            for (Review review : reviews) {
                VBox card = new VBox(5);
                card.getStyleClass().add("review-card");
                card.setStyle("-fx-cursor: hand;");

                try {
                    Book book = bookService.getBookById(review.getBookId());
                    Label bookTitle = new Label(book != null ? book.getTitle() : "Unknown Book");
                    bookTitle.getStyleClass().add("review-book-title");
                    card.getChildren().add(bookTitle);

                    card.setOnMouseClicked(e -> {
                        if (book != null) {
                            BookDetailController.setSelectedBook(book);
                            NavigationUtil.navigateTo("/com/nook/views/book-detail.fxml");
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Error loading book: " + e.getMessage());
                }

                Label rating = new Label("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
                rating.getStyleClass().add("review-rating");

                Label text = new Label(review.getReviewText());
                text.setWrapText(true);
                text.getStyleClass().add("review-text");

                card.getChildren().addAll(rating, text);
                reviewsBox.getChildren().add(card);
            }
        } catch (Exception e) {
            System.out.println("Error loading reviews: " + e.getMessage());
        }
    }

    private void loadWishlist() {
        try {
            wishlistBox.getChildren().clear();
            List<Wishlist> wishlist = bookListService.getWishlist(viewedUser.getId());

            if (wishlist.isEmpty()) {
                Label noBooks = new Label("No books in wishlist yet.");
                noBooks.getStyleClass().add("no-reviews-label");
                wishlistBox.getChildren().add(noBooks);
                return;
            }

            FlowPane booksGrid = new FlowPane();
            booksGrid.setHgap(20);
            booksGrid.setVgap(20);
            booksGrid.getStyleClass().add("books-grid");

            for (Wishlist item : wishlist) {
                Book book = bookService.getBookById(item.getBookId());
                if (book != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nook/views/book-card.fxml"));
                        VBox bookCard = loader.load();
                        BookCardController controller = loader.getController();
                        controller.setBook(book);
                        booksGrid.getChildren().add(bookCard);
                    } catch (Exception e) {
                        System.out.println("Error loading book card: " + e.getMessage());
                    }
                }
            }
            wishlistBox.getChildren().add(booksGrid);
        } catch (Exception e) {
            System.out.println("Error loading wishlist: " + e.getMessage());
        }
    }

    private void loadBookLists() {
        try {
            bookListsBox.getChildren().clear();
            boolean isOwnProfile = SessionManager.getInstance().isLoggedIn() &&
                    SessionManager.getInstance().getCurrentUser().getId() == viewedUser.getId();

            List<BookList> lists = isOwnProfile ?
                    bookListService.getUserBookLists(viewedUser.getId()) :
                    bookListService.getPublicBookLists(viewedUser.getId());

            if (lists.isEmpty()) {
                Label noLists = new Label("No book lists yet.");
                noLists.getStyleClass().add("no-reviews-label");
                bookListsBox.getChildren().add(noLists);
                return;
            }

            for (BookList list : lists) {
                if (!isOwnProfile && list.isPrivate()) continue;

                VBox listCard = new VBox(12);
                listCard.getStyleClass().add("list-card");

                HBox header = new HBox(10);
                header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label nameLabel = new Label(list.getName());
                nameLabel.getStyleClass().add("list-card-title");

                Label privacyLabel = new Label(list.isPrivate() ? "🔒 Private" : "🌐 Public");
                privacyLabel.getStyleClass().add("list-privacy-badge");

                header.getChildren().addAll(nameLabel, privacyLabel);

                Separator divider = new Separator();

                FlowPane booksGrid = new FlowPane();
                booksGrid.setHgap(20);
                booksGrid.setVgap(20);
                booksGrid.getStyleClass().add("books-grid");

                List<com.nook.model.BookListItem> items = bookListService.getBooksInList(list.getId());
                if (items.isEmpty()) {
                    Label empty = new Label("No books in this list yet.");
                    empty.getStyleClass().add("list-empty-label");
                    booksGrid.getChildren().add(empty);
                } else {
                    for (com.nook.model.BookListItem item : items) {
                        Book book = bookService.getBookById(item.getBookId());
                        if (book != null) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nook/views/book-card.fxml"));
                                VBox bookCard = loader.load();
                                BookCardController controller = loader.getController();
                                controller.setBook(book);
                                booksGrid.getChildren().add(bookCard);
                            } catch (Exception e) {
                                System.out.println("Error loading book card: " + e.getMessage());
                            }
                        }
                    }
                }

                listCard.getChildren().addAll(header, divider, booksGrid);
                bookListsBox.getChildren().add(listCard);
            }
        } catch (Exception e) {
            System.out.println("Error loading book lists: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProfile() {
        NavigationUtil.navigateTo("/com/nook/views/edit-profile.fxml");
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }
}