author: Chev Kodama

ALL OTHER INFORMATION IN USER'S MANUAL (FarmersMarkets/manuals/manual.pdf)

FarmersMarkets Directory Contents:
	Directories
	- src (source code)
	- lib (jdbc jar)
	- out (executables)
	- docs (all Javadoc)
	- manuals (user's manual, database diagram, readme)
	- scripts (run and javadoc scripts)


How to run scripts from the command line:

**IMPORTANT**
You must have a JDK, JavaFX, and MySQL server installed to run this application.
Your MySQL server must be set up with at least one user able to create, edit, and view databases.
	This is what you need from your MySQL server (assuming it is installed on the computer running the application):
	- Port number
	- Username
	- Password

1) Navigate to the FarmersMarkets/src/main/resources directory.
2) Open the db.properties file.
3) Replace "____PORT_NUMBER____" with your MySQL server port number.
4) Replace "____USERNAME____" with your MySQL server user's username.
5) Replace "____PASSWORD____" with your MySQL server user's password.
6) Navigate back to the FarmersMarkets directory.
7) Open a terminal in the FarmersMarkets directory.
8) Run the following command:
	Windows:
		scripts\Run.bat <PATH_TO_JAVAFX\lib>
	macOS/Linux:
		bash scripts/Run.sh <PATH_TO_JAVAFX\lib>

Development platform:
OS: Windows 11 Education
Version: 23H2