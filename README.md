# dividend-reformulater
Create a LibreOffice Calc spreadsheet using dividend data from a consolidated 1099 PDF tax form

## Installation Instructions
The following software needs to be installed in order to build `dividend-reformulater`:

* [Git](https://git-scm.com/downloads)
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
cd dividend-reformulator
mvn compile jar:jar
```

### Test the App
Try running the app:

```Shell
cd target
java -jar dividend-reformulater-2024.0.0-SNAPSHOT.jar
```
