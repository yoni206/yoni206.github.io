#INIT
rm -rf jars
mkdir jars

#Download
cd jars
wget https://yoni-tools.googlecode.com/svn/trunk/tex2smv/src/Tex2Smv.java
wget https://yoni-tools.googlecode.com/svn/trunk/smv-utils/src/SmvUtils.java
wget https://yoni-tools.googlecode.com/svn/trunk/smv-ext/src/SmvAbstractVariablizer.java

#tex2smv
echo "Main-Class: Tex2Smv\n" > Manifest.txt
javac SmvUtils.java
javac Tex2Smv.java
jar cfm tex2smv.jar Manifest.txt Tex2Smv.class SmvUtils.class

#abstractsmv
echo "Main-Class: SmvAbstractVariablizer\n" > Manifest.txt
javac SmvUtils.java
javac SmvAbstractVariablizer.java
jar cfm abstractsmv.jar Manifest.txt SmvAbstractVariablizer.class SmvUtils.class