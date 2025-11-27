# Location saisonnière - cote basque.
projet d'étude informatiques n°3 de Open Class Room

### Limites du projet

* Pas de gestion de pagination sur la liste des locations
* Utilisation de champs Integer pour les Id 
* Pas de protection des images (le token n'est pas envoyé.)
* Seuls les fichiers jpeg de moins de 8Mo sont autorisés pour la saisie 
* Mot de passe en clair entre le front end et le back end

### Points forts du projet

* Les mots de passe sont hachés et salés en base de données.
* Utilisation d'un token JWT pour se connecter.
* Architecture en couche endpoint, service et accès aux données.
* Utilisation de GlobalExceptionHandler qui attrape les exceptions 

### Spécifications techniques

Je suppose que ces logiciels sont installés et configuré.
* Java 21 (le JDK) installé sur le PC (pour le back end )
* Eclipse pour java et web developper (2025-06)
* Angular 14 pour le front end
* mysql 8.0.4 pour la base de données avec un compte admin
* openSSL pour générer les clefs publique et privées (MINGW64 dans l'outil git cmd fera parfaitement l'affaire)
* + accès en lecture / écriture à 1 répertoire front-end 
* + accès en lecture / écriture à 1 répertoire back-end à mettre dans votre répertoire eclispe-workspace

### Procédure d'installation : 

##### Pour le Front end	

* Télécharger le projet à cette adresse : [https://github.com/OpenClassrooms-Student-Center/Developpez-le-back-end-en-utilisant-Java-et-Spring](https://github.com/OpenClassrooms-Student-Center/Developpez-le-back-end-en-utilisant-Java-et-Spring)
* le coller dans le répertoire front-end
* changer le fichier src/proxy.config.json : remplacer `localhost:3001` par `localhost:8080`  (8080 sera le port du back end)
* Exécuter la commande : `npm install` à la racine du front end
* Exécuter la commande `ng serve` pour démarrer le front end

##### Pour le Back End

* Télécharger le projet à cette adresse : [https://github.com/franck-g75/ocr-p3-chatopbackend](https://github.com/franck-g75/ocr-p3-chatopbackend)
* Le coller dans le répertoire back-end

##### Pour la base de données

* Créer une base de donnée nommée **chatop** avec les paramètres UTF-8-general-ci
* Noter le nom de la nouvelle base (chatop) ainsi que le login et mot de passe pour paramétrage ultérieur.
* Exécuter le code SQL situé dans le fichier `src/main/resources/sql/script.sql` dans la base nouvellement créée

##### Génération des cles privées et publiques

Utilisez l'outil git en ligne de commande : **GIT CMD** qui continent MINGW64 qui possède le programme openSSL. (Vous pouvez aussi utiliser cygwin pour générer les clefs.)

* Taper `cd [votre répertoire racine du back end]\src\main\resources`

* Exécuter la commande suivante pour générer la clé privée dans le fichier chatop_private_key :

		`openssl genrsa -out chatop_private_key.pk 2048`
* Exécuter la commande suivante pour générer la clé publique dans le fichier chatop_certificate.pem

		`openssl req -new -x509 -key chatop_private_key.pk -out chatop_certificate.pem -days 365`

Les clés privées et publiques sont dans le répertoire de base du fichier de paramétrage.

##### paramétrage  `src/main/resource/application.properties`

* Créer un répertoire `tomcat.8080` dans votre répertoire eclipse-workspace
* Changer le répertoire de base du back end `server.tomcat.basedir` pour correspondre avec le répertoire nouvellement créé.

* Créer un répertoire `image` dans eclipse-workspace\tomcat.8080\work\Tomcat\localhost\ROOT\
* Configurer le paramétrage de l'emplacement des images `image.basedir` pour correspondre avec le répertoire nouvellement créé.

* Pour paramétrer les repertoires : ne pas oublier de doubler les anti slash.


* Régler l'url de la source de données grace au paramètre `spring.datasource.url` (3306 étant le port par défaut) 
* Régler les spring.datasource.username à `defaultuser`
* Régler la variable spring.datasource.password à `defaultpassword`

Exemple de fichier de paramétrage... modifier les propriétés pour les faire correspondre à votre environnement

```
#src/main/resource/application.properties
spring.application.name=ChaTopBackEnd

#redefining root directory
server.tomcat.basedir=C:\\mes-dev\\projects\\eclipse-workspace\\tomcat.8080

#defining image base directory
image.basedir=work\\Tomcat\\localhost\\ROOT\\image
image.baseurl=http://localhost:8080/image

#Log level
# TRACE > DEBUG > INFO > WARN > ERROR
logging.level.root=DEBUG
logging.level.org.springframework=TRACE
logging.level.org.springframework.security=TRACE
logging.level.com.chatop.ChaTopBackEndApplication=TRACE

#BearerToken security
jwt.private.key=classpath:./chatop_private_key.pk
jwt.public.key=classpath:./chatop_certificate.pem

#db connectivity
spring.jpa.hibernate.ddl-auto=none
spring.datasource.url=jdbc:mysql://localhost:3306/chatop
spring.datasource.username=defaultuser
spring.datasource.password=defaultpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql: true

#size of request and file (for the images) 
spring.servlet.multipart.max-request-size=8192KB
spring.servlet.multipart.max-file-size=8192KB
```

exemple de fichier src/main/resource/logback-spring.xml pour régler les niveaux de log

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
    		<pattern>%black(%d{HH:mm:ss.SSS}) %highlight(%-5level) [%blue(%t)] %logger{36} - %msg%n%throwable</pattern>
    	</encoder>
    </appender>
    
    <!-- LOG everything at INFO level -->
    <root level="debug">
        <appender-ref ref="Console" />
    </root>

	<logger name="org.springframework" level="trace" additivity="false">
        <appender-ref ref="Console" />
    </logger>
    
    <logger name="org.springframework.security" level="trace" additivity="false">
        <appender-ref ref="Console" />
    </logger>
    
    <logger name="com.chatop.ChaTopBackEndApplication" level="trace" additivity="false">
        <appender-ref ref="Console" />
    </logger>
    
</configuration>
```

##### compilation et lancement du programme

* Réperer la racine du projet
* Cliquer sur le bouton droit (a la racine du projet)
* Cliquer sur **Run as** 
* Cliquer sur **maven build...** une fenêtre s'ouvre
* Dans la zone de texte **goals**, saisir `spring-boot:run`
* Dans l'onglet environment, ajouter 2 variables `SPRING_DATASOURCE_USERNAME` et `SPRING_DATASOURCE_PASSWORD` qui contiennent les **login** et **mot de passe** de la base de données chatop.
* Cliquer sur le bouton **run** du bas de la fenêtre
* Le programme se lance : le développeur peut voir les logs du serveur s'afficher dans la console.
* Enfin, taper [http://localhost:4200/](http://localhost:4200/) dans un navigateur pour voir s'afficher la première page du site.

##### Note d'aide à l'éxécution

* Commencer par créer un compte en cliquant sur register. Puis se logger en cliquant sur login.
* Attention : seuls les fichiers jpeg de moins de 8Mo sont autorisés pour la saisie 

## Documentation de l'API


L'API peut etre testée à l'adresse ci dessous :
* Url du site : [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

