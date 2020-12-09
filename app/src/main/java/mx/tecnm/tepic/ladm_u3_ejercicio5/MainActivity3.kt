package mx.tecnm.tepic.ladm_u3_ejercicio5

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.ListaAct2
import java.sql.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity3 : AppCompatActivity() {
    var baseDatos=BD(this,"basedatos1",null,1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        cargarevt()
        btnBorrar_ant.setOnClickListener{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                elim_now()
            }
        }
        btnAct3reg.setOnClickListener{
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }
        btnAct3Consul.setOnClickListener{
            var ventana= Intent(this,MainActivity5::class.java)
            startActivity(ventana)
            finish()
        }


}
    @RequiresApi(Build.VERSION_CODES.O)
    private fun elim_now(){
        try {
            var arr_fecha=ArrayList<Date>()
            var trans_2= baseDatos.readableDatabase
            var trans=baseDatos.writableDatabase
            var respuesta = trans_2.query("EVENTO", arrayOf("ID","FECHA"), null, null, null, null, null)
            if(respuesta.moveToFirst()) {
                do {
                    var fecha_hoy= LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    println(fecha_hoy)
                    println("\n"+respuesta.getString(1).format(DateTimeFormatter.ofPattern("dd/mm/yy")))

                    if(respuesta.getString(1).format(DateTimeFormatter.ofPattern("dd/mm/yy"))<fecha_hoy){
                        var resultado=trans.delete("EVENTO","ID=?",
                            arrayOf(respuesta.getString(0)))
                        if (resultado==0){
                            mensaje("ERROR! No se pudo elimminar")

                        }else{
                            mensaje("Se logro eliminar con éxito el ID${respuesta.getString(0)}")
                            cargarevt()////
                        }
                    }

                } while (respuesta.moveToNext())
            }else {

            }
            trans_2.close()
            trans.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }
    }
    private fun cargarevt(){
        try{
            var trans=baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var respuesta=trans.query("EVENTO", arrayOf("*"),null,null,null,null,null)
            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                            "HORA :${respuesta.getString(4)}"
                    eventos.add(concatenacion)
                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO TIENES EVENTO")
            }
            ListaAct2.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(ListaAct2)
            trans.close()
        }catch (e: SQLiteException){mensaje("ERROR: "+e.message!!)}
    }
    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }
}