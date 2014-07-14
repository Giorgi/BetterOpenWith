package com.aboutmycode.betteropenwith;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.collector.CrashReportData;
import org.acra.sender.EmailIntentSender;
import org.acra.sender.ReportSenderException;


@ReportsCrashes(formKey = "", // will not be used
        mailTo = "android@aboutmycode.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crashToast)
public class BetterOpenWithApplication extends Application {
    @Override
    public void onCreate() {
        ACRA.init(this);
        EnhancedMailSender mailSender = new EnhancedMailSender(this);
        ACRA.getErrorReporter().setReportSender(mailSender);

        super.onCreate();

        try {
            // workaround bug in AsyncTask, can show up (for example) when you toast from a service
            // this makes sure AsyncTask's internal handler is created from the right (main) thread
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
    }
}

class EnhancedMailSender extends EmailIntentSender {

    private final Context context;

    public EnhancedMailSender(Context ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public void send(CrashReportData errorContent) throws ReportSenderException {
        final String subject = context.getPackageName() + " Crash Report";
        final String body = buildBody(errorContent);

        try {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ACRA.getConfig().mailTo()});
            context.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ACRA.getConfig().mailTo()});
            context.startActivity(emailIntent);
        }
    }

    private String buildBody(CrashReportData errorContent) {
        ReportField[] fields = ACRA.getConfig().customReportContent();
        if (fields.length == 0) {
            fields = new ReportField[]{ReportField.STACK_TRACE, ReportField.ANDROID_VERSION, ReportField.APP_VERSION_NAME,
                    ReportField.BRAND, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA};
        }

        final StringBuilder builder = new StringBuilder();
        for (ReportField field : fields) {
            builder.append(field.toString()).append("=");
            builder.append(errorContent.get(field));
            builder.append('\n');
        }
        return builder.toString();
    }
}