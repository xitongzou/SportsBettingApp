## Sports Betting App

This is a Java application that is very similar to the Stock simulator app I made back in 2010. 
This is intended to be used with Bovada.lv to help predict NBA basketball bets, and make the largest profit.
It needs an initial file with the NBA team records for all 30 teams to start with. This needs to be a few months worth of data, so usually
the first 3 months of the NBA season is fine. Then based on that analysis it will try to predict the outcome of each game in the NBA schedule 
depending on the record of that team and the opposing team and the margins of the wins it has won by in the past.

Currently, since there is no reliable NBA game score API, I have to manually input past game scores by hand for the initial input file. Thereafter you have to input the rest of the NBA schedule for the rest of the days in the season and then the app will try to compute the outcome of the match for each day. 

-Tong Zou