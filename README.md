# RisingVoiceIndicator

🔵🔴🟡🟢 A voice animation view like google assistant voice indicator for android.

## Usage

### In layout
```xml
    <com.risingpark.risingvoiceindicator.RisingVoiceIndicator
        android:id="@+id/voice_indicator"
        android:layout_centerInParent="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:radius="5dp"
        app:ball_colors="@array/ball_colors"/>
```
### Attributes
|Attributes|format|describe
|---|---|---|
|radius|dimension| circle radius size
|ball_colors|array| resource colors. The circle increases as the color increases.

### In code

voice_indicator start at the start of recording.
```kotlin
voice_indicator.start()
```

(option) set random decibel automatically.
```kotlin
voice_indicator.start(VoiceIndicator.START_SYSTEM)
```

voice_indicator stop at the stop of recording.
```kotlin
voice_indicator.stop()
```

Decibel setting during recording.
```kotlin
voice_indicator.setDecibel()    // input float
```


# License
```xml
Copyright 2020 RisingPark

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```