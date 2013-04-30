package com.android.tecla.keyboard;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

public class TeclaIME extends InputMethodService {

	private static TeclaIME sInstance;

	@Override
	public void onCreate() {
		super.onCreate();		
		sInstance = this;
		TeclaApp.setIMEInstance(sInstance);
	}
	
	public static TeclaIME getInstance() {
		return sInstance;
	}

	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
		TeclaApp.persistence.setIMEShowing(true);
	}

	@Override
	public void onFinishInputView(boolean finishingInput) {
		IMEAdapter.setKeyboardView(null);
		TeclaApp.persistence.setIMEShowing(false);
		super.onFinishInputView(finishingInput);
	}
	
	public void pressHomeKey() {
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.addCategory(Intent.CATEGORY_HOME);
		home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(home);
	}
	
	public void pressBackKey() {
		keyDownUp(KeyEvent.KEYCODE_BACK);
	}
	
	/**
	 * Helper to send a key down / key up pair to the current editor.
	 */
	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}	

}
