# Movie Recommender

## Project Overview
Movie Recommender is an Android application that helps users discover and get personalized movie recommendations. The app uses modern Android development practices and provides a user-friendly interface for browsing and discovering movies.

## Technologies Used
- **Programming Language**: Java
- **Android SDK**: Minimum SDK 26, Target SDK 35
- **Architecture Components**:
  - Room Database for local storage
  - ViewModel and LiveData for UI state management
  - RecyclerView for efficient list display
- **UI Components**:
  - Material Design Components
  - ConstraintLayout
  - CardView
  - SwipeRefreshLayout
- **Third-party Libraries**:
  - Glide for image loading
  - Gson for JSON parsing
  - CircleImageView for circular image views

## Prerequisites
- Android Studio (latest version recommended)
- Java Development Kit (JDK) 11 or higher
- Android SDK with API level 26 or higher
- Gradle build system

## Installation Steps

### For Mac Users
1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd movierecommender
   ```

2. Open Android Studio:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. Wait for the project to sync and download dependencies

4. Connect an Android device or start an emulator

5. Run the application:
   - Click the "Run" button (green play icon) in the toolbar
   - Select your target device
   - Wait for the app to install and launch

### For Windows Users
1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd movierecommender
   ```

2. Open Android Studio:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. Wait for the project to sync and download dependencies

4. Connect an Android device or start an emulator

5. Run the application:
   - Click the "Run" button (green play icon) in the toolbar
   - Select your target device
   - Wait for the app to install and launch

## Troubleshooting
If you encounter any issues:
1. Clean and rebuild the project:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```
2. Invalidate caches in Android Studio:
   - Go to File → Invalidate Caches / Restart
   - Select "Invalidate and Restart"

## Stakeholders
- **Developers**: Android developers working on the application
- **Users**: Movie enthusiasts looking for personalized recommendations
- **Product Owners**: Stakeholders defining the features and requirements
- **Testers**: QA team members ensuring application quality

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details. 