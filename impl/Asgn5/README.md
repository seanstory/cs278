#Asgn 5

(Reliable Data Transfer)
I implemented a non-pipelined RDT protocol.  The sender sends a packet, and waits a given
amount of time for an ACK message back from the receiver.  If the ACK is not received
within the given timeout, the sender re-sends the same packet and waits again for an ACK.
As soon as an ACK is received for the current packet, the sender begins sending the
next packet, repeating the process.

If the sender receives and ACK containing the non-current sequence number, it simply
ignores that ACK.

If the receiver receives a packet containing the non-current sequence number, it also
ignores and discards it, but it then sends an ACK for that sequence number.  This handles
the case where the packet was successfully received but the ACK message was lost, so the
sender resent the packet even though it was indeed successfully received.

To run the assignment, please do:

mvm clean package
vagrant up
vagrant ssh
java -cp shared/target/RDT-App-1.0-SNAPSHOT.jar edu.vanderbilt.cs278.Asgn5.RDTServer 192.168.50.4 8888


(in another window, not in vagrant)
java -cp target/RDT-Snapshot.jar edu.vanderbilt.cs278.Asgn5.RDTClient 192.168.50.4 8888 bigfile.txt


