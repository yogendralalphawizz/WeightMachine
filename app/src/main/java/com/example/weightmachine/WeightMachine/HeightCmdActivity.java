package com.example.weightmachine.WeightMachine;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.weightmachine.R;
import com.example.weightmachine.base.BleBaseActivity;
import com.example.weightmachine.base.BleNewBaseActivity;
import com.holtek.libHTBodyfat.HTBodyBasicInfo;
import com.holtek.libHTBodyfat.HTBodyResultAllBody;
import com.pingwang.bluetoothlib.AILinkBleManager;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleBroadcastDataListener;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import cn.net.aicare.algorithmutil.AlgorithmUtil;
import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleBleConfig;
import cn.net.aicare.modulelibrary.module.BroadcastScale.BroadcastScaleDeviceData;
import cn.net.aicare.modulelibrary.module.babyscale.BabyBleConfig;
import cn.net.aicare.modulelibrary.module.height.HeightDeviceData;
import cn.net.aicare.modulelibrary.module.utils.AicareBleConfig;
import utils.TimeUtils;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.device.SendBleBean;
import com.pingwang.bluetoothlib.device.SendMcuBean;
import com.pingwang.bluetoothlib.listener.CallbackDisIm;
import com.pingwang.bluetoothlib.listener.OnBleCompanyListener;
import com.pingwang.bluetoothlib.listener.OnBleVersionListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
import com.pingwang.bluetoothlib.listener.OnMcuParameterListener;
import com.pingwang.bluetoothlib.utils.BleDensityUtil;
import com.pingwang.bluetoothlib.utils.BleLog;
import com.pingwang.bluetoothlib.utils.BleStrUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.net.aicare.modulelibrary.module.height.HeightDeviceData;


/**
 * xing<br>
 * 2019/4/25<br>
 * 显示数据
 */
