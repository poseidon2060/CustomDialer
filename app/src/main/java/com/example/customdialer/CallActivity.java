package com.example.customdialer;

import static com.example.customdialer.Constants.asString;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.customdialer.databinding.ActivityCallBinding;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.collections.CollectionsKt;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding;
    Button btnAnswer;
    Button btnHangup;
    TextView tvCallInfo;

    private CompositeDisposable disposables;
    private String number;
    private OngoingCall ongoingCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ongoingCall = new OngoingCall();
        disposables = new CompositeDisposable();

        btnAnswer = binding.btnAnswer;
        btnHangup = binding.btnHangup;
        tvCallInfo = binding.tvCallInfo;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        number = Objects.requireNonNull(getIntent().getData().getSchemeSpecificPart());

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ongoingCall.answer();
            }
        });

        btnHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ongoingCall.hangup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        assert updateUi(-1) != null;
        disposables.add(
                OngoingCall.state
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                updateUi(integer);
                            }
                        })
        );
    }

//    @SuppressLint("SetTextI18n")
    private Consumer<? super Integer> updateUi(Integer state) {

        tvCallInfo.setText(asString(state) + "\n" + number);

        if (state != Call.STATE_RINGING) {
            btnAnswer.setVisibility(View.GONE);
        } else btnAnswer.setVisibility(View.VISIBLE);

        if (CollectionsKt.listOf(new Integer[]{
                Call.STATE_DIALING,
                Call.STATE_RINGING,
                Call.STATE_ACTIVE}).contains(state)) {
            btnHangup.setVisibility(View.VISIBLE);
        } else
            btnHangup.setVisibility(View.GONE);

        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }

    public static void start(Context context, Call call){
        Intent intent = new Intent(context,CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(call.getDetails().getHandle());
        context.startActivity(intent);
    }
}