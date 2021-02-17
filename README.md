Program requires the following cmd line parameter to run properly.
```wishSourceFilePathname```
Single value, first entry is used.

At the moment wishes within xml should be supplied with the following date format
'dd/MM/yyyy'. Otherwise, the date of the processing will be assigned.

By default, in memory H2 db is provided and accessible under (access can be configured by properties file): 
```http://localhost:8080/h2-console```

Example windows PShell run:
```
.\\mvnw.cmd spring-boot:run -D"spring-boot.run.arguments=--wishSourceFilePathname=C:\\pathToFileWithWishes.xml"
```
By default, application operates on in memory h2 db, to achieve persistence across multiple runs please override the

```spring.datasource.url``` parameter. Example:
```
.\\mvnw.cmd spring-boot:run -D"spring-boot.run.arguments=--wishSourceFilePathname=C:\\wish2.xml --spring.datasource.url=jdbc:h2:file:C:/data/sample"
```

Db credentials can be found in application.properties file.

Xml structure example:
```
<?xml version="1.0" encoding="UTF-8"?>
<wishes>
    <wish>
        <child>
			<name>Jane</name>
			<surname>Doe</surname>
		</child>
        <text>I wish for a teddy.</text>
        <datetime>12/11/2021</datetime>
    </wish>
    <wish>
        <child>
			<name>Jhon</name>
			<surname>Doe</surname>
		</child>
        <text>I wish for a car.</text>
        <datetime>11/11/2021</datetime>
    </wish>
</wishes>
```