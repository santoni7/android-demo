# Android Showcase Application
#### Author: *Anton Sakhniuk* 

#### anton.sakhniuk@gmail.com
---
## Task
-  An application, which parses .json file with list of people, and displays it in MainActivity. 
- When a user clicks on item in `MainActivity`, `DetailsActivity` is opened, showing more detail information on a selected person (`age` only).
- Every person has a link to image file, which has to be downloaded and displayed on both screens. 
- Image URL may point to Wikipedia File Page (eg. `https://en.wikipedia.org/wiki/File:Jon_Snow-Kit_Harington.jpg`), so it should be checked, and, if needed, send a request to Wikipedia API in order to retrieve real image file URL
## 3rd Party Libraries
- *RxJava2*  - to implement Reactive Programming Paradigm
- *Dagger2*  - to implement Dependency Injection Pattern

I decided that those two, and Android SDK would be quite enough for required tasks, so no need to increase APK size by adding *Gson, Retrofit, Picasso etc*.

## Features
### MainActivity:
  - Contains `RecyclerView` (a list) with Cards, that display person name and image:
  - This list can be swiped to refresh, or by choosing "Refresh" option in Toolbar menu
  - A click on card would open DetailsActivity with selected person info
  - Both `MainActivity` & `DetailsActivity` are designed to follow MVP-pattern
### DetailsActivity:
  - Simply shows person avatar image, full name, and age
  - Swipe left to go back
### Data processing:
  - At first, data is parsed from `data.json` file (located at assets folder) by `PersonDataSource` class, for the request of MainPresenter
  - A sequence of people is represented by `Observable<Person>` everywhere possible, so that we don't have to wait until all data is processed to display already existing
  - Then, a sequence, returned by `PersonDataSource` is passed by `MainPresenter` to *`ImageRepository`*, and at the same time to `RecyclerView` in order to display cards as-is (without images)
  - `ImageRepository` is a class, responsible for populating every Person object, passed to it through `Observable<Person>` with images from two `ImageDataSources`: `LocalImageDataSource` and `RemoteImageDataSource`. It has two strategies: 
    - `LocalFirst` - an image is looked for in application's internal storage, and only those not found are downloaded from web
  
    - `RemoteFirst` - all images are downloaded from web, those not found are tried to load from storage
  
  - Both strategies save all web downloaded images to internal storage. LocalFirst strategy is used by default on application startup. RemoteFirst strategy is used on user-requested refresh.
  -  As a result, every Person, passed to `ImageRepository`, has it's field `Observable<Bitmap> imageSource` set by repo, and this source is immediately subscribed by `RecyclerView's` adapter so that when image is loaded, it shows up in person card view, independently from other people.
  - Also, `ImageRepository` stores all received populated `Person` objects (by holding `ReplaySubject<Person>` observables), and provides method `findPersonById(id)`, which is used in DetailsActivity
### UI:
  - Items in `RecyclerView` appear one-by-one with neat animation
  - When user rotates screen, `RecyclerView` rearranges it's layout from 1-column to 2-column, and vice versa
  - `ProgressBar` is displayed on image's place until image is loaded
