package autoworks.app.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import autoworks.app.R;

/**
 * Created by volyminhnhan on 24/02/2015.
 */
public class TransparentProgressDialog extends Dialog {

    private ImageView iv;

    public TransparentProgressDialog(Context context) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
        iv = new ImageView(context);
       // Drawable myDrawable = context.getDrawable(R.drawable.progress_icon);
       // iv.setImageDrawable(myDrawable);
        iv.setImageResource(R.drawable.loading);
        layout.addView(iv, params);
        addContentView(layout, params);
    }

    public static Dialog createProgressDialog(Context context)
    {
      return new TransparentProgressDialog(context);

    }
    @Override
    public void show() {
      super.show();
       RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(3000);
        iv.setAnimation(anim);
        iv.startAnimation(anim);
    }
}