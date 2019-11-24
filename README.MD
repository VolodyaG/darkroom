# Darkroom
##### Allows Valera and Volodya scan negatives with our old chinese film scanner in good resolution

#### Requirements:
* Kotlin 1.3.50+
* TornadoFx 13
* QPix Digital FS110 scanner

#### Features:
* Negative/positive and color/black-and-white film support
* Colors and shadows/highlights adjustments
* Contrast/brightness control
* Rotation and crop
* Magnifying glass

![Black-and-white](https://github.com/VolodyaG/darkroom/blob/master/src/main/resources/Screen%201.png)

![Color-with-crop](https://github.com/VolodyaG/darkroom/blob/master/src/main/resources/Screen%202.png)

![Positive-with-magnifier](https://github.com/VolodyaG/darkroom/blob/master/src/main/resources/Screen%203.png)

#### Flags and environment variables:
* **WITHOUT_SCANNER** - when set to true runs application with default test image
* **PERFORMANCE_DEBUG** - when set to true performance logs are shown
* **SELECT_ON_MOUSE_CLICK** - when set to true crop selection area can be drawn via mouse 

#### To do:
* [ ] Levels midpoint
* [ ] Handle scanner not connected error
* [ ] Dispose of scanner instance in app stop
* [ ] Edge detection for crop area
    * Edge detection algorithm
    * Neural network
* [ ] Horizon adjustment
* [ ] Tooltips