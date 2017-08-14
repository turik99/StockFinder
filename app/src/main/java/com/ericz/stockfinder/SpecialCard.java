package com.ericz.stockfinder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static android.content.ContentValues.TAG;

/**
 * Created by ericz on 8/14/2017.
 */

public class SpecialCard extends CardView
{


    public SpecialCard(Context context) {
        super(context);
    }

    public void expand() {
        final int initialHeight = getHeight();

        measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = getMeasuredHeight();

        final int distanceToExpand = targetHeight - initialHeight;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1){
                    // Do this after expanded
                }

                getLayoutParams().height = (int) (initialHeight + (distanceToExpand * interpolatedTime));
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((long) distanceToExpand);
        startAnimation(a);
    }

    public void collapse(int collapsedHeight) {
        final int initialHeight = getMeasuredHeight();

        final int distanceToCollapse = (int) (initialHeight - collapsedHeight);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1){
                    // Do this after collapsed
                }


                Log.i(TAG, "Collapse | InterpolatedTime = " + interpolatedTime);

                getLayoutParams().height = (int) (initialHeight - (distanceToCollapse * interpolatedTime));
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((long) distanceToCollapse);
        startAnimation(a);
    }

}