public class HeightCmdActivity extends BleBaseActivity implements OnCallbackDis,
        HeightDeviceData.onNotifyData, OnBleVersionListener, OnMcuParameterListener, OnBleCompanyListener, View.OnClickListener {

    private static String TAG = HeightCmdActivity.class.getName();
    private final int REFRESH_DATA = 3;
    private List<String> mList;
    private ArrayAdapter listAdapter;

    /**
     * 服务Intent
     */
    private Context mContext;
    private EditText et_type;
    private HeightDeviceData mBleDevice;
    private String mAddress;
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case REFRESH_DATA:
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);
        mContext = this;
        mAddress = getIntent().getStringExtra("mac");
        type = getIntent().getIntExtra("type", -1);
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);



       findViewById(R.id.clear).setOnClickListener(this);
       findViewById(R.id.btn1).setOnClickListener(this);
       findViewById(R.id.btnVersion).setOnClickListener(this);
       findViewById(R.id.btnBattery).setOnClickListener(this);
      findViewById(R.id.btn_get_did).setOnClickListener(this);
        et_type = findViewById(R.id.et_type);

        cmdBtn();
    }


    private void cmdBtn() {
        EditText et_unit_weight = findViewById(R.id.et_unit_weight);
        EditText et_unit_height = findViewById(R.id.et_unit_height);
        Button btn_set_unit = findViewById(R.id.btn_set_unit);
        btn_set_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightStr = et_unit_weight.getText().toString();
                String heightStr = et_unit_height.getText().toString();
                if (!weightStr.equals("") && !heightStr.equals("")) {
                    int weight = Integer.valueOf(weightStr);
                    int height = Integer.valueOf(heightStr);
                    mBleDevice.setUnit((byte) height, (byte) weight);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        SendBleBean sendBleBean = new SendBleBean();
        int viewId = v.getId(); // Get the view ID only once to avoid repeated calls

        if (viewId == R.id.btnVersion) {
            sendBleBean.setHex(mBleSendCmdUtil.getBleVersion());
            mBleDevice.sendData(sendBleBean);
        } else if (viewId == R.id.btnBattery) {
            sendBleBean.setHex(mBleSendCmdUtil.getMcuBatteryStatus());
            mBleDevice.sendData(sendBleBean);
        } else if (viewId == R.id.btn_get_did) {
            sendBleBean.setHex(mBleSendCmdUtil.getDid());
            mBleDevice.sendData(sendBleBean);
        } else if (viewId == R.id.btn1) {
            String cmd = et_type.getText().toString().trim();
            SendMcuBean sendDataBean = new SendMcuBean();
            sendDataBean.setHex(type, cmd.getBytes());
            mBleDevice.sendData(sendDataBean);
        } else if (viewId == R.id.clear) {
            if (mList != null)
                mList.clear();
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }
    }

    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "服务与界面建立连接成功");
        CallbackDisIm.getInstance().addListListener(this);
        if (mBluetoothService != null) {
            BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);
            if (bleDevice != null) {
                mBleDevice = HeightDeviceData.getInstance(bleDevice);
                mBleDevice.setOnNotifyData(HeightCmdActivity.this);
                mBleDevice.setOnBleVersionListener(HeightCmdActivity.this);
                mBleDevice.setOnMcuParameterListener(HeightCmdActivity.this);
                mBleDevice.setOnCompanyListener(HeightCmdActivity.this);
            }
        }
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "服务与界面连接断开");
        //与服务断开连接
        mBluetoothService = null;
    }


    @Override
    public void unbindServices() {
        CallbackDisIm.getInstance().removeListener(this);
        if (mBleDevice!=null) {
            mBleDevice.disconnect();
            mBleDevice.clear();
            mBleDevice=null;
        }
    }


    //-----------------状态-------------------


    @Override
    public void onConnecting(@NonNull String mac) {
        //TODO 连接中
        BleLog.i(TAG, "连接中");
    }

    @Override
    public void onDisConnected(@NonNull String mac, int code) {
        //TODO 连接断开
        BleLog.i(TAG, "连接断开");
        finish();
    }

    @Override
    public void onServicesDiscovered(@NonNull String mac) {
        //TODO 连接成功(获取服务成功)
        BleLog.i(TAG, "连接成功(获取服务成功)");
    }


    @Override
    public void bleOpen() {

    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        finish();
    }

    //-----------------通知-------------------

    @Override
    public void onData(byte[] status,int type) {
        String data = "";
        if (status != null)
            data = BleStrUtils.byte2HexStr(status);
        if (type == 100) {
            mList.add(TimeUtils.getTime() + "send->" + data);
        } else {
            mList.add(TimeUtils.getTime() + "notify->" + data);
        }
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void height(int height, int decimalHeight, byte heightUnit, int weight,
                       int decimalWeight, byte weightUnit) {
        String heightStr= BleDensityUtil.getInstance().holdDecimals(height,decimalHeight);
        String weightStr= BleDensityUtil.getInstance().holdDecimals(weight,decimalWeight);
        mList.add(TimeUtils.getTime() + "身高:" + heightStr + "|" + heightUnit + "体重:" + weightStr + "|" + weightUnit);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }



    @Override
    public void getUnit(byte status) {
        mList.add(TimeUtils.getTime() + "单位结果:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void getErr(byte status) {
        mList.add(TimeUtils.getTime() + "错误:" + status);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onBmVersion(String version) {
        mList.add(TimeUtils.getTime() + "版本号:" + version);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        mList.add(TimeUtils.getTime() + "ID:" + didStr);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onMcuBatteryStatus(int status, int battery) {
        mList.add(TimeUtils.getTime() + "电量:" + battery + "%");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void onSysTime(int status, int[] times) {
        String time =
                times[0] + "-" + times[1] + "-" + times[2] + "  " + times[3] + ":" + times[4] +
                        ":" + times[5];
        mList.add(TimeUtils.getTime() + "系统时间:" + time);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }
}
