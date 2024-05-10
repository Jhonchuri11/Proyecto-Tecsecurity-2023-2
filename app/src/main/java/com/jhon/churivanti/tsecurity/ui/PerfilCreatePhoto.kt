package com.jhon.churivanti.tsecurity.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import com.jhon.churivanti.tsecurity.databinding.ActivityPerfilCreatePhotoBinding

class PerfilCreatePhoto : BaseActivity<ActivityPerfilCreatePhotoBinding>(ActivityPerfilCreatePhotoBinding::inflate) {

    val REQUEST_CODE_TAKE_PHOTO = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Boton para tomar foto desde el dispositivo
        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        // Boton para seleccionar imagen del dispositivo
        binding.btnTakePhotoSelection.setOnClickListener {
            takephotoSelection()
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { taskPhotos ->
            taskPhotos.resolveActivity(packageManager)?.also {
                startActivityForResult(taskPhotos, REQUEST_CODE_TAKE_PHOTO )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            data?.extras?.let { bundle ->
                val imageBitmap = bundle.get("data") as Bitmap
                binding.shapeableImageView.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun takephotoSelection() {

    }

}