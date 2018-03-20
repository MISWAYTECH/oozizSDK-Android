package com.miswaytech.miswaytranslation.demo;

import android.app.Application;

import com.miswaytech.miswaytranslation.misbtdevice.MisDevice;

import java.util.HashMap;

/**
 * Created by wang on 3/19/18.
 */

public class MisApplication extends Application {

    public static HashMap<String, MisDevice> hashMap = new HashMap<String, MisDevice>();

}
