package com.xsf.zxing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.xsf.zxing.R.id.webview;

/**
 * Created by asus-pc on 2017/10/10.
 */

public class WebResultActivity extends Activity{
    private WebView webView;
    private TextView scanResult;
    private ProgressBar pg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_result);

        webView = (WebView) findViewById(R.id.webview);
        scanResult = (TextView) findViewById(R.id.scanResult);
        pg1=(ProgressBar) findViewById(R.id.progressBar1);
        Bundle extras = getIntent().getExtras();

        if (null != extras) {
            String result = extras.getString("result");
            Toast.makeText(this,result,Toast.LENGTH_LONG).show();
            if (result.contains("http")) {
                scanResult.setVisibility(View.GONE);

                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setSupportMultipleWindows(true);
                webView.getSettings().setDomStorageEnabled(true);
//                webView.setWebViewClient(new WebViewClient());//防止有的网页重定向跳转到浏览器(重定向就是，在网页上设置一个约束条件，条件满足，就自动转入到其它网页、网址)
//                webView.setWebChromeClient(new WebChromeClient());
                initProgress();

                webView.loadUrl(result);
            }else {
                scanResult.setVisibility(View.VISIBLE);
                scanResult.setText(result);
            }

        }
    }

    private void initProgress() {
        webView.setWebViewClient(new WebViewClient(){
            //覆写shouldOverrideUrlLoading实现内部显示网页
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO 自动生成的方法存根
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if(newProgress==100){
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }
        });

    }
}
