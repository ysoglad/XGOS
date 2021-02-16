# Deploy
Program requires the following cmd line parameter to run properly.
```wishSourceFilePathname```
Single, first entry is used.

At the moment wishes within xml should be supplied with the following date format
'dd/MM/yyyy'. Otherwise, the date of the processing will be assigned.

By default, in memory H2 db is provided and accessible under (access can be configured by properties file): 
```http://localhost:8080/h2-console```

Example windows PShell run:
```
.\\mvnw.cmd spring-boot:run -D"spring-boot.run.arguments=--wishSourceFilePathname=C:\\pathToFileWithWishes.xml"
```

