# RisingVoiceIndicator


## Usage

### RisingVoiceIndicator in layout
```xml
    <com.risingpark.risingvoiceindicator.RisingVoiceIndicator
        android:id="@+id/voice_indicator"
        android:layout_centerInParent="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:radius="5dp"
        app:ball_colors="@array/ball_colors"/>
```

|Attributes|format|describe
|---|---|---|
|radius|dimension| circle radius size
|ball_colors|array| resource colors. The circle increases as the color increases.
