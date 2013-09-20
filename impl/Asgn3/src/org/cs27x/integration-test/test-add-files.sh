# test-add-files.sh
# This file will test the ability of the Dropbox tool to add files correctly

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

# test adding 1 file to the host
touch $HOST/a.txt
verify-added-file $CLIENT/a.txt "Adding 1 file to the host" 1 
STOP_FLAG=0

# test adding 1 file to the client
touch $CLIENT/b.txt
verify-added-file $HOST/b.txt "Adding 1 file to the client"  1
STOP_FLAG=0

# test adding 1000 files to the host
echo "Adding 1000 files to the host"
for i in {1..1000}; do touch $HOST/$i.txt; done
for i in {1..1000}; do verify-added-file $CLIENT/$i.txt "Adding 1000 files to the host" 0; done
STOP_FLAG=0

# test adding 1000 files to the client
echo "Adding 1000 files to the client"
for i in {1001..2000}; do touch $CLIENT/$i.txt; done
for i in {1001..2000}; do verify-added-file $HOST/$i.txt "Adding 1000 files to the client" 0; done
STOP_FLAG=0

# test adding 1000 files to both the client and the host
echo "Adding 1000 files to the both, simultaneously"
for i in {2001..2500}; do touch $HOST/$i-a.txt; touch $CLIENT/$i-b.txt; done
for i in {2001..2500}; do
  verify-added-file $CLIENT/$i-a.txt "Adding 1000 files to the both, simultaneously" 0
  verify-added-file $HOST/$i-b.txt "Adding 1000 files to the both, simultaneously" 0
done


echo