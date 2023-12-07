package com.example.contactapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

//class for database helper
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create table on database
        db.execSQL(Constants.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //actualizar la tabla si hay algún cambio de estructura en la base de datos

        // soltar tabla si existe
        db.execSQL("DROP TABLE IF EXISTS "+Constants.TABLE_NAME);

        // crear tabla nuevamente
        onCreate(db);

    }

    // Insertar función para insertar datos en la base de datos.
    public long insertContact(String image,String name,String phone,String email,String note,String addedTime,String updatedTime){

        //obtener una base de datos grabable para escribir datos en la base de datos
        SQLiteDatabase db = this.getWritableDatabase();

        // crear un objeto de clase ContentValue para guardar datos
        ContentValues contentValues = new ContentValues();

        // La identificación se guardará automáticamente a medida que escribimos la consulta.
        contentValues.put(Constants.C_IMAGE,image);
        contentValues.put(Constants.C_NAME,name);
        contentValues.put(Constants.C_PHONE,phone);
        contentValues.put(Constants.C_EMAIL,email);
        contentValues.put(Constants.C_NOTE,note);
        contentValues.put(Constants.C_ADDED_TIME,addedTime);
        contentValues.put(Constants.C_UPDATED_TIME,updatedTime);

        //inserte datos en la fila, devolverá la identificación del registro
        long id = db.insert(Constants.TABLE_NAME,null,contentValues);

        // close db
        db.close();

        //return id
        return id;

    }

    // Función de actualización para actualizar datos en la base de datos.
    public void updateContact(String id,String image,String name,String phone,String email,String note,String addedTime,String updatedTime){

        //obtener una base de datos grabable para escribir datos en la base de datos
        SQLiteDatabase db = this.getWritableDatabase();

        // crear un objeto de clase ContentValue para guardar datos
        ContentValues contentValues = new ContentValues();

        contentValues.put(Constants.C_IMAGE,image);
        contentValues.put(Constants.C_NAME,name);
        contentValues.put(Constants.C_PHONE,phone);
        contentValues.put(Constants.C_EMAIL,email);
        contentValues.put(Constants.C_NOTE,note);
        contentValues.put(Constants.C_ADDED_TIME,addedTime);
        contentValues.put(Constants.C_UPDATED_TIME,updatedTime);

        //actualice los datos en la fila, devolverá la identificación del registro
        db.update(Constants.TABLE_NAME,contentValues,Constants.C_ID+" =? ",new String[]{id} );

        // close db
        db.close();

    }

    // eliminar datos por id
    public void deleteContact(String id){
        //obtener base de datos grabable
        SQLiteDatabase db =  getWritableDatabase();

        //eliminar consulta
        db.delete(Constants.TABLE_NAME,"WHERE"+" =? ",new String[]{id});

        db.close();
    }

    // borrar todos los datos
    public void deleteAllContact(){
        //obtener base de datos grabable
        SQLiteDatabase db = getWritableDatabase();

        //consulta para eliminar
        db.execSQL("DELETE FROM "+Constants.TABLE_NAME);
        db.close();
    }


    // obtener datos
    public ArrayList<ModelContact> getAllData(){
        //create arrayList
        ArrayList<ModelContact> arrayList = new ArrayList<>();
        //consulta de comando sql
        String selectQuery = "SELECT * FROM "+Constants.TABLE_NAME;

        //obtener base de datos legible
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // recorrer todos los registros y agregarlos a la lista
        if (cursor.moveToFirst()){
            do {
                ModelContact modelContact = new ModelContact(
                        // solo la identificación es de tipo entero
                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATED_TIME))
                );
                arrayList.add(modelContact);
            }while (cursor.moveToNext());
        }
        db.close();
        return arrayList;
    }

    // buscar datos en base de datos sql
    public ArrayList<ModelContact> getSearchContact(String query){

        // devolverá la lista de matrices de la clase modelContact
        ArrayList<ModelContact> contactList = new ArrayList<>();

        // obtener base de datos legible
        SQLiteDatabase db = getReadableDatabase();

        //consulta para buscar
        String queryToSearch = "SELECT * FROM "+Constants.TABLE_NAME+" WHERE "+Constants.C_NAME + " LIKE '%" +query+"%'";

        Cursor cursor = db.rawQuery(queryToSearch,null);

        // recorrer todos los registros y agregarlos a la lista
        if (cursor.moveToFirst()){
            do {
                ModelContact modelContact = new ModelContact(
                        // only id is integer type
                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(Constants.C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NAME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_PHONE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_NOTE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_ADDED_TIME)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(Constants.C_UPDATED_TIME))
                );
                contactList.add(modelContact);
            }while (cursor.moveToNext());
        }
        db.close();
        return contactList;

    }

}
