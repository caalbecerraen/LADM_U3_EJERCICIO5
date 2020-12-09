package mx.tecnm.tepic.ladm_u3_ejercicio5

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.util.*
import kotlin.collections.ArrayList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    ////////Firebase
    var BDF=FirebaseFirestore.getInstance();
    ////////sql
    var baseDatos=BD(this,"basedatos1",null,1)

    var listaID=ArrayList<String>()
    var datos=ArrayList<String>()
    var DATA= ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cargarevt()
        btnagEvent.setOnClickListener {
            var ventana= Intent(this,MainActivity4::class.java)
            startActivity(ventana)
            finish()
        }
        btn1fech.setOnClickListener {
            var ventana= Intent(this,MainActivity6::class.java)
            startActivity(ventana)
            finish()
        }
        btnSinc.setOnClickListener{
            sinc()
        }
        btnesp.setOnClickListener{
            var ventana= Intent(this,MainActivity3::class.java)
            startActivity(ventana)
            finish()
        }
        Lista.setOnItemClickListener {adapterView,view, i, l ->mostrarAlertEliminarActualizar(i) }
    }
    //////////////////////////////////////////////////////////////RELLENAR LA LISTA
    private fun cargarevt(){
        datos.clear()/////
        listaID.clear()////->Elimina el id 1-2-_-4-5-6
                        /////1-2-_-4
        try{
            var trans=baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var respuesta=trans.query("EVENTO", arrayOf("*"),null,null,null,null,null)
            listaID.clear()
            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                            "HORA :${respuesta.getString(4)}"
                    eventos.add(concatenacion)
                    datos.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO TIENES EVENTO")
            }
                Lista.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(Lista)
            trans.close()
        }catch (e: SQLiteException){mensaje("ERROR: "+e.message!!)}
    }
    ////////////////////////////////////////////////////////////DESPLEGAR MENU PARA OPCIONES
    private fun mostrarAlertEliminarActualizar(Posicion:Int){
        var idLista=listaID.get(Posicion)
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage("¿Que desea hacer con \n${datos.get(Posicion)}?")
            .setPositiveButton("ELIMINIAR"){d,i->eliminar(idLista)}
            .setNeutralButton("CANCELAR"){d,i->}
            .setNegativeButton("Actualizar"){d,i->llamarVentanaAcualizar(idLista)}
            .show()
    }
    //////////////////////////////////////////////////////////////MENSAJES
    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }
    private fun llamarVentanaAcualizar(idLista:String){
                var ventana= Intent(this,MainActivity2::class.java)
                ventana.putExtra("id",idLista)
                mensaje(idLista)
                startActivity(ventana)
                finish()
    }
    private fun sinc(){
        DATA.clear()///Traer Firestore
        BDF.collection("evento").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                mensaje("Error! No se pudo recuperar data desde FireBase")
                return@addSnapshotListener
            }
            var cadena = ""
            for (registro in querySnapshot!!) {
                cadena = registro.id.toString()////IDS
                DATA.add(cadena)///.....IDS.....IDS
            }
            try {
                var trans = baseDatos.readableDatabase
                var respuesta = trans.query("EVENTO", arrayOf("*"), null, null, null, null, null)
                if (respuesta.moveToFirst()) {
                    do{
                        BDF.waitForPendingWrites()
                        if (DATA.any{respuesta.getString(0).toString()==it})//////id de la tabla
                        {
                            DATA.remove(respuesta.getString(0).toString())
                            BDF.collection("evento")
                                .document(respuesta.getString(0))
                                .update("DESCRIPCION",respuesta.getString(1),
                                    "LUGAR",respuesta.getString(2),
                                    "FECHA",respuesta.getString(3),"HORA",respuesta.getString(4)
                                ).addOnSuccessListener {
                                    /*Toast.makeText(this,"SE ACTUALIZO", Toast.LENGTH_LONG)
                                        .show()*/
                                    BDF.waitForPendingWrites()
                                }.addOnFailureListener {
                                    AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("NO SE PUDO ACTUALIZAR\n${it.message!!}")
                                        .setPositiveButton("Ok"){d,i->}
                                        .show()
                                }
                        } else {
                            var datosInsertar = hashMapOf(
                                "DESCRIPCION" to respuesta.getString(1),
                                "LUGAR" to respuesta.getString(2),
                                "FECHA" to respuesta.getString(3),
                                "HORA" to respuesta.getString(4)
                            )
                            BDF.collection("evento").document("${respuesta.getString(0)}")
                                .set(datosInsertar as Any).addOnSuccessListener {
                                    /*Toast.makeText(
                                        this,
                                        "Se inserto correctamente el ID",
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                }
                                .addOnFailureListener {
                                    mensaje("NO SE PUDO INSERTAR:\n${it.message!!}")
                                }
                        }
                    }while (respuesta.moveToNext())

                } else {
                    datos.add("NO TIENES EVENTO")
                }
                trans.close()
            } catch (e: SQLiteException) {
                mensaje("ERROR: " + e.message!!)
            }
            var el = DATA.subtract(listaID)
            //////1,2,3,4 data(fire)
            /////1,2,4      (sql)
            //////3
            if (el.isEmpty()) {

            } else {
                el.forEach {
                    BDF.collection("evento")
                        .document(it)
                        .delete()
                        .addOnSuccessListener {}
                        .addOnFailureListener { mensaje("Error:No se elimino\n" + it.message!!) }
                }
            }

        }
        mensaje("Sincronizado con exito")
    }
    private fun eliminar(idEliminar:String){
        try {
            var trans=baseDatos.writableDatabase
            var resultado=trans.delete("EVENTO","ID=?",
                arrayOf(idEliminar))
            if (resultado==0){
                mensaje("ERROR! No se pudo elimminar")

            }else{
                mensaje("Se logro eliminar con éxito el ID${idEliminar}")
                cargarevt()////
            }
            trans.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
    }

}