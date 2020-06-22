package com.haha.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseDatos {

    static SQLiteDatabase db;
    private Context context;

    /**
     * constructor de la clase.
     * @param ctx el context del activity
     * @param database  de la clase de OpenHelper .getwritabledatabase()
     */
    public BaseDatos(Context ctx, SQLiteDatabase database) {

        context = ctx;
        db = database;
    }

    public SQLiteDatabase obtenerConexionBD() {
        return db;
    }

    public Long InsertarRegistro(String Tabla, ContentValues values) {
        try {

            Long insert = db.insert(Tabla, null, values);

            return insert;

        } catch (Exception e) {
            e.printStackTrace();
            return -1l;
        }
    }

    public int ActualizarRegistro(String Tabla, ContentValues values, String where) {
        try {

            int insert = db.update(Tabla, values, where, null);
            //appendToFile(values);
            return insert;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long InsertOrReplaceResumen(String Tabla, String CampoEvaluar, ContentValues values) {
		/*String sql = "INSERT OR REPLACE INTO tblResumenCredit (idClientRC, CantTotalComp, CantTotalAbono, FechaUpdated) " +
				"VALUES ( " + IdClient + ", " +
				"COALESCE(" + totalCompras + ", (SELECT CantTotalComp FROM tblResumenCredit WHERE idClientRC = " + IdClient + "))), " +
				"COALESCE(" + totalAbonos + ", (SELECT CantTotalAbono FROM tblResumenCredit WHERE idClientRC = " + IdClient + "))), " +
				"'" + Fecha + "');";*/

        long ln = db.insertWithOnConflict(Tabla, CampoEvaluar, values, SQLiteDatabase.CONFLICT_REPLACE);

        return ln;
    }

    public int BorrarRegistro(String Tabla, String where) {
        try {

            int deleted = db.delete(Tabla, where, null);
            ejecutarConsulta("VACUUM");
            //appendToFile(values);
            return deleted;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int BorrarTodosLosRegistro(String Tabla) {
        try {

            int deleted = db.delete(Tabla, null, null);
            //appendToFile(values);
            return deleted;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Cursor ejecutarConsulta(String sql) {
        try {

            Cursor cursor = db.rawQuery(sql, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int obtenerMaxIdTabla(String Tabla, String CampoId) {
        try {
            Cursor cursor = db.rawQuery(String.format("Select max(%1$s) as maximo from %2$s ", CampoId, Tabla), null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("maximo"));
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String[] obtenerColumnasDeUnaTabla(String Tabla) {
        try {
            Cursor cursor = db.rawQuery("select * from " + Tabla + " limit 1", null);
            String[] columns = cursor.getColumnNames();
            return columns;

        } catch (Exception e) {
            return null;
        }
    }

    public void appendToFile(ContentValues values, String RutaGuardar) {

        String Codref = "";
        Object value;
        BufferedWriter bw = null;
        try {
            Set<Map.Entry<String, Object>> s = values.valueSet();
            Iterator itr = s.iterator();


            Log.d("DatabaseSync", "ContentValue Length :: " + values.size());

            while (itr.hasNext()) {
                Map.Entry me = (Map.Entry) itr.next();
                String key = me.getKey().toString();
                value = me.getValue();
                if (key.equalsIgnoreCase("CodRef"))
                    Codref = value.toString();

                Log.d("DatabaseSync", "Key:" + key + ", values:" + (value == null ? null : value.toString()));
            }

            if (Codref.length() > 0) {
                // APPEND MODE SET HERE
                bw = new BufferedWriter(new FileWriter(RutaGuardar + Codref + ".txt", true));
                while (itr.hasNext()) {
                    Map.Entry me = (Map.Entry) itr.next();
                    String key = me.getKey().toString();
                    value = me.getValue();
                    bw.write("Key:" + key + ", value:" + (value == null ? null : value.toString()));
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) try {
                bw.close();
            } catch (IOException ioe2) {
                // just ignore it
            }
        } // end try/catch/finally

    } // end test()

    public void AgregarColumnaSiNoExiste(String Tabla, String newColumna, String TipoDato) {

        if (!ExisteColumna(Tabla, newColumna))
            AgregarColumna(Tabla, newColumna, TipoDato);
    }

    public boolean ExisteColumna(String Tabla, String Columna) {

        boolean Existe = false;
        try {

            Cursor cVerificaColumna = db.rawQuery("select * from " + Tabla + " where 0", null);

            String[] ColumnasExis = cVerificaColumna.getColumnNames();
            if (ColumnasExis.length > 0) {
                for (int i = 0; i < ColumnasExis.length; i++) {
                    if (ColumnasExis[i].equalsIgnoreCase(Columna)) {
                        Existe = true;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return Existe;
    }

    public void AgregarColumna(String Tabla, String newColumna, String TipoDato) {
        try {

            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", Tabla, newColumna, TipoDato));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
