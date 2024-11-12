# DNotes

This is a native Android application that combines note-taking and to-do list functionalities to help users stay organized and manage their tasks efficiently. The application provides a user-friendly interface and offers various features to enhance task management and productivity.

## Features

- **Add Note**: Users can add new notes by providing a title, content, and selecting a category for each note.
- **Categorize Notes**: Notes can be categorized into different categories to keep them organized and easily accessible.
- **Edit Note**: Users can edit the title, content, or category of an existing note.
- **Delete Note**: Users can delete a note from the application.
- **View Notes**: Users can view all notes or filter notes based on categories.
- **Delete Task**: Users can delete a task from the application.(Not completed)
- **View Tasks**: Users can view all tasks or filter tasks based on completion status or priority level.(Not completed)
- **Offline Access**: The application supports offline access, allowing users to view and edit notes and tasks even without an internet connection.
- **Dark Mode**: The application offers a dark mode option for users who prefer a darker color scheme.
 
## Tech Stack

The application is built using modern tools and libraries for Android development:

- **Base Android Components**: The core building blocks of Android applications, including activities, fragments, views, and bottom sheets.
- **Kotlin**: The primary programming language used for Android app development, offering concise syntax, null safety, coroutines, and other modern language features.
- **Room**: A persistence library that provides an abstraction layer over SQLite, making it easier to work with a database in Android applications.
- **Coroutines**: A lightweight concurrency framework for Kotlin that simplifies the execution of asynchronous tasks and improves code readability and scalability.
- **Flow**: A reactive streams library that offers an alternative to LiveData for handling asynchronous data streams in a more flexible and composable manner.
- **State/Shared Flow**: A state holder component of Kotlin coroutines, providing a flow-based approach to handle and emit state updates.
- **Navigation**: A library for handling navigation between different destinations and passing arguments in a safe and structured manner.
- **Hilt**: A dependency injection framework for Android applications that simplifies the setup and management of dependencies.
- **Data Binding**: A library that allows for declarative binding of UI components to data models, reducing boilerplate code and enhancing code readability.

## To-Do

- **Firebase Auth**
- **Backup to Firebase**
- **Watch support**
- **Push notification**
- **Progress Bar**
- **Widgets**
- **Google maps**
- **Image support**
- **Voice notes**
- **Reminders**
- **Note tags**
- **Priority property**
- **Add search/filter property**

## Installation

Application can be downloaded and installed from [google play](https://play.google.com/store/apps/details?id=com.duhapp.dnotes).

To build and run the application from source, follow these steps:
1. Clone the repository:
   ```bash
   git clone https://github.com/ahmethudaikaya25/DNotes.git
   ```
2. Open the project in Android Studio.
3. Build and run the application on an Android device or emulator.

## Usage

1. Launch the application on your Android device.
2. Use the interface to add, categorize, edit, and delete notes as desired.
3. Tap on a note to view its details, or use the search functionality to find specific notes.
4. Create tasks by providing the necessary details, including title, description, due date, and priority level.
5. Edit or delete tasks as needed.

