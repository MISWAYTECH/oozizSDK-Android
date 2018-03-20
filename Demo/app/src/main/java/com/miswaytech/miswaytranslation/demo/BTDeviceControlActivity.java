package com.miswaytech.miswaytranslation.demo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.miswaytech.miswaytranslation.misbtdevice.DeviceListener;
import com.miswaytech.miswaytranslation.misbtdevice.MisDevice;

/**
 * Created by wang on 3/19/18.
 */

public class BTDeviceControlActivity extends Activity implements View.OnClickListener,
        DeviceListener {

    private TextView mDataField;
    private TextView electricity;
    private TextView humidity;
    private TextView temperature;
    private TextView connection_state;
    private TextView txt_rssi;
    private String tag;
    private Button btn_alarm, btn_mute, btn_close, btn_rssi;

    private MisDevice swDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        final Intent intent = getIntent();
        tag = intent.getStringExtra("tag");
        swDevice = MisApplication.hashMap.get(tag);
        swDevice.setDeviceListener(this);
        ((TextView) findViewById(R.id.device_address)).setText(tag);
        mDataField = (TextView) findViewById(R.id.data_value);
        electricity = (TextView) findViewById(R.id.electricity);
        humidity = (TextView) findViewById(R.id.humidity);
        temperature = (TextView) findViewById(R.id.temperature);
        connection_state = (TextView) findViewById(R.id.connection_state);
        txt_rssi = (TextView) findViewById(R.id.txt_rssi);
        btn_alarm = (Button) findViewById(R.id.alarm);
        btn_mute = (Button) findViewById(R.id.mute);
        btn_close = (Button) findViewById(R.id.close);
        btn_rssi = (Button) findViewById(R.id.btn_rssi);
        btn_rssi.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
        btn_mute.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        getActionBar().setTitle(swDevice.getBluetoothDevice().getName());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        connection_state.setText("connected");
        swDevice.readRssi();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.alarm:
                swDevice.startRing();
                break;
            case R.id.mute:
                swDevice.stopRing();
                break;
            case R.id.close:
                swDevice.closeBluetoothDevice();
                BTDeviceControlActivity.this.finish();
                break;
            case R.id.btn_rssi:
                swDevice.readRssi();
                break;
        }

    }

    @Override
    public void onConnected(String tag, BluetoothDevice device) {
        if (tag.equals(swDevice.getTag())) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connection_state.setText("connected");
                }
            });

        }

    }

    @Override
    public void onDisconnected(String tag, BluetoothDevice device) {
        if (tag.equals(swDevice.getTag())) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            swDevice.connect();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    connection_state.setText("disconnected");
                }
            });
        }

    }

    @Override
    public void onGetRssi(String tag, int rssi, BluetoothDevice device) {
        final int rssi2 = rssi;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt_rssi.setText(rssi2+"");
            }
        });

    }

    @Override
    public void onGetValue(String tag, byte[] value, BluetoothDevice device) {
        final byte[] data = value;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                switch (data[0]) {
                    case 7:
                        electricity.setText(data[1] + "%");
                        break;
                    case 8:
                        temperature.setText(data[1] + "." + data[2] + "��");
                        break;
                    case 9:
                        humidity.setText(data[1] + "." + data[2] + "%");
                        break;
                    case -86:
                        if (data[1] == 1) {
                            mDataField.setText("doubleclick=" + data[1]);
                        } else if (data[1] == 2) {
                            mDataField.setText("long_press=" + data[1]);
                        } else if (data[1] == 3) {
                            mDataField.setText("click=" + data[1]);
                        } else if (data[1] == 4) {
                            mDataField.setText("secondary_alarm=" + data[1]);
                        } else if (data[1] == 11) {
                            mDataField.setText("app_mute=" + data[1]);
                        }
                }
            }
        });

    }

    @Override
    public void onWriteSuccess(String tag, byte[] value, BluetoothDevice device) {
        // TODO Auto-generated method stub

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        swDevice.remove();
    }

}
