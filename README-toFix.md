##README - Needs Fixing Items

1. incorrect template : 
        - age should be int not string, 
        - event_ts key should have quote for legal json string ("event_ts":xxx not event_ts:xxx) 

2. Business Story :  It is important to note that a certain user record can appear many times as he progress in the game.
(event if the generated template will not do it ).

3. average_level_per_country : Actually its not average. you wrote "the MAX for each country".

4. if you take zookeeper from confluent, take kafka from confluent as well and use latest version (5.4.0)

5. consider using the standard docker-compose templates with the standard advertised ports for example from confluent example repo :
https://github.com/confluentinc/demo-scene/blob/master/no-more-silos/docker-compose.yml

6. consider using the avg_level_per_country response from 
   countries : ["NY":10,"AZ":14] 
to 
   countries : {"NY":10,"AZ":14}
since the json format is more standard than array of un-unified keys , or maybe even :
   countries : [{"code":"NY","value":10},{"code":"AZ","value":10]

6. typo : "After writing the service please containerize you're service" -=> "After writing the service please containerize YOUR service"


Great Exercise , I really enjoyed it .