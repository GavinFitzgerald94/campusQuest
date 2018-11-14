package com.example.campusquest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class CharacterSheet extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private List<String> mAttributes;
    private Spinner mSpinnerAttributes;
    private String mSelectecAttribute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_sheet);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] arr =  {"Level", "Strength", "Endurance"};
        mAttributes = Arrays.asList(arr);

        mSpinnerAttributes = findViewById(R.id.spinner_attributes);
        buildSpinner();
        mSpinnerAttributes.setOnItemSelectedListener(this);

        DrawerUtil.getDrawer(this,toolbar);

    }

    private void buildSpinner() {
        ArrayAdapter adapterQuests = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mAttributes);
        adapterQuests.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAttributes.setAdapter(adapterQuests);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectecAttribute = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Auto generated stub.

    }
}
