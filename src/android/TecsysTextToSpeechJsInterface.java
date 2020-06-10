package org.apache.cordova.inappbrowser;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Browser;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.util.Log;

import java.util.ArrayList;
import java.lang.annotation.Annotation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaHttpAuthHandler;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

import java.util.*;

public class TecsysTextToSpeechJsInterface implements OnInitListener {

    public static final String ERR_NOT_INITIALIZED = "ERR_NOT_INITIALIZED";
    public static final String ERR_LOCALE_UNAVAILABLE = "ERR_LOCALE_UNAVAILABLE";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    public static final String DEFAULT_LOCALE = "en-US";
    public static final float DEFAULT_RATE = 1.0f;

    Context mContext;
    CallbackContext callbackContext;
    TextToSpeech tts = null;
    boolean ttsInitialized = false;

    public TecsysTextToSpeechJsInterface(Context c, CallbackContext callbackContext) {
        mContext = c;
        this.callbackContext = callbackContext;
        tts = new TextToSpeech(c, this);
    }

    @JavascriptInterface
    public void speak(String message) {
        speakTTS(message, DEFAULT_LOCALE, DEFAULT_RATE);
    }

    @JavascriptInterface
    public void speak(String message, String localeStr, double rate) {
        speakTTS(message, localeStr, rate);
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            tts = null;
        } else {
            // warm up the tts engine with an empty string
            HashMap<String, String> ttsParams = new HashMap<String, String>();
            ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            tts.setLanguage(new Locale(DEFAULT_LOCALE));
            tts.speak("", TextToSpeech.QUEUE_FLUSH, ttsParams);
            ttsInitialized = true;
        }
    }

    private void speakTTS(String message, String localeStr, double rate) throws NullPointerException {

        if (!validateTtsInitialized()) {
            return;
        }

        setTtsLocale(localeStr);

        setTtsSpeechRate((float) rate);

        HashMap<String, String> ttsParams = new HashMap<String, String>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, callbackContext.getCallbackId());

        tts.speak(message, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }

    private void setTtsSpeechRate(float rate) {
        if (Build.VERSION.SDK_INT >= 27) {
            tts.setSpeechRate(rate * 0.7f);
        } else {
            tts.setSpeechRate(rate);
        }
    }

    private void setTtsLocale(String localeStr) {
        String[] localeArgs = localeStr.split("-");
        Locale locale = new Locale(localeArgs[0], localeArgs[1]);

        tts.setLanguage(locale);
    }

    private boolean validateTtsInitialized() {
        if (tts == null || !ttsInitialized) {
            callbackContext.error(ERR_NOT_INITIALIZED);
            return false;
        }
        return true;
    }
}