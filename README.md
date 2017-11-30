# Automatic File Renamer

A simple JavaFX application for batch-renaming files. Currently only a fixed length preceding string from file names is removed.
It could be used to remove text water-marks from files.

## Getting Started

There is a runnable .jar file in project root folder which should run on Windows/Linux/Mac without any other prerequisites except JRE 1.8+. 
The software consists of a single page GUI in which you can choose the target directory via drag & drop or fileChooser dialog. After choosing the fixed preceding string and 
clicking on start, files are renamed and an exportable log text is generated.

This project is done using Eclipse and e(fx)clipse plugin. You can setup Eclipse to import the project or you can use any other IDEs/methods and use the source files.
### Prerequisites

You need to have at least Java 8 (JRE 1.8) on your system.
```
One way to check your version of JRE is typing `java -version` in windows command line. 
```
## Authors

* **Pooya Moradi** - *Initial work* - [pvonmoradi](https://github.com/pvonmoradi)

## License

This project is licensed under the GNU GPLv3  - see the [LICENSE.txt](LICENSE.txt) file for details
