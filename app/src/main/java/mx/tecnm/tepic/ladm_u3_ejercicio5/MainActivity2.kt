package mx.tecnm.tepic.ladm_u3_ejercicio5

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var bd_act=BD(this,"basedatos1",null,1)
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        act2reg.setOnClickListener{
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }
        var extra=intent.extras
        id=extra!!.getString("id")!!
        try {
            var base=bd_act.readableDatabase
            var respuesta=base.query("evento", arrayOf("DESCRIPCION","HORA","FECHA","LUGAR"),"ID=?",
                arrayOf(id),null,null,null,null)
            if(respuesta.moveToFirst()){
                ActevtDes.setText(respuesta.getString(0))
                ActevtHour.setText(respuesta.getString(1))
                ActevtFecha.setText(respuesta.getString(2))
                ActevtLugar.setText(respuesta.getString(3))
            }else{
                mensaje("ERROR! no se encontro ID")
            }
            base.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }
        btnAct.setOnClickListener{
            actualizar(id)
        }
        btnCerrar.setOnClickListener { finish() }
    }
    private fun actualizar (id:String){
        try {
            var trans=bd_act.writableDatabase
            var valores= ContentValues()
            valores.put("DESCRIPCION",ActevtDes.text.toString())
            valores.put("HORA",ActevtHour.text.toString())
            valores.put("FECHA",ActevtFecha.text.toString())
            valores.put("LUGAR",ActevtLugar.text.toString())
            var res=trans.update("evento",valores,"ID=?", arrayOf(id))
            if(res>0){
                mensaje("Se actualizo correctamente el ID")
                var itent= Intent(this,MainActivity::class.java)
                startActivity(itent)
                finish()
            }else{
                mensaje("No se pudo actualizar")
            }
        }catch (e:SQLiteException){
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