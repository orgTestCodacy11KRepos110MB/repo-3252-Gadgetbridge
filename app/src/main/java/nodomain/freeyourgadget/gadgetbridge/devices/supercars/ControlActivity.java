package nodomain.freeyourgadget.gadgetbridge.devices.supercars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.activities.AbstractGBActivity;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.service.devices.supercars.SuperCarsSupport;

public class ControlActivity extends AbstractGBActivity implements JoystickView.JoystickListener {
    private static final Logger LOG = LoggerFactory.getLogger(ControlActivity.class);
    LocalBroadcastManager localBroadcastManager;
    boolean lights = false;
    boolean blinking = false;
    boolean turbo = false;
    CountDownTimer periodicDataSenderRunner;

    SuperCarsConstants.Direction direction = SuperCarsConstants.Direction.CENTER;
    SuperCarsConstants.Movement movement = SuperCarsConstants.Movement.IDLE;
    SuperCarsConstants.Speed speed = SuperCarsConstants.Speed.NORMAL;
    SuperCarsConstants.Light light = SuperCarsConstants.Light.OFF;
    public GBDevice device;
    TextView batteryPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supercars_control);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            device = bundle.getParcelable(GBDevice.EXTRA_DEVICE);
        } else {
            throw new IllegalArgumentException("Must provide a device when invoking this activity");
        }

        CheckBox turboMode = findViewById(R.id.turboMode);
        CheckBox lightsOn = findViewById(R.id.lightsOn);
        CheckBox lightsBlinking = findViewById(R.id.lightsBlinking);
        batteryPercentage = findViewById(R.id.battery_percentage_label);
        setBatteryLabel();
        localBroadcastManager = LocalBroadcastManager.getInstance(ControlActivity.this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(GBDevice.ACTION_DEVICE_CHANGED);
        LocalBroadcastManager.getInstance(this).registerReceiver(commandReceiver, filter);

        turboMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                turbo = isChecked;
            }
        });

        lightsOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lights = isChecked;
                setLights();

            }
        });

        lightsBlinking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (lightsOn.isChecked()) {
                        lightsOn.setChecked(false);
                        lights = false;
                    }
                }
                lightsOn.setEnabled(!isChecked);
                blinking = isChecked;
                setLights();
            }
        });

        //when this activity is open, data is sent continuously every 200ms
        periodicDataSender();
        periodicDataSenderRunner.start();
    }

    private void setLights() {
        if (!blinking) {
            if (lights) {
                light = SuperCarsConstants.Light.ON;
            } else {
                light = SuperCarsConstants.Light.OFF;
            }
        }

    }

    private void setBatteryLabel() {
        String level = device.getBatteryLevel() > 0 ? String.format("%1s%%", device.getBatteryLevel()) : device.getName();
        //batteryPercentage.setText(level);  //the device seems to be cached, thus the value is old...!
    }

    private void sendLocalBroadcast(Intent intent) {
        localBroadcastManager.sendBroadcast(intent);
    }

    public void periodicDataSender() {
        periodicDataSenderRunner = new CountDownTimer(Long.MAX_VALUE, 100) {

            public void onTick(long millisUntilFinished) {

                if (blinking) {
                    if (light.equals(SuperCarsConstants.Light.ON)) {
                        light = SuperCarsConstants.Light.OFF;
                    } else {
                        light = SuperCarsConstants.Light.ON;
                    }
                }

                create_intent_with_data();
            }

            public void onFinish() {
                start();
            }
        };
    }

    private void create_intent_with_data() {
        Intent intent = new Intent(SuperCarsSupport.COMMAND_DRIVE_CONTROL);
        intent.putExtra(SuperCarsSupport.EXTRA_DIRECTION, direction);
        intent.putExtra(SuperCarsSupport.EXTRA_MOVEMENT, movement);
        intent.putExtra(SuperCarsSupport.EXTRA_SPEED, speed);
        intent.putExtra(SuperCarsSupport.EXTRA_LIGHT, light);
        sendLocalBroadcast(intent);
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {

        if (yPercent < 0.2 && yPercent != 0) {
            movement = SuperCarsConstants.Movement.UP;
        } else if (yPercent > 0.2) {
            movement = SuperCarsConstants.Movement.DOWN;
        } else {
            movement = SuperCarsConstants.Movement.IDLE;
        }

        if (xPercent < -0.5) {
            direction = SuperCarsConstants.Direction.LEFT;
        } else if (xPercent > 0.5) {
            direction = SuperCarsConstants.Direction.RIGHT;
        } else {
            direction = SuperCarsConstants.Direction.CENTER;
        }

        if (turbo) {
            speed = SuperCarsConstants.Speed.TURBO;
        } else {
            speed = SuperCarsConstants.Speed.NORMAL;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        periodicDataSenderRunner.cancel();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(commandReceiver);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        periodicDataSenderRunner.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        periodicDataSenderRunner.cancel();
    }

    BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOG.debug("device receiver received " + intent.getAction());
            if (intent.getAction().equals(GBDevice.ACTION_DEVICE_CHANGED)) {
                setBatteryLabel();
            }
        }
    };

}
