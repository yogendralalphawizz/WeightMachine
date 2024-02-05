package com.example.weightmachine.WeightMachine;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ComponentActivity;


import com.example.weightmachine.R;
import com.example.weightmachine.base.BleBaseActivity;
import com.example.weightmachine.base.BleNewBaseActivity;
import com.example.weightmachine.base.ShowBleActivity;
import com.holtek.libHTBodyfat.HTBodyBasicInfo;
import com.holtek.libHTBodyfat.HTBodyResultAllBody;
import com.pingwang.bluetoothlib.AILinkBleManager;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.device.BleDevice;
import com.pingwang.bluetoothlib.device.BleSendCmdUtil;
import com.pingwang.bluetoothlib.listener.OnBleBroadcastDataListener;
import com.pingwang.bluetoothlib.listener.OnCallbackDis;
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


/**
 * xing<br>
 * 2020/08/10<br>
 * 广播秤
 */
public class BroadcastScaleActivity extends BleBaseActivity implements OnCallbackDis, BroadcastScaleDeviceData.onNotifyData, OnBleBroadcastDataListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, HeightDeviceData.onNotifyData{

    private static String TAG = BroadcastScaleActivity.class.getName();

    private final int REFRESH_DATA = 3;

    private RadioButton mRadioButtonKg, mRadioButtonLb, mRadioButtonLbLb, mRadioButtonG, mRadioButtonOz, mRadioButtonStLb, mRadioButtonJin;
    private TextView tv_broadcast_temp, tv_broadcast_mac, tv_broadcast_did, tv_broadcast_weight;

    private List<String> mList;
    private ArrayAdapter listAdapter;
    private Context mContext;
    private BroadcastScaleDeviceData mDevice;
    private HeightDeviceData  mDevice1;
    //private BleDevice bleDevice ;


