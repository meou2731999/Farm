package com.example.farm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.MQTTHelper;
import com.example.farm.MainActivity;
import com.example.farm.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class HomeFragment extends Fragment {

    public TextView txtTempValue, txtHumiValue;
    int temparature = 0;
    int humidity = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        txtTempValue = (TextView) view.findViewById(R.id.tempValue);
        txtHumiValue = (TextView) view.findViewById(R.id.humiValue);

        startMQTT();

        return view;
    }

    MQTTHelper mqttHelper;
    public void startMQTT(){
        mqttHelper = new MQTTHelper(getContext(),"Topic/TempHumi");
        mqttHelper.setCallback(new MqttCallbackExtended(){

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (topic.equals("Topic/TempHumi")) {
                    final String message = mqttMessage.toString().substring(1, mqttMessage.toString().length() - 1);
                    Log.w("Home", message);
                    JSONObject jsonObject = new JSONObject(message);
                    String device_id = jsonObject.getString("device_id");
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    temparature = Integer.parseInt(valuesArray.getString(0));
                    humidity = Integer.parseInt(valuesArray.getString(1));

                    txtHumiValue.setText(String.valueOf(humidity));
                    txtTempValue.setText(String.valueOf(temparature));

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
        });

    }
}