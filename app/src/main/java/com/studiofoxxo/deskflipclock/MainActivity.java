package com.studiofoxxo.deskflipclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.studiofoxxo.deskflipclock.model.WorldClockItem;
import com.studiofoxxo.deskflipclock.ui.FlipPairView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class MainActivity extends Activity {

    private View clckvw;
    private View plsbtn;
    private TextView tvct;
    private TextView tvdt;
    private FlipPairView tmflp;

    private SharedPreferences prfs;
    private List<WorldClockItem> clcks;
    private int dx;

    private final Calendar clcl = Calendar.getInstance();
    private final Calendar ctrgt = Calendar.getInstance();
    private final SimpleDateFormat dtfmt = new SimpleDateFormat("EEEE d MMMM", Locale.FRANCE);

    private final BroadcastReceiver tckrcvr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent ntnt) {
            if (dx < clcks.size()) pdt(true, false);
        }
    };

    @Override
    protected void onCreate(Bundle bndl) {
        super.onCreate(bndl);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        hdbrs();

        prfs = getSharedPreferences("flp_prfs", MODE_PRIVATE);
        ldclcks();

        View rt = findViewById(R.id.rt);
        clckvw = findViewById(R.id.clckvw);
        plsbtn = findViewById(R.id.plsbtn);
        tvct = findViewById(R.id.tvct);
        tvdt = findViewById(R.id.tvdt);
        tmflp = findViewById(R.id.tmflp);

        plsbtn.setOnClickListener(v -> shwdddlg());

        GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent vnt) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent v1, MotionEvent v2, float vx, float vz) {
                if (v1 == null || v2 == null) return false;
                float dfx = v2.getX() - v1.getX();
                if (Math.abs(dfx) > Math.abs(v2.getY() - v1.getY()) && Math.abs(dfx) > 80) {
                    if (dfx < 0 && dx < clcks.size()) {
                        dx++;
                        pdt(true, true);
                    } else if (dfx > 0 && dx > 0) {
                        dx--;
                        pdt(true, true);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent vnt) {
                if (dx > 0 && dx < clcks.size()) {
                    clcks.remove(dx);
                    svclcks();
                    dx--;
                    pdt(true, true);
                }
            }
        });

        rt.setOnTouchListener((v, vnt) -> gd.onTouchEvent(vnt));
    }

    private void ldclcks() {
        clcks = new ArrayList<>();
        String tzd = TimeZone.getDefault().getID();
        int slsh = tzd.lastIndexOf('/');
        clcks.add(new WorldClockItem(slsh >= 0 ? tzd.substring(slsh + 1).replace('_', ' ') : tzd, "", tzd));

        try {
            JSONArray rr = new JSONArray(prfs.getString("clcks", "[]"));
            for (int k = 0, ln = rr.length(); k < ln; k++) {
                JSONObject bj = rr.getJSONObject(k);
                clcks.add(new WorldClockItem(
                        bj.optString("cty", "UTC"),
                        bj.optString("rgn", ""),
                        bj.optString("tz", "UTC")
                ));
            }
        } catch (JSONException ignored) {
        }

        dx = prfs.getInt("lst_idx", 0);
        if (dx < 0 || dx >= clcks.size()) dx = 0;
    }

    private void svclcks() {
        JSONArray rr = new JSONArray();
        try {
            for (int k = 1, sz = clcks.size(); k < sz; k++) {
                WorldClockItem c = clcks.get(k);
                rr.put(new JSONObject()
                        .put("cty", c.ct)
                        .put("rgn", c.rgn)
                        .put("tz", c.tzd));
            }
        } catch (JSONException ignored) {
        }
        prfs.edit().putString("clcks", rr.toString()).apply();
    }

    private void hdbrs() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    private void pdt(boolean nmt, boolean frc) {
        if (dx >= clcks.size()) {
            clckvw.setVisibility(View.GONE);
            plsbtn.setVisibility(View.VISIBLE);
            return;
        }

        plsbtn.setVisibility(View.GONE);
        clckvw.setVisibility(View.VISIBLE);
        prfs.edit().putInt("lst_idx", dx).apply();

        WorldClockItem tm = clcks.get(dx);
        long nw = System.currentTimeMillis();
        TimeZone lcltz = TimeZone.getDefault();

        ctrgt.setTimeZone(tm.tz);
        ctrgt.setTimeInMillis(nw);

        tmflp.stvls(ctrgt.get(Calendar.HOUR_OF_DAY), ctrgt.get(Calendar.MINUTE), nmt, frc);

        dtfmt.setTimeZone(tm.tz);
        tvdt.setText(dtfmt.format(nw).toUpperCase(Locale.FRANCE));

        if (dx == 0) {
            tvct.setText("[ " + tm.ct + " // LCL ]");
        } else {
            tvct.setText(tm.ct + " " + tm.fmtffst(nw, lcltz, clcl, ctrgt));
        }
    }

    private void shwdddlg() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        View dlgvw = LayoutInflater.from(this).inflate(R.layout.dialog_add_overseas, null);
        EditText tsrch = dlgvw.findViewById(R.id.tsrch);
        ListView lvcts = dlgvw.findViewById(R.id.lvcts);

        List<WorldClockItem> ctlg = WorldClockItem.gtctlg();
        List<WorldClockItem> fltrd = new ArrayList<>(ctlg);
        Typeface mn = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        TimeZone lcltz = TimeZone.getDefault();

        ArrayAdapter<WorldClockItem> lstdptr = new ArrayAdapter<WorldClockItem>(
                this, android.R.layout.simple_list_item_2, android.R.id.text1, fltrd
        ) {
            @Override
            public View getView(int ps, View cv, ViewGroup prnt) {
                View v = super.getView(ps, cv, prnt);
                TextView t1 = v.findViewById(android.R.id.text1);
                TextView t2 = v.findViewById(android.R.id.text2);

                WorldClockItem tm = getItem(ps);
                if (tm != null) {
                    long nw = System.currentTimeMillis();
                    ctrgt.setTimeZone(tm.tz);
                    ctrgt.setTimeInMillis(nw);

                    String prvw = String.format(
                            Locale.FRANCE, "%02d:%02d",
                            ctrgt.get(Calendar.HOUR_OF_DAY),
                            ctrgt.get(Calendar.MINUTE)
                    );

                    t1.setTypeface(mn);
                    t1.setTextSize(13f);
                    t1.setText(tm.ct + (tm.rgn.isEmpty() ? "" : " / " + tm.rgn));
                    t1.setTextColor(0xFFEBE6CA);

                    t2.setTypeface(mn);
                    t2.setTextSize(11f);
                    t2.setText(prvw + " " + tm.fmtffst(nw, lcltz, clcl, ctrgt));
                    t2.setTextColor(0xFF97B1AA);
                }
                return v;
            }
        };

        lvcts.setAdapter(lstdptr);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(dlgvw)
                .create();
        dlg.setOnDismissListener(d -> {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            hdbrs();
        });
        if (dlg.getWindow() != null) {
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        }

        tsrch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int cnt) {}

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String[] tkns = s.toString().trim().toLowerCase(Locale.FRANCE).split("[\\s,]+");
                fltrd.clear();
                for (int k = 0, sz = ctlg.size(); k < sz; k++) {
                    WorldClockItem tm = ctlg.get(k);
                    boolean mtch = true;
                    for (String tk : tkns) {
                        if (!tk.isEmpty() && !tm.srch.contains(tk)) {
                            mtch = false;
                            break;
                        }
                    }
                    if (mtch) fltrd.add(tm);
                }
                lstdptr.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        lvcts.setOnItemClickListener((p, v, ps, d) -> {
            clcks.add(fltrd.get(ps));
            svclcks();
            dx = clcks.size() - 1;
            dlg.dismiss();
            pdt(true, true);
        });

        dlg.show();
    }

    @Override
    public void onWindowFocusChanged(boolean fcs) {
        super.onWindowFocusChanged(fcs);
        if (fcs) hdbrs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hdbrs();
        pdt(false, false);
        IntentFilter fltr = new IntentFilter();
        fltr.addAction(Intent.ACTION_TIME_TICK);
        fltr.addAction(Intent.ACTION_TIME_CHANGED);
        fltr.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(tckrcvr, fltr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(tckrcvr);
    }
}
