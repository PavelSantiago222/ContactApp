package com.example.contactapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;

import java.util.ArrayList;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ContactViewHolder> {

    private Context context;
    private ArrayList<ModelContact> contactList;
    private DbHelper dbHelper;

    // agregar constructor
    // alt + ins

    public AdapterContact(Context context, ArrayList<ModelContact> contactList) {
        this.context = context;
        this.contactList = contactList;
        dbHelper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_contact_item,parent,false);
        ContactViewHolder vh = new ContactViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        ModelContact modelContact = contactList.get(position);

        //obtener datos
        //solo necesitamos todos los datos
        String id = modelContact.getId();
        String image = modelContact.getImage();
        String name = modelContact.getName();
        String phone= modelContact.getPhone();
        String email = modelContact.getEmail();
        String note = modelContact.getNote();
        String addedTime = modelContact.getAddedTime();
        String updatedTime = modelContact.getUpdatedTime();

        //establecer datos a la vista
        holder.contactName.setText(name);
        if (image.equals("")){
            holder.contactImage.setImageResource(R.drawable.ic_baseline_person_24);
        }else {
            holder.contactImage.setImageURI(Uri.parse(image));
        }

        //manejar el oyente de clics
        holder.contactDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //manejar el artículo, hacer clic y mostrar los datos de contacto
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // crear la intención de pasar a contactsDetails Actividad con ID de contacto como referencia
                Intent intent = new Intent(context,ContactDetails.class);
                intent.putExtra("contactId",id);
                context.startActivity(intent); // ahora obtenga datos de los detalles Actividad
                Toast.makeText(context, "Heelo", Toast.LENGTH_SHORT).show();
            }
        });

        // manejar editBtn clic
        holder.contactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // crear la intención de mover AddEditActivity para actualizar los datos
                Intent intent = new Intent(context,AddEditContact.class);
                //pasar el valor de la posición actual
                intent.putExtra("ID",id);
                intent.putExtra("NAME",name);
                intent.putExtra("PHONE",phone);
                intent.putExtra("EMAIL",email);
                intent.putExtra("NOTE",note);
                intent.putExtra("ADDEDTIME",addedTime);
                intent.putExtra("UPDATEDTIME",updatedTime);
                intent.putExtra("IMAGE",image);

                // pasar un dato booleano para definirlo es para fines de edición
                intent.putExtra("isEditMode",true);

                //intención de inicio
                context.startActivity(intent);


            }
        });

        // manejar borrar hacer clic
        holder.contactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Necesitamos una referencia de clase auxiliar de base de datos.
                dbHelper.deleteContact(id);

                //actualizar datos llamando al estado de reanudación de MainActivity
                ((MainActivity)context).onResume();

            }
        });






    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        //ver para row_contact_item
        ImageView contactImage,contactDial;
        TextView contactName,contactEdit,contactDelete;
        RelativeLayout relativeLayout;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            //vista inicial
            contactImage = itemView.findViewById(R.id.contact_image);
            contactDial = itemView.findViewById(R.id.contact_number_dial);
            contactName = itemView.findViewById(R.id.contact_name);
            contactDelete = itemView.findViewById(R.id.contact_delete);
            contactEdit = itemView.findViewById(R.id.contact_edit);
            relativeLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
