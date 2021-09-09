# embARC CLI (Metadata Embedded for Archival Content)

### About
embARC CLI is a free, open source application that enables users to audit embedded metadata of DPX and MXF files.

embARC, short for “metadata embedded for archival content,” is in active development by the Federal Agencies Digital Guidelines Initiative (FADGI) to support two major guideline projects:
- [Guidelines for Embedded Metadata within DPX File Headers for Digitized Motion Picture Film.](http://www.digitizationguidelines.gov/guidelines/digitize-DPXembedding.html)
- [SMPTE RDD 48: MXF Archive and Preservation Format](http://www.digitizationguidelines.gov/guidelines/MXF_app_spec.html)

#### DPX Usage
```java -jar [path/to/embARC-CLI.jar] [input] [output] [options]```

[input] = path to target DPX file or DPX sequence folder
[output] =
-csv <filepath/newfile.csv>        CSV formatted output
-json <filepath/newfile.json>      JSON formatted output
[options] =
         -print
         -conformanceInputJSON <arg>   Input validation json file
         -conformanceOutputCSV <arg>   Output validation csv file

#### MXF Usage
```java -jar [path/to/embARC-CLI.jar] [input] [options]```

[input] = path to target MXF file
[options] =
         -print                      Print file metadata to console
         -downloadTDStream <arg>   Write text data stream to local directory
         -downloadBDStream <arg>   Write binary data stream to local directory
         -streamOutputPath <arg>   Specify data stream output directory
