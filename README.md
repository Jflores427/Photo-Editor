# PhotoEditor

## Project Overview
Developed a Photo Editor with Java as a final project for my Software Design course. I have decided to also share a PowerPoint containing a demo and further details of the application.

## Illustrations
Review the Android Studio Photo Editor Experience- Josue Flores.pptx file.

## Scope of Functionality
As aforementioned within the Project Overview, this project is a photo editor that allows the user to edit any image file within their gallery or any photo they take within the application. Once you open the application, you are prompted into your gallery to select an image to edit. Once selected, you are provided an interface that allows you to apply filters, effects,
edits(cropping/rotating) the image, take a new photo with the built-in camera (the camera icon), select a different image (plus Note icon), and/or save the newly edited image (download icon).

Upon clicking the Edits card, you are given the opportunity to crop the image to various aspect ratio such as (3:4, 2:3, 16:9, 9:16), rotate the photo up to 360 degrees clockwise and counter-clockwise, and scale the size of the image based on a sliding percentage.

Upon clicking the Effects card, you can alter the brightness and saturation of the photo with the built-in value sliders.

Upon clicking the Filters card, you can select from an assortment of filters to apply to your image such as Clarendon, Mars, Amazon, Starlit, and much more.

## Known Bugs
  1) When changing the brightness and saturation of an image, if you double click on the slider after moving, it changes the        brightness/saturation once more, despite no movement of the slider value.
  2) Trying to alter an empty image (This only occurs when the gallery is intially empty).
  3) Editing an Image removes all filters/effects currently placed on the image, if they have not been saved.

## Prerequisities
Knowledge of Java Object Oriented Principles and Android Development Concepts.
An IDE for Android App Development.

Dependencies:
  ```
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.2.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    implementation 'info.androidhive:imagefilters:1.0.7'
    implementation 'com.karumi:dexter:4.1.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'ja.burhanrashid52:photoeditor:0.2.1'
    implementation 'com.github.yalantis:ucrop:2.2.6-native'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
  ```

## Installation
  1) Download/Install Android Studio (or someother IDE for Android Development)
  2) Clone this Git Repository or download the Zip File.
  3) Open Android Studio
  4) Open this Project by finding it within your downloads directory, if it was downloaded.
  5) Create an Android Virtual Device (AVD), if you do not already have one. 
   5a) If you do not have one already, visit the Device Manager from the following tabs Tools > Device Manager
   5b) Select 'Create device'
   5c) Configure your AVD.
  6) Run the application and start Photo-Editing! :D
 
## Technologies Used
   1) Java
   2) Android Studio
