package hioa.mappe3.s171183;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutDialog extends DialogFragment {

	public AboutDialog() {
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		TextView content = new TextView(getActivity());
		final SpannableString message =  new SpannableString(getString(R.string.about_content));
		Linkify.addLinks(message, Linkify.WEB_URLS);
		content.setText(message);
	    content.setMovementMethod(LinkMovementMethod.getInstance());
	    
	    content.setPadding(20, 10, 20, 10);
	   
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.about_this_app));
		builder.setView(content);
		

		builder.setPositiveButton("OK", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		return builder.create();
	}

}
