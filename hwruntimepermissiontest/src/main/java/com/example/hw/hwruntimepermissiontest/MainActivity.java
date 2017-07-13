package com.example.hw.hwruntimepermissiontest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {//先判断是否有权限，若无就申请权限
                    //当用户之前已多次拒绝，但未勾选“不再提醒”的提示情况下，就需要向用户说明为啥你需要这个权限，
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)) {
                        showRequestPermissionDialog();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);//1:请求码，唯一
                    }

                } else {
                    call();
                }
            }
        });
    }

    private void showRequestPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("说明")
                .setMessage("  若不授予所需权限，会影响程序运行")
                .setCancelable(false)//不可撤销
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }
                }).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }

    private void call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:18855581039"));
        startActivity(intent);
    }


    //处理权限回调.  运行完方法requestPermissions后，结果都会回调到该方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//已申请到权限
                        call();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        //判断用户是否勾选了“不再询问”,若勾选了，则弹出对话框
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {

                            Log.d("MainActivity", "脚标：" + i);

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("帮助")
                                    .setMessage("  当前应用缺少必要权限，请点击“设置”-“权限”-打开所需权限")
                                    .setCancelable(false)
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                            Uri uri = Uri.fromParts("package", getPackageName(), null);

                                            intent.setData(uri);

                                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            builder.show();


                        }
                        //用户只拒绝但未勾选“不再询问”
                        Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            default:
                break;
        }
    }
}
