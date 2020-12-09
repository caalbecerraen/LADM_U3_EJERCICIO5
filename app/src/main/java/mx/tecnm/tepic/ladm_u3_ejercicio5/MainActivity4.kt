package mx.tecnm.tepic.ladm_u3_ejercicio5

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main4.*

class MainActivity4 : AppCompatActivity() {
    var baseDatos=BD(this,"basedatos1",null,1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        btn4inser.setOnClickListener {
            insertar()
        }
        btn4reg.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }
    }
    private fun insertar(){
        try {
            var trans=baseDatos.writableDatabase
            var variables= ContentValues()
            variables.put("DESCRIPCION",txt4des.text.toString())
            variables.put("LUGAR",txt4lugar.text.toString())
            variables.put("FECHA",txt4fecha.text.toString())
            variables.put("HORA",txt4hora.text.toString())
            var respuesta =trans.insert("EVENTO",null,variables)
            if(respuesta==-1L){
                mensaje("FALLO AL INSERTAR")
            }else{
                mensaje("INSERCION EXITOSA")
                var ventana= Intent(this,MainActivity::class.java)
                startActivity(ventana)
                finish()
            }
            trans.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }

    }
    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }
}