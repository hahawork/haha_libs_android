package com.haha.libreria;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class clsGlobales {


    private static Context mContext;
    SharedPreferences setting;

    public clsGlobales(Context context) {
        mContext = context;
        setting = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    private void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public void sendSMS(String NumTel, String NombCliente, String MontDeuda) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        StringBuilder cadena = new StringBuilder("Sr/a. ");
        cadena.append(NombCliente + ", ");
        cadena.append(setting.getString("NombreNeg", "Puperia de confianza"));
        cadena.append(" le recuerda que a la fecha tiene un saldo de C$" + MontDeuda);
        cadena.append(", Agrezco su pronto pago.");

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", NumTel);
        smsIntent.putExtra("sms_body", cadena.toString());
        try {
            mContext.startActivity(smsIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        /*Applying information Subject and Body.*/
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, NumTel);
        sendIntent.putExtra(Intent.EXTRA_TEXT, cadena.toString());
        /*Fire!*/
        Intent shareIntent = Intent.createChooser(sendIntent, "Botón atras para SMS directo.");
        mContext.startActivity(shareIntent);
    }

    /**
     * Crea un respaldo de la base de datos de la aplicacion a la sdcard con el nombre  DDBB_yyyyMMdd
     *
     * @param DBNAME                 El nombre de la base de datos a respaldar.
     * @param PATH_EXTERNAL_RESOURCE La ruta de la carpeta donde se va a guardar.
     * @param extensDB               La extension del archivo a guardar (ej: "db")
     * @param borrarMayoresADias     Si desea borrar respaldos mayores a N dias
     * @param borrarRespaldos        Si desea borrar respaldos a N dias enviar TRUE, si no FALSE
     * @param forzarRespald          Si ya existe un respaldo con el dia de hoy no se crea otro, enviar TRUE para reemplazarlo
     * @throws IOException
     */
    public void RespaldarBBDD(String DBNAME,
                              String PATH_EXTERNAL_RESOURCE,
                              String extensDB,
                              int borrarMayoresADias,
                              boolean borrarRespaldos,
                              boolean forzarRespald) throws IOException {

        try {
            //obtenemos todos los repaldos de base de datos
            File directory = new File(PATH_EXTERNAL_RESOURCE);
            File[] files = directory.listFiles();
            for (File bk : files) {
                //si es un archivo con la extension .db o es .csv
                if (bk.getName().contains("." + extensDB)/* || bk.getName().contains(".csv")*/) {
                    //obtiene la fecha de creacion
                    Date fechCreacion = new Date(bk.lastModified());
                    //si tiene mas de 5 dias se borra el archivo para liberar espacio
                    long diff = new Date().getTime() - fechCreacion.getTime();
                    int numDias = (int) (diff / (1000 * 60 * 60 * 24));
                    if (numDias >= borrarMayoresADias)
                        if (borrarRespaldos)
                            bk.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean success = true;
        File file = new File(PATH_EXTERNAL_RESOURCE + "DDBB_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "." + extensDB);

        if (file.exists()) {
            success = false;
        }

        //SI es un respaldo a peticion del usuario entonces forzarRespald=true
        success = forzarRespald ? forzarRespald : success;

        if (success) {
            File inFileName = mContext.getDatabasePath(DBNAME);
            //File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(inFileName);

            String outFileName = PATH_EXTERNAL_RESOURCE + "DDBB_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();

            if (forzarRespald)
                Toast.makeText(mContext, "Se ha realzado un respaldo de la BBDD en la sdcard.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene la lista de todos los respaldo de base de datos en la sdcard
     *
     * @param PATH_EXTERNAL_RESOURCE La ruta de la carpeta donde se hacen los respaldos.
     * @param extDB                  L extension del archivo de la base de datos sin el '.' (db, sqlite, sqlite3)
     * @return Un JSONArray con cada respaldo de la base. Llaves:(nombre, fecha, ruta) en cada
     * JSONObject. [{"nombre": "test1.db", "fecha":"Hace yyy dias", "ruta":"/sdcard/basedatos/test1.db"},{}]
     */
    public JSONArray ObtenerRespaldosBBDD(String PATH_EXTERNAL_RESOURCE, String extDB) {

        JSONArray jaRespaldos = new JSONArray();

        try {
            //obtenemos todos los repaldos de base de datos
            File directory = new File(PATH_EXTERNAL_RESOURCE);
            File[] files = directory.listFiles();
            for (File bk : files) {
                //si es un archivo con la extension .db o es .csv
                if (bk.getName().contains("." + extDB) && !bk.getName().contains(".db-journal") && bk.isFile()) {
                    //obtiene la fecha de creacion
                    Date fechCreacion = new Date(bk.lastModified());
                    Date now = new Date();
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - fechCreacion.getTime());
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - fechCreacion.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - fechCreacion.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - fechCreacion.getTime());
                    //si tiene mas de 5 dias se borra el archivo para liberar espacio
                    long diff = new Date().getTime() - fechCreacion.getTime();
                    int numDias = (int) (diff / (1000 * 60 * 60 * 24));

                    StringBuilder cadena = new StringBuilder();
                    cadena.append((days > 0) ? String.format(" %d d.,", days) : "");
                    cadena.append((hours > 0) ? String.format(" %d hor.,", hours) : "");
                    cadena.append((minutes > 0) ? String.format(" %d min.,", minutes) : "");
                    cadena.append((seconds > 0) ? String.format("%d seg.", seconds) : "");

                    JSONObject joRespaldos = new JSONObject();
                    joRespaldos.put("nombre", bk.getName());
                    joRespaldos.put("fecha", String.format("hace %s.", cadena.toString()));
                    joRespaldos.put("ruta", bk.getAbsoluteFile());

                    jaRespaldos.put(joRespaldos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jaRespaldos;
    }

    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     *
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String remover_acentos(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜñÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUnNcC";
        String output = input;
        for (int i = 0; i < original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    /**
     * Obtiene la version de la app en el gradle
     *
     * @return
     */
    public float getVersionApp() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            //int versionCode = android.support.design.BuildConfig.VERSION_CODE;
            // String versionName = android.support.design.BuildConfig.VERSION_NAME;
            return Float.parseFloat(version);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Comprueba si existe una conexion de internet
     *
     * @return True si existe una conexion, False si no existe conexion
     */
    public boolean TieneConexion() {
        boolean bConectado = false;
        try {
            ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] redes = new NetworkInfo[0];
            if (connec != null) {
                redes = connec.getAllNetworkInfo();
            }
            for (int i = 0; i < 2; i++) {
                if (redes[i].getState() == NetworkInfo.State.CONNECTED && redes[i].isConnected() && redes[i].isAvailable()) {
                    bConectado = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!bConectado) {
            Toast toast = Toast.makeText(mContext, "Revisar la conexión a internet.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP/* | Gravity.LEFT*/, 0, 0);
            toast.show();
        }
        return bConectado;
    }

    /**
     * Comprime un archivo de imagen a uno mas pequeño
     *
     * @param file       La ruta del archivo de imagen
     * @param sampleSize La medida del tamaño de 1-10 (1- normal, 10- el mas pequeño
     * @param quality    La calidad de la nuev imagen de 0-100 siendo 100 la mejor calidad.
     */
    public void ComprimeBitmap(File file, int sampleSize, int quality) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            FileInputStream inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream("location to save");
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.close();


           /* long lengthInKb = file.length() / 1024; //in kb
            if (lengthInKb > (setting.getInt("", 3) * 1024)) { //Todo Revisar esto
                ComprimeBitmap(file, (sampleSize * 2), (quality / 4));
            }*/

            selectedBitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}