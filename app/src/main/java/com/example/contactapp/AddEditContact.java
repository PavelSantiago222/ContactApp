package com.example.contactapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.Permission;

public class AddEditContact extends AppCompatActivity {

    private ImageView profileIv;
    private EditText nameEt,phoneEt,emailEt,noteEt;
    private FloatingActionButton fab;

    //String variable;
    private String id,image,name,phone,email,note,addedTime,updatedTime;
    private Boolean isEditMode;

    //action bar
    private ActionBar actionBar;

    //permiso constante
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    // string array de permiso
    private String[] cameraPermission;
    private String[] storagePermission;

    //Imagen uri var
    private Uri imageUri;

    //database ayudante
    private DbHelper dbHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        //inicio db
        dbHelper = new DbHelper(this);

        //permiso de inicio
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //inicio actionBar
        actionBar = getSupportActionBar();


        //botón de retroceso
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //inicio view
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        noteEt = findViewById(R.id.noteEt);
        fab = findViewById(R.id.fab);

        // get intent data
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);

        if (isEditMode){
            //establecer toolbar title
            actionBar.setTitle("Update Contact");

            //obtener el otro valor de la intención
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            phone = intent.getStringExtra("PHONE");
            email = intent.getStringExtra("EMAIL");
            note = intent.getStringExtra("NOTE");
            addedTime = intent.getStringExtra("ADDEDTIME");
            updatedTime = intent.getStringExtra("UPDATEDTIME");
            image = intent.getStringExtra("IMAGE");

            //establecer valor en el campo editarTexto
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);
            noteEt.setText(note);

            imageUri = Uri.parse(image);

            if (image.equals("")){
                profileIv.setImageResource(R.drawable.ic_baseline_person_24);
            }else {
                profileIv.setImageURI(imageUri);
            }

        }else {
            // agregar modo activado
            actionBar.setTitle("Add Contact");
        }

        // agregar controlador par
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    private void showImagePickerDialog() {

        //opción de diálogo
        String options[] = {"Camera","Gallery"};

        // Generador de diálogos de alerta
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);

        //setTitle
        builder.setTitle("Choose An Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //manejar elemento haga clic
                if (which == 0){ //empezar desde 0 índice
                    //cámara seleccionada
                    if (!checkCameraPermission()){
                        //solicitar permiso de cámara
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                        
                }else if (which == 1){
                    //Galería seleccionada
                    if (!checkStoragePermission()){
                        //solicitar permiso de almacenamiento
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                    
                }
            }
        }).create().show();
    }

    private void pickFromGallery() {
        //intención de tomar una imagen de la galería
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); // solo imagen

        startActivityForResult(galleryIntent,IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {

//       ContentValues para información de imagen
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"IMAGE_DETAIL");

        //guardar imagenUri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intención de abrir la cámara
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        startActivityForResult(cameraIntent,IMAGE_FROM_CAMERA_CODE);
    }

    private void saveData() {

        //tomar datos del usuario dador en variable
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();
        email = emailEt.getText().toString();
        note = noteEt.getText().toString();

        // obtener la hora actual para guardar como tiempo agregado
        String timeStamp = ""+System.currentTimeMillis();


        //comprobar datos archivados
        if (!name.isEmpty() || !phone.isEmpty() || !email.isEmpty() || !note.isEmpty()){
            //guardar datos, si el usuario tiene solo un dato

            //marque el modo editar o agregar para guardar datos en SQL
            if (isEditMode){
                // modo de edición
                 dbHelper.updateContact(
                        ""+id,
                        ""+image,
                        ""+name,
                        ""+phone,
                        ""+email,
                        ""+note,
                        ""+addedTime,
                         ""+timeStamp // la hora actualizada será la nueva hora
                );

                Toast.makeText(getApplicationContext(), "Actualizado con éxito....", Toast.LENGTH_SHORT).show();

            }else {
                // agregar modo
                long id =  dbHelper.insertContact(
                        ""+imageUri,
                        ""+name,
                        ""+phone,
                        ""+email,
                        ""+note,
                        ""+timeStamp,
                        ""+timeStamp
                );
                //Para verificar que los datos se hayan insertado correctamente, muestre un mensaje
                Toast.makeText(getApplicationContext(), "Insertado con éxito...."+id, Toast.LENGTH_SHORT).show();
            }

        }else {
            //mostrar mensaje
            Toast.makeText(getApplicationContext(), "Nada que guardar....", Toast.LENGTH_SHORT).show();
        }

    }

    //ctr + O

    //clic en el botón Atrás
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //comprobar el permiso de la cámara
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & result1;
    }

    //solicitud de permiso de cámara
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_PERMISSION_CODE); // handle request permission on override method
    }

    //comprobar el permiso de almacenamiento
    private boolean checkStoragePermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result1;
    }

    //solicitud de permiso de cámara
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_PERMISSION_CODE);
    }


    //manejar la solicitud de permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length >0){

                    //si todos los permisos están permitidos, devuelve verdadero; de lo contrario, falso
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        //ambos permisos concedidos
                        pickFromCamera();
                    }else {
                        //permiso no concedido
                        Toast.makeText(getApplicationContext(), "Se necesita permiso para cámara y almacenamiento....", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length >0){

                    //si todos los permisos están permitidos, devuelve verdadero; de lo contrario, falso
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted){
                        //permission granpermiso concedidoted
                        pickFromGallery();
                    }else {
                        //permiso no concedido
                        Toast.makeText(getApplicationContext(), "Se necesita permiso de almacenamiento...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_FROM_GALLERY_CODE){
                // imagen elegida de la galería
                //elimitar imagen
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddEditContact.this);

            }else if (requestCode == IMAGE_FROM_CAMERA_CODE){
                //imagen escogida de la cámara
                //delimitar imagen
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddEditContact.this);
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                //imagen recortada recibida
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                profileIv.setImageURI(imageUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //para el manejo de errores
                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // crear objeto de vista en un archivo java
    // Toma de imagen de perfil con permiso del usuario y funcionalidad de recorte
    // primer permiso del manifiesto, comprobar, solicitar permiso
    // haciendo clic en perfilIv abre el cuadro de diálogo para elegir la imagen
    // elige Imagen y guarda en la variable ImageUri
    // crear actividad para recortar imagen en archivo de manifiesto
    // en el siguiente tutorial creamos la base de datos SQLite y agregamos datos.
    // crea una clase llamada "Constantes" para el título de la base de datos y la tabla
    // ahora insertamos datos en la base de datos desde la clase AddEditContact
    // ahora ejecuta la aplicación, terminamos con nuestra función de inserción





}