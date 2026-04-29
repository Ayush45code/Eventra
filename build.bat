@echo off
echo Building Eventra Backend...

REM Create directories for compiled classes
if not exist "target\classes" mkdir target\classes
if not exist "target\classes\com\eventra\model" mkdir target\classes\com\eventra\model
if not exist "target\classes\com\eventra\dao" mkdir target\classes\com\eventra\dao
if not exist "target\classes\com\eventra\servlet" mkdir target\classes\com\eventra\servlet
if not exist "target\classes\com\eventra\util" mkdir target\classes\com\eventra\util

REM Compile Java files
echo Compiling Java files...
javac -cp "lib\*" -d target\classes ^
    src\main\java\com\eventra\model\User.java ^
    src\main\java\com\eventra\model\Event.java ^
    src\main\java\com\eventra\util\DatabaseConnection.java ^
    src\main\java\com\eventra\dao\UserDAO.java ^
    src\main\java\com\eventra\servlet\ApiServlet.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Creating WAR file...
    
    REM Create lib directory if it doesn't exist
    if not exist "lib" mkdir lib
    
    REM Download required JAR files if they don't exist
    echo Checking for required libraries...
    if not exist "lib\postgresql-42.7.4.jar" (
        echo Please download postgresql-42.7.4.jar and place it in lib directory
        echo Download from: https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
    )
    
    if not exist "lib\gson-2.10.1.jar" (
        echo Please download gson-2.10.1.jar and place it in lib directory  
        echo Download from: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
    )
    
    if not exist "lib\javax.servlet-api-4.0.1.jar" (
        echo Please download javax.servlet-api-4.0.1.jar and place it in lib directory
        echo Download from: https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar
    )
    
    echo.
    echo Build completed successfully!
    echo Copy the compiled classes and lib folder to your Tomcat webapps directory.
    echo Or create a WAR file manually if needed.
    
) else (
    echo Compilation failed!
    echo Please check for compilation errors above.
)

pause