    private String mAddress = "";
    private BleSendCmdUtil mBleSendCmdUtil;
    private int type = BroadcastScaleBleConfig.BROADCAST_SCALE;
    private int mWeightUnit = 0;
    private int mTemp;

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
        setContentView(R.layout.activity_broadcast_scale);
        mContext = this;
        //        mAddress = getIntent().getStringExtra("mac");
        mBleSendCmdUtil = BleSendCmdUtil.getInstance();
        init();
    }

    private void init() {
        mList = new ArrayList<>();
        ListView listView = findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(listAdapter);

        findViewById(R.id.clear).setOnClickListener(view -> {
            mList.clear();
            mHandler.sendEmptyMessage(REFRESH_DATA);
        });
        findViewById(R.id.open).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        // ((RadioGroup) findViewById(R.id.radio_weight)).setOnCheckedChangeListener(this);

        /*tv_broadcast_temp = findViewById(R.id.tv_broadcast_temp);

        mRadioButtonKg = findViewById(R.id.radio_weight_kg);
        mRadioButtonJin = findViewById(R.id.radio_weight_jin);
        mRadioButtonStLb = findViewById(R.id.radio_weight_st_lb);
        mRadioButtonLb = findViewById(R.id.radio_weight_lb);
        mRadioButtonOz = findViewById(R.id.radio_weight_oz);
        mRadioButtonG = findViewById(R.id.radio_weight_g);
        mRadioButtonLbLb = findViewById(R.id.radio_weight_lb_lb);


        tv_broadcast_did = findViewById(R.id.tv_broadcast_did);
*/
        tv_broadcast_mac = findViewById(R.id.tv_broadcast_mac);

        // tv_broadcast_weight = findViewById(R.id.tv_broadcast_weight);

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mWeightUnit = BabyBleConfig.BABY_KG;
        //if (checkedId == -1)

        // return;//不是人为点击不触发
       /*  switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_weight_kg:
                mWeightUnit = BabyBleConfig.BABY_KG;
               break;
           case R.id.radio_weight_jin:
                mWeightUnit = BabyBleConfig.BABY_FG;
                break;
            case R.id.radio_weight_lb:
                mWeightUnit = BabyBleConfig.BABY_LB;
                break;
            case R.id.radio_weight_oz:
                mWeightUnit = BabyBleConfig.BABY_OZ;
                break;
            case R.id.radio_weight_st_lb:
                mWeightUnit = BabyBleConfig.BABY_ST;
                break;
            case R.id.radio_weight_g:
                mWeightUnit = BabyBleConfig.BABY_G;
                break;
            case R.id.radio_weight_lb_lb:
                mWeightUnit = BabyBleConfig.BABY_LB_LB;*/
        //  break;

        //}
        //BleLog.i(TAG, "weightUnit:" + mWeightUnit);
    }


    private void showWeightUnit(int unit) {
        switch (unit) {
            case BabyBleConfig.BABY_KG:
                mRadioButtonKg.setChecked(true);
                break;
            case BabyBleConfig.BABY_FG:
                mRadioButtonJin.setChecked(true);
                break;
            case BabyBleConfig.BABY_LB:
                mRadioButtonLb.setChecked(true);
                break;
            case BabyBleConfig.BABY_OZ:
                mRadioButtonOz.setChecked(true);
                break;
            case BabyBleConfig.BABY_ST:
                mRadioButtonStLb.setChecked(true);
                break;
            case BabyBleConfig.BABY_G:
                mRadioButtonG.setChecked(true);
                break;
            case BabyBleConfig.BABY_LB_LB:
                mRadioButtonLbLb.setChecked(true);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        AILinkBleManager.getInstance().startScan(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
       /* switch (v.getId()) {
            case R.id.clear:
                if (mList != null)
                    mList.clear();
                mHandler.sendEmptyMessage(REFRESH_DATA);
                break;
            case R.id.open:
                AILinkBleManager.getInstance().startScan(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
                break;
            case R.id.stop:
                AILinkBleManager.getInstance().stopScan();
                break;
        }*/
    }


    //---------------------------------服务---------------------------------------------------


    @Override
    public void onServiceSuccess() {
        BleLog.i(TAG, "The connection between the service and the interface is successfully established.");
        //与服务建立连接
        mDevice = BroadcastScaleDeviceData.getInstance();

       // BleDevice bleDevice1 =  AILinkBleManager.getInstance().getBleDevice("01:BA:01:00:D0:5E");


      if(mBluetoothService!=null) {
       //
          BleDevice bleDevice = mBluetoothService.getBleDevice(mAddress);

          Log.e("macAddress:",String.valueOf(bleDevice));






        //  if(bleDevice!=null) {

              mDevice1 = HeightDeviceData.getInstance(bleDevice);

              mDevice1.setOnNotifyData(this);
       //   }

      }

       //  mBluetoothService;
        mDevice.setOnNotifyData(this);
        AILinkBleManager.getInstance().setOnBleBroadcastDataListener((OnBleBroadcastDataListener) this);
        AILinkBleManager.getInstance().startScan(0, BleConfig.UUID_SERVER_BROADCAST_AILINK);
    }

    @Override
    public void onServiceErr() {
        BleLog.i(TAG, "Service and interface disconnected");
        //与服务断开连接
    }

    @Override
    public void unbindServices() {
        if (mDevice != null||mDevice1!=null) {
            mDevice.clear();
            mDevice1.clear();
            mDevice = null;
            mDevice1=null;
        }

    }


    //-----------------状态-------------------


    @Override
    public void bleOpen() {
        mList.add(TimeUtils.getTime() + "\n" +
                "Bluetooth on");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void bleClose() {
        BleLog.i(TAG, "蓝牙未开启,可请求开启");
        mList.add(TimeUtils.getTime() + "Bluetooth off");
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    //-----------------通知-------------------

    private String mOldData = "";

    @Override
    public void onData(byte[] dataOriginal, byte[] hex, int type) {
        String data = "";
        if (hex != null)
            data = BleStrUtils.byte2HexStr(hex);
        if (mOldData.equals(data)) {
            return;
        }
        mOldData = data;
        mList.add(TimeUtils.getTime() + "Data ID" + type + " ,Decrypt data:" + data + " ,Raw data:" + BleStrUtils.byte2HexStr(dataOriginal));
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }


    @Override
    public void getWeightData(int status, int tempUnit, int weightUnit, int weightDecimal, int weightStatus, int weightNegative, int weight, int adc, int algorithmId, int tempNegative, int temp) {
        String weightUnitStr = "kg";
        switch (weightUnit) {
            case BroadcastScaleBleConfig.UNIT_KG:
                weightUnitStr = "kg";
                break;
            case BroadcastScaleBleConfig.UNIT_FG:
                weightUnitStr = "斤";
                break;
            case BroadcastScaleBleConfig.UNIT_LB:
                weightUnitStr = "lb:oz";
                break;
            case BroadcastScaleBleConfig.UNIT_OZ:
                weightUnitStr = "oz";
                break;
            case BroadcastScaleBleConfig.UNIT_ST:
                weightUnitStr = "st:lb";
                break;
            case BroadcastScaleBleConfig.UNIT_G:
                weightUnitStr = "g";
                break;
            case BroadcastScaleBleConfig.UNIT_LB_LB:
                weightUnitStr = "LB";
                break;
        }
        String tempUnitStr = "℃";//℉
        switch (tempUnit) {
            case BroadcastScaleBleConfig.UNIT_C:
                tempUnitStr = "℃";
                break;
            case BroadcastScaleBleConfig.UNIT_F:
                tempUnitStr = "℉";
                break;
        }
        String statusStr = "state=";
        switch (status) {

            case BroadcastScaleBleConfig.GET_WEIGHT_TESTING:
                statusStr += "Measuring weight";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_TESTING:
                statusStr += "Measuring impedance";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_SUCCESS:
                statusStr += "Impedance measurement successful";
                break;
            case BroadcastScaleBleConfig.GET_IMPEDANCE_FAIL:
                statusStr += "Impedance measurement failed";
                break;
            case BroadcastScaleBleConfig.GET_TEST_FINISH:
                statusStr += "Measurement completed";
                break;
            default:
                statusStr += Integer.toHexString(status);
                break;

        }
        //String weightStr = BleDensityUtil.getInstance().holdDecimals(weight, weightDecimal);
        String weightStr = BleDensityUtil.getInstance().holdDecimals(weight, weightDecimal);
        //String fat = BleDensityUtil.getInstance().holdDecimals(5, 2);
       // Log.i(TAG, "kskldsfhkfhsdfjdh");
        Log.i(TAG, String.valueOf(weight));
        Log.i(TAG, String.valueOf(weightDecimal));

        if (weightNegative == 1) {
            weightStr = "-" + weightStr;
        }
        String showData = TimeUtils.getTime();
        //showData += statusStr;
        if (weightStatus == 1) {
            showData += "\nStable weight=" + weightStr + /*";Decimal places=" + weightDecimal + ";unit=" + weightUnit + ";" +*/ weightUnitStr;
        } else {
            // tv_broadcast_weight.setText(weightStr);
            showData += "\nreal time weight=" + weightStr + /*";Decimal places=" + weightDecimal + ";unit=" + weightUnit + ";" +*/ weightUnitStr;
        }
        showData += "\nimpedance=" + adc;
        if (temp == 65535) {
            //不支持温度
            showData += "\nTemperature = not supported yet";
        } else {
            if (tempNegative == 1) {
                showData += "\ntemperature=" + (-temp / 10F) + tempUnitStr;
            } else {
                showData += "\ntemperature=" + (temp / 10F) + tempUnitStr;
            }
            if (mTemp != temp) {
                mTemp = temp;
                tv_broadcast_temp.setText((mTemp / 10F) + tempUnitStr);
            }
        }

        showData += "\nAlgorithm ID=" + algorithmId;

        if (mWeightUnit != weightUnit) {
            mWeightUnit = weightUnit;
            showWeightUnit(mWeightUnit);
        }


        mList.add(showData);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    @Override
    public void height(int height, int decimalHeight, byte heightUnit, int weight,
                       int decimalWeight, byte weightUnit) {
        String heightStr= BleDensityUtil.getInstance().holdDecimals(height,decimalHeight);
        String weightStr= BleDensityUtil.getInstance().holdDecimals(weight,decimalWeight);

        Log.i(TAG, "kskldsfhkfhsdfjdh");
      //  Log.i(TAG, String.valueOf(weight));
        Log.i(TAG, String.valueOf(heightStr));
        mList.add(TimeUtils.getTime() + "身高:" + heightStr + "|" + heightUnit + "体重:" + weightStr + "|" + weightUnit);
        mHandler.sendEmptyMessage(REFRESH_DATA);
    }

    ;


    @Override
    public void OnDID(int cid, int vid, int pid) {
        String didStr = "cid:" + cid + "||vid:" + vid + "||pid:" + pid;
        if (tv_broadcast_did != null) {
            tv_broadcast_did.setText(didStr);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
    }


    @Override
    public void onBleBroadcastData(BleValueBean bleValueBean, byte[] payload) {
        if (TextUtils.isEmpty(mAddress) && bleValueBean.isBroadcastModule()) {
            mAddress = bleValueBean.getMac();



            if (tv_broadcast_mac != null) {
                tv_broadcast_mac.setText(mAddress);
            }

        }
        //地址相同,并且是广播秤
        if (mAddress.equalsIgnoreCase(bleValueBean.getMac()) && bleValueBean.isBroadcastModule()) {


            Log.i(TAG, bleValueBean.getMac());
            byte[] manufacturerData = bleValueBean.getManufacturerData();
            int cid = bleValueBean.getCid();
            int vid = bleValueBean.getVid();
            int pid = bleValueBean.getPid();
            if (mDevice != null||mDevice1!=null) {
               // mDevice1.onNotifyData("",manufacturerData,cid);
                mDevice.onNotifyData("", manufacturerData, cid, vid, pid);

            }
        }
    }



}
