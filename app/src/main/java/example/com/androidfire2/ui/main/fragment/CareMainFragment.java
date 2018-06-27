package example.com.androidfire2.ui.main.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.jaydenxiao.common.base.BaseFragment;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import example.com.androidfire2.R;
import example.com.androidfire2.ui.main.activity.ContactsProviderActivity;
import example.com.androidfire2.ui.main.activity.PicCompressionActivity;
import example.com.androidfire2.ui.news.activty.SaoSaoActivityTest;


/**
 * Created by asus-pc on 2017/7/20.
 */

public class CareMainFragment extends BaseFragment {

    @Bind(R.id.tv)
    TextView tv;

    private String mkdir;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_care;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.btn_saosao,R.id.log_save,R.id.start_other_app_service,R.id.share,R.id.compression,R.id.contentProvider})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_saosao:
                Intent intent = new Intent(getActivity(), SaoSaoActivityTest.class);
                startActivity(intent);
                break;

            case R.id.log_save:

                //openFileOutput()来读写应用在内部存储空间上的文件；getFilesDir()获取你app的内部存储空间，相当于你的应用在内部存储上的根目录
//                File file = new File(AppApplication.getAppContext().getFilesDir(), "zhangjialin");
//                try {
//                    FileOutputStream outStream = getActivity().openFileOutput("a.text", Context.MODE_PRIVATE);
//                    outStream.write(Environment.getExternalStorageDirectory().toString().getBytes());
//                    outStream.close();
//                } catch (FileNotFoundException e) {
//                    return;
//                } catch (IOException e) {
//                    return;
//                }

                //在手机sdcard路径下新建名为 zhangjialin 的文件夹
                newPackage();
                tv.setText(mkdir);
                File file = new File(mkdir+"/1.txt");
                try {
                    file.createNewFile();//createNewFile 创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start_other_app_service:
                //需要用户允许 程序关联启动
                Intent i = new Intent();
                i.setComponent(new ComponentName("example.com.aaaa","example.com.aaaa.service.PushService"));
                getActivity().startService(i);
                break;
            case R.id.share:
//                OnekeyShare share = new OnekeyShare();
//                share.show(getActivity());
                break;
            case R.id.compression:
                Intent intent1 = new Intent(getActivity(), PicCompressionActivity.class);
                startActivity(intent1);
                break;
            case R.id.contentProvider:
                Intent intent2 = new Intent(getActivity(), ContactsProviderActivity.class);
                startActivity(intent2);
                break;
            default:
                break;

        }

    }

    //在手机sdcard路径下新建名为 zhangjialin 的文件夹
    public void newPackage(){
        mkdir = Environment.getExternalStorageDirectory()+"/zhangjialin/log";
        File file = new File(mkdir);
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        if(file.exists()){
            build.setMessage("文件夹已存在").show();
        }else{
            file.mkdirs();//mkdirs可以建立多级文件夹
            build.setMessage("新建成功").show();
        }
    }

    //获取sdcard路径
    public void getDIR(View v){
        String sdpath = Environment.getExternalStorageDirectory().toString();
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setMessage(sdpath).show();
    }

}
