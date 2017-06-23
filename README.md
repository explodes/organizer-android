Organizer
=========
This app lets you perform the simple task of creating notes and optionally attach images to them.

#### Implementation features
 - Uses the new Room ORM
 - Written in Kotlin
 - Unit testing (w/ Robolectric)
 - Ui testing (w/ Espresso)
 - Android architecture components such as `ViewModel`
 - Event tracking and error reporting with Crashlytics
 - Also uses RxJava, Dagger, Picasso
 
#### Running tests
When running UI tests with Robolectric, it is sometimes necessary to run them multiple times to work around the `Stub!!!` RuntimeException.

 - _It is known that Roboelectric's automatic closing of `SQLiteDatabase` interferes with this app's setup (Room, injected in-memory instances for tests) so pardon our dust._

There is a shared run configuration in the AS project that will run tests.

### Overview
Create an app to help organize the user's stuff. The app will include the ability to create multiple categories, and add items to those categories. Items will include information about the item and an optional photo.

### Requirements 
 - The app should persist data between sessions
 - The user should be able to
   - create new categories to organize their stuff
   - view a list of categories and select one to see the items assigned to it. This list of items should display item photos for all items that include a photo.
   - select an item in a category to see the details for that item
   - delete an item
 - Items should include
   - a name
   - a description
   - an optional image
 - Add the ability to rate items 1 to 5 stars. Include the rating on the item list.
 - Add the ability to delete categories, which should also delete all items in the category.