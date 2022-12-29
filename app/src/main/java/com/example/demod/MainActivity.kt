package com.example.demod

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.demod.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private var imageUri: Uri? = null

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardPerformance.setOnClickListener {
            goToCamera()
        }
    }


    private fun permissionsAlertDialog(): AlertDialog {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_permission)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }, REQUEST_PERMISSION)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                permissionsAlertDialog().cancel()
            }
            .show()
        return alertDialog
    }


    private fun goToCamera() {
        val cameraPermissionState =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermissionState == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(intent)
            val file =
                File(this.externalCacheDir, "Image_${System.currentTimeMillis()}.png")
        } else {
            permissionsAlertDialog().show()
        }
    }

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                if (imageUri != null) {

                }
            }
        }

    companion object {
        private const val REQUEST_PERMISSION = 2022
    }


}