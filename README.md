# Nook

The application is a book tracking and reviewing desktop application inspired by Goodreads, 
built using Java 26, JavaFX 26, SQLite, and IntelliJ IDEA 2025.3.4. It will allow users to discover, 
add, and review books, and manage personal reading lists.

## Tech Stack

- Java 26
- JavaFX 26
- SQLite (via sqlite-jdbc)
- IntelliJ IDEA

## Features

### Authentication
- The system shall allow users to register with a unique email address and password. 
- The system shall allow registered users to log in and out. 
- The system shall hash all passwords before storing them. 
- The system shall prevent banned users from logging in.

### User Account
- The user shall be able to edit their email, password and biography. 
- The user shall be able to delete their own account.

### Admin Account
- The administrator shall be able to ban, promotes and delete user accounts.
- The administrator shall be able to edit and delete books.

### Books
- The user shall be able to add books via ISBN. 
- The system shall automatically retrieve book metadata via the OpenLibrary API. 
- The system shall detect and prevent duplicate ISBN entries. 
- The user shall be able to search for books by title, author or ISBN.

### Reviews
- The user shall be able to write one review per book including a rating and spoiler tag. 
- The system shall display all reviews publicly. 
- The user shall be able to like reviews.

### Wishlist & Booklists
- The user shall be able to wishlist books. 
- The user shall be able to create, edit and delete named book lists. 
- The user shall be able to set book lists as public or private.

### Profiles
- The system shall provide each user with a public profile page. 
- The user shall be able to follow and unfollow other users.

## Setup

1. Clone the repository
2. Open in IntelliJ IDEA
3. Add the following to project libraries:
   - `javafx-sdk-26\lib`
   - `sqlite-jdbc-3.52.0.jar`
4. Configure VM options:
```
   --module-path "C:\PATH\TO\javafx-sdk-26\lib" --add-modules javafx.controls,javafx.fxml
```
5. Run `Main.java`

The database will be created automatically on first run.