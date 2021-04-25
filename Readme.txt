Instructions to install and Run:
1) All sourecode is available under Parser/*java
2) Test Input file is available at "InputFile.txt"
3) Jar files required to compile are at Jars/ directory
(org.json.*)

4) Used following compiler to run this tool
java version "16.0.1" 2021-04-20
Java(TM) SE Runtime Environment (build 16.0.1+9-24)
Java HotSpot(TM) 64-Bit Server VM (build 16.0.1+9-24, mixed mode, sharing)

4) To compile at command prompt
javac -cp .;Jars\json-20210307.jar Parser\LogParser.java

5) To run

java -cp .;Jars\json-20210307.jar -DWorkDir=C:\Users -DInputFile=inputFile.txt -DOutputFile=error.json -DAlertWordFile=AlertWords.txt  Parser.LogParser