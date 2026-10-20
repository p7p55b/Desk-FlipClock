package com.studiofoxxo.deskflipclock.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public final class FlipPairView extends LinearLayout {

    private final FlipCardView lft;
    private final FlipCardView rght;
    private final TextView sprtr;

    public FlipPairView(Context ctx) {
        this(ctx, null);
    }

    public FlipPairView(Context ctx, AttributeSet trs) {
        super(ctx, trs);
        float dnst = ctx.getResources().getDisplayMetrics().density;
        int gp = Math.round(8 * dnst);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        lft = new FlipCardView(ctx);
        sprtr = new TextView(ctx);
        rght = new FlipCardView(ctx);

        lft.stcrnrlbl("[ HR ]");
        rght.stcrnrlbl("[ MIN ]");

        sprtr.setText(":");
        sprtr.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        sprtr.setTextColor(0xFFCDCCB2);
        sprtr.setGravity(Gravity.CENTER);

        addView(lft, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));

        LayoutParams lpsp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lpsp.leftMargin = gp;
        lpsp.rightMargin = gp;
        addView(sprtr, lpsp);

        addView(rght, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
    }

    @Override
    protected void onSizeChanged(int w, int h, int pw, int ph) {
        super.onSizeChanged(w, h, pw, ph);
        if (h > 0) {
            sprtr.setTextSize(TypedValue.COMPLEX_UNIT_PX, h * 0.42f);
        }
    }

    public void stvls(int v1, int v2, boolean nmt, boolean frc) {
        lft.stvl(String.format(Locale.US, "%02d", v1), nmt, frc);
        rght.stvl(String.format(Locale.US, "%02d", v2), nmt, frc);
    }
}
