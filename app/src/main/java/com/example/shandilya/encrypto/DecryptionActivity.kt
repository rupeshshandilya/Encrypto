package com.example.shandilya.encrypto

import android.app.Activity
import android.app.Dialog
import android.app.Notification.Action
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream

class DecryptionActivity : AppCompatActivity() {
    private var key: EditText? = null
    private var decryptImage: TextView? = null
    private var decodeText: TextView?= null
    private var decryptButton: Button? = null
    private var bitMap: Bitmap? = null

    //For Dialog Box
    private var builder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decryption)
        key = findViewById<View>(R.id.key) as EditText
        decryptImage = findViewById<View>(R.id.image) as TextView
        decryptButton = findViewById<View>(R.id.decryptBtn) as Button
        decodeText = findViewById<View>(R.id.decodedText) as TextView
        builder = AlertDialog.Builder(this)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.progressbar, null)
        builder!!.setView(dialogView)
        builder!!.setTitle("Decrypting")
        builder!!.setCancelable(false)
        val dialog:Dialog = builder!!.create()

        decodeText!!.setOnClickListener {
            val clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text",decodeText!!.text.toString())
            clipManager.setPrimaryClip(clipData)
            Toast.makeText(this,"text copied",Toast.LENGTH_SHORT).show()
        }

        decryptImage!!.setOnClickListener {
            //For Opening Gallery
            try{
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type=("image/*")
                val mediaType = arrayOf("image/jpg","image/png","image/jpeg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mediaType)
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(intent,1)

            }
            catch (e: Exception){
                Toast.makeText(this,"Failed to Open Gallery😔",Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            data?.data.let { uri ->
                try{
                   bitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                   val outputStream = ByteArrayOutputStream()
                    bitMap!!.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                    Toast.makeText(this,"Image added successfully😀",Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){
                    Toast.makeText(this,"Failed to Open Gallery😔",Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }
}