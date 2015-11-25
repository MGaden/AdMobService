# AdMobService
Service to help you to monetize your android app by show Interstatial Ad every day

I made a complete example using this service to help you.

How to use it:

1- you will find source code folder contains all needed files.

2- copy package(com.gaden.admob) to your java src folder.

3- in this package i imported main package so you need to change import by your package name.

4- you need to add 2 class (AppParameters.java , OneShotAlarm.java) in Your main java src , and change package name with your package Name.

5- you need to add 1 xml in your layout (activity_interstatial.xml)

6- you need to add style node in your Style.xml

7- copy jar file (AdApp.jar) in your libs folder and add it as module in your project

8- Enable Google Play Service in your project

9- Edit Your manifest File to add permissions and activities (you will find in in file named :Add Me in manifest file.txt)

10- Build Your Project.

11- If You find errors, i think you need to check your package name in added files)

12- After Build Success, You need to add Interstatial Keys in (com.gaden.admob.MyConstant) Class , public static final String[] InterstatialKey

13- Best practice , Please Don't add more than 5 Keys in this array.

