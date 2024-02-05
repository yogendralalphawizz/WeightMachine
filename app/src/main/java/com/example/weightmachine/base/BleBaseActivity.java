package com.example.weightmachine.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pingwang.bluetoothlib.AILinkBleManager;
import com.pingwang.bluetoothlib.bean.BleValueBean;
import com.pingwang.bluetoothlib.config.BleConfig;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleLog;

/*import aicare.net.cn.sdk.ailinksdkdemoandroid.BuildConfig;
import aicare.net.cn.sdk.ailinksdkdemoandroid.R;*/


/**
 * xing<br>
 * 2019/4/25<br>
 * 显示数据
 */
public abstract class BleBaseActivity extends AppCompatActivity {

    private static String TAG = BleBaseActivity.class.getName();

    protected AILinkBleManager mAILinkBleManager;
    protected ELinkBleServer mBluetoothService;
    private Intent bindIntent;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
       /* if (supportActionBar != null)
            supportActionBar.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);*/
        init();
    }

    private void init() {
        bindService();

    }

    /**
     * 搜索蓝牙(默认通过uuid过滤)
     * @param timeOut 超时,小于等于0代表永不超时
     */
    protected void startScanBle(long timeOut){
        if (AILinkBleManager.getInstance().isInitOk()){
            AILinkBleManager.getInstance().startScan(timeOut,BleConfig.UUID_SERVER_AILINK);
        }
    }

    /**
     * 主动停止搜索
     */
    protected void stopScanBle(){
        if (AILinkBleManager.getInstance().isInitOk()){
            AILinkBleManager.getInstance().stopScan();
        }
    }




    /**
     * 连接设备
     * @param bleValueBean 搜索到的地址
     */
    protected void connectBle(BleValueBean bleValueBean){
        if (AILinkBleManager.getInstance().isInitOk()){
            AILinkBleManager.getInstance().stopScan();
            AILinkBleManager.getInstance().connectDevice(bleValueBean);
        }
    }

    /**
     * 连接设备
     * @param mac 设备的地址
     */
    protected void connectBle(String mac){
        if (AILinkBleManager.getInstance().isInitOk()){
            AILinkBleManager.getInstance().stopScan();
            AILinkBleManager.getInstance().connectDevice(mac);
        }
    }

    //---------------------------------服务---------------------------------------------------

    private void bindService() {
        BleLog.i(TAG, "绑定服务");
        if (bindIntent == null) {
            bindIntent = new Intent(BleBaseActivity.this, ELinkBleServer.class);
//            if (mFhrSCon != null)
                bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
        }

        if (!AILinkBleManager.getInstance().isInitOk()) {
            AILinkBleManager.getInstance().init(this, new AILinkBleManager.onInitListener() {
                @Override
                public void onInitSuccess() {
                    mAILinkBleManager = AILinkBleManager.getInstance();
                    onServiceSuccess();

                }

                @Override
                public void onInitFailure() {
                    AILinkBleManager.getInstance().clear();
                    mAILinkBleManager = null;
                    onServiceErr();
                }
            });
        }else {
            mHandler.postDelayed(()->{
                mAILinkBleManager = AILinkBleManager.getInstance();
                onServiceSuccess();
            },500);
        }

    }
    private ServiceConnection mFhrSCon = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleLog.i(TAG, "服务与界面建立连接成功");
            //与服务建立连接
            mBluetoothService = ((ELinkBleServer.BluetoothBinder) service).getService();
            onServiceSuccess();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BleLog.e(TAG, "服务与界面连接断开");
            //与服务断开连接
            mBluetoothService = null;
            onServiceErr();
        }
    };
    private void unbindService() {
        unbindServices();
    }


    /**
     * 绑定服务成功
     */
    public abstract void onServiceSuccess();

    /**
     * 绑定服务失败
     */
    public abstract void onServiceErr();

    /**
     * 解绑服务,去掉与服务相关的操作,接口等
     */
    public abstract void unbindServices();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.i(TAG, "onDestroy");
        unbindService();
    }
}
