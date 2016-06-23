package com.example.testmap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by 1 on 2016/5/7.
 */
public class MyOrientionListener implements SensorEventListener {
    private SensorManager msensorManager;
    private Context mcontext;
    private Sensor msensor;
    private float lastX;

     public MyOrientionListener(Context context)
     {
         this.mcontext=context;
     }
    public void start()
    {
        msensorManager= (SensorManager) mcontext.getSystemService(Context.SENSOR_SERVICE);
        if(msensorManager!=null)
        {
           msensor= msensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if(msensor!=null)
            msensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_UI);
    }
    public void stop()
    {
        msensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)
        {
            float x=event.values[SensorManager.DATA_X];
            if(Math.abs(x-lastX)>1.0)
            {
                if(mOnOrientionListener!=null)
                {
                    mOnOrientionListener.onOrientionChanged(x);
                }
            }
            lastX=x;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private OnOrientionListener mOnOrientionListener;

    public void setOnOrientionListener(OnOrientionListener mOnOrientionListener) {
        this.mOnOrientionListener = mOnOrientionListener;
    }

    public interface OnOrientionListener
    {
        void onOrientionChanged(float x);


    }
}
