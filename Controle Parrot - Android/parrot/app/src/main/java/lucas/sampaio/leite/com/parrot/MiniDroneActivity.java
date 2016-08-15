package lucas.sampaio.leite.com.parrot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parrot.arsdk.arcommands.ARCOMMANDS_MINIDRONE_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM;
import com.parrot.arsdk.arcommands.ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARDeviceController;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;

public class MiniDroneActivity extends AppCompatActivity  {
    private static final String TAG = "MiniDroneActivity";
    private static final int DELAY = 300;
    private MiniDrone mMiniDrone;

    private ProgressDialog mConnectionProgressDialog;
    private ProgressDialog mDownloadProgressDialog;

    private ARDeviceController mDeviceController;

    private TextView mBatteryLabel;
    private Button mTakeOffLandBt;

    private Button startBtn;

    private int mNbMaxDownload;
    private int mCurrentDownloadIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minidrone);

        initIHM();

        Intent intent = getIntent();
        ARDiscoveryDeviceService service = intent.getParcelableExtra(DeviceListActivity.EXTRA_DEVICE_SERVICE);
        mMiniDrone = new MiniDrone(this, service);
        mMiniDrone.addListener(mMiniDroneListener);


    }


    @Override
    protected void onStart() {
        super.onStart();

        // show a loading view while the minidrone is connecting
        if ((mMiniDrone != null) && !(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING.equals(mMiniDrone.getConnectionState())))
        {
            mConnectionProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            mConnectionProgressDialog.setIndeterminate(true);
            mConnectionProgressDialog.setMessage("Connecting ...");
            mConnectionProgressDialog.setCancelable(false);
            mConnectionProgressDialog.show();

            // if the connection to the MiniDrone fails, finish the activity
            if (!mMiniDrone.connect()) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mMiniDrone != null)
        {
            mConnectionProgressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
            mConnectionProgressDialog.setIndeterminate(true);
            mConnectionProgressDialog.setMessage("Disconnecting ...");
            mConnectionProgressDialog.setCancelable(false);
            mConnectionProgressDialog.show();

            if (!mMiniDrone.disconnect()) {
                finish();
            }
        } else {
            finish();
        }
    }

    private void initIHM() {

        findViewById(R.id.emergencyBt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMiniDrone.emergency();
            }
        });

        startBtn = (Button) findViewById(R.id.buttonStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int tempoDeEspera = 600;
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        moveLeft(tempoDeEspera);
                        moveForward(tempoDeEspera);
                        moveRight(tempoDeEspera);
                        moveBack(tempoDeEspera);
//                        turnLeft(2000);
//                        turnRight(2000);
//                        up(tempoDeEspera);
//                        down(tempoDeEspera);

                    }
                });
        }
        });

        mTakeOffLandBt = (Button) findViewById(R.id.takeOffOrLandBt);
        mTakeOffLandBt.setEnabled(false);
        mTakeOffLandBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (mMiniDrone.getFlyingState()) {
                    case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                        mMiniDrone.takeOff();
                        break;
                    case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                    case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                        mMiniDrone.land();
                        break;
                    default:
                }
            }
        });
        mBatteryLabel = (TextView) findViewById(R.id.batteryLabel);
    }

    private void moveLeft(int time){
        mMiniDrone.setRoll((byte) -50);
        mMiniDrone.setFlag((byte) 1);
        SystemClock.sleep(time);
        mMiniDrone.setRoll((byte) 0);
        mMiniDrone.setFlag((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void moveRight(int time){
        mMiniDrone.setRoll((byte) 50);
        mMiniDrone.setFlag((byte) 1);
        SystemClock.sleep(time);
        mMiniDrone.setRoll((byte) 0);
        mMiniDrone.setFlag((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void moveForward(int time){
        mMiniDrone.setPitch((byte) 50);
        mMiniDrone.setFlag((byte) 1);
        SystemClock.sleep(time);
        mMiniDrone.setPitch((byte) 0);
        mMiniDrone.setFlag((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void moveBack(int time){
        mMiniDrone.setPitch((byte) -50);
        mMiniDrone.setFlag((byte) 1);
        SystemClock.sleep(time);
        mMiniDrone.setPitch((byte) 0);
        mMiniDrone.setFlag((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void turnLeft(int time){
        mMiniDrone.setYaw((byte) -50);
        SystemClock.sleep(time);
        mMiniDrone.setYaw((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void turnRight(int time){
        mMiniDrone.setYaw((byte) 50);
        SystemClock.sleep(time);
        mMiniDrone.setYaw((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void up(int time){
        mMiniDrone.setGaz((byte) 50);
        SystemClock.sleep(time);
        mMiniDrone.setGaz((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void down(int time){
        mMiniDrone.setGaz((byte) -50);
        SystemClock.sleep(time);
        mMiniDrone.setGaz((byte) 0);
        SystemClock.sleep(DELAY);
    }

    private void takeOffLand(){
        Toast.makeText(getApplicationContext(), "mMiniDrone.getFlyingState()", Toast.LENGTH_SHORT).show();
        switch (mMiniDrone.getFlyingState()) {
            case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                mMiniDrone.takeOff();
                break;
            case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
            case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                mMiniDrone.land();
                break;
            default:
        }
        SystemClock.sleep(DELAY);
    }

    private final MiniDrone.Listener mMiniDroneListener = new MiniDrone.Listener() {
        @Override
        public void onDroneConnectionChanged(ARCONTROLLER_DEVICE_STATE_ENUM state) {
            switch (state)
            {
                case ARCONTROLLER_DEVICE_STATE_RUNNING:
                    mConnectionProgressDialog.dismiss();
                    break;

                case ARCONTROLLER_DEVICE_STATE_STOPPED:
                    // if the deviceController is stopped, go back to the previous activity
                    mConnectionProgressDialog.dismiss();
                    finish();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onBatteryChargeChanged(int batteryPercentage) {
            mBatteryLabel.setText(String.format("%d%%", batteryPercentage));
        }

        @Override
        public void onPilotingStateChanged(ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_ENUM state) {
            switch (state) {
                case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_LANDED:
                    mTakeOffLandBt.setText("Take off");
                    mTakeOffLandBt.setEnabled(true);
                    break;
                case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_FLYING:
                case ARCOMMANDS_MINIDRONE_PILOTINGSTATE_FLYINGSTATECHANGED_STATE_HOVERING:
                    mTakeOffLandBt.setText("Land");
                    mTakeOffLandBt.setEnabled(true);
                    break;
                default:
                    mTakeOffLandBt.setEnabled(false);
            }
        }

        @Override
        public void onPictureTaken(ARCOMMANDS_MINIDRONE_MEDIARECORDEVENT_PICTUREEVENTCHANGED_ERROR_ENUM error) {
            Log.i(TAG, "Picture has been taken");
        }

        @Override
        public void onMatchingMediasFound(int nbMedias) {
            mDownloadProgressDialog.dismiss();

            mNbMaxDownload = nbMedias;
            mCurrentDownloadIndex = 1;

            if (nbMedias > 0) {
                mDownloadProgressDialog = new ProgressDialog(MiniDroneActivity.this, R.style.AppCompatAlertDialogStyle);
                mDownloadProgressDialog.setIndeterminate(false);
                mDownloadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDownloadProgressDialog.setMessage("Downloading medias");
                mDownloadProgressDialog.setMax(mNbMaxDownload * 100);
                mDownloadProgressDialog.setSecondaryProgress(mCurrentDownloadIndex * 100);
                mDownloadProgressDialog.setProgress(0);
                mDownloadProgressDialog.setCancelable(false);
                mDownloadProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMiniDrone.cancelGetLastFlightMedias();
                    }
                });
                mDownloadProgressDialog.show();
            }
        }

        @Override
        public void onDownloadProgressed(String mediaName, int progress) {
            mDownloadProgressDialog.setProgress(((mCurrentDownloadIndex - 1) * 100) + progress);
        }

        @Override
        public void onDownloadComplete(String mediaName) {
            mCurrentDownloadIndex++;
            mDownloadProgressDialog.setSecondaryProgress(mCurrentDownloadIndex * 100);

            if (mCurrentDownloadIndex > mNbMaxDownload) {
                mDownloadProgressDialog.dismiss();
                mDownloadProgressDialog = null;
            }
        }
    };
}
