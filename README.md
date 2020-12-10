# SmashBracket (Name Not Final)
### Created by: Matthew Krauskopf

## Description
Over the last 5 years and before the Covid-19 pandemic, I was a frequent entrant for Super Smash Brothers Ultimate tournaments. I have been to hundreds of events in that time, 
and subsequently have needed to be seeded into a bracket as accurately as possible. As I learned from the T.O.'s that run these brackets, seeding can be a very tedious and 
thankless task, as there is always at least someone who is not happy with their projected matchups. This project aims to solve this issue by doing the seeding for the T.O.'s 
and resolve as many people's complaints as possible. 

## Technology/Dependencies Used
[Java JDK](https://www.oracle.com/java/technologies/javase-jdk15-downloads.html)

[htmlunit-2.38.0](https://sourceforge.net/projects/htmlunit/files/htmlunit/2.38.0/)

[MySQL](https://dev.mysql.com/downloads/installer/)

## Install

## Features

### Seasons
Seasons are used to group tournament results into date ranges or separate results of various games. 
#### • Season Creation
Upon starting up the program for the first time, nothing can be done until a "season" is created. By clicking the "+" at the top of the main menu, a new season can be created.
The season must have a unique name; otherwise, the user will be prompted to select a different season name. 
#### • Season Selection
After a season or multiple seasons have been created, they will appear in the drop-down menu at the top left of the main menu. To use a season, simple select it from the 
drop down menu. 
#### • Season Settings
Once a season is selected, its settings can be viewed by clicking the info button at the top right of the main menu. Here, you can change the name of the season to a name
that is not currently in use, see when the season was created, how many tournaments are recorded in the season, and delete the season. 

**NOTE:** Deleting the season cannot be undone and will delete all tournament info in that season. 

___________________________________________________________________________________________________________________________________________________________________
### Seed Bracket
This feature takes a challonge.com bracket link and automatically seeds the entrants based on past performances. The more data imported into the season in use, the more accurate 
the seeding will be. 
#### • New Players
If there are players or tags in the bracket that have been in a bracket seeded or imported by the program, a menu will pop-up displaying all of the unknown player tags.
If a player has entered the bracket under a different than normal, type in the normal tag for this player, and the program will cross reference that player's info when making the bracket.
#### • Conflict Reseeding
By default, this program seeds entrants strictly upon their ranking relative those entered. However, a common complaint T.O.s' receive is that entrants are consistently
playing the same people every week or are seeded to play against someone they practice against. By selecting "Reseed to avoid recent matchups," the program will attempt to 
reseed the tournament  bracket avoid having entrants play someone they have played against within their last 2 tournaments. The number of rounds the bracket will look ahead in its reseeding 
can be controlled by option directly below the reseed checkbox. If a conflict cannot be resolved by the program, it will display all unresolved conflicts in a menu that pops up with the bracket.

By choosing to reseed the bracket through 0 rounds, the user can see what conflicts would arise through the first 3 rounds by seeding strictly based on player rank.
#### • Player Highlighting
Once the bracket is seeded, a new window will pop up that displays the recommended bracket alongside a list of the seeding order. By clicking on one of the players in the seeding list,
all of that player's matches will be highlighted in the bracket. To unhighlight the player, simply reselect the player in the seeded list. 

___________________________________________________________________________________________________________________________________________________________________
### Import Results
This feature takes challonge.com bracket links with finished tournament brackets and imports the results into the season data to more accurately seed future brackets. 
#### • New Players
If there are players or tags in the bracket that have been in a bracket seeded or imported by the program, a menu will pop-up displaying all the unknown player tags.
If a player has entered the bracket under a different than normal, type in the normal tag for this player, and the program will import the tournament results under the 
user-entered name. 

___________________________________________________________________________________________________________________________________________________________________
### View Rankings
This feature allows the user to view the season data for each entrant in order of their ranking. The rankings display each player's main tag, wins, losses, and ELO score that
determines their rank.
#### • Ranking System
Players are ranked in the season by utilizing an ELO scoring system. The formula used is as follows:
```
(player_elo * player_sets) + opponent_score +- 400) / (player_sets+1);
```
The +- is determined by if the player won the set or not: +400 if won, -400 if lost. If either player has yet to record 5 played sets, their ELO is treated as 1200.

As ELO tries to rank players based on strength of opponents rather than finishes, the rankings can have situations where some players are ranked lower than what they 
typically place. As more data is imported into a season, the rankings should more closely resemble what the user might expect.

#### • Search for player
To search for a certain player in the rankings, type part or all of the player's main tag in the search box in the filter window. Once searched for, the rankings list will display 
only players whose tags contain at least part of the searched for name.

___________________________________________________________________________________________________________________________________________________________________
### Player Profiles
While inside of the rankings window, a player profile window can be pulled up by double clicking on a player's info. Doing so will bring up a player's main tag, rank in the season,
set count, win percentage, and 4 tabbed windows.
#### • Tournies
The first tabbed window shows all of the tournaments that player has entered in this season. The data lists the tournament's name on Challonge.com, the date it was held, the player's
ranking in that tournament, and how many entrants there were. 
#### • H2H (Head to Head)
The second tabbed window shows the player's head-to-head matchups vs everyone played in that season. The data will show the main tag of the opponent, wins against, losses against, 
and the last date the two players played.
#### • Tags
The third tabbed window shows all the tags that player has ever entered a bracket under, regardless of season. The player's main tag is on top. The user can also add new tags to
associate the player with, select a new main tag for the player, or delete tags from the player profile. 

**Note:** a main tag cannot be deleted. A new tag must first be made the main tag before deleting the old main tag. 
#### • Exceptions
The fourth tabbed window allows the user to specify opponents a player should always be reseeded to avoid playing against. Some reasons for this would include the two players
being training buddies, roommates, or various other reasons. Any exception can also be deleted from the list.
