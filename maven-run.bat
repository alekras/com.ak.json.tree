SET HOME=%USERPROFILE%
SET HOMEDRIVE=C:
SET JAVA_HOME=C:\Software\jdk1.7.0_76
SET PATH=%PATH%;c:\Software\apache-maven-3.3.3\bin
SET
cd c:\Users\akrasnop\ECLIPSE\workspace\com.ak.json.tree
start cmd

mvn source:jar package
mvn source:jar install