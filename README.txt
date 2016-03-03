For compiling:
move terminal to the code folder.
Then:

javac *.java

Move into smartsewage folder

javac *.java

For running:
move terminal to the tcode folder.
Then:

sudo java -cp ".:smartsewage/mysql-connector-java-5.0.8-bin.jar" smartsewage.SmartSewageServer jdbc:mysql://localhost/smartsewage root 31011995 5 20000


(run terminal as administrator)

for running a sample client:
java ServerTest localhost.

for testing :
./tester.sh testcase-7.txt | java smartsewage.SewageSimulator localhost 5
