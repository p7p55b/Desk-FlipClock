package com.studiofoxxo.deskflipclock.ui;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public final class FlipCardView extends View {

    private static final long FLP_DRTN = 420L;

    private String crvl = "00";
    private String prvl = "00";
    private String crnrlbl = "";

    private boolean clckflpg;
    private long flpstrt;

    private final Paint fllpnt = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint brdrpnt = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint txtpnt = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lblpnt = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF crdrct = new RectF();
    private final RectF ntchlft = new RectF();
    private final RectF ntchrght = new RectF();

    private final Camera cmr = new Camera();
    private final Matrix mtrx = new Matrix();

    private final float dnst;
    private final float crnrrds;

    private float txtbsln;
    private float lblx;
    private float lblv;

    public FlipCardView(Context ctx) {
        this(ctx, null);
    }

    public FlipCardView(Context ctx, AttributeSet trs) {
        super(ctx, trs);
        dnst = ctx.getResources().getDisplayMetrics().density;
        crnrrds = 3f * dnst;

        fllpnt.setStyle(Paint.Style.FILL);

        brdrpnt.setStyle(Paint.Style.STROKE);
        brdrpnt.setStrokeWidth(Math.max(1f, 1f * dnst));
        brdrpnt.setColor(0xFFCDCCB2);

        Typeface mn = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);

        txtpnt.setColor(0xFFEBE6CA);
        txtpnt.setTextAlign(Paint.Align.CENTER);
        txtpnt.setTypeface(mn);

        lblpnt.setColor(0xB397B1AA);
        lblpnt.setTextAlign(Paint.Align.LEFT);
        lblpnt.setTypeface(mn);
        lblpnt.setTextSize(11f * dnst);

        cmr.setLocation(0, 0, -8f * dnst);
    }

    public void stcrnrlbl(String lbl) {
        crnrlbl = lbl != null ? lbl : "";
        invalidate();
    }

    public void stvl(String nwvl, boolean nmt, boolean frc) {
        if (nwvl == null) nwvl = "";
        if (!frc && nwvl.equals(crvl)) return;

        if (!nmt) {
            prvl = nwvl;
            crvl = nwvl;
            clckflpg = false;
            invalidate();
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            String trgt = nwvl;
            post(() -> stvl(trgt, true, true));
            return;
        }

        prvl = crvl;
        crvl = nwvl;
        clckflpg = true;
        flpstrt = SystemClock.uptimeMillis();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int pw, int ph) {
        float pd = 1f * dnst;
        crdrct.set(pd, pd, w - pd, h - pd);

        float cv = h * 0.5f;
        lblx = crdrct.left + 8f * dnst;
        lblv = crdrct.top + lblpnt.getTextSize() + 6f * dnst;

        txtpnt.setTextSize(Math.min(h * 0.72f, w * 0.62f));
        Paint.FontMetrics fm = txtpnt.getFontMetrics();
        txtbsln = cv - (fm.ascent + fm.descent) * 0.5f;

        float nw = 4f * dnst;
        float nh = 5f * dnst;
        ntchlft.set(pd, cv - nh, pd + nw, cv + nh);
        ntchrght.set(w - pd - nw, cv - nh, w - pd, cv + nh);
    }

    @Override
    protected void onDraw(Canvas cnvs) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        float flpngl = 0f;
        if (clckflpg) {
            long lpsd = SystemClock.uptimeMillis() - flpstrt;
            if (lpsd >= FLP_DRTN) {
                prvl = crvl;
                clckflpg = false;
            } else {
                float inv = 1f - ((float) lpsd / FLP_DRTN);
                flpngl = 180f * (1f - inv * inv);
            }
        }

        float cx = w * 0.5f;
        float cv = h * 0.5f;

        int tshd = (clckflpg && flpngl < 90f) ? (int) ((1f - (flpngl / 90f)) * 160f) : 0;
        drwhlf(cnvs, w, h, cx, cv, true, crvl, tshd);

        int bshd = (clckflpg && flpngl > 90f) ? (int) (((flpngl - 90f) / 90f) * 160f) : 0;
        drwhlf(cnvs, w, h, cx, cv, false, clckflpg ? prvl : crvl, bshd);

        if (clckflpg && flpngl > 0f && flpngl < 180f) {
            cnvs.save();
            cmr.save();

            boolean frsthlf = flpngl <= 90f;
            cmr.rotateX(frsthlf ? -flpngl : 180f - flpngl);
            cmr.getMatrix(mtrx);
            cmr.restore();

            mtrx.preTranslate(-cx, -cv);
            mtrx.postTranslate(cx, cv);
            cnvs.concat(mtrx);

            int shd = (int) ((frsthlf ? (flpngl / 90f) : ((180f - flpngl) / 90f)) * 140f);
            drwhlf(cnvs, w, h, cx, cv, frsthlf, frsthlf ? prvl : crvl, shd);
            cnvs.restore();
        }

        cnvs.drawLine(crdrct.left, cv, crdrct.right, cv, brdrpnt);
        fllpnt.setColor(0xFF2A3536);
        cnvs.drawRect(ntchlft, fllpnt);
        cnvs.drawRect(ntchlft, brdrpnt);
        cnvs.drawRect(ntchrght, fllpnt);
        cnvs.drawRect(ntchrght, brdrpnt);

        if (clckflpg) {
            postInvalidateOnAnimation();
        }
    }

    private void drwhlf(Canvas cnvs, int w, int h, float cx, float cv,
                        boolean tp, String vl, int shdlph) {
        cnvs.save();
        if (tp) cnvs.clipRect(-w, 0, w * 2, cv);
        else cnvs.clipRect(-w, cv, w * 2, h);

        fllpnt.setColor(0xFF162326);
        cnvs.drawRoundRect(crdrct, crnrrds, crnrrds, fllpnt);

        if (tp && !crnrlbl.isEmpty()) {
            cnvs.drawText(crnrlbl, lblx, lblv, lblpnt);
        }

        cnvs.drawText(vl, cx, txtbsln, txtpnt);
        cnvs.drawRoundRect(crdrct, crnrrds, crnrrds, brdrpnt);

        if (shdlph > 0) {
            fllpnt.setColor(shdlph << 24);
            cnvs.drawRoundRect(crdrct, crnrrds, crnrrds, fllpnt);
        }
        cnvs.restore();
    }
}
