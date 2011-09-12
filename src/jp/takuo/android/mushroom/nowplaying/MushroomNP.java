package jp.takuo.android.mushroom.nowplaying;

/**

 * Copyright (c) 2011, Takuo Kitame (http://takuo.jp/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.preference.PreferenceManager;

public class MushroomNP extends Activity implements OnClickListener {

    private static final String PREFS_FORMAT_STRING = "format_string";
    private static final String DEFAULT_FORMAT_STRING = "#NowPlaying %t - %a";
    private static final String LOG_TAG = "MushroomNP";
    private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
    private static SharedPreferences mPrefs;
    private Button mButtonOK;
    private Button mButtonCancel;
    private Button mButtonRefresh;
    private EditText mEditFormat;
    private TextView mTextView;
    private String mFormatString;

    private void replace(String str) {
        Intent out = new Intent(ACTION_INTERCEPT);
        out.putExtra("replace_key", str);
        setResult(RESULT_OK, out);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences("nowplaying", Context.MODE_PRIVATE);
        mFormatString = 
            PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getString(
                    PREFS_FORMAT_STRING, DEFAULT_FORMAT_STRING);
        
        MushroomNP.this.setContentView(R.layout.main);
        mButtonOK = (Button)findViewById(R.id.btn_ok);
        mButtonCancel = (Button)findViewById(R.id.btn_cancel);
        mButtonRefresh = (Button)findViewById(R.id.btn_refresh);
        mEditFormat = (EditText)findViewById(R.id.edit_format);
        mEditFormat.setText(mFormatString);
        mTextView = (TextView)findViewById(R.id.text_now_playing);

        mButtonOK.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mButtonRefresh.setOnClickListener(this);
        mTextView.setText(getFormattedString(mFormatString));
    }
    
    private String getFormattedString(String format) {
        String ret;
         ret = format.replace("%%", "%");
         if (mPrefs.getBoolean("playstate", false)){
             ret = ret.replace("%a", mPrefs.getString("artist", "unknown"));
             ret = ret.replace("%t", mPrefs.getString("track", "unknown"));
             ret = ret.replace("%l", mPrefs.getString("album", "unknown'"));
         } else {
             ret = "Not Playing";
         }
            Log.d(LOG_TAG, "Return value: " + ret);
            return ret;
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonOK)
            replace(mTextView.getText().toString());
        if (v == mButtonOK || v == mButtonCancel)
            finish();
        if (v == mButtonRefresh) {
            mFormatString = mEditFormat.getText().toString();
            PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).edit().putString(PREFS_FORMAT_STRING, mFormatString).commit();
            Log.d(LOG_TAG, "Format: " + mFormatString);
            mTextView.setText(getFormattedString(mFormatString));
        }
    }
}