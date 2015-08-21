package example.coursera.james.modernartui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final int SEEK_BAR_START = 0;
    // Max is 89 so there are 90 'ticks'.
    // Each 'tick' in SeekBar represents a change of '4' hue
    private static final int SEEK_BAR_MAX = 89;
    private static final int MAX_HUE = 360;

    private SeekBar mSeekBar;

    private View mTopLeft;
    private float[] mTopLeftStartHsv = new float[3];

    private View mMiddleRight;
    private float[] mMiddleRightStartHsv = new float[3];

    private DialogFragment mMomaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopLeft = (View) findViewById(R.id.topLeft);
        getStartBackgroundHsv(mTopLeft, mTopLeftStartHsv);
        setBackgroundColor(mTopLeft, mTopLeftStartHsv, SEEK_BAR_START);

        mMiddleRight = (View) findViewById(R.id.middleRight);
        getStartBackgroundHsv(mMiddleRight, mMiddleRightStartHsv);
        setBackgroundColor(mMiddleRight, mMiddleRightStartHsv, SEEK_BAR_START);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setMax(SEEK_BAR_MAX);
        mSeekBar.setProgress(SEEK_BAR_START);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBackgroundColor(mTopLeft, mTopLeftStartHsv, progress);
                setBackgroundColor(mMiddleRight, mMiddleRightStartHsv, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //STUB
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //STUB
            }
        });
    }

    private void setBackgroundColor(View v, float[] startHsv, int progress) {
        // Convert progress to hue range, i.e. map [0, SEEK_BAR_MAX] to [0, 360)
        float deltaHue = (float) progress / (SEEK_BAR_MAX + 1) * MAX_HUE;

        float startHue = startHsv[0];
        // End hue must be in range of [0, 360)
        float endHue = (startHue + deltaHue) % MAX_HUE;

        float[] newHsv = new float[] {endHue, startHsv[1], startHsv[2]};
        int newColor = Color.HSVToColor(newHsv);
        v.setBackgroundColor(newColor);
    }

    private void getStartBackgroundHsv(View v, float[] hsv) {
        // Initialize to random color that sounds cool
        int color = R.color.white;
        if (v.getId() == R.id.topLeft) {
            color = getResources().getColor(R.color.red);
        } else if (v.getId() == R.id.middleRight) {
            color = getResources().getColor(R.color.blue);
        } else {
            Log.w(TAG, "Unable to determine background color of a view");
        }
        Color.colorToHSV(color, hsv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_more_information) {
            showMomaDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showMomaDialog() {
        mMomaDialog = MomaDialogFragment.newInstance();
        mMomaDialog.show(getFragmentManager(), "moma");
    }

    // Class that creates the AlertDialog
    public static class MomaDialogFragment extends DialogFragment {

        private static final String TAG = "MomaDialogFragment";
        private static final String URL = "http://www.moma.org/collection/works/79816";

        public static MomaDialogFragment newInstance() {
            return new MomaDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.art_description)
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                }
                            })
                    .setPositiveButton(R.string.visit_moma,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    visitMoma();
                                }
                            })
                    .create();
        }

        private void visitMoma() {
            Log.i(TAG, "Visiting MOMA");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            startActivity(intent);
        }
    }

}
