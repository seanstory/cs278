# test-all.sh
# This file will test adding, updating, and deleting files with Dropbox

#setup constants
OUTPUT=$1
WAIT=$2
CLIENT=$3
HOST=$4
STOP_FLAG=0 #indicate to loops to exit
set -e

#define helper functions to minimize code duplication
function verify-added-file { # @params,  1:expected file, 2:echo note, 3:print echo note?
	if [ $STOP_FLAG -eq 0 ]
	then
		if [ $3 -eq 1 ];then echo $2; fi #print an echo note, if explicitly told to
		counter=0
		while [ ! -e $1 ]
		do 
		  sleep 1
		  if [ $counter -gt $WAIT ]; then echo "FAILED "$2": "$1 >> $OUTPUT; STOP_FLAG=1; break; fi
		  counter=$(($counter+1))
		done
	fi
}

function verify-updated-file { # @params,  1:expected file, 2:updated file 3:echo note, 4:print echo note?
	if [ $STOP_FLAG -eq 0 ]
	then
		if [ $4 -eq 1 ];then echo $3; fi #print an echo note, if explicitly told to
		counter=0
		while [[ ! -e $1 || ! -z $(diff $1 $2) ]] # While the file doesn't exist or the contents of the files are different
		do 
		  sleep 1
		  if [ $counter -gt $WAIT ]; then echo "FAILED "$3": "$1 >> $OUTPUT; STOP_FLAG=1; break; fi
		  counter=$(($counter+1))
		done
	fi
}

function verify-deleted-file { # @params,  1:expected file, 2:echo note, 3:print echo note?
	if [ $STOP_FLAG -eq 0 ]
	then
		if [ $3 -eq 1 ];then echo $2; fi #print an echo note, if explicitly told to
		counter=0
		while [ -e $1 ]
		do 
		  sleep 1
		  if [ $counter -gt $WAIT ]; then echo "FAILED "$2": "$1 >> $OUTPUT; STOP_FLAG=1; break; fi
		  counter=$(($counter+1))
		done
	fi
}

# test add, update, file host
touch $HOST/all-1.txt
echo "update 1" >> $HOST/all-1.txt
verify-added-file $CLIENT/all-1.txt "Adding and updating a file in host" 1
verify-updated-file $CLIENT/all-1.txt $HOST/all-1.txt "adding and Updating a file in host" 0
STOP_FLAG=0

# test add, update, file client
touch $CLIENT/all-2.txt
echo "update 2" >> $CLIENT/all-2.txt
verify-added-file $HOST/all-2.txt "Adding and updating a file in client" 1
verify-updated-file $HOST/all-2.txt $CLIENT/all-2.txt "adding and Updating a file in client" 0
STOP_FLAG=0

# test update, delete file host
echo "update 3" >> $HOST/all-1.txt
rm $HOST/all-1.txt
verify-deleted-file $CLIENT/all-1.txt "deleting a file in host after update" 1
STOP_FLAG=0

# test update, delete file client
echo "update 4" >> $CLIENT/all-2.txt
rm $CLIENT/all-2.txt
verify-deleted-file $HOST/all-1.txt "deleting a file in client after update" 1
STOP_FLAG=0

# test add, update, delete  host
touch $HOST/all.txt
echo "remove this" >> $HOST/all.txt
rm $HOST/all.txt
verify-deleted-file $CLIENT/all.txt "deleting a file in client after add and update" 1
STOP_FLAG=0

# test add, update, delete client
touch $CLIENT/all.txt
echo "remove this" >> $CLIENT/all.txt
rm $CLIENT/all.txt
verify-deleted-file $HOST/all.txt "deleting a file in host after add and update" 1
STOP_FLAG=0

# test add-update-delete
touch $CLIENT/gone.txt
touch $HOST/gone.txt
echo "host" >> $HOST/gone.txt
echo "client" >> $CLIENT/gone.txt
rm $HOST/gone.txt
rm $CLIENT/gone.txt
verify-deleted-file $HOST/gone.txt "add-update-delete a file in both" 1
verify-deleted-file $CLIENT/gone.txt "add-update-delete a file in both" 0
STOP_FLAG=0

echo

