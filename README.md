# DVa
This is the code base for the [USENIX Security 2024](https://www.usenix.org/conference/usenixsecurity24) paper titled "[DVa: Extracting Victims and Abuse Vectors from Android Accessibility Malware](https://www.usenix.org/conference/usenixsecurity24/presentation/xu-haichuan)".
Its main functionality is extracting Android accessibility (a11y) malware's targeted victims, abuse vectors, and persistence mechanisms.
DVa consists of a primary static symbolic execution module to analyze the malware and a peripheral dynamic analysis framework to aid the loading of malicious payloads.

## Static Malware Analysis Module
This is the main component of DVa that reveals a11y malware's targeted victims, abuse vectors, and persistence mechanisms.
It relies on [Soot](https://soot-oss.github.io/soot/), an open-source Android static analysis framework.

### Setup

#### Hardware Requirements

1. A Linux Machine.
The framework was tested on a Ubuntu 20.04.6 LTS machine with a 24-core CPU and 128GB Memory.
However, DVa should work with any recent version of Ubuntu or any Debian 11 and up (64-bit) machines.

#### Software Requirements

1. Java 1.8 JDK.
The framework was tested with OpenJDK version 1.8.0_412 with Java Runtime Environment(JRE).
This can be installed by running:
```sh
sudo apt install openjdk-8-jdk
```

2. (Optional) Android SDKs.
For analyzing the malware in [Usage](#usage), no additional setup is required.
The required Android SDK's library reference is already included.
For analyzing additional malware that targets different Android SDK versions, place the corresponding SDK's `android.jar` file inside the `DVa/static_analysis/android_jar` directory.
For example, if you are to analyze malware targeting Android 14 (SDK version 34), you should:
    1. [Install Android Studio](https://developer.android.com/studio).
    2. [Install Android SDK version 34](https://developer.android.com/about/versions/14/setup-sdk#install-sdk).
    3. Extract Android SDK version 34's android.jar file. The file is located at `$ANDROID_SDK/platforms/platform-34/android.jar` where `$ANDROID_SDK` is usually `~/Android/Sdk`.
    4. Place the `android.jar` file in `DVa/static_analysis/android_jar/platform-34`.


### Usage

Next, we show an example of executing DVa to extract the targeted victims, abuse vectors, and persistence mechanisms of the pixstealer a11y malware.

1. `cd` to DVa's directory.
2. Run DVa's malware analysis module against the malware:
```sh
java -jar static_analysis/static_analysis.jar samples/pixstealer.apk $OUTPUT_PATH
```
3. Retrieve the malware analysis report at `$OUTPUT_PATH/pixstealer.json`.

### Interpretation of Reports

The report.json outputted by DVa shows:

1. Victim targets of the malware.
This is organized as the package names of the victim apps or the system service that the malware targets.
The victim target is shown under the "victim" key in the output json file together with its corresponding abuse vectors or persistence mechanisms.
For the pixstealer malware, you will see it targets the system "settings" app with multiple persistence mechanisms, and the Nubank (com.nu.production), and the Inter&Co Financial APP (br.com.intermedium) apps with multiple abuse vectors.
1. Abuse vectors of the malware.
This is shown under the "Abuse Vectors" key of the report and labeled as one of the a11y abuse vectors the malware uses to abuse each victim.
Each abuse vector is accompanied by the concrete data-flow that leads to the execution of the vector.
The vectors are all initiated from accessibility handlers of the malware and end with concrete a11y APIs.
For the pixstealer malware, you will see that it "steals credential", and conducts "automated transactions".
1. Persistence mechanisms of the malware.
This is shown under the "Persistence Mechanisms" key of the report and contains one of the a11y persistence mechanisms the malware adopts to hinder the user's removal of the malware.
Each mechanism is accompanied by traces of the mechanism triggers.
For the pixstealer malware, it prevents the a11y permission revocation and prevents info look up or uninstalling the malware.


## Peripheral Dynamic Anti-anti-analysis Module

<span style="color: red;">**WARNING: The steps below involve installing and executing malware, conducting live communication with malware C&C servers, and modifying and patching the ROM image of physical phones which all inherit intrinsic risks to the security of your device and privacy of your information on the device.
Collect and run active malware at your own risk.**</span>

For malware that utilize anti-analysis techniques such as packing, dynamic code loading, and victim scanning before deploying and executing their malicious behaviors, DVa offers a dynamic hooking framework to aid the process of revealing malware's abuse payloads.
After extracting the abuse payloads, run the [Static Malware Analysis Module](#static-malware-analysis-module) to extract a11y malware's targeted victims, abuse vectors, and persistence mechanisms.

### Setup

#### Hardware Requirements

1. Google Pixel 3 phones (with OEM unlock).
To maximize the chance of creating a valid execution environment of malware, DVa uses five physical Google Pixel 3 phones to execute the dynamic analysis. The phones run a patch version of Android 9 blueline image with version `image-blueline-pq1a.181105.017.a1`.
The original image can be obtained [here](https://developers.google.com/android/images#blueline).
Patch and flash the image when installing Magisk.

#### Software Requirements

1. adb.
Install the [android debug bridge (adb)](https://developer.android.com/tools/adb) on the host computer to command and control the phones from USB.
2. Python 3.10.12.
[Conda](https://conda.io/projects/conda/en/latest/user-guide/install/linux.html) is recommended for managing the Python environments.
3. Magisk.
[Install Magisk](https://topjohnwu.github.io/Magisk/install.html) on the phones to apply a patched ROM image, obtain root privilege, and allow the installation of dynamic hooking frameworks. 
4. EdXposed.
[Install EdXposed](https://github.com/ElderDrivers/EdXposed) as the dynamic hooking framework.

### Usage

1. Connect the Google Pixel 3 phones to the USB ports of the host machine.
2. Enable USB Debugging on the Google Pixel 3 phones.
3. Place collected malware APKs in `./samples`.
4. Run the dynamic analysis manager by executing:
```sh
python3 dynamic_analysis/dynamicManager.py
```