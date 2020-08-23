package com.example.chatapplication.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatapplication.R
import com.example.chatapplication.databinding.ActivityUpdateProfileBinding
import com.example.chatapplication.viewmodels.LoginViewModel
import com.example.chatapplication.viewmodels.UpdateUserProfileViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.tvUserStatus
import kotlinx.android.synthetic.main.activity_update_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    lateinit var viewModel: UpdateUserProfileViewModel
    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_GALLERY_PHOTO = 2
    var tempCurrentPhotoPath: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUpdateProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)

        viewModel = ViewModelProviders.of(this, LoginViewModel.Factory()).get(UpdateUserProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()
        initObservables()
    }

    private fun initObservables() {
        viewModel.showStatus.observe(this, Observer<String> { loginStatus ->
            if(!loginStatus.isNullOrEmpty()) {
                Toast.makeText(this@UpdateProfileActivity, loginStatus, Toast.LENGTH_LONG).show()
                tvUserStatus.text = loginStatus
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showImagePicker(view: View) {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@UpdateProfileActivity)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    if (checkPermission(REQUEST_TAKE_PHOTO)) {
                        dispatchTakePictureIntent()
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                        //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showSnackbarMessage(REQUEST_TAKE_PHOTO)
                    } else {
                        requestCameraPermission()
                    }
                }
                items[item] == "Choose from Gallery" -> {
                    if (checkPermission(REQUEST_GALLERY_PHOTO)) {
                        dispatchGalleryIntent()
                    } else if (
                        //ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showSnackbarMessage(REQUEST_GALLERY_PHOTO)
                    } else {
                        requestGalleryPermission()
                    }
                }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(this, addGalleryPermission(), REQUEST_GALLERY_PHOTO)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, addCameraPermission(), REQUEST_TAKE_PHOTO)
    }

    /**
     * All about permission
     */
    private fun checkPermission(permission:Int): Boolean {
        val result1: Int
        val result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if(permission==REQUEST_TAKE_PHOTO) {
            result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            return result1 == PackageManager.PERMISSION_GRANTED /*&& result2 == PackageManager.PERMISSION_GRANTED*/ && result3 == PackageManager.PERMISSION_GRANTED
        } else {
            return /*result2 == PackageManager.PERMISSION_GRANTED && */result3 == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            val permission1 = grantResults[0] == PackageManager.PERMISSION_GRANTED
            //val permission2 = grantResults[1] == PackageManager.PERMISSION_GRANTED
            if (requestCode == REQUEST_TAKE_PHOTO) {
                val permission3 = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permission1 /*&& permission2*/ && permission3) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    dispatchTakePictureIntent()
                } else if (Build.VERSION.SDK_INT >= 23
                    && (!shouldShowRequestPermissionRationale(permissions[0])
                            || !shouldShowRequestPermissionRationale(permissions[1])
                            /*|| !shouldShowRequestPermissionRationale(permissions[2])*/)) {
                    Toast.makeText(this@UpdateProfileActivity, "Go to Settings and Grant the permission to use this feature.",
                        Toast.LENGTH_SHORT).show()
                    // User selected the Never Ask Again Option
                    navigateToSettings()
                }
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                if (permission1 /*&& permission2*/) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    dispatchGalleryIntent()
                } else if (Build.VERSION.SDK_INT >= 23
                    && (!shouldShowRequestPermissionRationale(permissions[0])
                            /*|| !shouldShowRequestPermissionRationale(permissions[1])*/)) {
                    Toast.makeText(this@UpdateProfileActivity, "Go to Settings and Grant the permission to use this feature.",
                        Toast.LENGTH_SHORT).show()
                    // User selected the Never Ask Again Option
                    navigateToSettings()
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showSnackbarMessage(permissionType: Int) {
        Snackbar.make(findViewById(android.R.id.content), "Please Grant Permissions to access your location",
            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE") {
            if(permissionType==REQUEST_TAKE_PHOTO) {
                requestCameraPermission()
            } else {
                requestGalleryPermission()
            }
        }.show()
    }

    private fun addCameraPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.CAMERA,
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun addGalleryPermission(): Array<String> {
        return arrayOf(
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun navigateToSettings() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.android.provider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun dispatchGalleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            tempCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //val imageBitmap = data?.extras?.get("data") as Bitmap
            //ivProfilePicture.setImageBitmap(imageBitmap)
            viewModel.currentPhotoPath = tempCurrentPhotoPath
        } else if (requestCode == REQUEST_GALLERY_PHOTO && data!=null) {
            val selectedImage = data.data
            try {
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c: Cursor? = contentResolver.query(selectedImage!!, filePath, null, null, null)
                c?.moveToFirst()
                val columnIndex: Int? = c?.getColumnIndex(filePath[0])
                viewModel.currentPhotoPath = columnIndex?.let { c.getString(it) }.toString()
                c?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if(viewModel.currentPhotoPath.isNotEmpty()) {
            Glide.with(this@UpdateProfileActivity)
                .load(viewModel.currentPhotoPath)
                .apply(
                    RequestOptions().centerCrop()
                        .circleCrop()
                        .placeholder(R.mipmap.ic_launcher)
                )
                .into(ivProfilePicture)
        }
    }

    /*The following example method demonstrates how to invoke the system's media scanner
       to add your photo to the Media Provider's database,
       making it available in the Android Gallery application and to other apps.*/
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(viewModel.currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = ivProfilePicture.width
        val targetH: Int = ivProfilePicture.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(viewModel.currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = 1.coerceAtLeast((photoW / targetW).coerceAtMost(photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(viewModel.currentPhotoPath, bmOptions)?.also { bitmap ->
            ivProfilePicture.setImageBitmap(bitmap)
        }
    }
}