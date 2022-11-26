package com.example.photoeditor4.Interface;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onEditStarted();
    void onEditCompleted();
}
