NOTE: The executable is a jar file so to execute it enter java -jar 15puzzlesolver.jar
NOTE: The text files containing representations of the 15 puzzle initial configuration and goal configurations need to be in the same directory as the jar file

Simply follow the prompts given. 

It will first ask you for the name of the text file containing the 15 puzzle representation. This should be in a form where each tile is represented by a single number. One row in the text file represents one row in the 15 puzzle board. Each number should be separated by a space. There should be no excess lines. The text file needs to look like the following as the 15 puzzle configuration text file input has minimal error handling:

5 3 0 4
7 2 6 8
1 9 10 11
13 14 15 12

It will then ask you for the name of the text file containing the goal configuration. For this project I believe Professor Biswas wanted it to look like the following:

1 2 3 4
5 6 7 8
9 10 11 12
13 14 15 0

From here on out there is substantial error handling involved. 

It will then ask you for which search algorithm to use. After this it will simply ask you for whatever values are relevant to the particular search algorithm you chose to use.

After everything is set up, the program will search, print the solution and associated statistics, then ask if you want to play again. Enter 1 to play again from the beginning, any other number will exit the program.