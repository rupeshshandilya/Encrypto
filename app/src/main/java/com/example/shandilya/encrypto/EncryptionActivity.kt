package com.example.shandilya.encrypto

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Video.Media
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import javax.crypto.SecretKey
import java.security.Key
import android.util.Base64
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.math.round

class EncryptionActivity : AppCompatActivity() {

    private var key: EditText?=null
    private var encryptData: EditText?=null
    private  var image: TextView?=null
    private  var encryptBtn: Button?=null
    private var string: String?=""
    //for storing graphics
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
            inputDialog()
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


        //Encrypting data into image
        encryptBtn!!.setOnClickListener {
            if(key!!.text.toString().length == 16){
                if(encryptData!!.text.toString().length > 0){
                    if(bitMap != null){
                        Thread{
                            runOnUiThread{dialog.show()}
                            //Encrypt data using AES
                            val data: String = encryptData()

                            val map: Bitmap = hideData(data, bitMap!!)
                            //reset encrypted data
                            string=""
                            //save encrypted image to device
                            saveMediaFile(map)

                        }
                    }
                }
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            data?.data?.let { uri ->
                try{
                    //bitMap Creation
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        uri,
                        filePathColumn, null, null,null
                    )
                    cursor!!.moveToFirst()
                    val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                    val picturePath: String = cursor.getString(columnIndex)
                    cursor.close()

                    val options = BitmapFactory.Options().apply{
                        inJustDecodeBounds = true
                    }

                    BitmapFactory.decodeFile(picturePath,options)
                    //resize image
                    options.inSampleSize = calculateInSampleSize(options, 400, 400)
                    options.inJustDecodeBounds= false
                    bitMap = BitmapFactory.decodeFile(picturePath,options)

                    imageView!!.setImageBitmap(bitMap)
                    imageView!!.visibility = View.VISIBLE
                    image!!.setText("Change Image!!")
                }
                catch (e: Exception){
                    Toast.makeText(this,"image is not selected try again please!",Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private  fun saveMediaFile(bitMap : Bitmap){
        try{
            val fileName = "${System.currentTimeMillis()}.png"
            var fos: OutputStream? = null

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                contentResolver?.also { resolver ->
                    val contentValue = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri?= resolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValue)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                    runOnUiThread{
                        Toast.makeText(this,"Image Saved to External Storage!",Toast.LENGTH_SHORT).show()
                    }
                }
            } else{
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imageDir, fileName)
                fos = FileOutputStream(image)
                runOnUiThread{
                    Toast.makeText(this,"Image saved in $imageDir",Toast.LENGTH_SHORT).show()
                }
            }
            fos?.use {
                bitMap.compress(Bitmap.CompressFormat.PNG,100,it)
            }
        } catch (e: Exception){
            e.printStackTrace()
            runOnUiThread{
                Toast.makeText(this,"Error Image not Saved!!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideData(data: String, bitMap: Bitmap):Bitmap{
        val string = "0001011100011110"
        val startingString = "011010010110111001100110011010010110111001101001"

        val encodeString = data + string + startingString

        val width = bitMap.getWidth()
        val height = bitMap.getHeight()


        val array = IntArray(width * height)
        bitMap.getPixels(array, 0, width, 0, 0, width, height)
        Log.e("width",width.toString())
        Log.e("height", height.toString())

        var count = 0

        //Modifying pixel data by encoded string
        for(x in 0 until height){
            if(count > encodeString!!.length-1){
                break
            }
            else{
                for(y in 0 until width){
                    if(count > encodeString!!.length-1){
                        break
                    }
                    else{
                        val index: Int = x * width + y

                        //bitwise shifting
                        var R: Int = array.get(index) shr 16 and 0xff
                        var G: Int = array.get(index) shr 8 and 0xff
                        var B: Int = array.get(index) and 0xff

                        R = encode(R,count,encodeString)
                        count++

                        if(count < encodeString!!.length){
                            G = encode(G,count,encodeString)
                            count++
                        }
                        if(count < encodeString!!.length){
                            B = encode(B,count,encodeString)
                            count++
                        }

                        //storing modified rgb value
                        array[index] = -0x1000000 or (R shl 16) or (G shl 8) or B
                    }
                }
            }
        }

        val newBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        //creating bitmap of modified pixel
        newBitmap.setPixels(array,0, width, 0, 0, width, height)
        return newBitmap
    }


    //encoding into RGB
    private fun encode(color: Int, count: Int, encodeString: String): Int{
        var binary = Integer.toBinaryString(color)
        if(binary.length < 8){
            for(x in 1 .. (8-binary.length)){
                binary = "0" + binary
            }
        }

        binary = binary.slice(0 .. (binary.length - 2)) + encodeString!![count]

        return Integer.parseInt(binary, 2)
    }

    private fun encryptData(): String{
        val key = key!!.text.toString()
        val data = encryptData!!.text.toString()

        val seckey: Key = SecretKeySpec(key.toByteArray(), "AES")

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, seckey)

        val encrypt = cipher.doFinal(data.toByteArray())


        //Converting Encrypted data to BASE_64
        val encrypt_64 = android.util.Base64.encodeToString(encrypt, Base64.NO_WRAP or Base64.NO_PADDING)


        for(index in encrypt_64){
            var binaryString = Integer.toBinaryString((index.toInt()))
            if(binaryString.length < 8){
                for(index2 in 1..(8-binaryString.length)){
                    binaryString = "0" + binaryString
                }
            }

            string = binaryString + string
        }
        return string.toString()
    }

    private fun inputDialog(){
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