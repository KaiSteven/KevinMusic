package com.example.asus.codingplayer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.codingplayer.vo.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    EditText login_name,login_password;
    Button login_button,register_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //获取界面中相关的View
        login_name = (EditText) findViewById(R.id.login_name);
        login_password = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);
        //设置登录按钮点击事件
        login_button.setOnClickListener(this);
        //注册按钮跳转
        register_button.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                goLogin();
                break;
            case R.id.register_button:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录方法
     */
    private void goLogin() {
        //获取用户输入的用户名和密码
        String username = login_name.getText().toString();
        String password = login_password.getText().toString();
        //非空验证
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"用户名或密码不能为空", LENGTH_SHORT).show();
            return;
        }

        //使用BmobSDK提供登录功能
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setPassword(password);
        user.login(new SaveListener<MyUser>() {

            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e == null){
                    Toast.makeText(LoginActivity.this, "登录成功！" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this, "登录失败" , Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // ignore
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
}
