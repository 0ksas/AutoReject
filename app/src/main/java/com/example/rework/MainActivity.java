package com.example.rework;

import android.Manifest;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    static public SaveClass mainList;
    private static final int REQUEST_ID = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ID) {
            if(resultCode == android.app.Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Activity result method is activates", Toast.LENGTH_LONG);
            } else {
                Toast.makeText(getApplicationContext(), "Activity result method is activates", Toast.LENGTH_SHORT);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
        Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING);
        startActivityForResult(intent, REQUEST_ID);
        
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        } else {
            // do nothing
        }

        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // We do not have this permission. Let's ask the user
        }
        mainList = readList(getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Defining all items
        FloatingActionButton fab = findViewById(R.id.fab);
        final TextView numberInput = findViewById(R.id.numberInput);
        final ListView numberList = findViewById(R.id.numberList);
        final ListView greenList = findViewById(R.id.greenList);

        final ArrayAdapter<String> blackListAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mainList.numberList);
        final ArrayAdapter<String> greenListAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, mainList.greenList);

        numberList.setAdapter(blackListAdapter);
        greenList.setAdapter(greenListAdapter);
        blackListAdapter.notifyDataSetChanged();

        final Switch greenListSwitch = findViewById(R.id.greenListSwitch);
        greenListSwitch.setChecked(mainList.isGreenListActive);

        final Switch blackListSwitch = findViewById(R.id.blackListSwitch);
        blackListSwitch.setChecked(mainList.isBlackListActive);
        //


        //Actions

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainList.greenList.contains(numberInput.getText().toString())) {
                    Snackbar.make(view, "Number is already in the green list.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if(numberInput.getText().toString().length() > 0) {
                    Snackbar.make(view, "Number added to auto reject list", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    mainList.addItem(numberInput.getText().toString());
                    blackListAdapter.notifyDataSetChanged();
                    saveList(mainList, getApplicationContext());
                }
                numberInput.setText("");
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mainList.greenList.contains(numberInput.getText().toString())) {
                    Snackbar.make(view, "Number is already in the white list.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return false;
                }
                if(numberInput.getText().toString().length() > 0) {
                    Snackbar.make(view, "Number added to white list", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    mainList.addGreenList(numberInput.getText().toString());
                    greenListAdapter.notifyDataSetChanged();
                    saveList(mainList, getApplicationContext());
                    numberInput.setText("");
                    return true;
                }
                return false;
            }
        });

        numberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                mainList.numberList.remove(position);
                blackListAdapter.notifyDataSetChanged();
                saveList(mainList, getApplicationContext());
            }
        });

        greenList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                mainList.greenList.remove(position);
                greenListAdapter.notifyDataSetChanged();
                saveList(mainList, getApplicationContext());
            }
        });

        greenListSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainList.isGreenListActive = isChecked;
                //Toast.makeText(getApplicationContext(), isChecked + "", Toast.LENGTH_SHORT).show();
            }
        });

        blackListSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainList.isBlackListActive = isChecked;
                //Toast.makeText(getApplicationContext(), isChecked + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean containsList(String number, Context context) {
        if(mainList == null) mainList = readList(context);
        for(String temp : mainList.numberList) {
            if(number.startsWith(temp)) return true;
        }

        return false;
    }

    public static boolean containsGreenList(String number, Context context) {
        if(mainList == null) mainList = readList(context);
        for(String temp : mainList.greenList) {
            if(number.startsWith(temp)) return true;
        }
        return false;
    }

    public static boolean isGreenListActive(Context context) {
        if(mainList == null) mainList = readList(context);
        try {
            return mainList.isGreenListActive;
        } catch(Exception e) {
            return false;
        }
    }

    public static boolean isBlackListActive(Context context) {
        if(mainList == null) mainList = readList(context);
        try {
            return mainList.isBlackListActive;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    public static SaveClass readList(Context context) {
        SaveClass saveClass;
        try(FileInputStream inputStream = context.openFileInput("list.bin")) {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            saveClass = (SaveClass) objectInputStream.readObject();
            objectInputStream.close();
            return saveClass;
        } catch(IOException e) {
            System.out.println("FAILED file input");
            System.out.println(e.getStackTrace());
            //Put in some warning
        } catch(ClassNotFoundException e) {
            System.out.println("FAILED class input");
            System.out.println(e.getStackTrace());
        } catch(Exception e) {
            System.out.println("FAILED other exception");
            System.out.println(e.getStackTrace());
        }
        return new SaveClass();
    }

    public static void saveList(SaveClass saveClass, Context context) {
        //ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("list.bin"))
        try(FileOutputStream outputStream = context.openFileOutput("list.bin", Context.MODE_PRIVATE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(saveClass);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch(IOException e) {
            System.out.println("failed output");
            //Put in some warning
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

