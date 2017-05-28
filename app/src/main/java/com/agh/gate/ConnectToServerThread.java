package com.agh.gate;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Damian on 2017-04-16.
 */

public class ConnectToServerThread extends Thread {

    public CommsThread commsThread;
    public BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;

    public ConnectToServerThread(BluetoothDevice device, BluetoothAdapter btAdapter){
        BluetoothSocket tmp = null;
        bluetoothAdapter = btAdapter;
        // pobierz BluetoothSocket, aby polaczyc sie z danym urzadzeniem

        try
        {
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MainActivity.UUID));
        }
        catch (IOException e){
            Log.d("ConnectToServerTheard", e.getLocalizedMessage());
        }
        bluetoothSocket = tmp;
    }

    public void run(){
        //wylaczam wykrywanie, bo spowalnia polaczenie
        bluetoothAdapter.cancelDiscovery();

        try{
            //bedziemy laczyc sie poprzez gniazdo. to bedzie blokowac az do powodzenia lub
            // odrzucenia wyjatku

            bluetoothSocket.connect();

            commsThread = new CommsThread(bluetoothSocket);
            commsThread.start();

        }catch (IOException connectException){
            // w tym przypadku nie moza bylo sie polaczyc. zamykam gniazdo i wychodze
            try{
                bluetoothSocket.close();
            }catch (IOException closeException){
                Log.d("ConnectToServerTheard", closeException.getLocalizedMessage());
            }
            return;
        }
    }

    public void cancel(){
        try {
            bluetoothSocket.close();
            if(commsThread != null) commsThread.cancel();
        }
        catch (IOException e){
            Log.d("ConnectToServerTheard", e.getLocalizedMessage());
        }

    }
}
