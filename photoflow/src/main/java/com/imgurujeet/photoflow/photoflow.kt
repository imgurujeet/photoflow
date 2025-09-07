

/**
 * PhotoFlow Library
 * Author: Gurujeet (imgurujeet)
 * GitHub: https://github.com/imgurujeet
 * Version: 1.0.0
 *
 * A simple Jetpack Compose camera and gallery picker module.
 *
 * Resources are prefixed with 'pf_' to avoid conflicts.
 */

package com.imgurujeet.photoflow

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun PhotoFlow(
    onImageSelected: (Uri) -> Unit,
    onClose: () -> Unit,
    cameraIcon: Painter? = null,
    galleryIcon: Painter? = null,
    closeIcon: Painter? = null,
    cameraIconSize: Dp = 60.dp,
    galleryIconSize: Dp = 40.dp,
    closeIconSize: Dp = 40.dp
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val imageCapture = remember { ImageCapture.Builder().build() }

    // Permission launcher
    val cameraPermission = Manifest.permission.CAMERA
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            onClose()
        }
    }

    LaunchedEffect(Unit) { launcher.launch(cameraPermission) }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onImageSelected(it) } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Camera preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(ctx, "Camera failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // Controls overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gallery button
            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(
                    painter = galleryIcon ?: painterResource(R.drawable.pf_ic_gallery),
                    contentDescription = "Gallery",
                    tint = Color.White,
                    modifier = Modifier.size(galleryIconSize)
                )
            }

            // Capture button
            IconButton(onClick = {
                val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onImageSelected(Uri.fromFile(photoFile))
                        }
                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(
                                context,
                                "Capture failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }) {
                Icon(
                    painter = cameraIcon ?: painterResource(R.drawable.pf_ic_camera),
                    contentDescription = "Capture",
                    tint = Color.White,
                    modifier = Modifier.size(cameraIconSize)
                )
            }

            // Close button
            IconButton(onClick = { onClose() }) {
                Icon(
                    painter = closeIcon ?: painterResource(R.drawable.pf_ic_close),
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(closeIconSize)
                )
            }
        }
    }
}
