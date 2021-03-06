package com.example.asus.codingplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.codingplayer.vo.MyUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    EditText register_id,register_password;
    Button register_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //获取界面相关的VIEW
        register_id = (EditText) findViewById(R.id.register_id);
        register_password = (EditText) findViewById(R.id.register_password);
        register_button = (Button) findViewById(R.id.register_button);
        //按钮点击事件
        register_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                toRegister();
                break;
        }
    }

    /**
     * 注册方法
     */

    private void toRegister() {
        //获取用户输入的用户名和密码
        String username = register_id.getText().toString();
        String password = register_password.getText().toString();
        //非空验证
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this,"用户名或密码不能为空", LENGTH_SHORT).show();
            return;
        }

        //使用BmobSDK提供注册功能
        MyUser user = new MyUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUp(new SaveListener<MyUser>() {

            @Override
            public void done(MyUser s, BmobException e) {
                if (e == null){
                    Toast.makeText(RegisterActivity.this, "注册成功" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(RegisterActivity.this, "注册失败" , Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        });
    }


    ///////////////////////////////////////////////////////////////////////////
    // ignore
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
}

