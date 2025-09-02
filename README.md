# PhotoFlow
![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blue) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Ready-brightgreen) ![License](https://img.shields.io/badge/License-MIT-green) ![JitPack](https://img.shields.io/badge/JitPack-v1.0.0-orange)



A lightweight, Jetpack Compose-based camera and gallery module for Android apps.
Easily integrate camera and image picking functionality into your Compose projects.

## Features

- Jetpack Compose ready ✅

- Camera preview using CameraX

- Capture photos and pick images from the gallery

- Customizable UI icons and sizes

- Safe to integrate with any Android project

- Easily publishable via JitPack



## Installation
- Step 1: Add JitPack repository

Add the following to your root settings.gradle file:

```bash

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```



- Step 2: Add the dependency

Replace v1.0.0 with the latest tag:
```bash
dependencies {
    implementation 'com.github.imgurujeet:photoflow:v1.0.0'
}
```



## Usage Example
```bash 
import com.imgurujeet.photoflow.PhotoFlow
import android.net.Uri
import androidx.compose.runtime.*

@Composable
fun PhotoFlowDemo() {
    var showCamera by remember { mutableStateOf(true) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    if (showCamera) {
        PhotoFlow(
            onImageSelected = { uri ->
                selectedImage = uri
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    }
}
```


### You can customize the icons and their sizes:
```bash
PhotoFlow(
    onImageSelected = { uri -> /* handle image */ },
    onClose = { /* handle close */ },
    cameraIcon = painterResource(R.drawable.pf_ic_camera),
    galleryIcon = painterResource(R.drawable.pf_ic_gallery),
    closeIcon = painterResource(R.drawable.pf_ic_close),
    cameraIconSize = 70.dp
)
```

### XML Permissions

#### Make sure to add these permissions in your AndroidManifest.xml:
```bash 
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

## License

This project is licensed under the MIT License – see the LICENSE
 file for details.

#### Support & Contribution

If you find bugs, need help, or want to contribute, feel free to open issues or pull requests on GitHub.
