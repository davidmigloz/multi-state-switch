package com.davidmiguel.multistateswitch.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;

import com.davidmiguel.multistateswitch.R;
import com.davidmiguel.multistateswitch.State;
import com.davidmiguel.multistateswitch.StateListener;
import com.davidmiguel.multistateswitch.StateStyle;
import com.davidmiguel.multistateswitch.databinding.ActivityMainBinding;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements StateListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupDefaultSwitch();
        setupDisabledSwitch();
        setupCustomizedSwitch();
    }

    @Override
    public void onStateSelected(int stateIndex, @NonNull State state) {
        binding.listener.setText(getString(R.string.listener, stateIndex, state.getText()));
    }

    private void setupDefaultSwitch() {
        binding.defaultSwitch.addStates(Arrays.asList(new State("One"), new State("Two"), new State("Three")));
        binding.defaultSwitch.addStateListener(this);
    }

    private void setupDisabledSwitch() {
        binding.disabledSwitch.addStates(Arrays.asList(new State("€0"), new State("€25"),
                new State("€50"), new State("€75"), new State("€100")));
        binding.disabledSwitch.addStateListener(this);
    }

    private void setupCustomizedSwitch() {
        binding.customizedSwitch.addState(new State("Cold"), new StateStyle.Builder()
                .withSelectedBackgroundColor(Color.BLUE)
                .build());
        binding.customizedSwitch.addState(new State("ON", "OFF", "OFF"), new StateStyle.Builder()
                .withTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .withDisabledBackgroundColor(Color.BLACK)
                .withDisabledTextColor(Color.WHITE)
                .build());
        binding.customizedSwitch.addState(new State("Hot"), new StateStyle.Builder()
                .withSelectedBackgroundColor(Color.RED)
                .build());
        binding.customizedSwitch.addStateListener(this);
        setupCustomizedButtons();
    }

    private void setupCustomizedButtons() {
        binding.select1Btn.setOnClickListener(v -> binding.customizedSwitch.selectState(0));
        binding.select2Btn.setOnClickListener(v -> binding.customizedSwitch.selectState(1));
        binding.select3Btn.setOnClickListener(v -> binding.customizedSwitch.selectState(2));
    }
}
