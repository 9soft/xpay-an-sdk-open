package com.xpay.demoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.xpay.sdk.XPay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientSDKActivity extends Activity implements OnClickListener {
    /**
     * 开发者需要填一个服务端URL 该URL是用来请求支付需要的charge。务必确保，URL能返回json格式的charge对象。
     */
    private static String YOUR_URL = "http://106.15.61.30:9080/payment/api_payment";
    public static final String CHARGE_URL = YOUR_URL;
    private Button aliPayButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_sdk);
        aliPayButton = (Button) findViewById(R.id.aliPayButton);
        aliPayButton.setOnClickListener(ClientSDKActivity.this);
    }

    @Override
    public void onClick(View view) {
        int amount = 1;
        if (view.getId() == R.id.aliPayButton) {
            new PaymentTask().execute(new PaymentRequest(amount));
        }
    }

    class PaymentTask extends AsyncTask<PaymentRequest, Void, String> {
        @Override
        protected void onPreExecute() {
            //按键点击之后的禁用，防止重复点击
            aliPayButton.setOnClickListener(null);
        }

        @Override
        protected String doInBackground(PaymentRequest... pr) {
            PaymentRequest paymentRequest = pr[0];
            String data = null;
            try {
                StringBuffer formStr = new StringBuffer();
                formStr.append("&amount=" + paymentRequest.amount).append("&subject=测试商品名称&body=测试商品信息描述&remark=Android移动支付测试");
                data = postForm(CHARGE_URL, formStr.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        /**
         * 获得服务端的charge，调用 sdk。
         */
        @Override
        protected void onPostExecute(String data) {
            if (null == data) {
                showMsg("请求出错", "请检查URL", "URL无法获取charge");
                return;
            }
            Log.d("charge", data);
            //参数一：Activity  当前调起支付的Activity
            //参数二：data  获取到的charge或order的JSON字符串
            XPay.createPayment(ClientSDKActivity.this, data);

        }

    }

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到支付平台服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        aliPayButton.setOnClickListener(ClientSDKActivity.this);
        //支付页面返回处理
        if (requestCode == XPay.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    public void showMsg(String title, String msg1, String msg2) {
        String str = title;
        if (null != msg1 && msg1.length() != 0) {
            str += "\n" + msg1;
        }
        if (null != msg2 && msg2.length() != 0) {
            str += "\n" + msg2;
        }
        AlertDialog.Builder builder = new Builder(ClientSDKActivity.this);
        builder.setMessage(str);
        builder.setTitle("提示");
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private static String postForm(String urlStr, String formParams) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        byte[] bytes = formParams.getBytes();
        conn.getOutputStream().write(bytes);// 输入参数

        if (conn.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
        return null;
    }

    class PaymentRequest {
        int amount;
        String subject;
        String body;
        String remark;

        public PaymentRequest(int amount) {
            this.amount = amount;
        }

        public PaymentRequest(int amount, String subject, String body, String remark) {
            this.amount = amount;
            this.subject = subject;
            this.body = body;
            this.remark = remark;
        }
    }
}
