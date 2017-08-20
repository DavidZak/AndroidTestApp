package com.example.mradmin.androidtestapp.activities;

import android.content.Intent;

import com.daimajia.androidanimations.library.Techniques;
import com.example.mradmin.androidtestapp.R;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class MainActivity extends AwesomeSplash {

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.colorBackground);
        configSplash.setAnimCircularRevealDuration(2000);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);

        configSplash.setLogoSplash(R.drawable.logo_unitem);
        configSplash.setAnimLogoSplashDuration(2000);
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce);

        //configSplash.setOriginalHeight(400);
        //configSplash.setOriginalWidth(400);
        configSplash.setAnimPathStrokeDrawingDuration(2000);
        configSplash.setPathSplashStrokeSize(2);
        //configSplash.setPathSplashFillColor();
        configSplash.setAnimPathFillingDuration(2000);
        //configSplash.setPathSplashFillColor();

        configSplash.setTitleSplash("");
    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(MainActivity.this, FirstActivity.class));
    }
}
