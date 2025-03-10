# dividend-reformulater
Create a LibreOffice Calc spreadsheet using dividend data from a consolidated 1099 PDF tax form

## Overview
The app reformulates the dividend data from a consolidated 1099 PDF into a LibreOffice Calc spreadsheet with multiple sheets and pivot tables.
The main goals of the app are to make it easier to:

1. calculate the amount of dividend income from U.S. government securities,
2. calculate the amount of dividend income from foreign sources, and
3. understand how values, like *Total ordinary dividends*, on Form 1099-DIV are calculated.

The consolidated 1099 PDFs that may be compatible with the app have pages with a **Detail for Dividends and Distributions**
header and, optionally, pages with a **Mutual Fund and UIT Supplemental Information** header.
Multiple brokerage firms use this format.

The app can handle a consolidated 1099 PDF that has a Form 1099-DIV with non-zero data in the following boxes only:

* `1a` &ndash; Total ordinary dividends
* `1b` &ndash; Qualified dividends
* `2a` &ndash; Total capital gain distributions
* `2b` &ndash; Unrecaptured Section 1250 gain
* `3 ` &ndash; Nondividend distributions
* `5 ` &ndash; Section 199A dividends
* `7 ` &ndash; Foreign tax paid
* `12` &ndash; Exempt-interest dividends

The app has been tested on 64-bit Linux and 64-bit Windows systems.
The app has not been tested with LibreOffice as Flatpak, Snap, AppImage, etc.

## License
The `dividend-reformulater` app is licensed under the [MIT License](/LICENSE).

## Installation Instructions
### Prerequisites
Before attempting to run the app, install the following software:

1. A build of OpenJDK 17 or higher, such as [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17) or [Amazon Corretto 17](https://aws.amazon.com/corretto/)
   - A JDK download can be used to build and/or run the app.
   - A JRE download is smaller and can be used to run the app, but not build it.
2. [LibreOffice](https://www.libreoffice.org/download/download-libreoffice/) (24.8.x is recommended)

### Option 1: Install a Build of the App
Builds of the app are available for Linux, MacOS, and Windows.

1. From the [Releases](/../../releases) page, download the `.tar.gz` or `.zip` distributable archive and the `.sha256` file for your operating system.
2. Verify the integrity of the downloaded archive (e.g., on Linux, run `sha256sum -c dividend-reformulater-2024.0.5-linux.tar.gz.sha256` from a terminal).
3. Extract the the files from the `.tar.gz` or `.zip` archive, keeping the directory structure that is in the archive.

### Option 2: Build the App
See [How to Build the App](/../../wiki/How-to-Build-the-App) in the Wiki.

### Test the App
Test the app by running the app without any arguments from a terminal. For example,

```Shell
java -jar dividend-reformulater-2024.0.5.jar
```

If the app was packaged properly for the system, the app displays the following message:

```
Usage: java -jar dividend-reformulater.jar <consolidated-1099.pdf>
```

However, if the app fails with a message like

```
java.lang.ClassNotFoundException: com.sun.star.comp.helper.Bootstrap
```

verify that the LibreOffice installation has a `libreoffice.jar` file.
If the file exists, then either the wrong distributable archive was installed or the app needs to be repackaged with the
correct absolute `file` URL of the `libreoffice.jar` file.
See the **Troubleshooting** section on the [How to Build the App](/../../wiki/How-to-Build-the-App) page in the Wiki.

## Running the App
When a consolidated 1099 PDF is passed to the app, the app will launch LibreOffice Calc and populate multiple sheets.
When the app has finished running, the `form-1099-div` sheet should be visible.
The `form-1099-div` sheet is there as a sanity check;
the user should verify that the values on the sheet match the values on the Form 1099-DIV in the consolidated 1099 PDF.

Note that the app does not save the spreadsheet. It is up to the user to decide whether or not to save the spreadsheet.
