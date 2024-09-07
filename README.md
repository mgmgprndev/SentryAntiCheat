# SentryAntiCheat
SentryAntiCheat is open-source  packet based anti-cheat solution for Minecraft 1.8. <br>
Detects Fly, Speed, WaterWalk, NoFall, Timer etc... (:
And Free........ HaHa!

# Issues
1. WallClimb falses if jump near wall.
2. WaterWalk falses if in water and move vertically slowly. 

# ToDo (It will delay a lot)
1. Customize Alert System - allows use many customized alerts!
2. GUI, Useful commands - make it easier to use, moderators will be happy.
3. Configuration System - make it allows disable, enable or configure checks. (This is not easy btw....)
5. Auto Punish System - let's ban player cuz they cheating!

# Command ToDo
1. `/sentry webui` webui
2. `/sentry alt <player>` command, allow to scan player's alt.
3. `/sentry info <player>` lookup player IP, ISP, country
4. `/sentry aurabot <player>` test aura

# WebUI Idea Memo (no eta, no guarantee)
It can turn on by running `/sentry webui`, it will generates URL for check some violation data from web.
After open the link, it will send message to server to let accept connection. It will show verification code, and put the code on web to get access.
In WebUI you can see these informations:
1. Player list
2. Violation datas
3. debug informations
