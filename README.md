FocusFlow FocusFlow is a habit-tracking application designed to help users monitor and build habits efficiently. The app combines gamification, motivational quotes, self-reflection tools, and advanced security features to create an engaging, user-friendly experience.
User defined features I implemented a graph so the users can visually see their progress as stated in part 1. I implemented biometric authentication for the diary so users personal thoughts are secured. Implementation of a calendar that displays the users habit name, start date and end date as well as gives the user the option of adding their habits to their google calendar as stated in part 1.

Features

User Registration and Login Single Sign-On (SSO): Users can register and log in using Google Sign-In for secure and quick authentication. Biometric Authentication: For added security, FocusFlow supports fingerprint or facial recognition, allowing users to log in using biometric data.

Habit Tracking Users can create, edit, and delete habits, tracking progress over time. Each habit includes details such as the habit name, description, start and end dates, and duration. Users are notified upon completing a habit, earning badges as rewards, which are stored and tracked in Firebase.

Diary Feature The self-reflection diary allows users to document progress and keep notes on their habits, supporting personal growth and habit maintenance. All diary entries are saved to Firebase, and users can view past entries to reflect on their journey.

Motivational Quotes On the splash screen, FocusFlow pulls motivational quotes from a RESTful API, offering users daily inspiration. The quotes API can be self-hosted or configured to use a public API like ZenQuotes (https://zenquotes.io/keywords/success).

Settings and Personalization Light and Dark Mode: Users can toggle between light and dark themes based on preference, with settings saved across sessions. Multi-language Support: The app supports multiple South African languages, including English and Afrikaans, ensuring accessibility for diverse users.

Gamification Achievement System: Users earn badges for consistent habit tracking, making the experience more engaging and rewarding. Leveling System: Users level up as they track habits, unlocking new achievements to boost motivation.

Offline Mode with Sync Offline Data Storage: Users can interact with specific features (like loggin in a user offline) offline. When connectivity is restored, changes are automatically synchronized with Firebase. RoomDB is used for local data storage during offline use, ensuring that data is stored safely and updated once online.

Real-time Notifications Push Notifications: Users receive real-time notifications and alerts send reminders. Notifications are managed through Firebase Cloud Messaging.

User-Friendly Interface FocusFlow’s clean, minimalist design makes navigation intuitive, and error handling ensures the app is robust and user-friendly. All inputs are validated to prevent crashes, with helpful feedback messages to guide users.

App Icon and Image Assets FocusFlow includes a custom app icon and optimized image assets for a polished look and consistent brand identity.

How does FocusFlow Work?

Registration & Login: Users register and log in using Google Sign-In, with Firebase Authentication providing secure, seamless access. Users can also login offline after they successfully register and login online. In the event the user forgets their password they can click the forgot password and enter their email in to get a link to change their password.

After logging in, users can create a habit by either clicking the floating action button or accessing it by using the side menu. Entering details like name, description, start and end dates, and duration. Habit data is saved in Firebase Firestore, allowing retrieval on any logged-in device.

Track Progress: Users can view and manage their habits on the home screen, they can also save their times they spent working on a habit and it will display on a graph and they can save their progress. They can also mark their habits as completed by clicking the tick or they can delete the habit by clicking the cross. Achievement badges are awarded as users complete habits, fostering motivation.

Diary Entries: Users record daily reflections in a diary to track their journey, with entries saved to Firebase which is locked by biometric authentication.

Calendar: Users can view their habit names as well as their start date and end date of the habits on the calendar.

Motivational Quotes: Each time the app launches, a motivational quote is fetched from a REST API and displayed on the splash screen, providing fresh inspiration daily.

Offline Mode: When offline, users can still login via RoomDB. Data syncs automatically with Firebase once the device reconnects.

FAQ (Frequently Asked Questions)

General Questions

What is FocusFlow? FocusFlow is a habit-tracking app with features for habit management, motivational quotes, a diary, and gamification to promote consistency and user engagement.

Is FocusFlow free to use? Yes, FocusFlow is free. However, future updates might include optional premium features.

Can I use FocusFlow offline? Yes, FocusFlow supports offline functionality. Data is stored locally via RoomDB and syncs automatically when online.

Registration & Login 4. How do I register for FocusFlow? You can register using Google Sign-In. Once registered, login information syncs across devices connected to your Google account.

How do I reset my password? If you forget your password, select “Forgot Password” on the login screen. Enter your registered email, and a reset link will be sent to your inbox.

Can I use biometric authentication? Yes, biometric authentication is supported for diary entries. Enable it in settings after your initial login.

Habit Tracking & Diary 7. How do I create a new habit? Tap the floating action button or select "Create Habit" from the side menu, fill in details like habit name, description, duration, and start/end dates, then save.

Can I delete or edit a habit? Yes, habits can be edited or deleted by accessing the habit details from the home screen.

How is habit progress displayed? Habit progress is shown in a graph, with completed times visually tracked. You can also mark habits as complete to gain achievement badges.

How secure is my diary? Your diary entries are securely stored in Firebase, and biometric authentication provides additional protection.

Calendar & Notifications

How do I view my habits in a calendar? Navigate to the calendar screen to view your habits with start and end dates. You can also add reminders to your Google Calendar.

Can I receive habit reminders? Yes, FocusFlow uses Firebase Cloud Messaging to send reminders and notifications based on your settings.

Settings & Customization

Does FocusFlow support multiple languages? Yes, you can choose from English and Afrikaans. Go to Settings > Language to select your preferred language.

Can I switch to Dark Mode? Yes, FocusFlow offers Light and Dark themes. Select your preference in Settings.

How do I enable motivational quotes? Quotes automatically appear on the splash screen, fetching new ones each day from an external API.

Troubleshooting

I’m not receiving notifications. What should I do? Ensure notifications are enabled in your device settings. You may also need to check permissions for Firebase Cloud Messaging.

My progress isn’t syncing. If FocusFlow cannot sync, it may be due to connectivity issues. Once you reconnect to the internet, all data should automatically sync with Firebase.

Can I reset my achievements? Currently, achievements cannot be reset. They reflect your progress and consistency.

Can i run this app on a apple device? Currently you can not this app has been made for android devices.

Technologies Used Firebase Authentication: For user registration, login, and secure handling of credentials.

Firebase Firestore: For saving user habits, diary entries, and user-specific data.

Google Sign-In: For SSO functionality.

REST API: Used to pull motivational quotes displayed on the splash screen.

RoomDB: For offline storage and synchronization.

Firebase Cloud Messaging: For real-time notifications.

Light/Dark Mode & Multi-language Support: Customizable in the settings for user preferences.

Kotlin: The primary language used for Android development.

Android SDK: For building the native mobile app.

Instructions to Run the App Clone the Repository:

git clone https:https://github.com/VCWVL/opsc7312-poe-RohanM007

Open the Project in Android Studio

Connect Firebase:

Set up Firebase Authentication, Firestore, and Firebase Cloud Messaging by linking the app to your Firebase project. Add Google Sign-In Credentials:

Follow the instructions to configure Google Sign-In in the Firebase console and add your google-services.json file to the app’s app directory. Set Up the REST API:

Host a quotes API for motivational quotes, or configure the app to use an existing API, like ZenQuotes (https://zenquotes.io/keywords/success). Run the App on Your Device or Emulator

Video Demonstration For a demonstration of the app’s key features, please view the video linked below. https://youtu.be/CMDoQcXRz3g

In this video, the following actions are shown:

Register and log in using Google Sign-In and biometric authentication. Create, edit, and delete habits and view progress tracking. Use the diary feature to record progress. Switch between light and dark mode. View motivational quotes on the splash screen. Earn badges by completing habits. Receive real-time notifications and alerts. Language option of 3 South African Languages

Demo Video https://youtu.be/CMDoQcXRz3g

Conclusion FocusFlow is designed to make habit tracking engaging, convenient, and secure. By combining gamification, motivational tools, and self-reflection, FocusFlow enhances users' commitment to building lasting habits.

References

Name:Working with Charts in Kotlin Channel name: Android Geek URL: https://www.youtube.com/watch?v=4ou5yRJtuKU

Name:Android Tutorial - How to create firebase push notification in Android Studio Channel: CodingKarr URL: https://www.youtube.com/watch?v=miH60EYs5Fw

Name:NEW Per App Language Preferences in Android 13+ (Backwards Compatible) - Android Studio Tutorial Channel:Stevdza-San URL: https://www.youtube.com/watch?v=OM821CgOr0g

Name: ROOM Database - Kotlin Channel:Stevdza-San URL: https://www.youtube.com/watch?v=lwAvI3WDXBY&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o

Name:How to Implement Biometric Auth in Your Android App Channel: Philipp Lackner URL: https://www.youtube.com/watch?v=_dCRQ9wta-I&t=98s

Name: FRAGMENTS - Android Fundamentals Channel: Philipp Lackner URL: https://www.youtube.com/watch?v=-vAI7RSPxOA

Name: Calendar View | Android Studio | Kotlin 2024 Channel: Deccon Tech URL: https://www.youtube.com/watch?v=uqFjKhq32QI
