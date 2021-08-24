# Auto backup for Project Zomboid

Simple javaFX application to automatically backup your Project Zomboid save file, periodically or once after starting the program.  
Perfect when you don't want to lose your long-term character to a bug, a freeze or a save file corruption when messing around with mods.

There is a compiled version in the Release tab, I uploaded the files aswell, so you can compile it yourself.

![Screenshot of the program](https://i.imgur.com/Nox4K7M.png)

**How to use it:**

•Run the jar file (It will generate a backup.properties file to remember the folder paths)  
•Choose your save file, you wish to backup (Zomboid\Saves\Survivor\\**Your save folder**)  
•Choose a folder, where to back them up (ex. Zomboid\Saves\Backups)  
•Type in the interval, how often the program should backup the save  
•Start the program  


**How to automatically open it when you open up the game:**  

•Drag the jar into the game folder  
•Create a BAT file and edit the file locations if necessary (.jar and .exe)  
```
@echo off 
cd "C:\Program Files (x86)\Steam\steamapps\common\ProjectZomboid\PZ_Autobackup.jar"
start PZ_Autobackup.jar
cd "C:\Program Files (x86)\Steam\steamapps\common\ProjectZomboid\ProjectZomboid64.exe"
start ProjectZomboid64.exe
exit
```
•Open up your steam library and right click Project Zomboid -> Properties  
•In launch options type the path of the BAT file and %command% to the end:
```
"C:\Program Files (x86)\Steam\steamapps\common\ProjectZomboid\RUN with backup.bat" %command%
```
• When you open your game up from steam it will automatically open up the backup program aswell.  
