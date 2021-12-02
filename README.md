ok 2 things, i hope you like what i have done with my stuff, i needed to make my own protocols, and spent like 3 hours debugging why packet stuff was breaking, because order matters...

ANYWAY:
Servers: 
java -jar JarfileHere.jar -server -port=8123

Client:
 java -jar JarfileHere.jar -client -address=localhost -port=8123

You NEED to start the server first, and then 2 clients, there's currently a bug with client connecting and disconnecting but for the most part if both people connect and both disconnect it should be fine