package example.com.androidfire2.ui.news.activty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.zxing.activity.CaptureActivity;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.xsf.zxing.ScanMainActivity;

import butterknife.OnClick;
import example.com.androidfire2.R;

/**
 * Created by asus-pc on 2017/10/11.
 */

public class SaoSaoActivityTest extends BaseActivity {
    private static final int REQ_CODE_PERMISSION = 0x1111;
    public static final int REQ_CODE = 0xF0F0;

    @Override
    public int getLayoutId() {
        return R.layout.act_saosao;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.iv_saosao,R.id.btn_generate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_saosao:
                //检测是否拥有权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 申请授权
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    startCaptureActivityForResult();
                }
                break;
            case R.id.btn_generate:
                Intent intent = new Intent(this, ScanMainActivity.class);
                startActivity(intent);
            default:
                break;
        }

    }

    //获取扫描结果
    private void startCaptureActivityForResult() {
        Intent intent = new Intent(this, CaptureActivity.class);
        this.startActivityForResult(intent, REQ_CODE);
    }

    /**
     * 请求权限的回调
     *
     * 参数1：requestCode-->是requestPermissions()方法传递过来的请求码。
     * 参数2：permissions-->是requestPermissions()方法传递过来的需要申请权限
     * 参数3：grantResults-->是申请权限后，系统返回的结果，PackageManager.PERMISSION_GRANTED表示授权成功，PackageManager.PERMISSION_DENIED表示授权失败。
     * grantResults和permissions是一一对应的
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    ToastUitl.showShort("You must agree the camera permission request before you use the code scan function");
                }
            break;
        }
    }

    //图片解析成功回调的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        ToastUitl.showShort(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            ToastUitl.showShort(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                }
                break;
        }
    }
}
