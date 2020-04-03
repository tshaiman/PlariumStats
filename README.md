##Welcome to Plarium's data Infrastructure Team

#### Exam global description

A main component in our Platform is Kafka. Kafka acts as a async layer between data producers and consumers,
it allows the offline jobs to be decoupled from upstream traffic.
In this exam you will implement a service the expose general business metrics that consumed by our clients.   

The service should expose business values for the game servers the affects the game behavior, allows the game developers to 
tune the game based on global metrics calculate in the data platform.

#### About this project

This project contains scala sbt project with AKKA HTTP  as a serving layer, please implement the service as required and if you want/need to add more lib to the project you can do that just explain why.  

docker-compose.yaml that loads the following services:

   * kafka-simulator - a microservice that generates random data based on template to Kafka container (the topic name is "events")
   * zoo - a zookeeper service to manage kafka metadata
   * kafka1_0 - a Kafka broker

To run the docker-compose run the following command in the root folder:
```
 docker-compose up
```
Some events examples:
```
{"name": "Heath Young","age": "60","last_game_level":19, "level_attempts":  5 ,"email": "heath.young@hivemind.club",event_ts:1567660341,"country_code": "RO","is_depositor": true ,"user_uuid":"feb71252-03ac-4203-8bdc-fa56c5b374f1"" }
{"name": "Aiden Furlough","age": "46","last_game_level":2, "level_attempts":  6 ,"email": "aiden.furlough@solexis.club",event_ts:1563529474,"country_code": "RO","is_depositor": true ,"user_uuid":"4ef0227d-dd9d-4f6f-9e55-5801d09145c2"" }
{"name": "Rico Smit","age": "35","last_game_level":5, "level_attempts":  8 ,"email": "rico.smit@hassifix.club",event_ts:1575919829,"country_code": "FR","is_depositor": false ,"user_uuid":"fb13266c-fb35-4275-9a75-2d81e2ef6bf1"" }
{"name": "Raymond Correa","age": "57","last_game_level":18, "level_attempts":  4 ,"email": "raymond.correa@vtgrafix.co",event_ts:1564172827,"country_code": "TZ","is_depositor": true ,"user_uuid":"90b8dc2d-9194-4d3a-951e-9c310bc7b333"" }
{"name": "Jason Starck","age": "45","last_game_level":6, "level_attempts":  7 ,"email": "jason.starck@cryosoft.net",event_ts:1566300840,"country_code": "NP","is_depositor": true ,"user_uuid":"f7df714a-614b-4852-a6bf-e171784398ff"" }
{"name": "Andy Starck","age": "54","last_game_level":4, "level_attempts":  8 ,"email": "andy.starck@galcom.xyz",event_ts:1563756538,"country_code": "EE","is_depositor": true ,"user_uuid":"75fca6bc-77dd-4c83-968b-5baf19f2abdc"" }
{"name": "Myong Zobel","age": "18","last_game_level":19, "level_attempts":  7 ,"email": "myong.zobel@logico.eu",event_ts:1554230190,"country_code": "UZ","is_depositor": false ,"user_uuid":"f00c9afa-a743-4755-8811-ab35fee00372"" }
{"name": "Rolf Gamble","age": "55","last_game_level":8, "level_attempts":  5 ,"email": "rolf.gamble@multiserv.net",event_ts:1570870803,"country_code": "SS","is_depositor": false ,"user_uuid":"aee6e709-f6a1-4e15-b716-efbe8ef65170"" }
{"name": "Chloe Cappel","age": "41","last_game_level":3, "level_attempts":  4 ,"email": "chloe.cappel@qualcore.info",event_ts:1561928446,"country_code": "PY","is_depositor": true ,"user_uuid":"db47b751-8177-4ecf-b038-87e53641c5ef"" }
{"name": "Theo Zobel","age": "33","last_game_level":8, "level_attempts":  5 ,"email": "theo.zobel@celmax.co",event_ts:1558091485,"country_code": "MO","is_depositor": true ,"user_uuid":"52e92d9b-9837-406b-aa82-44e841558f93"" }
{"name": "Joslyn Whitson","age": "35","last_game_level":18, "level_attempts":  4 ,"email": "joslyn.whitson@airconix.mobi",event_ts:1571810915,"country_code": "ZM","is_depositor": true ,"user_uuid":"f1f54f88-0d83-4c04-8348-c1ff6099968b"" }
```

