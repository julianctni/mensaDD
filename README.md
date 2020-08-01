# mensaDD
An Android application for student canteens in Dresden.

**Available on [Google Play Store](https://play.google.com/store/apps/details?id=com.pasta.mensadd).**


## API

The used API is protected by a key. So if you want to use the app by compiling it yourself, you won't have access to the canteen, meal and news data. Furthermore you need to get an access token for mapbox.
You have to add a file called `/res/values/mensadd_config.xml` with the following content:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="mapbox_access_token" type="string" translatable="false">[MAPBOX ACCESS TOKEN]</string>
    <string name="mapbox_style_url" type="string" translatable="false">[MAPBOX STYLE URL]</string>
    <string name="mapbox_style_url_dark" type="string" translatable="false">[MAPBOX STYLE URL DARK MODE]</string>
    <string name="mapbox_style_url" type="string" translatable="false">[MAPBOX STYLE URL DEFAULT MODE]</string>

    <string name="url_base" type="string" translatable="false">[BASE URL FOR API]</string>
    <string name="url_suffix_news" type="string" translatable="false">[API SUFFIX NEWS]</string>
    <string name="url_suffix_canteens" type="string" translatable="false">[API SUFFIX CANTEENS]</string>
    <string name="url_suffix_meals" type="string" translatable="false">[API SUFFIX MEALS FOR CANTEEN]</string>

    <string name="mensadd_api_key" type="string" translatable="false">[API KEY]</string>
</resources>
```
