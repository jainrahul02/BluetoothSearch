package com.example.bluetoothsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.net.Inet4Address;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

        TextView statusTextView;
        Button searchButton;
        ListView listView;
        BluetoothAdapter bluetoothAdapter;
        ArrayList<String> bluetoothDevices=new ArrayList<>();
        ArrayList<String> addresses=new ArrayList<>();
        ArrayAdapter arrayAdapter;


        private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("Action",action);
                //if searching is finished, make the buttton enabled and display finished msg
                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                    statusTextView.setText("Finished");
                    searchButton.setEnabled(true);
                    //this will allow us to search for nearby bluetooth devices

                } else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    String address = device.getAddress();
                    //signal strength
                    String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                    //Log.i("Device found","name: "+ name + "address: "+ address + "rssi: "+ rssi);
                    if(!addresses.contains(address)) {
                        addresses.add(address);
                        String deviceName="";
                        if(name==null || name.equals("")){
                            deviceName=address+"- RSSI "+ rssi +"dBm";
                        }else {
                            deviceName = name + "- RSSI " + rssi + "dBm";
                        }
                            bluetoothDevices.add(deviceName);
                        arrayAdapter.notifyDataSetChanged();
                    }

                }

            }
        };
    public void searchClicked(View view){
        statusTextView.setText("Searching for a device...");
        searchButton.setEnabled(false);
        //clear the list of previous devices before searching new devices
        bluetoothDevices.clear();
        addresses.clear();
        bluetoothAdapter.startDiscovery();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =findViewById(R.id.listView);
        statusTextView =findViewById(R.id.statusTextView);
        searchButton =findViewById(R.id.searchButton);

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,bluetoothDevices);
        listView.setAdapter(arrayAdapter);

        //give bluetooth & admin permission in androidManifest
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //An IntentFilter can match against actions, categories, and data in an Intent. It also includes a "priority" value which is used to order multiple matching filters.
        IntentFilter intentFilter= new IntentFilter();
         intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
         //to know when a bluetooth device is found
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      //we need to register to allow the intent to work
        registerReceiver(broadcastReceiver,intentFilter);




    }
}