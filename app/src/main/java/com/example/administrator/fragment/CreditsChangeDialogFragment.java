package com.example.administrator.fragment;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.DialogUtils;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.xxxq_grid.IntegralExchangeOrderActivity;
import com.example.administrator.xxxq_grid.LoginActivity;
import com.example.administrator.xxxq_grid.R;
import com.insplatform.core.utils.DateUtil;
import com.insplatform.core.utils.TextUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreditsChangeDialogFragment extends DialogFragment {

    private Button btnCreditChangeSelect;
    private EditText code;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_integral_layout, container);

        code = (EditText) view.findViewById(R.id.dialog_integral_code);

        btnCreditChangeSelect = view.findViewById(R.id.dialog_integral_submit);
        btnCreditChangeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c = code.getText().toString();
                if (TextUtil.isEmpty(c)) {
                    Toast.makeText(view.getContext(), "请输入兑换码!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("code", c);

                new HttpRequest().post(view.getContext(), "member/loadIgOrderInfo", map, new CallBackSuccess() {
                    @Override
                    public void onCallBackSuccess(Object data) {
                        if (data == null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "兑换码有误,请重新输入!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Date endTime = DateUtil.stringtoDate(BaseUtils.toString(((Map<String, Object>) data).get("endTime")), "yyyy-MM-dd HH:mm:ss");
                            if (endTime.before(new Date())) {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @TargetApi(Build.VERSION_CODES.M)
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "兑换码已过期,请重新输入!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getActivity(), IntegralExchangeOrderActivity.class);
                                String c = code.getText().toString();
                                bundle.putCharSequence("code", c);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dismiss();
                            }
                        }
                    }
                });
            }
        });
        return view;
    }
}
