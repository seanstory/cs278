# test-delete-files.sh
# This file will test the ability of Dropbox to delete files

#setup constants
OUTPUT=$1
WAIT=$2
CLIENT=$3
HOST=$4
STOP_FLAG=0 #indicate to loops to exit
set -e

#define function to minimize code duplication
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

# test deleting 1 file from the host
touch $HOST/delete-1.txt
touch $CLIENT/delete-1.txt
sleep 5
rm $HOST/delete-1.txt
verify-deleted-file $CLIENT/delete-1.txt "deleting 1 file from the host" 1
STOP_FLAG=0

# test deleting 1 file from the client
touch $HOST/delete-2.txt
touch $CLIENT/delete-2.txt
sleep 5
rm $CLIENT/delete-2.txt
verify-deleted-file $HOST/delete-2.txt "deleting 1 file from the client" 1
STOP_FLAG=0

# test deleting 1000 files from the host
echo "deleting 100 files from the host"
for i in {3001..4000}; do touch $HOST/delete-$i.txt; touch $CLIENT/delete-$i.txt; done
sleep 5
for i in {3001..4000}; do rm $HOST/delete-$i.txt; done
for i in {3001..4000}; do verify-deleted-file $CLIENT/delete-$i.txt "deleting 1000 files from the host" 0; done
STOP_FLAG=0

# test deleting 1000 files from the client
echo "deleting 1000 files from the client"
for i in {4001..5000}; do touch $CLIENT/delete-$i.txt; touch $HOST/delete-$i.txt; done
sleep 5
for i in {4001..5000}; do rm $CLIENT/delete-$i.txt; done
for i in {4001..5000}; do verify-deleted-file $HOST/delete-$i.txt "deleting 1000 files from the client" 0; done
STOP_FLAG=0

echo
