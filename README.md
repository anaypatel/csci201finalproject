# csci201finalproject

How to run in Eclipse:

1. Make sure your build path is updated. If there are errors, there is a folder called "jar files" in the project folder. Update the build path such that those jar files are linked with the project.

2. Make sure the SQL server on your machine has a username of “root” and a password of “root”. 
- Make sure the SQL server is started (this is very important, as you will not be able to create the database or login/signup to the game unless the server is running)
- Execute the create_database.sql query in the project within the sql folder, which creates a database called GameInfo with a table called Users (id, username, password, kills, deaths)
- This can also be done manually, but it is important that the schema name, the table name, and the column headers match those above.

3. Open the GameServer class and run it. In the console, choose a port greater than 1024.

4. Open the Client class and run it. When prompted, sign up for an account or log in. Make sure your username/password are greater than 6 characters when you sign up.

5. Once you are signed in, connect to "localhost" with the port that you specified when you ran the server. Click the join button.

6. Use the WASD keys to move around, and the spacebar button to fire.

7. Press escape to log out and view the leaderboard.

8. Try testing it with multiple clients on your computer.

Game rules:

1. Each player can fire up to 5 shots at once (once the shots have left the screen, they can fire again)
 
2. Players have 5 lives, and they will randomly respawn after they die.

3. If a player runs out of lives, they will wait until there is only one player standing, after which the game will reset. 

4. You can only see a small radius around you.

5. Once there is only one player left standing, a new round will commence and players will be able to re-connect.