To consume the messages from Kafka you can download the following like: https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka_2.12-2.3.0.tgz  
Extract the compressed file and execute the following command to see the events:

```
 ./bin/kafka-console-consumer.sh --bootstrap-server 0.0.0.0:9092 --topic events
```

In you're implementation please make sure you're modeling the Actors as needed, think about how you can expend the service, and give the best way to store and extract the metrics. 

#### Task Definition

You're mission is to create a gateway that exposes HTTP REST service to extract the business metrics

Some global events definition:  
- last_game_level: The last level the player played in the game
- level_attempts: The number of times the player tried to finish the game level (As more attempts needed to pass the level considered harder)
- depositor: A player that deposit at least once in the game 

#####Routes Definition:

- /average_players_age - the service should expose the current average for the players until now:
```
    Request 
      - GET /average_players_age 
    Response 
      Status: 200 OK
       Body:
        {
            "average_players_age": 19
            "time": 1427970978000
            "fetched_records": 100
        }
```
- /average_level_per_country - 
service should return the maximum game levels players reached for each country.  
country_code parameter - if supplied service should filter only the country_code requested, if empty return all countries values
                       
```
    Route Params:
    Name            Type       IsMendetory 
    country_code    String     false

 --------------------------------------  
  Request 
     - GET /average_level_per_country
  Response 
    Status: 200 OK
    Body:
    {
        "counties": [
            "NY: 10,
            "AZ": 14,
        ]
        "time": 1427970978000
        "fetched_records": 100
    }
--------------------------------------
  Request    
    GET /average_level_per_country 
    Params ["country_code":"NY"]
  Response 
    Status: 200 OK
    Body:
    {
        "counties": [
            "NY: 10,
        ]
        "time": 1427970978000
        "fetched_records": 100
    }    
----------------------------------------
```
- /hardest_levels - The service should expose the 2 levels with the max level_attempts.  
  The param filter_depositors indicates that the respond should include values only of depositors users or all data  
 
  For example:  
```
  Data: 
  
  { "level":1, "level_attempts": 2, is_depositor: true }
  { "level":1, "level_attempts": 4, is_depositor: false  }
  
  Response: 
  
  case when filter_depositors == false
    return [ "level": 1, "level_attempts": 4 ]
    
  case when filter_depositors == true //only depositors
      return [ "level": 1, "level_attempts": 2 ]
```

Request Examples: 

```
    Request Params
    Name              Type         default 
    filter_depositors Boolean       false       
--------------------------------------
  Request    
    GET /hardest_levels 
    Params ["filter_depositors":"false"]
  Response 
    Status: 200 OK
    Body:
    {
        "levels":[
            {
                "level": 10
                "max_attempts": 10
            },
            {
                "level": 18
                "max_attempts": 9
            }
        ]
        "time": 1427970978000
        "fetched_records": 100
    }
```

All response should return the following:  
- time: epoch time of the response in milliseconds.
- fetched_records:  How many records the service fetched until the request happened.

After writing the service please containerize you're service and add it to be part of the docker-compose.yaml as a gateway server, prepare and configure Kubernetes deployment file that can run on minikube and in the cloud 

####*Some Technical Mitigation*

- All metrics can be stored in memory, choose the right data structure to expose the data as fast as possible.
- You're service can be a single instance.
- While coding the solution try to think about is that possible make the service stateless? how ?
- If app gos down it can start calculating the the metrics from the time service stated.

##Good Luck