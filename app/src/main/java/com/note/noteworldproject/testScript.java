package com.note.noteworldproject;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class testScript {
    private WebView webView;

    // Конструктор для передачи контекста и WebView
    testScript(Context context, WebView webView) {
        this.webView = webView;
    }

    // Метод, вызываемый из JavaScript
    @JavascriptInterface
    public void callFromJS() {
        // Вызываем функцию JavaScript из Java для изменения текста
        webView.post(() -> webView.loadUrl("javascript:changeText('hello--------');"));
    }
}