package com.example.shandilya.encrypto


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class DecryptionActivity : AppCompatActivity() {
    private var key: EditText? = null
    private var decryptImage: TextView? = null
    private var decodeText: TextView?= null
    private var decryptButton: Button? = null
    private var bitMap: Bitmap? = null
    private var decodeString: String = ""
    private var finalString: String?= ""


    //To Check Whether image is valid or not
    private val validImage = "011010010110111001100110011010010110111001101001"


    //For Dialog Box
    private var builder: AlertDialog.Builder? = null

    //to check whether data is present in image
    private var dataPresent: Int = 1

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
            showToast("Text Copied", Toast.LENGTH_SHORT)
        }

        decryptButton!!.setOnClickListener {
            if(key!!.text.toString().length == 16){
                if(bitMap != null){
                    Thread{
                        this.runOnUiThread {
                            dialog.show()
                        }
                            extractImage(bitMap!!)
                        if(dataPresent == 1) {
                            //converting decode string to base64 for decrypting
                            val baseString = converToBase64(decodeString)

                            //decrypting final String
                            finalString = decodestr(baseString)

                            this.runOnUiThread {
                                dialog.dismiss()
                                finalString?.let { it }

                                decodeText!!.text = finalString
                                decodeText!!.visibility = View.VISIBLE

                                decodeString = ""
                                finalString = ""
                                dataPresent = 1
                            }
                        }
                            else{
                                runOnUiThread {
                                    dialog.dismiss()
                                    showToast("No data present in image", Toast.LENGTH_SHORT)
                                }
                            }
                    }.start()
                }
                else{
                    showToast("Image is not found",Toast.LENGTH_SHORT)
                }
            }
            else{
                val keySize = key!!.text.toString().length
                showToast("Key size must be $keySize",Toast.LENGTH_SHORT)
            }
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
                e.printStackTrace()
            }
        }
    }

    private fun decodestr(baseString: String):String?{
        val decryptKey: Key = SecretKeySpec(key!!.text.toString().toByteArray(),"AES")
        val cipher: Cipher =  Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE,decryptKey)
        var decrypt:ByteArray?=null
        try {
            decrypt = cipher.doFinal(Base64.decode(baseString,Base64.NO_WRAP or Base64.NO_PADDING))
        }
        catch (e:Exception){
            runOnUiThread{
                showToast("Entered Wrong Security Key",Toast.LENGTH_SHORT)
            }
        }
        val string: String? = decrypt?.let { String(it) }
        return  string
    }

    private fun converToBase64(decodeString: String): String{
        var base64Str: String = ""
        for(i in 0 .. decodeString.length-8 step 8){
            val toInt = Integer.parseInt(decodeString.slice(i..i+7),2)
            val tochar = toInt.toChar()
            base64Str+=tochar
        }
        return base64Str
    }

    private fun extractImage(bitMap: Bitmap){
        val width: Int = bitMap.getWidth()
        val height: Int = bitMap.getHeight()
        var array = IntArray(width*height)
        bitMap.getPixels(array,0,width,0,0,width,height)

        Log.i("width",width.toString())
        Log.i("height",height.toString())

        var count = 0
        var check = 1

        for(x in 0 until height){
            if((check == 1) && (dataPresent == 1)){
                for(y in 0 until width){
                    val index: Int = x * width + y
                    //bitwise shifting
                    val R: Int = array.get(index) shr 16 and 0xff
                    val G: Int = array.get(index) shr 8 and 0xff
                    val B:Int = array.get(index) and 0xff

                    if(terminating(count) or (dataPresent == 0)){
                        check = 0
                        break
                    }
                    else{
                        terminatingDecode(R)
                        count++
                    }

                    if(terminating(count) or (dataPresent == 0)){
                        check = 0
                        break
                    }
                    else{
                        terminatingDecode(G)
                        count++
                    }

                    if(terminating(count) or (dataPresent == 0)){
                        check = 0
                        break
                    }
                    else{
                        terminatingDecode(B)
                        count++
                    }

                    //To Restore Values after RGB modification
                    array[index] = -0x1000000 or (R shl 16) or (G shl 8) or B

                }
            }
            else{
                break
            }
        }
    }

    //For Decoding RGB Pixel Value and Least Significant Bit
    private fun terminatingDecode(color: Int){
        val binary_To_String = Integer.toBinaryString(color)
        decodeString = decodeString + binary_To_String[binary_To_String.length-1]

        if(decodeString!!.length==48){
            if(decodeString!=validImage){
                //Data is not present
                dataPresent = 0
            }
        }
    }

    //Checking Terminating Symbol
    private fun terminating(count: Int):Boolean{
        var output = false
        if(decodeString.length>=16){
            val terminate1 = decodeString.slice(decodeString.length - 16 .. decodeString.length-9)
            val terminateInt = Integer.parseInt(terminate1,2)
            val terminat2  = decodeString.slice(decodeString.length - 8 .. decodeString.length-1)
            val terminate2Int = Integer.parseInt(terminat2,2)
            if((terminateInt == 23) and (terminate2Int == 30)){
                decodeString = decodeString.slice(48..decodeString.length-16)
                output = true
            }
        }
        return output;
    }

    //For Generating Toast Message
    fun Context.showToast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,message,duration).show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            data?.data?.let { uri ->
                try{
                   bitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                   val outputStream = ByteArrayOutputStream()
                    bitMap!!.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                    showToast("Image added successfully ",Toast.LENGTH_SHORT)
                }
                catch (e: Exception){
                    showToast("Failed to Open Gallery😞",Toast.LENGTH_SHORT)
                    e.printStackTrace()
                }
            }
        }
    }
}