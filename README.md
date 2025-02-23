# dividend-reformulater
Create a LibreOffice Calc spreadsheet using dividend data from a consolidated 1099 PDF tax form

## Overview
The `dividend-reformulater` app might be useful to those investors who have a brokerage account and file federal and state income taxes in the United States.
The app reformulates the dividend data from a consolidated 1099 PDF into a LibreOffice Calc spreadsheet with multiple sheets and pivot tables.
The main goals of the app are to make it easier to:

1. calculate the amount of dividend income from U.S. government securities,
2. calculate the amount of dividend income from foreign sources, and
3. understand how the values on Form 1099-DIV are calculated.

The consolidated 1099 PDFs that may be compatible with the app have pages with a **Detail for Dividends and Distributions**
header and, optionally, pages with a **Mutual Fund and UIT Supplemental Information** header.
Multiple brokerage firms use this format.

Currently, the app can handle a consolidated 1099 PDF that has a Form 1099-DIV with non-zero data in only the following boxes:

* 1a - Total ordinary dividends
* 1b - Qualified dividends
* 2a - Total capital gain distributions
* 2b - Unrecaptured Section 1250 gain
* 3 - Nondividend distributions
* 5 - Section 199A dividends
* 7 - Foreign tax paid
* 12 - Exempt-interest dividends

The `dividend-reformulater` app has been tested on 64-bit Linux and 64-bit Windows systems.
The app has not been tested with LibreOffice as Flatpak, Snap, AppImage, etc.

## Installation Instructions
The `dividend-reformulater` app is a Java application that is typically installed by extracting files from a distributable archive, keeping the directory structure that is in the archive.
Distributable archives are attached to releases published on Github.
Distributable archives can also be created by end users; see [How to Build the App](/../../wiki/How-to-Build-the-App) in the Wiki.

The following software should be installed before attempting to run the `dividend-reformulater` app:

* A build of OpenJDK 17 or higher, such as [Amazon Corretto 17](https://aws.amazon.com/corretto/)
* [LibreOffice](https://www.libreoffice.org/download/download-libreoffice/) (24.8.x is recommended)

### Test the App
Test the `dividend-reformulater` app by running the app without any arguments:

```Shell
java -jar dividend-reformulater-2024.0.0.jar
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
