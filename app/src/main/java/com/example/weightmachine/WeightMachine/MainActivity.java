package com.example.weightmachine.WeightMachine;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.besthealth.bhBodyComposition120.BhBodyComposition;
import com.besthealth.bhBodyComposition120.BuildConfig;
import com.example.weightmachine.R;
import com.example.weightmachine.base.HintDataDialogFragment;
import com.example.weightmachine.base.ShowBleActivity;
import com.pingwang.bluetoothlib.AILinkBleManager;
import com.pingwang.bluetoothlib.AILinkSDK;
import com.pingwang.bluetoothlib.bean.AilinkLicenseBean;
import com.pingwang.bluetoothlib.server.ELinkBleServer;
import com.pingwang.bluetoothlib.utils.BleLog;

import java.util.ArrayList;
import java.util.List;

import utils.AppStart;
import utils.L;
import utils.SP;


public class MainActivity extends AppCompatActivity {


    private static String TAG = MainActivity.class.getName();
    private List<View> mList = new ArrayList<>();
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ELinkBleServer mBluetoothService;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
       initData();
        initListener();
        int bodyComposition = new BhBodyComposition().getBodyComposition();
        L.i("bodyComposition:" + bodyComposition);

    }


    protected void initListener() {
        String version = getString(R.string.version) + ":" + BuildConfig.VERSION_NAME;
       // ((TextView) findViewById(R.id.tv_app_version)).setText(version);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME);
        findViewById(R.id.bt_fetch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBroadcast = new Intent(MainActivity.this, BroadcastScaleActivity.class);
                startActivity(intentBroadcast);
            }
        });



    }

    protected void initData() {
        initPermissions();
        List<AilinkLicenseBean> list = new ArrayList<>();
        list.add(new AilinkLicenseBean(0x2F, 0x03, 0x09));
        AILinkSDK.getInstance().initLicense(list);
    }

    protected void initView() {
        BleLog.init(true);
        //connectDevice(BleValueBean bleValueBean);连接的时候需要传广播对象,否则返回的cid,vid,pid始终都是0
        //sdk
        AILinkSDK.getInstance().init(mContext);
        AILinkBleManager.getInstance().init(mContext, new AILinkBleManager.onInitListener() {
            @Override
            public void onInitSuccess() {

                AILinkBleManager.getInstance().startScan(0);


            }

            @Override
            public void onInitFailure() {

            }
        });
        SP.init(this);
    }






    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleLog.quit();
    }


    //-----------------------权限----------------------------------------
    /**
     * 需要申请的权限
     */
    private String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static String[] BLUETOOTH_PERMISSION = new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT};
    /**
     * 权限请求返回
     */
    private final int PERMISSION = 101;
    /**
     * 定位服务返回
     */
    protected final int LOCATION_SERVER = 102;
    private HintDataDialogFragment mHintDataDialog = null;

    protected void initPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onPermissionsOk();
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, PERMISSION);
            } else {
                boolean bleStatus = AppStart.isLocServiceEnable(mContext);
                if (!bleStatus) {
                    //没有开启定位服务
                    mHintDataDialog = HintDataDialogFragment.newInstance()
                                                            .setTitle("提示", 0)
                                                            .setCancel("取消", 0)
                                                            .setOk("确定", 0)
                                                            .setContent("请求开启定位服务", true)
                                                            .setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                                                                @Override
                                                                public void onSucceedListener(View v) {
                                                                    startLocationActivity();
                                                                }
                                                            });
                    mHintDataDialog.show(getSupportFragmentManager());


                } else {
                    onPermissionsOk();
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, BLUETOOTH_PERMISSION[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, BLUETOOTH_PERMISSION, PERMISSION);
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //请求权限被拒绝
        if (requestCode != PERMISSION)
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initPermissions();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION[0])) {
                    //权限请求失败，但未选中“不再提示”选项,再次请求
                    ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, PERMISSION);
                } else {
                    //权限请求失败，选中“不再提示”选项
                    mHintDataDialog = HintDataDialogFragment.newInstance()
                                                            .setTitle("提示", 0)
                                                            .setCancel("取消", 0)
                                                            .setOk("确定", 0)
                                                            .setContent("请求开启定位权限", true)
                                                            .setOnDialogListener(new HintDataDialogFragment.onDialogListener() {
                                                                @Override
                                                                public void onSucceedListener(View v) {
                                                                    AppStart.startUseSetActivity(mContext);
                                                                }
                                                            });
                    mHintDataDialog.show(getSupportFragmentManager());

                }

            }
        } else {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initPermissions();
            }
        }


    }


    /**
     * 启动去设置定位服务
     */
    protected void startLocationActivity() {

        AppStart.startLocationActivity(this, LOCATION_SERVER);

    }


    /**
     * 权限ok
     */
    protected void onPermissionsOk() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SERVER) {
            //定位服务页面返回
            initPermissions();
        }
    }

}
