package mx.tecnm.tepic.ladm_u3_ejercicio5
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main5.*
import kotlinx.android.synthetic.main.activity_main6.*
import java.sql.Date
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity6 : AppCompatActivity() {
    var baseDatos=BD(this,"basedatos1",null,1)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)
        cargarevt()
        btn6Reg.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            startActivity(ventana)
            finish()
        }
        btn6Bus.setOnClickListener {
            var operacion = Act6spin.selectedItemPosition.toString().toInt()
            when (operacion) {
                0 -> {
                    //En el combo es descripcion
                    cargarevt()
                }
                1 -> {
                    cargarevtMAN()
                }
                2 -> {
                    cargaentre()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarevt(){
        try{
            var trans=baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var fecha_hoy= LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            var respuesta=trans.query("EVENTO", arrayOf("*"),"FECHA=?", arrayOf(fecha_hoy.toString()),null,null,null)
            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                            "HORA :${respuesta.getString(4)}"
                    eventos.add(concatenacion)
                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO TIENES EVENTOS HOY")
            }
            list6f.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(list6f)
            trans.close()
        }catch (e: SQLiteException){mensaje("ERROR: "+e.message!!)}
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarevtMAN(){
        try{
            var trans=baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var fecha_hoy= LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            var respuesta=trans.query("EVENTO", arrayOf("*"),"FECHA=?", arrayOf(fecha_hoy.toString()),null,null,null)
            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                            "HORA :${respuesta.getString(4)}"
                    eventos.add(concatenacion)
                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO TIENES EVENTOS MAÑANA")
            }
            list6f.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(list6f)
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargaentre(){
        try {
            if (txt6f1.text.isEmpty()||txt6f1.text.isEmpty()){return}
            var f1=txt6f1.text.toString().format(DateTimeFormatter.ofPattern("dd/mm/yy"))
            var f2=txt6f2.text.toString().format(DateTimeFormatter.ofPattern("dd/mm/yy"))
            var trans_2= baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var respuesta = trans_2.query("EVENTO", arrayOf("*"), null, null, null, null, null)
            if(respuesta.moveToFirst()) {
                do {
                    println("\n"+respuesta.getString(3).format(DateTimeFormatter.ofPattern("dd/mm/yy")))

                    if(f1<respuesta.getString(3).format(DateTimeFormatter.ofPattern("dd/mm/yy"))&&f2>respuesta.getString(3).format(DateTimeFormatter.ofPattern("dd/mm/yy"))){
                        var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                        "HORA :${respuesta.getString(4)}"
                        eventos.add(concatenacion)
                    }
                } while (respuesta.moveToNext())
            }else {
                eventos.add("NO TIENES EVENTOS NADA EN ESAS FECHA")
            }
            list6f.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(list6f)
            trans_2.close()
        }catch (e: SQLiteException){
            mensaje(e.message!!)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun elim_now(){
        try {
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
}