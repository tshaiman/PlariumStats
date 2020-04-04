##README - Needs Fixing Items

1. incorrect template : 
        - age should be int not string, 
        - event_ts key should have quote for legal json string ("event_ts":xxx not event_ts:xxx) 
        - extra double quotes at the end of line

2. Business Story :  It is important to note that a certain user record can appear many times as he progress in the game.
(event if the generated template will not do it ).

3. average_level_per_country : Actually its not average. you wrote "the MAX for each country".

4. consider using the avg_level_per_country response from 
   countries : ["NY":10,"AZ":14] 
to 
   countries : {"NY":10,"AZ":14}
since the json format is more standard than array of un-unified keys , or maybe even :
   countries : [{"code":"NY","value":10},{"code":"AZ","value":10]



Great Exercise , I really enjoyed it .