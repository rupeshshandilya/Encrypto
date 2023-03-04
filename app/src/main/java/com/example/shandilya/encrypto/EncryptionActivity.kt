package com.example.shandilya.encrypto

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class EncryptionActivity : AppCompatActivity() {

    private var key: EditText?=null
    private var encryptData: EditText?=null
    private  var image: TextView?=null
    private  var encryptBtn: Button?=null
    private var string: String?=""
    private var bitMap: Bitmap?=null
    private  var imageView: ImageView?=null
    private var builder: AlertDialog.Builder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encryption)

        key = findViewById<View>(R.id.key) as EditText
        encryptData = findViewById<View>(R.id.encryptData) as EditText
        image = findViewById<View>(R.id.image) as TextView
        encryptBtn = findViewById<View>(R.id.encryptBtn) as Button
        imageView = findViewById<View>(R.id.imageView) as ImageView
        builder = AlertDialog.Builder(this)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView: View = inflater.inflate(R.layout.progressbar, null)
        builder!!.setView(dialogView)
        builder!!.setTitle("Encrytping...")
        builder!!.setCancelable(false)

        val dialog: Dialog = builder!!.create()

        encryptData!!.setOnClickListener{
            show_input_dialog()
        }

        //Adding image in data
        image!!.setOnClickListener{
            //Gallery Opening
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type=("image/*")
            val mimeType = arrayOf("image/jpeg","image/jpg","image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, 1);
        }



    }

    private fun show_input_dialog(){
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle("Encrypt Data")
        dialog.setMessage("Enter data you want to encrypt")
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewInflated: View = inflater.inflate(R.layout.inputdialog, null)
        val input: EditText? = viewInflated.findViewById<View>(R.id.input) as? EditText

        input!!.setText(encryptData!!.text.toString())
        dialog.setView(viewInflated)

        dialog.setPositiveButton("Done", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.dismiss()
                encryptData!!.setText(input!!.text.toString())
            }
        })

        dialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                dialog!!.cancel()
            }
        })
            .create()
        dialog.show()
    }
}