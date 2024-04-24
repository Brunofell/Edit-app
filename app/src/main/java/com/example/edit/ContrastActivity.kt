package com.example.edit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class ContrastActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var contrastSeekBar: SeekBar
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contrast)

        imageView = findViewById(R.id.imageView)
        contrastSeekBar = findViewById(R.id.seekBarContrast)

        val imageUriString = intent.getStringExtra("imageUri")
        imageUri = Uri.parse(imageUriString)

        // Carregar a imagem na ImageView
        imageView.setImageURI(imageUri)

        // Configurar o controle deslizante de contraste
        contrastSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Atualizar a visualização da imagem com o contraste alterado
                imageView.setImageBitmap(adjustContrast(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar o botão de salvar
        findViewById<Button>(R.id.btnSaveContrast).setOnClickListener {
            val editedBitmap = adjustContrast(contrastSeekBar.progress)
            saveImage(editedBitmap)
        }
    }

    private fun adjustContrast(progress: Int): Bitmap {

        // Carregar a imagem original
        var bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))

        // Verificar se a imagem está na orientação correta
        if (bitmap.width > bitmap.height) {
            val matrix = Matrix()
            matrix.postRotate(90f)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        // Criar uma matriz de cores para ajustar o contraste
        val colorMatrix = ColorMatrix()
        val contrastValue = progress / 100f
        colorMatrix.set(floatArrayOf(
            contrastValue, 0f, 0f, 0f, 0f,
            0f, contrastValue, 0f, 0f, 0f,
            0f, 0f, contrastValue, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

        // Aplicar o filtro de matriz de cores à pintura
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        // Criar um novo bitmap para a imagem processada
        val filteredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        // Desenhar o bitmap original no novo bitmap com o filtro de pintura aplicado
        val canvas = Canvas(filteredBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Retornar o bitmap processado com o ajuste de contraste
        return filteredBitmap
    }

    private fun saveImage(bitmap: Bitmap) {
        // Obter um timestamp único para o nome do arquivo
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "edited_image_$timeStamp.jpg"

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

                // Mostrar uma mensagem de sucesso
                showToast("Imagem salva com sucesso!")

                // Retornar à tela inicial
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Erro ao salvar a imagem.")
            }
        } else {
            // Se o armazenamento externo não estiver disponível, exibir uma mensagem de erro
            showToast("O armazenamento externo não está disponível.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}