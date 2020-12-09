package mx.tecnm.tepic.ladm_u3_ejercicio5

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main5.*

class MainActivity5 : AppCompatActivity() {
    var listaID=ArrayList<String>()
    var datos=ArrayList<String>()
    var baseDatos=BD(this,"basedatos1",null,1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        cargarevt()
        btnConsult.setOnClickListener {
        consultar()
        }
        btn5Regresar.setOnClickListener {
            var ventana= Intent(this,MainActivity3::class.java)
            startActivity(ventana)
            finish()
        }
        ListaAct5.setOnItemClickListener {adapterView,view, i, l ->mostrarAlertEliminarActualizar(i) }
    }
    private fun consultar(){
        var operacion = spinner2.selectedItemPosition.toString().toInt()
        var parametro=""
        var campo=""
        when (operacion) {
            0 -> {
                //En el combo es descripcion
                parametro=txt3DES.text.toString()
                campo="DESCRIPCION"
            }
            1 -> {
                //En el combo es Hora
                parametro=txt3Hour.text.toString()
                campo="HORA"
            }
            2 -> {
                //En el combo Fecha
                parametro=txt3Fecha.text.toString()
                campo="FECHA"
            }
            3 -> {
                // En el combo es lugar
                parametro=txt3Lugar.text.toString()
                campo="LUGAR"
            }
        }
        try{
            datos.clear()/////
            listaID.clear()
            var trans=baseDatos.readableDatabase
            var eventos=ArrayList<String>()
            var respuesta=trans.query("EVENTO", arrayOf("*"),"${campo}=?", arrayOf(parametro),null,null,null)
            if (respuesta.moveToFirst()){
                do{
                    var concatenacion="DESCRIPCION: ${respuesta.getString(1)}\nLUGAR: ${respuesta.getString(2)}\nFECHA :${respuesta.getString(3)}\n" +
                            "HORA :${respuesta.getString(4)}"
                    datos.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                    eventos.add(concatenacion)
                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO TIENES EVENTO")
            }
            ListaAct5.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
            this.registerForContextMenu(ListaAct5)
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
                ListaAct5.adapter=
                    ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,eventos)
                this.registerForContextMenu(ListaAct5)
                trans.close()
            }catch (e: SQLiteException){mensaje("ERROR: "+e.message!!)}
    }

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
    private fun llamarVentanaAcualizar(idLista:String){
        var ventana= Intent(this,MainActivity2::class.java)
        ventana.putExtra("id",idLista)
        mensaje(idLista)
        startActivity(ventana)
        finish()
    }
}