package com.example.ntudepartmentdefense.util;

import com.example.ntudepartmentdefense.manager.ResourceManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

import android.widget.EditText;


public class NameDialog extends DialogFragment{

	public interface NameDialogListener {
		public void onNameDialogOK( String name );
	}
	public NameDialogListener mListener;
	public EditText text;
	public void setListener( NameDialogListener pListener) {
		mListener = pListener;
	}
	public NameDialogListener getListener() {
		return mListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		text = new EditText( ResourceManager.getInstance().context); 
		return new AlertDialog.Builder(ResourceManager.getInstance().context)
				.setTitle("Enter Name...")
				.setCancelable(false).setView( text)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onNameDialogOK( NameDialog.this.text.getText().toString() );
					}
				}).create();
	}

	
}
