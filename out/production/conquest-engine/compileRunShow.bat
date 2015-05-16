::Compile
dir /b /s *.java>sources.txt
md classes
javac -d classes @sources.txt
del sources.txt

::Run
cd classes
java main.RunGame 0 0 0 "java bot.BotStarter" "java bot.BotStarter2"

pause