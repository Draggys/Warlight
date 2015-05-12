dir /b /s *.java>sources.txt
md classes
javac -d classes @sources.txt
del sources.txt
pause