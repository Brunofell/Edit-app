package com.example.edit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.os.Environment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        imageView = findViewById(R.id.imageView)
        val imageUriString = intent.getStringExtra("imageUri")
        imageUri = Uri.parse(imageUriString)

        // Carregar a imagem na ImageView
        imageView.setImageURI(imageUri)

        // Configurar os botões
        setupButtons()


        // Aqui você pode implementar os métodos de processamento de imagem
        // Por exemplo, para converter a imagem para tons de cinza:
        // val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        // val grayBitmap = applyGrayScale(bitmap)
        // imageView.setImageBitmap(grayBitmap)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnNegative).setOnClickListener {
            applyNegative()
        }

        findViewById<Button>(R.id.btnSepia).setOnClickListener {
            applySepia()
        }

        findViewById<Button>(R.id.btnGrayScale).setOnClickListener {
            applyGrayScale()
        }
    }

    //FILTRO NEGATIVO

    // Aplicar o efeito negativo na imagem
    private fun applyNegative() {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        val negativeBitmap = applyNegativeEffect(bitmap)

        // Rotacionar a imagem para corrigir a orientação
        val rotatedBitmap = rotateBitmap(negativeBitmap, 90f) // Rotação de 90 graus, ajuste conforme necessário

        // Exibir a imagem processada na ImageView
        imageView.setImageBitmap(rotatedBitmap)

        // Salvar a imagem editada na galeria e obter a Uri da imagem salva
        imageUri = saveEditedImage(rotatedBitmap)

        // Retornar à tela inicial
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    // Função para aplicar o efeito negativo
    private fun applyNegativeEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val negativeBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = 255 - Color.red(pixel)
                val green = 255 - Color.green(pixel)
                val blue = 255 - Color.blue(pixel)
                negativeBitmap.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }
        return negativeBitmap
    }

    // FILTRO SÉPIA


    // Aplicar o efeito Sépia na imagem
    private fun applySepia() {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        val sepiaBitmap = applySepiaEffect(bitmap)

        // Rotacionar a imagem para corrigir a orientação
        val rotatedBitmap = rotateBitmap(sepiaBitmap, 90f) // Rotação de 90 graus, ajuste conforme necessário

        // Exibir a imagem processada na ImageView
        imageView.setImageBitmap(rotatedBitmap)

        // Salvar a imagem editada na galeria e obter a Uri da imagem salva
        imageUri = saveEditedImage(rotatedBitmap)

        // Retornar à tela inicial
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    // Função para aplicar o efeito Sépia
    private fun applySepiaEffect(bitmap: Bitmap): Bitmap {
        // Parâmetros para o efeito Sépia
        val depth = 20
        val intensity = 40

        val width = bitmap.width
        val height = bitmap.height
        val sepiaBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)
                val tr = (0.393 * r + 0.769 * g + 0.189 * b).coerceAtMost(255.0)
                val tg = (0.349 * r + 0.686 * g + 0.168 * b).coerceAtMost(255.0)
                val tb = (0.272 * r + 0.534 * g + 0.131 * b).coerceAtMost(255.0)
                val newPixel =
                    Color.argb(Color.alpha(pixel), tr.toInt(), tg.toInt(), tb.toInt())
                sepiaBitmap.setPixel(x, y, newPixel)
            }
        }
        return sepiaBitmap
    }

    // Conversão para tons de cinza.


    private fun applyGrayScale() {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        val grayBitmap = applyGrayScaleEffect(bitmap)

        // Rotacionar a imagem para corrigir a orientação
        val rotatedBitmap = rotateBitmap(grayBitmap, 90f)

        // Exibir a imagem processada na ImageView
        imageView.setImageBitmap(rotatedBitmap)

        // Salvar a imagem editada na galeria e obter a Uri da imagem salva
        imageUri = saveEditedImage(rotatedBitmap)

        // Retornar à tela inicial
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    // Função para aplicar a conversão para tons de cinza
    private fun applyGrayScaleEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayBitmap = Bitmap.createBitmap(width, height, bitmap.config)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)
                val grayValue = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
                val newPixel = Color.rgb(grayValue, grayValue, grayValue)
                grayBitmap.setPixel(x, y, newPixel)
            }
        }
        return grayBitmap
    }





    private fun saveEditedImage(bitmap: Bitmap): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "edited_image_$timeStamp.jpg"
        val resolver = applicationContext.contentResolver

        // Verificar se o armazenamento externo está disponível
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            // Diretório onde a imagem será salva
            val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(imageDir, filename)

            try {
                // Salvar a imagem processada no arquivo
                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                // Adicionar a imagem à galeria
                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(imageFile.absolutePath),
                    arrayOf("image/jpeg"),
                    null
                )

                // Retornar a Uri da imagem salva
                return Uri.fromFile(imageFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            // Se o armazenamento externo não estiver disponível, use o armazenamento interno
            val imageDir = File(applicationContext.filesDir, "images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            val imageFile = File(imageDir, filename)

            try {
                FileOutputStream(imageFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                return Uri.fromFile(imageFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Em caso de falha, retorna a Uri original
        return imageUri
    }

    // Função para rotacionar a Bitmap
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}