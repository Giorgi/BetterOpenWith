package com.aboutmycode.betteropenwith;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Giorgi on 6/27/2014.
 */
public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        String version = "";
        try {
            PackageInfo pInfo = Utils.getCurrentPackageInfo(this);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String creditText = "<b>Thanks to reddit's oroboros74, who <a href=\"http://www.reddit.com/r/Android/comments/24okaq/what_apps_would_you_like_to_have_that_dont_exist/ch96jid\">came up with the idea</a> for this app!</b>";
        Spanned text = Html.fromHtml("    <img src=\"ic_launcher.png\"/>" +
                        "<h3>Better Open With</h3>\n" +
                        String.format(String.format("    <p>%s</p>\n", getString(R.string.version)), version) +
                        String.format("    <p>%s<sup>©</sup> 2014 <a href=\"mailto:android@aboutmycode.com?subject=Better Open With\">Giorgi Dalakishvili</a>. %s</p>", getString(R.string.copyright),
                                getString(R.string.rights) +
                                        "<p>" +
                                        creditText +
                                        "</p>"
                        ),

                new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String s) {
                        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        return drawable;
                    }
                }, null
        );

        TextView about = (TextView) findViewById(R.id.about_text);
        about.setText(text);

        about.setMovementMethod(LinkMovementMethod.getInstance());
        about.setLinkTextColor(Color.BLUE);
    }

    public void viewChangelog(View view) {
        ChangelogDialogFragment dialogStandardFragment = new ChangelogDialogFragment();
        FragmentManager fm = getFragmentManager();
        Fragment prev = fm.findFragmentByTag("ChangelogDialogFragment");
        if (prev != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(prev);
        }

        dialogStandardFragment.show(fm, "ChangelogDialogFragment");
    }

    public void rateApp(View view) {
        try {
            Uri marketUri = Uri.parse(String.format("market://details?id=%s", getPackageName()));
            startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.play_store), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareApp(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text));
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format("https://play.google.com/store/apps/details?id=%s", getPackageName()));
        startActivity(shareIntent);
    }
}