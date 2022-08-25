# TCS3200 pyrometer
A pyrometer is a type of thermometer that uses light sensors to get radiation from a source and then process it to calculate the temperature. This is a non contact device.

The [TCS3200](https://www.mouser.com/catalog/specsheets/tcs3200-e11.pdf) is a low-cost color sensor that uses photodiodes. This sensor, is sensible to the radiation wavelength emitted by a heated body, and from [Wein's Law](https://en.wikipedia.org/wiki/Wien%27s_displacement_law) we know the emitted wavelength is related to the body's temperature.

To know the actual radiation wavelength of a body at high temperatures with the TCS3200 is very difficult because it can only measure from 300nm to 1100nm, and wavelengths at high temperatures tend to be greater than that. But as the sensor reads those wavelengths in the range, a temperature profile for an specific material can be obtained using a thermocouple. Then we can build a low-cost characterization pyrometer with the TCS3200:

<p align="center">
    <img src="https://user-images.githubusercontent.com/38321649/186570784-9054e781-6166-4432-bfc1-969b1c01d672.jpg" width="300" /> <img src="https://user-images.githubusercontent.com/38321649/186570799-5c7fa43b-d00b-4330-8858-c48ac628f4f1.png" width="300" />
</p>

# Usage
1. Characterize the material:
    1. Connect the material to a thermocouple and also place the color sensor towards it.
    2. Start heating the material.
    3. Use the command line tool written in Java at `/serial-recorder` along with the Arduino script in `/arduino/characterization` to get the measures of the color sensor and thermocouple to then save it to an excel file.
    ```bash
    ant -f ./serial-recorder/build.xml run
    ```
    4. With a [regression analysis](https://en.wikipedia.org/wiki/Regression_analysis), get a function that best describes the data.
2. Place the characterization function in `/arduino/operation/operation.ino`:

    ```arduino
    float frecuency2Temperature(int f){
      return ...;
    }
    ```

3. Receive the data over bluetooth
