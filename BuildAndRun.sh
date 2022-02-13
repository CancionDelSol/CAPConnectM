#Prepare environment
echo "Preparing environment"
mkdir src/build
mkdir src/build/classes
mkdir src/package
cp src/*.java src/package/

# Create class intermediates
echo "Creating class intermediates"
javac -sourcepath src -d src/build/classes src/package/*.java

echo "Executing from classes"
#java -classpath src/build/classes/ Program

echo "Creating manifest"
echo Main-Class: Program>src/myManifest

echo "Creating jar"
jar cfm src/build/ConnectM.jar src/myManifest -C src/build/classes/ .
cp src/build/ConnectM.jar ConnectM.jar

# Clean up intermediates
echo "Cleaning Up"
#rm -r src/build
#rm src/myManifest
#rm -r src/package

# Start program
echo "Starting program"
java -jar ConnectM.jar