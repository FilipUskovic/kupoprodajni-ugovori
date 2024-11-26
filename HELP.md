# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.6/maven-plugin/build-image.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.3.6/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.3.6/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Spring Security](https://docs.spring.io/spring-boot/3.3.6/reference/web/spring-security.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.3.6/reference/web/servlet.html)

### Guides

       * Pokretanje (first steps)
   
Pokrenuti docker ako vec niste 
1. Spojiti se na bazu

2. dockerfile ako ne zelimo cekati build (cca oko 2 min) mozemo ga zacementirati ili ukolniti


3. Koraci za izgradnju docker image-a  (Docker dio)
   -> docker compose build
   -> docker compose up -d  (-d je za detach)
   -> spojiti se na bazu

Ili jednostavno ako korsiti IntelidijiIDEA editor pokrenuti u compose.yaml file s run 
ili  jos lake u editoru samo stisnuti run 

Optionalno

* "There are already Docker Compose services running "ako dobijete tu gresku ugasite ("stop") ->  kupoprodajniugovori-app-1
*  No ne bi trebala, i onda ponovno samo startajte app, i u bazi bi trebala biti kreiran schema i tablice


*  ako koriste swagger ui umjesot postman-a http://localhost:8081/swagger-ui/index.html


* peporucam mvn clean install kao prvi korak za clean state projekta i dependecija (ovisnosti) no opcionalno je


takoder odmah testirai docker konekcije/konfuguraciju kroz build koji ce nam pokazati postoje li neke greske
-> docker compose up --build



 -> viola




//TODO: Rijesit Validacije +
//TODO: dodat kesiranje -
//TODO: Security +
//TODO: Servis +
//TODO: Unit i integraciski test +
//TODO: dodati metrics - 
// TODO Continouse Integraction and Test -

 Ostale stvari


  ako nas zanmaju logovi
  -> docker logs <container_name> a da bi dobili kontainer name mozemo korsiti komandu "docker ps"



 Objasnjenja i neki tok misli  
  - Kao sto sam spomenuo ranije u pravome svjetu bi korsitio dev i prod profile s ne hardcodiranim vrijednostima vjv i neki tool
   za security kao npr vault (hashicorp) za spremanje osljetiljiv podatka i lozinik i autenitfikaciju. Isto tako korsiti bi .env file
   i ${env} env varijable jer je to najbolja praksa

(Hibernate i jpa)
 Audit i envers -> korisitm ih za biljezenje promjena i zapisa u bazi npr - (tko je kreirao na koji datum zapis u bazi itd)
 te kao nekaku "arhivu podatka" takoderm korisit hibernate orm za jednostavnije mapiranje objekta u relaciske baze i smanjene pisanja
 manualnih sql upita. Mogao sam koristi i alat kao loombok za jos dodatno smanjeje boilrplate koda no to ima svoje prednosti i mane a i smatram
 za ovakv jednstavno primjer nije potrebna dodatna kompleksnost i 3 party library

(Aduit i envers)
  dodao bi enverse za detaljnije pracanje promjena na bazi i revezije, te arhivske podatke, iako bi performansa baze bila sporija
  i zahtijevalo vise mjesta u bazi za vece projekte gdje zelimo revizije, detaljne promjene nad bazi vracanje stanja itd..
  no smatarm za ovu potrebno nije potrebno


(flyway)
 - koristim flyway za mograciju baza i nekak switch safe ako se dogode neke promjene ili greske u bazi da mozemo vratiti na 
   prijasnje stanje

 - integraciski controller test i servis logika unit test plus neke validacije , nisam provjerio sve use caseve niti sve validacije


 - dockerfile ako ne zelimo cekati build mozemo ga zacementirati ili ukolniti 
 - Dodao bi continuirano integraciju no nema potrebe a niti sam stigao 







