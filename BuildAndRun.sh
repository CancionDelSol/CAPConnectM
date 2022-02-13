javac -sourcepath src -d src/build/classes src/package/*.java
cd src/package/
cp *.java ../
cd ..
#java -classpath build/classes/ Program
echo Main-Class: Program>myManifest
jar cfm build/ConnectM.jar myManifest -C build/classes/ .
cp build/ConnectM.jar ../ConnectM.jar
rm build/classes/*
rm myManifest
rm *.java
java -jar build/ConnectM.jar