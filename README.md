# AXPathLoadingView
 A simple android view to create loading with a Path. <br> Designed for [open-watch](https://github.com/SMotlaq/open-watch) project.
 
<img src="./1.gif" height=200 title="AXPathLoadingView"> <img src="./2.gif" height=200 title="AXPathLoadingView">

## Usage

```xml
<com.aghajari.axpathloading.AXPathLoadingView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="24dp"
    app:alphaStart="0.68"
    app:path="#createHR"
    app:progressColor="#0AB28C"
    app:thickness="11dp"
    app:trackColor="#300AB28C" />
    
<com.aghajari.axpathloading.AXPathLoadingView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="24dp"
    app:animationDuration="1800"
    app:delay="200"
    app:path="#createHeart"
    app:progressColor="#C5227A"
    app:thickness="16dp"
    app:trackColor="#30C5227A"/>
```

```java
public static Path createHR(AXPathLoadingView view) {
    float h = view.getResources().getDisplayMetrics().density * 80;
    float w = view.getResources().getDisplayMetrics().density * 112;
    float part = w / 4;
    float center = h / 2f;

    Path path = new Path();
    path.moveTo(0, center);
    path.lineTo(part, center);
    path.lineTo(part + part / 2f, h);
    path.lineTo(2 * part, center);
    path.lineTo(2 * part + part / 2f, 0);
    path.lineTo(3 * part, center);
    path.lineTo(4 * part, center);
    return path;
}
```

License
=======

    Copyright 2022 Amir Hossein Aghajari
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


<br><br>
<div align="center">
  <img width="64" alt="LCoders | AmirHosseinAghajari" src="https://user-images.githubusercontent.com/30867537/90538314-a0a79200-e193-11ea-8d90-0a3576e28a18.png">
  <br><a>Amir Hossein Aghajari</a> • <a href="mailto:amirhossein.aghajari.82@gmail.com">Email</a> • <a href="https://github.com/Aghajari">GitHub</a>
</div>
