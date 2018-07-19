package example.com.androidfire2.ui.main.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.jaydenxiao.common.compressorutils.Compressor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import butterknife.Bind;
import example.com.androidfire2.BuildConfig;
import example.com.androidfire2.R;
import example.com.androidfire2.utils.BitmapUtils;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2018/6/26 0026.
 */

public class PicCompressionActivity extends BaseActivity {
    @Bind(R.id.compressionBtn)
    Button compressionBtn;
    @Bind(R.id.selectorPic)
    Button selectorPic;
    @Bind(R.id.takePhoto)
    Button takePhoto;
    @Bind(R.id.compressionBefore)
    PhotoView compressionBefore;
    @Bind(R.id.compressionAfter)
    PhotoView compressionAfter;
    private static final int COMPRESSION_CODE = 100;
    private String photoPath = "";

    private File outputImage;
    public static final int TAKE_PHOTO = 200;
    public static final File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);//获得外部存储器的第一层的文件对象
    public static final String JPG_SUFFIX = ".jpg";
    private String picName;
    private Uri imageUri;

    @Override
    public int getLayoutId() {
        return R.layout.act_compression;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        initListener();
    }

    private void initListener() {
        compressionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!photoPath.isEmpty()) {
                    File before = new File(photoPath);

                    Date date = new Date();
                    String strDate = date.toString();//返回日期的格式化字符串
                    LogUtils.loge("time  "+date.getTime()+"    "+date.toLocaleString()+"    "+strDate.substring(strDate.length()-4));

                    //如果压缩后的文件名已经存在是不会进行替换的（需要在压缩工具类的ImageUtil.generateFilePath中设置生成文件的命名方式才可以）
                    File compressedImage = new Compressor.Builder(PicCompressionActivity.this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(60)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .build()
                            .compressToFile(before);

                    Glide.with(PicCompressionActivity.this).load(compressedImage).into(compressionAfter);
                } else {
                    ToastUitl.showShort("请先选择需要压缩的图片");
                }

            }
        });

        selectorPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicture();//打开相册
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();//打开相机
            }
        });

    }

    //相机拍照
    private void openCamera() {
        Date date = new Date(System.nanoTime());
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(date.toString().getBytes());
            picName = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            picName = date.toString();
        }
        File storageFile = new File(PATH.getAbsoluteFile() + File.separator + "compressionPic");
        if (!storageFile.isDirectory()) {//创建目录
            storageFile.mkdirs();
        }
        outputImage = new File(storageFile, picName + JPG_SUFFIX);
        try {
            outputImage.createNewFile();//createNewFile()是创建一个不存在的文件。
        } catch (IOException e) {
            LogUtils.loge(e.toString());
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        //todo 添加版本判断7.0  URL暴露的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 将文件转换成content://Uri的形式
            imageUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".fileprovider", outputImage);
            // 申请临时访问权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else {
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            imageUri = Uri.fromFile(outputImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        startActivityForResult(intent, TAKE_PHOTO);
    }

    //调用相册
    public void openPicture() {
        //Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"

        //为了更好的适应版本,4.4前后获取的路径不同
        Intent innerIntent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            //innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            innerIntent.setAction(Intent.ACTION_PICK);
        }

        innerIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //弹出的对话框
        Intent wrapperIntent = Intent.createChooser(innerIntent,
                "选择二维码图片");
        this.startActivityForResult(wrapperIntent, COMPRESSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ProgressDialog progressDialog;
            switch (requestCode) {
                case COMPRESSION_CODE:
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(
                            data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        photoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.e("photoPath1", photoPath);
                    }
                    cursor.close();

                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("正在扫描...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    //第一种
                    Glide.with(this).load(photoPath).into(compressionBefore);

                    //第二种 自己写的（打开相册可以，下面拍照就不行，需要改进）
//                    Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
//                    if (bitmap != null) {
//                        compressionBefore.setImageBitmap(bitmap);
//                    }else {
//                        ToastUitl.showShort("打开照片失败！");
//                    }
                    progressDialog.dismiss();
                    break;

                case TAKE_PHOTO:
                    //扫描指定文件(通知系统刷新相册)
                    Intent intentBc1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc1.setData(imageUri);
                    this.sendBroadcast(intentBc1);

                    photoPath = outputImage.getAbsolutePath();
                    Log.e("photoPath2", photoPath);//  /storage/emulated/0/DCIM/compressionPic/65591d1321bfbe6ccc57d577d5f76ecd.jpg
//                    photoPath = imageUri.toString();
//                    Log.e("photoPath3", photoPath);//  file:///storage/emulated/0/DCIM/compressionPic/65591d1321bfbe6ccc57d577d5f76ecd.jpg

                    //第一种（快捷）
                    Glide.with(this).load(photoPath).into(compressionBefore);

                    //第二种
//                    Bitmap bitmap = BitmapUtils.decodeUri(this, imageUri, 800, 800);
//                    if (bitmap != null) {
//                        compressionBefore.setImageBitmap(bitmap);
//                    } else {
//                        ToastUitl.showShort("拍照失败！");
//                    }

                    //第三种 自己写的（根据bitmap获取,可能需要异步，不然获取不到，在相册中就可以） Android图片加载解析之Bitmap  https://www.jianshu.com/p/e00dce838fb2
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap bitmap1 = BitmapFactory.decodeFile(photoPath);
//                            if (bitmap1 != null) {
//                                compressionBefore.setImageBitmap(bitmap1);
//                            }else {
//                                ToastUitl.showShort("拍照失败！");
//                            }
//                        }
//                    }).start();

                    break;
                default:
                    break;
            }
        }

    }

}
