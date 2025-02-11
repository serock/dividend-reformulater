# dividend-reformulater
Create a LibreOffice Calc spreadsheet using dividend data from a consolidated 1099 PDF tax form

## Overview
The `dividend-reformulater` app might be useful to those investors who have a brokerage account and file federal and state income taxes in the United States.
The app reformulates the dividend data in a consolidated 1099 PDF into multiple tables and pivot tables in a LibreOffice Calc spreadsheet.
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

The `dividend-reformulater` app has been tested on 64-bit Linux and 64-bit Windows.
The app has not been tested with LibreOffice as Flatpak, Snap, AppImage, etc.

## Installation Instructions
The following software needs to be installed in order to build `dividend-reformulater`:

* [Git](https://git-scm.com/downloads) [^1]
* A build of OpenJDK 17 or higher, such as [Amazon Corretto 17](https://aws.amazon.com/corretto/)
* [LibreOffice](https://www.libreoffice.org/download/download-libreoffice/) (24.8.x recommended)
* [Apache Maven](https://maven.apache.org/)

### Download the Source Code
Use Git to download the source code:

```Shell
git clone https://github.com/serock/dividend-reformulater.git
```

### Build the App
Use Maven to build and package the app:

```Shell
cd dividend-reformulater
mvn package
```

If the build was successful, Maven displays the following message:

```
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Test the App
Try running the app:

```Shell
cd target
java -jar dividend-reformulater-2024.0.0-SNAPSHOT.jar
```

If the app is set up properly, the app displays the following message:

```
Usage: java -jar dividend-reformulater.jar <consolidated-1099.pdf>
```

### Running the App

When a consolidated 1099 PDF is passed to the app, the app will launch LibreOffice Calc and populate multiple sheets.
When the app has finished, the `form-1099-div` sheet should be visible.
The `form-1099-div` sheet is there as a sanity check;
the user should verify that the values on the sheet match the values on the Form 1099-DIV in the PDF.

Note that the app does not save the spreadsheet. It is up to the user to decide whether or not to save the spreadsheet.

## Troubleshooting

If the build failed with messages like

```
[ERROR] Failed to execute goal on project dividend-reformulater: Could not resolve dependencies for project com.github.serock:dividend-reformulater:jar:2024.0.0-SNAPSHOT
[ERROR] dependency: org.libreoffice:libreoffice:jar:24.8.4 (system)
[ERROR]     Could not find artifact org.libreoffice:libreoffice:jar:24.8.4 at specified path
```

then Maven is not finding the `libreoffice.jar` file.

The `pom.xml` file instructs Maven to use the `libreoffice.home` property to locate the `libreoffice.jar` file.
The value of the `libreoffice.home` property is the absolute path to the LibreOffice installation.
The `pom.xml` file sets the `libreoffice.home` property to `/usr/lib64/libreoffice` on Linux and to
`/C:/PROGRA~1/LibreOffice` on Windows.
Note that the value of the `libreoffice.home` property must not have any spaces.

To fix the build, locate the LibreOffice installation on your system and override the `libreoffice.home` property by changing the Maven command to:

```Shell
mvn -Dlibreoffice.home=<absolute-path-to-libreoffice> compile jar:jar
```

[^1]: Technically, Git is not really needed because you can [download a ZIP](https://github.com/serock/dividend-reformulater/archive/refs/heads/main.zip) of the source code.
  However, using Git will make it easier to keep your copy of the source code up to date.