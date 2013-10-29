#Asgn 5
To run the assignment, please do:

mvm clean package
vagrant up
vagrant ssh
java -cp shared/target/RDT-Snapshot.jar edu.vanderbilt.cs278.Asgn5.RDTServer <ipaddress> <port>

(in another window)
java -cp target/RDT-Snapshot.jar edu.vanderbilt.cs278.Asgn5.RDTClient <same-ipaddress> <same-port> bigfile.txt


