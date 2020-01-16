
You can set up this framework as dependency for your own project.

----- IVY -----
  
  1. resolver (inside ivysettings.xml) :
  
  <url name="sf_repo"> 
    <ivy pattern="http://sourceforge.net/projects/jsonnodetree/files/repo/[organisation]/[module]/[revision]/[type]s/[artifact].[ext]" />
    <artifact pattern="http://sourceforge.net/projects/jsonnodetree/files/repo/[organisation]/[module]/[revision]/[type]s/[artifact].[ext]" />
  </url> 

  2. dependency (inside ivy.xml) :
  
  <dependency org="com.ak" name="jsontree" rev="1.0.0" />

  3. example (template) of ivy.xml for your project depended on jsontree JAR :

<?xml version="1.0" encoding="ISO-8859-1"?>
<ivy-module version="2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:noNamespaceSchemaLocation="http://ant.apache.org/ivy/schemas/ivy.xsd">
  <info
    organisation="com.yourcompany"
    module="your.project"
    status="integration"
    revision="1.0.0" >
  </info>

  <dependencies>
    <dependency org="com.ak" name="jsontree" rev="1.0.0" transitive="false" />
  </dependencies>
</ivy-module>


----- MAVEN -----

  1. repository to resolve dependency (inside pom.xml) :
  
  <repositories>
    <repository>
      <id>jsontree</id>
      <name>Json tree repository</name>
      <url>http://sourceforge.net/projects/jsonnodetree/files/repo</url>
      <layout>default</layout>
    </repository>
  </repositories>

  2. dependency (inside pom.xml) :

    <dependency>
      <groupId>com.ak</groupId>
      <artifactId>jsontree</artifactId>
      <version>1.0.0</version>
    </dependency>

  3. example (template) of pom.xml for your project depended on jsontree JAR :

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
     
  <groupId>YOUR PROJECT</groupId>
  <artifactId>test-jsontree</artifactId>
  <version>1.0.0</version>
  <packaging>jar</packaging>

  <dependencies>
    <dependency>
      <groupId>com.ak</groupId>
      <artifactId>jsontree</artifactId>
      <version>1.0.0</version>
    </dependency>
    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
    </dependency>
  </dependencies>
  
  <repositories>
    <repository>
      <id>jsontree</id>
      <name>Json tree repository</name>
      <url>http://sourceforge.net/projects/jsonnodetree/files/repo</url>
      <layout>default</layout>
    </repository>
  </repositories>

</project>

   