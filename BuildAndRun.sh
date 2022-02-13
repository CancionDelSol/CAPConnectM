#Prepare environment
mkdir src/build/
mkdir src/build/classes
mkdir src/package
cp src/*.java src/package/

# Create class intermediates
javac -sourcepath src -d src/build/classes src/package/*.java

#java -classpath build/classes/ Program
echo Main-Class: Program>src/myManifest
jar cfm src/build/ConnectM.jar src/myManifest -C src/build/classes/ src/
cp src/build/ConnectM.jar ConnectM.jar

# Clean up intermediates
rm -r src/build
rm src/myManifest
rm -r src/package

# Start program
java -jar build/ConnectM.jar